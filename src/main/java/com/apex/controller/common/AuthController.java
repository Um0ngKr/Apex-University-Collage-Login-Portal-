package com.apex.controller.common;

import com.apex.dao.AuditLogDAO;
import com.apex.dao.UserDAO;
import com.apex.dao.UserDAO.AuthenticationResult;
import com.apex.model.User;
import com.apex.util.CsrfUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Core Authentication Controller for Apex University System.
 * Manages secure login, role-based dashboard dispatch, session fixation protection, and logout.
 */
@WebServlet(urlPatterns = {"/login", "/logout", "/change-password"})
public class AuthController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AuthController.class.getName());

    private UserDAO userDAO;
    private AuditLogDAO auditDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        auditDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            handleLogout(req, resp);
            return;
        }

        // If user already holds an authenticated session, redirect straight to their dashboard
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("CURRENT_USER") != null) {
            User user = (User) session.getAttribute("CURRENT_USER");
            if (user.isAdmin()) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                return;
            } else if (user.isTeacher()) {
                resp.sendRedirect(req.getContextPath() + "/teacher/dashboard");
                return;
            } else if (user.isCollege()) {
                resp.sendRedirect(req.getContextPath() + "/college/dashboard");
                return;
            } else if (user.isStudent()) {
                resp.sendRedirect(req.getContextPath() + "/student/dashboard");
                return;
            }
        }

        // Display login page
        String role = req.getParameter("role");
        if (role == null || role.trim().isEmpty()) {
            role = "STUDENT";
        }
        req.setAttribute("selectedRole", role.trim().toUpperCase());
        req.setAttribute("csrf_token", CsrfUtil.getToken(req.getSession(true)));
        req.getRequestDispatcher("/WEB-INF/views/public/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/login".equals(path)) {
            handleLogin(req, resp);
        } else if ("/change-password".equals(path)) {
            handleChangePassword(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String role = req.getParameter("role");
        if (role == null || role.trim().isEmpty()) {
            role = "STUDENT";
        }
        role = role.trim().toUpperCase();

        // 1. Validate CSRF token
        if (!CsrfUtil.isValid(req)) {
            LOGGER.warning("CSRF token validation failed for login request from IP: " + req.getRemoteAddr());
            req.setAttribute("errorMessage", "Session security token expired or invalid. Please refresh the page and try again.");
            req.setAttribute("selectedRole", role);
            req.setAttribute("csrf_token", CsrfUtil.getToken(req.getSession(true)));
            req.getRequestDispatcher("/WEB-INF/views/public/login.jsp").forward(req, resp);
            return;
        }

        String identifier = req.getParameter("identifier");
        String password = req.getParameter("password");

        // 2. Validate input presence
        if (identifier == null || identifier.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Username / Registration Number and Password are required.");
            req.setAttribute("selectedRole", role);
            req.setAttribute("csrf_token", CsrfUtil.getToken(req.getSession(true)));
            req.getRequestDispatcher("/WEB-INF/views/public/login.jsp").forward(req, resp);
            return;
        }

        identifier = identifier.trim();
        String clientIp = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");

        // 3. Authenticate against database records using verified BCrypt hashes
        AuthenticationResult authResult = userDAO.authenticateWithResult(identifier, password, role);

        if (authResult.isSuccess()) {
            User user = authResult.getUser();

            // Session Fixation Protection: regenerate session ID upon successful login
            req.changeSessionId();
            HttpSession session = req.getSession(true);
            session.setAttribute("CURRENT_USER", user);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes session timeout

            // Ensure fresh CSRF token is generated and tied to the newly regenerated session
            CsrfUtil.getToken(session);

            userDAO.recordLogin(user.getRole(), user.getRelatedId(), user.getUsername(), clientIp, userAgent, "SUCCESS");
            auditDAO.log(user.getRole(), user.getRelatedId(), user.getUsername(), "LOGIN", "login_history", null, "User logged in successfully", clientIp);

            // Redirect to appropriate role dashboard
            switch (user.getRole().toUpperCase()) {
                case "ADMIN":
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                    break;
                case "TEACHER":
                    resp.sendRedirect(req.getContextPath() + "/teacher/dashboard");
                    break;
                case "COLLEGE":
                    resp.sendRedirect(req.getContextPath() + "/college/dashboard");
                    break;
                case "STUDENT":
                default:
                    resp.sendRedirect(req.getContextPath() + "/student/dashboard");
                    break;
            }
        } else {
            userDAO.recordLogin(role, null, identifier, clientIp, userAgent, "FAILED");

            String userMessage;
            switch (authResult.getStatus()) {
                case ACCOUNT_INACTIVE:
                    userMessage = "Your account is currently inactive or deactivated. Please contact the administrator.";
                    break;
                case DATABASE_ERROR:
                    userMessage = "Database service is temporarily unavailable. Please try again shortly.";
                    break;
                case INVALID_ROLE:
                    userMessage = "Invalid portal role selected. Please choose a valid portal.";
                    break;
                case ACCOUNT_NOT_FOUND:
                case INVALID_CREDENTIALS:
                default:
                    userMessage = "Invalid credentials. Please verify your ID, Password, and selected Portal role.";
                    break;
            }

            req.setAttribute("errorMessage", userMessage);
            req.setAttribute("selectedRole", role);
            req.setAttribute("csrf_token", CsrfUtil.getToken(req.getSession(true)));
            req.getRequestDispatcher("/WEB-INF/views/public/login.jsp").forward(req, resp);
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("CURRENT_USER");
            if (user != null) {
                auditDAO.log(user.getRole(), user.getRelatedId(), user.getUsername(), "LOGOUT", "login_history", null, "User logged out", req.getRemoteAddr());
            }
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login?msg=You+have+been+successfully+logged+out.");
    }

    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("CURRENT_USER") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String oldPass = req.getParameter("old_password");
        String newPass = req.getParameter("new_password");
        String confirmPass = req.getParameter("confirm_password");

        String redirectUrl = req.getContextPath() + "/" + user.getRole().toLowerCase() + "/profile";

        if (newPass == null || newPass.length() < 6 || !newPass.equals(confirmPass)) {
            resp.sendRedirect(redirectUrl + "?error=Password+must+be+at+least+6+characters+and+match+confirmation.");
            return;
        }

        boolean success = userDAO.changePassword(user.getRole(), user.getRelatedId(), oldPass, newPass);
        if (success) {
            auditDAO.log(user.getRole(), user.getRelatedId(), user.getUsername(), "CHANGE_PASSWORD", user.getRole().toLowerCase() + "s", user.getRelatedId(), "Password updated successfully", req.getRemoteAddr());
            resp.sendRedirect(redirectUrl + "?msg=Password+updated+successfully.");
        } else {
            resp.sendRedirect(redirectUrl + "?error=Current+password+is+incorrect.");
        }
    }
}
