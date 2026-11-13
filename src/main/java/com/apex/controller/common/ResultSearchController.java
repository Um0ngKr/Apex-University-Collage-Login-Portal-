package com.apex.controller.common;

import com.apex.dao.AcademicSessionDAO;
import com.apex.dao.ExaminationDAO;
import com.apex.dao.MarkDAO;
import com.apex.dao.ResultDAO;
import com.apex.dao.StudentDAO;
import com.apex.model.AcademicSession;
import com.apex.model.Examination;
import com.apex.model.Mark;
import com.apex.model.Result;
import com.apex.model.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/result-search", "/results"})
public class ResultSearchController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ResultDAO resultDAO;
    private MarkDAO markDAO;
    private StudentDAO studentDAO;
    private ExaminationDAO examDAO;
    private AcademicSessionDAO sessionDAO;

    @Override
    public void init() throws ServletException {
        resultDAO = new ResultDAO();
        markDAO = new MarkDAO();
        studentDAO = new StudentDAO();
        examDAO = new ExaminationDAO();
        sessionDAO = new AcademicSessionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String regNo = req.getParameter("reg_no");
        String rollNo = req.getParameter("roll_no");

        List<Examination> exams = examDAO.getAllExaminations();
        List<AcademicSession> sessions = sessionDAO.getAllSessions();
        req.setAttribute("exams", exams);
        req.setAttribute("sessions", sessions);

        if ((regNo != null && !regNo.trim().isEmpty()) || (rollNo != null && !rollNo.trim().isEmpty())) {
            searchAndDisplay(req, resp);
        } else {
            req.getRequestDispatcher("/WEB-INF/views/public/result-search.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Examination> exams = examDAO.getAllExaminations();
        List<AcademicSession> sessions = sessionDAO.getAllSessions();
        req.setAttribute("exams", exams);
        req.setAttribute("sessions", sessions);

        searchAndDisplay(req, resp);
    }

    private void searchAndDisplay(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String regNo = req.getParameter("reg_no");
        String rollNo = req.getParameter("roll_no");
        String examIdStr = req.getParameter("exam_id");
        String semStr = req.getParameter("semester");

        Integer examId = (examIdStr != null && !examIdStr.isEmpty()) ? Integer.parseInt(examIdStr) : null;
        Integer sem = (semStr != null && !semStr.isEmpty()) ? Integer.parseInt(semStr) : null;

        Result result = resultDAO.searchPublicResult(regNo, rollNo, examId, sem);

        if (result != null) {
            Student student = studentDAO.getStudentById(result.getStudentId());
            List<Mark> marks = markDAO.getMarksByExamAndStudent(result.getExamId(), result.getStudentId());

            req.setAttribute("result", result);
            req.setAttribute("student", student);
            req.setAttribute("marks", marks);
            req.getRequestDispatcher("/WEB-INF/views/public/result-view.jsp").forward(req, resp);
        } else {
            req.setAttribute("searchError", "No published result found for the supplied Registration/Roll Number. Please ensure your details are correct or check back once results are officially declared.");
            req.setAttribute("searchedReg", regNo);
            req.setAttribute("searchedRoll", rollNo);
            req.getRequestDispatcher("/WEB-INF/views/public/result-search.jsp").forward(req, resp);
        }
    }
}
