package com.apex.controller.admin;

import com.apex.dao.*;
import com.apex.model.*;
import com.apex.util.DateUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Time;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/examinations", "/admin/exam-action"})
public class AdminExamController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ExaminationDAO examDAO;
    private ExamFormDAO formDAO;
    private AcademicSessionDAO sessionDAO;
    private SubjectDAO subjectDAO;
    private AuditLogDAO auditDAO;

    @Override
    public void init() throws ServletException {
        examDAO = new ExaminationDAO();
        formDAO = new ExamFormDAO();
        sessionDAO = new AcademicSessionDAO();
        subjectDAO = new SubjectDAO();
        auditDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tab = req.getParameter("tab");
        if (tab == null || tab.isEmpty()) {
            tab = "list";
        }

        List<Examination> exams = examDAO.getAllExaminations();
        List<AcademicSession> sessions = sessionDAO.getAllSessions();
        List<Subject> subjects = subjectDAO.getAllSubjects(null, null);

        req.setAttribute("activeTab", tab);
        req.setAttribute("exams", exams);
        req.setAttribute("sessions", sessions);
        req.setAttribute("subjects", subjects);

        String examIdStr = req.getParameter("exam_id");
        if (examIdStr != null && !examIdStr.isEmpty()) {
            int examId = Integer.parseInt(examIdStr);
            Examination selectedExam = examDAO.getExaminationById(examId);
            List<ExamSchedule> schedules = examDAO.getSchedulesByExam(examId);
            List<ExamForm> forms = formDAO.getExamForms(examId, null, null);

            req.setAttribute("selectedExam", selectedExam);
            req.setAttribute("schedules", schedules);
            req.setAttribute("forms", forms);
        }

        req.getRequestDispatcher("/WEB-INF/views/admin/examinations.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        User admin = (User) req.getSession().getAttribute("CURRENT_USER");

        if ("add".equalsIgnoreCase(action)) {
            Examination e = new Examination();
            String code = req.getParameter("exam_code");
            if (code == null || code.trim().isEmpty()) {
                code = "EXAM-" + System.currentTimeMillis() % 100000;
            }
            e.setExamCode(code.trim());
            e.setExamName(req.getParameter("exam_name"));
            e.setSessionId(Integer.parseInt(req.getParameter("session_id")));
            e.setSemesterNumber(Integer.parseInt(req.getParameter("semester_number")));
            e.setStartDate(DateUtil.parseSqlDate(req.getParameter("start_date")));
            e.setEndDate(DateUtil.parseSqlDate(req.getParameter("end_date")));
            e.setExamType(req.getParameter("exam_type"));
            e.setFeeAmount(Double.parseDouble(req.getParameter("fee_amount")));
            e.setStatus(req.getParameter("status"));
            e.setResultPublished(false);

            examDAO.addExamination(e);
            auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "CREATE_EXAM", "examinations", e.getId(), "Created: " + e.getExamName(), req.getRemoteAddr());
            resp.sendRedirect(req.getContextPath() + "/admin/examinations?msg=Examination+created+successfully.");
        } else if ("edit".equalsIgnoreCase(action)) {
            String idStr = req.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?error=Invalid+examination+ID.");
                return;
            }
            int id = Integer.parseInt(idStr.trim());
            Examination e = examDAO.getExaminationById(id);
            if (e == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?error=Examination+not+found.");
                return;
            }

            String examCode = req.getParameter("exam_code");
            String examName = req.getParameter("exam_name");
            String sessionIdStr = req.getParameter("session_id");
            String semNumStr = req.getParameter("semester_number");
            String startDateStr = req.getParameter("start_date");
            String endDateStr = req.getParameter("end_date");
            String examType = req.getParameter("exam_type");
            String feeStr = req.getParameter("fee_amount");
            String status = req.getParameter("status");

            if (examCode == null || examName == null || startDateStr == null || endDateStr == null ||
                examCode.trim().isEmpty() || examName.trim().isEmpty() || startDateStr.trim().isEmpty() || endDateStr.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?error=Please+fill+in+all+required+fields.");
                return;
            }

            java.sql.Date startDate = DateUtil.parseSqlDate(startDateStr);
            java.sql.Date endDate = DateUtil.parseSqlDate(endDateStr);

            if (startDate != null && endDate != null && endDate.before(startDate)) {
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?error=End+date+cannot+be+earlier+than+start+date.");
                return;
            }

            if (examDAO.isExamCodeExists(examCode.trim(), id)) {
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?error=Exam+code+already+exists.");
                return;
            }

            e.setExamCode(examCode.trim());
            e.setExamName(examName.trim());
            if (sessionIdStr != null && !sessionIdStr.trim().isEmpty()) {
                e.setSessionId(Integer.parseInt(sessionIdStr.trim()));
            }
            if (semNumStr != null && !semNumStr.trim().isEmpty()) {
                e.setSemesterNumber(Integer.parseInt(semNumStr.trim()));
            }
            e.setStartDate(startDate);
            e.setEndDate(endDate);
            e.setExamType(examType != null ? examType : "REGULAR");
            if (feeStr != null && !feeStr.trim().isEmpty()) {
                e.setFeeAmount(Double.parseDouble(feeStr.trim()));
            }
            e.setStatus(status != null ? status : "ACTIVE");

            boolean ok = examDAO.updateExamination(e);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "EDIT_EXAM", "examinations", id, "Updated exam: " + e.getExamName(), req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?msg=Examination+updated+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?error=Failed+to+update+examination.");
            }
        } else if ("delete".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            boolean ok = examDAO.deleteExamination(id);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "DELETE_EXAM", "examinations", id, "Deleted exam ID: " + id, req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?msg=Examination+deleted+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?error=Failed+to+delete+examination.+Existing+schedules+or+forms+may+be+linked.");
            }
        } else if ("add_schedule".equalsIgnoreCase(action)) {
            int examId = Integer.parseInt(req.getParameter("exam_id"));
            ExamSchedule s = new ExamSchedule();
            s.setExamId(examId);
            s.setSubjectId(Integer.parseInt(req.getParameter("subject_id")));
            s.setExamDate(DateUtil.parseSqlDate(req.getParameter("exam_date")));
            String st = req.getParameter("start_time");
            String et = req.getParameter("end_time");
            s.setStartTime(Time.valueOf((st.length() == 5 ? st + ":00" : st)));
            s.setEndTime(Time.valueOf((et.length() == 5 ? et + ":00" : et)));
            s.setRoomNo(req.getParameter("room_no"));

            boolean ok = examDAO.addExamSchedule(s);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "ADD_SCHEDULE", "exam_schedules", s.getId(), "Added routine slot for exam ID: " + examId, req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?tab=schedule&exam_id=" + examId + "&msg=Routine+slot+added.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?tab=schedule&exam_id=" + examId + "&error=Failed+to+add+routine+slot.");
            }
        } else if ("edit_schedule".equalsIgnoreCase(action)) {
            int scheduleId = Integer.parseInt(req.getParameter("schedule_id"));
            int examId = Integer.parseInt(req.getParameter("exam_id"));
            ExamSchedule s = new ExamSchedule();
            s.setId(scheduleId);
            s.setExamId(examId);
            s.setSubjectId(Integer.parseInt(req.getParameter("subject_id")));
            s.setExamDate(DateUtil.parseSqlDate(req.getParameter("exam_date")));
            String st = req.getParameter("start_time");
            String et = req.getParameter("end_time");
            s.setStartTime(Time.valueOf((st.length() == 5 ? st + ":00" : st)));
            s.setEndTime(Time.valueOf((et.length() == 5 ? et + ":00" : et)));
            s.setRoomNo(req.getParameter("room_no"));

            boolean ok = examDAO.updateExamSchedule(s);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "EDIT_SCHEDULE", "exam_schedules", scheduleId, "Updated routine slot ID: " + scheduleId, req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?tab=schedule&exam_id=" + examId + "&msg=Routine+slot+updated+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?tab=schedule&exam_id=" + examId + "&error=Failed+to+update+routine+slot.");
            }
        } else if ("delete_schedule".equalsIgnoreCase(action)) {
            int scheduleId = Integer.parseInt(req.getParameter("schedule_id"));
            int examId = Integer.parseInt(req.getParameter("exam_id"));
            boolean ok = examDAO.deleteExamSchedule(scheduleId);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "DELETE_SCHEDULE", "exam_schedules", scheduleId, "Deleted routine slot ID: " + scheduleId, req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?tab=schedule&exam_id=" + examId + "&msg=Routine+slot+removed.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?tab=schedule&exam_id=" + examId + "&error=Failed+to+delete+routine+slot.");
            }
        } else if ("edit_form".equalsIgnoreCase(action)) {
            int formId = Integer.parseInt(req.getParameter("form_id"));
            int examId = Integer.parseInt(req.getParameter("exam_id"));
            String collegeStatus = req.getParameter("college_approval_status");
            String adminStatus = req.getParameter("admin_approval_status");
            String paymentStatus = req.getParameter("payment_status");
            String rejectionReason = req.getParameter("rejection_reason");
            boolean admitIssued = "1".equals(req.getParameter("admit_card_issued"));

            ExamForm ef = formDAO.getFormById(formId);
            if (ef != null) {
                ef.setCollegeApprovalStatus(collegeStatus);
                ef.setAdminApprovalStatus(adminStatus);
                ef.setPaymentStatus(paymentStatus);
                ef.setRejectionReason(rejectionReason);
                ef.setAdmitCardIssued(admitIssued);
                boolean ok = formDAO.updateExamForm(ef);
                if (ok) {
                    if (admin != null) {
                        auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "EDIT_EXAM_FORM", "exam_forms", formId, "Updated form ID: " + formId + " (Admin: " + adminStatus + ")", req.getRemoteAddr());
                    }
                    resp.sendRedirect(req.getContextPath() + "/admin/examinations?tab=forms&exam_id=" + examId + "&msg=Exam+application+record+updated+successfully.");
                    return;
                }
            }
            resp.sendRedirect(req.getContextPath() + "/admin/examinations?tab=forms&exam_id=" + examId + "&error=Failed+to+update+exam+application+record.");
        } else if ("approve_form".equalsIgnoreCase(action)) {
            int formId = Integer.parseInt(req.getParameter("form_id"));
            int examId = Integer.parseInt(req.getParameter("exam_id"));
            String status = req.getParameter("approval_status"); // APPROVED / REJECTED
            String reason = req.getParameter("rejection_reason");

            boolean ok = formDAO.updateAdminApproval(formId, status, reason);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "EXAM_FORM_APPROVAL", "exam_forms", formId, "Status: " + status, req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?tab=forms&exam_id=" + examId + "&msg=Form+status+updated.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/examinations?tab=forms&exam_id=" + examId + "&error=Failed+to+update+form+approval.");
            }
        }
    }
}
