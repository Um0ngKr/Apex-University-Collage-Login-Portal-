package com.apex.controller.admin;

import com.apex.dao.AuditLogDAO;
import com.apex.dao.DashboardDAO;
import com.apex.dao.PaymentDAO;
import com.apex.model.AuditLog;
import com.apex.model.Payment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/admin/dashboard", "/admin"})
public class AdminDashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DashboardDAO dashboardDAO;
    private AuditLogDAO auditDAO;
    private PaymentDAO paymentDAO;

    @Override
    public void init() throws ServletException {
        dashboardDAO = new DashboardDAO();
        auditDAO = new AuditLogDAO();
        paymentDAO = new PaymentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> stats = dashboardDAO.getAdminStats();
        List<AuditLog> recentLogs = auditDAO.getRecentLogs(10);
        List<Payment> recentPayments = paymentDAO.getAllPayments(null);
        if (recentPayments.size() > 5) {
            recentPayments = recentPayments.subList(0, 5);
        }

        req.setAttribute("stats", stats);
        req.setAttribute("recentLogs", recentLogs);
        req.setAttribute("recentPayments", recentPayments);

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
