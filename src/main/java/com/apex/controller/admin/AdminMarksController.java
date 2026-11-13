package com.apex.controller.admin;

import com.apex.dao.*;
import com.apex.model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/marks", "/admin/marks-action"})
public class AdminMarksController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private MarkDAO markDAO;
    private ResultDAO resultDAO;
    private ExaminationDAO examDAO;
    private SubjectDAO subjectDAO;
    private StudentDAO studentDAO;
    private AuditLogDAO auditDAO;

    @Override
    public void init() throws ServletException {
        markDAO = new MarkDAO();
        resultDAO = new ResultDAO();
        examDAO = new ExaminationDAO();
        subjectDAO = new SubjectDAO();
        studentDAO = new StudentDAO();
        auditDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Examination> exams = examDAO.getAllExaminations();
        List<Subject> subjects = subjectDAO.getAllSubjects(null, null);

        req.setAttribute("exams", exams);
        req.setAttribute("subjects", subjects);

        String examIdStr = req.getParameter("exam_id");
        String subjectIdStr = req.getParameter("subject_id");

        if (examIdStr != null && !examIdStr.isEmpty()) {
            int examId = Integer.parseInt(examIdStr);
            Examination selectedExam = examDAO.getExaminationById(examId);
            req.setAttribute("selectedExam", selectedExam);

            if (subjectIdStr != null && !subjectIdStr.isEmpty()) {
                int subjectId = Integer.parseInt(subjectIdStr);
                List<Mark> marks = markDAO.getMarksByExamAndSubject(examId, subjectId);
                // Also get all students for this course/semester to allow marks upload
                List<Student> students = studentDAO.getAllStudents(null, null, 0, 100);
                req.setAttribute("marks", marks);
                req.setAttribute("students", students);
                req.setAttribute("selectedSubjectId", subjectId);
            }

            // Also load compiled results for this exam
            List<Result> results = resultDAO.getResultsByExam(examId);
            req.setAttribute("results", results);
        }

        req.getRequestDispatcher("/WEB-INF/views/admin/marks.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        User admin = (User) req.getSession().getAttribute("CURRENT_USER");

        if ("upload_mark".equalsIgnoreCase(action)) {
            int examId = Integer.parseInt(req.getParameter("exam_id"));
            int studentId = Integer.parseInt(req.getParameter("student_id"));
            int subjectId = Integer.parseInt(req.getParameter("subject_id"));
            double internal = Double.parseDouble(req.getParameter("internal_marks"));
            double external = Double.parseDouble(req.getParameter("external_marks"));

            Mark m = new Mark();
            m.setExamId(examId);
            m.setStudentId(studentId);
            m.setSubjectId(subjectId);
            m.setInternalMarks(internal);
            m.setExternalMarks(external);

            markDAO.saveOrUpdateMark(m);
            auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "SAVE_MARKS", "marks", null, "Saved marks for student ID: " + studentId, req.getRemoteAddr());
            resp.sendRedirect(req.getContextPath() + "/admin/marks?exam_id=" + examId + "&subject_id=" + subjectId + "&msg=Marks+saved+successfully.");
        } else if ("compile_results".equalsIgnoreCase(action)) {
            int examId = Integer.parseInt(req.getParameter("exam_id"));
            int count = resultDAO.compileExamResults(examId);
            auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "COMPILE_RESULTS", "results", examId, "Compiled results for " + count + " students", req.getRemoteAddr());
            resp.sendRedirect(req.getContextPath() + "/admin/marks?exam_id=" + examId + "&msg=Successfully+compiled+results+for+" + count + "+students.");
        } else if ("publish_results".equalsIgnoreCase(action)) {
            int examId = Integer.parseInt(req.getParameter("exam_id"));
            boolean publish = "1".equals(req.getParameter("publish"));
            examDAO.toggleResultPublication(examId, publish);
            auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "PUBLISH_RESULTS", "examinations", examId, "Publication status set to: " + publish, req.getRemoteAddr());
            resp.sendRedirect(req.getContextPath() + "/admin/marks?exam_id=" + examId + "&msg=Result+publication+status+updated.");
        } else if ("edit_result".equalsIgnoreCase(action)) {
            int resultId = Integer.parseInt(req.getParameter("result_id"));
            int examId = Integer.parseInt(req.getParameter("exam_id"));
            double totalObt = Double.parseDouble(req.getParameter("total_obtained"));
            double totalMax = Double.parseDouble(req.getParameter("total_max"));
            double sgpa = Double.parseDouble(req.getParameter("sgpa"));
            String status = req.getParameter("result_status");

            Result r = resultDAO.getResultById(resultId);
            if (r != null) {
                r.setTotalObtained(totalObt);
                r.setTotalMax(totalMax);
                double pct = totalMax > 0 ? (totalObt / totalMax) * 100.0 : 0.0;
                r.setPercentage(com.apex.util.GradeCalculator.round(pct, 2));
                r.setSgpa(com.apex.util.GradeCalculator.round(sgpa, 2));
                r.setResultStatus(status != null ? status : "PASS");
                boolean ok = resultDAO.updateResult(r);
                if (ok) {
                    if (admin != null) {
                        auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "EDIT_RESULT", "results", resultId, "Updated result for student ID: " + r.getStudentId(), req.getRemoteAddr());
                    }
                    resp.sendRedirect(req.getContextPath() + "/admin/marks?exam_id=" + examId + "&msg=Result+record+updated+successfully.");
                    return;
                }
            }
            resp.sendRedirect(req.getContextPath() + "/admin/marks?exam_id=" + examId + "&error=Failed+to+update+result+record.");
        }
    }
}
