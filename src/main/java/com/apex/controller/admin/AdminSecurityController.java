package com.apex.controller.admin;

import com.apex.dao.AuditLogDAO;
import com.apex.model.AuditLog;
import com.apex.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/security", "/admin/profile"})
public class AdminSecurityController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private AuditLogDAO auditDAO;

    @Override
    public void init() throws ServletException {
        auditDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/admin/profile".equals(path)) {
            User admin = (User) req.getSession().getAttribute("CURRENT_USER");
            req.setAttribute("admin", admin);
            req.getRequestDispatcher("/WEB-INF/views/admin/profile.jsp").forward(req, resp);
            return;
        }

        // Security & Audit Logs view
        List<AuditLog> logs = auditDAO.getRecentLogs(100);
        req.setAttribute("auditLogs", logs);
        req.getRequestDispatcher("/WEB-INF/views/admin/security.jsp").forward(req, resp);
    }
}
