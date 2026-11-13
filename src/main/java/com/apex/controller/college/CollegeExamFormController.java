package com.apex.controller.college;

import com.apex.dao.ExamFormDAO;
import com.apex.model.ExamForm;
import com.apex.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/college/exam-forms", "/college/exam-form-action"})
public class CollegeExamFormController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ExamFormDAO examFormDAO;

    @Override
    public void init() throws ServletException {
        examFormDAO = new ExamFormDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        int collegeId = user.getCollegeId() != null ? user.getCollegeId() : user.getRelatedId();

        String statusFilter = req.getParameter("status");
        List<ExamForm> forms = examFormDAO.getExamForms(null, collegeId, statusFilter);

        req.setAttribute("forms", forms);
        req.setAttribute("selectedStatus", statusFilter);

        req.getRequestDispatcher("/WEB-INF/views/college/exam-forms.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        int collegeId = user.getCollegeId() != null ? user.getCollegeId() : user.getRelatedId();

        int formId = Integer.parseInt(req.getParameter("form_id"));
        String status = req.getParameter("status"); // APPROVED / REJECTED
        String reason = req.getParameter("reason");

        // Verify form belongs to this college
        ExamForm form = examFormDAO.getFormById(formId);
        if (form != null && form.getCollegeId() == collegeId) {
            examFormDAO.updateCollegeApproval(formId, status, reason);
        }

        resp.sendRedirect(req.getContextPath() + "/college/exam-forms?msg=Exam+form+verification+updated.");
    }
}
