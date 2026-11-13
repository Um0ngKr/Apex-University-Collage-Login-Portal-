package com.apex.controller.export;

import com.apex.dao.*;
import com.apex.model.*;
import com.apex.util.DateUtil;
import com.apex.util.SimplePdfGenerator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/export/pdf"})
public class PdfExportController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private StudentDAO studentDAO;
    private ExaminationDAO examDAO;
    private ExamFormDAO examFormDAO;
    private MarkDAO markDAO;
    private ResultDAO resultDAO;
    private PaymentDAO paymentDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
        examDAO = new ExaminationDAO();
        examFormDAO = new ExamFormDAO();
        markDAO = new MarkDAO();
        resultDAO = new ResultDAO();
        paymentDAO = new PaymentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type"); // admit_card, marksheet, receipt

        if ("admit_card".equalsIgnoreCase(type)) {
            exportAdmitCard(req, resp);
        } else if ("marksheet".equalsIgnoreCase(type)) {
            exportMarksheet(req, resp);
        } else if ("receipt".equalsIgnoreCase(type)) {
            exportReceipt(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid PDF export type.");
        }
    }

    private void exportAdmitCard(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String formIdStr = req.getParameter("form_id");
        ExamForm form = null;
        if (formIdStr != null && !formIdStr.isEmpty()) {
            form = examFormDAO.getFormById(Integer.parseInt(formIdStr));
        }

        if (form == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Admit card record not found.");
            return;
        }

        Student student = studentDAO.getStudentById(form.getStudentId());
        Examination exam = examDAO.getExaminationById(form.getExamId());
        List<ExamSchedule> schedules = examDAO.getSchedulesByExam(form.getExamId());

        Map<String, String> data = new HashMap<>();
        data.put("studentName", student.getFullName());
        data.put("regNo", student.getRegistrationNo());
        data.put("rollNo", student.getRollNo());
        data.put("courseName", student.getCourseName());
        data.put("semester", String.valueOf(student.getSemesterNumber()));
        data.put("session", student.getSessionCode());
        data.put("collegeName", student.getCollegeName());
        data.put("examCenter", "University Examination Center, Kaikhali Main Rd Campus");

        List<Map<String, String>> routineList = new ArrayList<>();
        for (ExamSchedule sc : schedules) {
            Map<String, String> row = new HashMap<>();
            row.put("code", sc.getSubjectCode());
            row.put("name", sc.getSubjectName());
            row.put("date", DateUtil.formatDisplayDate(sc.getExamDate()));
            row.put("time", sc.getStartTime() + " - " + sc.getEndTime());
            routineList.add(row);
        }

        byte[] pdfBytes = SimplePdfGenerator.generateAdmitCard(data, routineList);

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "inline; filename=\"AdmitCard_" + student.getRollNo() + ".pdf\"");
        resp.setContentLength(pdfBytes.length);
        resp.getOutputStream().write(pdfBytes);
    }

    private void exportMarksheet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int studentId = Integer.parseInt(req.getParameter("student_id"));
        int examId = Integer.parseInt(req.getParameter("exam_id"));

        Result res = resultDAO.getResultByStudentAndExam(studentId, examId, true);
        if (res == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Official result not published yet.");
            return;
        }

        Student student = studentDAO.getStudentById(studentId);
        List<Mark> marks = markDAO.getMarksByExamAndStudent(examId, studentId);

        Map<String, String> data = new HashMap<>();
        data.put("studentName", student.getFullName());
        data.put("regNo", student.getRegistrationNo());
        data.put("rollNo", student.getRollNo());
        data.put("courseName", student.getCourseName());
        data.put("semester", String.valueOf(res.getSemesterNumber()));
        data.put("session", res.getSessionCode());
        data.put("collegeName", student.getCollegeName());
        data.put("totalObtained", String.valueOf(res.getTotalObtained()));
        data.put("totalMax", String.valueOf(res.getTotalMax()));
        data.put("percentage", String.valueOf(res.getPercentage()));
        data.put("sgpa", String.valueOf(res.getSgpa()));
        data.put("resultStatus", res.getResultStatus());

        List<Map<String, String>> subjectRows = new ArrayList<>();
        for (Mark m : marks) {
            Map<String, String> row = new HashMap<>();
            row.put("code", m.getSubjectCode());
            row.put("name", m.getSubjectName());
            row.put("internal", String.valueOf(m.getInternalMarks()));
            row.put("external", String.valueOf(m.getExternalMarks()));
            row.put("total", String.valueOf(m.getTotalMarks()));
            row.put("grade", m.getLetterGrade());
            subjectRows.add(row);
        }

        byte[] pdfBytes = SimplePdfGenerator.generateMarksheet(data, subjectRows);

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "inline; filename=\"Marksheet_" + student.getRollNo() + ".pdf\"");
        resp.setContentLength(pdfBytes.length);
        resp.getOutputStream().write(pdfBytes);
    }

    private void exportReceipt(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String receiptNo = req.getParameter("receipt_no");
        Payment p = paymentDAO.getPaymentByReceiptOrTxn(receiptNo);

        if (p == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Payment receipt not found.");
            return;
        }

        Student student = studentDAO.getStudentById(p.getStudentId());

        Map<String, String> data = new HashMap<>();
        data.put("receiptNo", p.getReceiptNo());
        data.put("transactionId", p.getTransactionId());
        data.put("paymentDate", DateUtil.formatDisplayDateTime(p.getPaymentDate()));
        data.put("paymentStatus", p.getPaymentStatus());
        data.put("paymentMethod", p.getPaymentMethod());
        data.put("studentName", student.getFullName());
        data.put("regNo", student.getRegistrationNo());
        data.put("rollNo", student.getRollNo());
        data.put("courseName", student.getCourseName());
        data.put("remarks", p.getRemarks());
        data.put("amount", String.valueOf(p.getAmount()));

        byte[] pdfBytes = SimplePdfGenerator.generatePaymentReceipt(data);

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "inline; filename=\"Receipt_" + p.getReceiptNo() + ".pdf\"");
        resp.setContentLength(pdfBytes.length);
        resp.getOutputStream().write(pdfBytes);
    }
}
