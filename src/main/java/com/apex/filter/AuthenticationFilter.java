package com.apex.filter;

import com.apex.model.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Role-Based Access Control Filter for Apex University System.
 * Protects Admin, Teacher, Student, and College portals.
 */
@WebFilter(urlPatterns = {"/admin/*", "/teacher/*", "/student/*", "/college/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String contextPath = req.getContextPath();
        String uri = req.getRequestURI().substring(contextPath.length());

        User user = (session != null) ? (User) session.getAttribute("CURRENT_USER") : null;

        if (user == null) {
            String targetRole = "STUDENT";
            if (uri.startsWith("/admin")) targetRole = "ADMIN";
            else if (uri.startsWith("/teacher")) targetRole = "TEACHER";
            else if (uri.startsWith("/college")) targetRole = "COLLEGE";

            res.sendRedirect(contextPath + "/login?role=" + targetRole + "&msg=Session+expired.+Please+log+in.");
            return;
        }

        // Verify role authorization
        boolean authorized = false;
        if (uri.startsWith("/admin") && user.isAdmin()) {
            authorized = true;
        } else if (uri.startsWith("/teacher") && user.isTeacher()) {
            authorized = true;
        } else if (uri.startsWith("/student") && user.isStudent()) {
            authorized = true;
        } else if (uri.startsWith("/college") && user.isCollege()) {
            authorized = true;
        }

        if (!authorized) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You do not have permissions to access this portal.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
