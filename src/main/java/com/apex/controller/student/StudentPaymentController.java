package com.apex.controller.student;

import com.apex.dao.ExamFormDAO;
import com.apex.dao.PaymentDAO;
import com.apex.dao.StudentDAO;
import com.apex.model.ExamForm;
import com.apex.model.Payment;
import com.apex.model.Student;
import com.apex.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/student/payments", "/student/pay-fee"})
public class StudentPaymentController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private PaymentDAO paymentDAO;
    private ExamFormDAO examFormDAO;
    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        paymentDAO = new PaymentDAO();
        examFormDAO = new ExamFormDAO();
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        int studentId = user.getRelatedId();

        Student student = studentDAO.getStudentById(studentId);
        List<Payment> paymentHistory = paymentDAO.getPaymentsByStudent(studentId);
        List<ExamForm> allForms = examFormDAO.getFormsByStudent(studentId);

        List<ExamForm> pendingForms = new ArrayList<>();
        for (ExamForm f : allForms) {
            if ("PENDING".equalsIgnoreCase(f.getPaymentStatus())) {
                pendingForms.add(f);
            }
        }

        req.setAttribute("student", student);
        req.setAttribute("paymentHistory", paymentHistory);
        req.setAttribute("pendingForms", pendingForms);

        req.getRequestDispatcher("/WEB-INF/views/student/payments.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        int studentId = user.getRelatedId();

        int formId = Integer.parseInt(req.getParameter("form_id"));
        int examId = Integer.parseInt(req.getParameter("exam_id"));
        double amount = Double.parseDouble(req.getParameter("amount"));
        String paymentMethod = req.getParameter("payment_method"); // SANDBOX_CARD, SANDBOX_UPI, SANDBOX_NETBANKING

        boolean ok = paymentDAO.createSandboxPayment(
            studentId,
            examId,
            formId,
            amount,
            paymentMethod,
            "Examination Form Fee (Sandbox Simulation)"
        );

        if (ok) {
            resp.sendRedirect(req.getContextPath() + "/student/payments?msg=Payment+completed+successfully+in+Sandbox+Mode.+Receipt+generated.");
        } else {
            resp.sendRedirect(req.getContextPath() + "/student/payments?error=Payment+failed.+Please+try+again.");
        }
    }
}
