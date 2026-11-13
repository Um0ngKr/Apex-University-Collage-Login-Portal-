package com.apex.controller.student;

import com.apex.dao.MarkDAO;
import com.apex.dao.ResultDAO;
import com.apex.dao.StudentDAO;
import com.apex.model.Mark;
import com.apex.model.Result;
import com.apex.model.Student;
import com.apex.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/student/results"})
public class StudentResultController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ResultDAO resultDAO;
    private MarkDAO markDAO;
    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        resultDAO = new ResultDAO();
        markDAO = new MarkDAO();
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        int studentId = user.getRelatedId();

        Student student = studentDAO.getStudentById(studentId);
        List<Result> results = resultDAO.getResultsByStudent(studentId);

        String examIdStr = req.getParameter("exam_id");
        if (examIdStr != null && !examIdStr.isEmpty()) {
            int examId = Integer.parseInt(examIdStr);
            Result selectedResult = resultDAO.getResultByStudentAndExam(studentId, examId, true);
            List<Mark> marks = markDAO.getMarksByExamAndStudent(examId, studentId);

            req.setAttribute("selectedResult", selectedResult);
            req.setAttribute("marks", marks);
        } else if (!results.isEmpty()) {
            // Default to latest result
            Result latest = results.get(0);
            List<Mark> marks = markDAO.getMarksByExamAndStudent(latest.getExamId(), studentId);

            req.setAttribute("selectedResult", latest);
            req.setAttribute("marks", marks);
        }

        req.setAttribute("student", student);
        req.setAttribute("results", results);

        req.getRequestDispatcher("/WEB-INF/views/student/results.jsp").forward(req, resp);
    }
}
