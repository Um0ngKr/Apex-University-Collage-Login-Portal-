package com.apex.controller.admin;

import com.apex.dao.AuditLogDAO;
import com.apex.dao.PaymentDAO;
import com.apex.model.Payment;
import com.apex.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/payments", "/admin/payment-action"})
public class AdminPaymentController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private PaymentDAO paymentDAO;
    private AuditLogDAO auditDAO;

    @Override
    public void init() throws ServletException {
        paymentDAO = new PaymentDAO();
        auditDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String status = req.getParameter("status");
        List<Payment> payments = paymentDAO.getAllPayments(status);

        double totalSuccessfulAmount = 0.0;
        int successfulCount = 0;
        int pendingCount = 0;
        int failedCount = 0;

        for (Payment p : payments) {
            if ("SUCCESS".equalsIgnoreCase(p.getPaymentStatus())) {
                totalSuccessfulAmount += p.getAmount();
                successfulCount++;
            } else if ("PENDING".equalsIgnoreCase(p.getPaymentStatus())) {
                pendingCount++;
            } else {
                failedCount++;
            }
        }

        req.setAttribute("payments", payments);
        req.setAttribute("selectedStatus", status);
        req.setAttribute("totalSuccessfulAmount", totalSuccessfulAmount);
        req.setAttribute("successfulCount", successfulCount);
        req.setAttribute("pendingCount", pendingCount);
        req.setAttribute("failedCount", failedCount);

        req.getRequestDispatcher("/WEB-INF/views/admin/payments.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        User admin = (User) req.getSession().getAttribute("CURRENT_USER");

        if ("verify".equalsIgnoreCase(action)) {
            int paymentId = Integer.parseInt(req.getParameter("payment_id"));
            String status = req.getParameter("status"); // SUCCESS / FAILED

            paymentDAO.updatePaymentVerification(paymentId, admin.getId(), status);
            auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "VERIFY_PAYMENT", "payments", paymentId, "Updated payment status to: " + status, req.getRemoteAddr());
            resp.sendRedirect(req.getContextPath() + "/admin/payments?msg=Payment+status+updated.");
        }
    }
}
