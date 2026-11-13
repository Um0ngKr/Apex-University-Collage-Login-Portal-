package com.apex.controller.export;

import com.apex.dao.PaymentDAO;
import com.apex.dao.ResultDAO;
import com.apex.dao.StudentDAO;
import com.apex.model.Payment;
import com.apex.model.Result;
import com.apex.model.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/export/csv"})
public class CsvExportController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private StudentDAO studentDAO;
    private PaymentDAO paymentDAO;
    private ResultDAO resultDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
        paymentDAO = new PaymentDAO();
        resultDAO = new ResultDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type"); // students, payments, results
        resp.setCharacterEncoding("UTF-8");

        if ("students".equalsIgnoreCase(type)) {
            String collegeIdStr = req.getParameter("college_id");
            Integer cid = (collegeIdStr != null && !collegeIdStr.isEmpty()) ? Integer.parseInt(collegeIdStr) : null;
            List<Student> students = studentDAO.getAllStudents(cid, null, 0, 5000);

            resp.setContentType("text/csv");
            resp.setHeader("Content-Disposition", "attachment; filename=\"Apex_Students_Report.csv\"");

            PrintWriter out = resp.getWriter();
            out.println("ID,Registration No,Roll No,Full Name,Email,Phone,Gender,College,Department,Course,Semester,Session,Status");
            for (Student s : students) {
                out.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",Semester %d,\"%s\",\"%s\"%n",
                    s.getId(), s.getRegistrationNo(), s.getRollNo(), s.getFullName(),
                    s.getEmail(), s.getPhone(), s.getGender(), s.getCollegeName(),
                    s.getDepartmentName(), s.getCourseName(), s.getSemesterNumber(),
                    s.getSessionCode(), s.getStatus());
            }
        } else if ("payments".equalsIgnoreCase(type)) {
            String status = req.getParameter("status");
            List<Payment> payments = paymentDAO.getAllPayments(status);

            resp.setContentType("text/csv");
            resp.setHeader("Content-Disposition", "attachment; filename=\"Apex_Payments_Report.csv\"");

            PrintWriter out = resp.getWriter();
            out.println("ID,Receipt No,Transaction ID,Student Name,Roll No,Exam,Amount,Method,Date,Status");
            for (Payment p : payments) {
                out.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%.2f,\"%s\",\"%s\",\"%s\"%n",
                    p.getId(), p.getReceiptNo(), p.getTransactionId(), p.getStudentName(),
                    p.getRollNo(), p.getExamName(), p.getAmount(), p.getPaymentMethod(),
                    p.getPaymentDate(), p.getPaymentStatus());
            }
        } else if ("results".equalsIgnoreCase(type)) {
            int examId = Integer.parseInt(req.getParameter("exam_id"));
            List<Result> results = resultDAO.getResultsByExam(examId);

            resp.setContentType("text/csv");
            resp.setHeader("Content-Disposition", "attachment; filename=\"Apex_Exam_Results.csv\"");

            PrintWriter out = resp.getWriter();
            out.println("ID,Registration No,Roll No,Student Name,Course,Semester,Total Obtained,Total Max,Percentage,SGPA,Status");
            for (Result r : results) {
                out.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",%d,%.2f,%.2f,%.2f,%.2f,\"%s\"%n",
                    r.getId(), r.getRegistrationNo(), r.getRollNo(), r.getStudentName(),
                    r.getCourseName(), r.getSemesterNumber(), r.getTotalObtained(),
                    r.getTotalMax(), r.getPercentage(), r.getSgpa(), r.getResultStatus());
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown CSV report type.");
        }
    }
}
