package com.apex.controller.student;

import com.apex.dao.ExaminationDAO;
import com.apex.dao.ExamFormDAO;
import com.apex.dao.StudentDAO;
import com.apex.dao.SubjectDAO;
import com.apex.model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/student/exam", "/student/exam-action"})
public class StudentExamController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ExaminationDAO examDAO;
    private ExamFormDAO examFormDAO;
    private StudentDAO studentDAO;
    private SubjectDAO subjectDAO;

    @Override
    public void init() throws ServletException {
        examDAO = new ExaminationDAO();
        examFormDAO = new ExamFormDAO();
        studentDAO = new StudentDAO();
        subjectDAO = new SubjectDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        int studentId = user.getRelatedId();

        Student student = studentDAO.getStudentById(studentId);
        List<Examination> activeExams = examDAO.getActiveFormFillupExams();
        List<ExamForm> myForms = examFormDAO.getFormsByStudent(studentId);
        List<Subject> regularSubjects = subjectDAO.getAllSubjects(student.getCourseId(), student.getCurrentSemesterId());

        req.setAttribute("student", student);
        req.setAttribute("activeExams", activeExams);
        req.setAttribute("myForms", myForms);
        req.setAttribute("regularSubjects", regularSubjects);

        req.getRequestDispatcher("/WEB-INF/views/student/exam-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        int studentId = user.getRelatedId();
        Student student = studentDAO.getStudentById(studentId);

        String examIdStr = req.getParameter("exam_id");
        if (examIdStr != null && !examIdStr.isEmpty()) {
            int examId = Integer.parseInt(examIdStr);
            boolean ok = examFormDAO.submitForm(examId, studentId, student.getCollegeId());
            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/student/exam?msg=Examination+form+submitted+successfully.+Please+complete+fee+payment+under+Payments+tab.");
                return;
            }
        }
        resp.sendRedirect(req.getContextPath() + "/student/exam?error=Unable+to+submit+examination+form.");
    }
}
