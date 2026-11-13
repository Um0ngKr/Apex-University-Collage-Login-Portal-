package com.apex.controller.teacher;

import com.apex.dao.AttendanceDAO;
import com.apex.dao.StudentDAO;
import com.apex.dao.TeacherDAO;
import com.apex.model.Attendance;
import com.apex.model.Student;
import com.apex.model.User;
import com.apex.util.DateUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/teacher/attendance", "/teacher/attendance-action"})
public class TeacherAttendanceController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private TeacherDAO teacherDAO;
    private AttendanceDAO attendanceDAO;
    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        teacherDAO = new TeacherDAO();
        attendanceDAO = new AttendanceDAO();
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?role=TEACHER");
            return;
        }
        int teacherId = getTeacherId(user);

        List<Map<String, Object>> assignments = teacherDAO.getAssignedSubjects(teacherId);
        req.setAttribute("assignments", assignments);

        String subjectIdStr = req.getParameter("subject_id");
        String dateStr = req.getParameter("class_date");

        if (subjectIdStr != null && !subjectIdStr.trim().isEmpty()) {
            int subId;
            try {
                subId = Integer.parseInt(subjectIdStr.trim());
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/teacher/attendance?error=Invalid+subject+selected.");
                return;
            }

            // Security check: teacher must be assigned to this subject
            Map<String, Object> selectedAssignment = null;
            for (Map<String, Object> asg : assignments) {
                if (asg.get("subjectId") != null && ((Integer) asg.get("subjectId")) == subId) {
                    selectedAssignment = asg;
                    break;
                }
            }

            if (selectedAssignment == null) {
                resp.sendRedirect(req.getContextPath() + "/teacher/attendance?error=Unauthorized:+You+are+not+assigned+to+this+subject.");
                return;
            }

            Date classDate = (dateStr != null && !dateStr.trim().isEmpty())
                    ? DateUtil.parseSqlDate(dateStr.trim())
                    : new Date(System.currentTimeMillis());

            int courseId = selectedAssignment.get("courseId") != null ? (Integer) selectedAssignment.get("courseId") : 0;
            int semesterId = selectedAssignment.get("semesterId") != null ? (Integer) selectedAssignment.get("semesterId") : 0;
            int sessionId = selectedAssignment.get("sessionId") != null ? (Integer) selectedAssignment.get("sessionId") : 2;

            List<Attendance> records = attendanceDAO.getAttendanceBySubjectAndDate(subId, classDate);
            // Fetch students belonging to the selected subject, course, and semester
            List<Student> students = studentDAO.getStudentsForSubjectAssignment(subId, courseId, semesterId);

            req.setAttribute("selectedSubjectId", subId);
            req.setAttribute("selectedSessionId", sessionId);
            req.setAttribute("selectedDate", classDate);
            req.setAttribute("records", records);
            req.setAttribute("students", students);
            req.setAttribute("selectedAssignment", selectedAssignment);
        }

        req.getRequestDispatcher("/WEB-INF/views/teacher/attendance.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?role=TEACHER");
            return;
        }
        int teacherId = getTeacherId(user);
        if (teacherId <= 0) {
            resp.sendRedirect(req.getContextPath() + "/login?role=TEACHER");
            return;
        }

        int subjectId;
        try {
            subjectId = Integer.parseInt(req.getParameter("subject_id"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/teacher/attendance?error=Invalid+subject+identifier.");
            return;
        }

        // Security check: teacher must be assigned to this subject
        if (!teacherDAO.isSubjectAssignedToTeacher(teacherId, subjectId)) {
            resp.sendRedirect(req.getContextPath() + "/teacher/attendance?error=Unauthorized:+You+are+not+assigned+to+mark+attendance+for+this+subject.");
            return;
        }

        int sessionId = 2;
        String sessStr = req.getParameter("session_id");
        if (sessStr != null && !sessStr.trim().isEmpty()) {
            try {
                sessionId = Integer.parseInt(sessStr.trim());
            } catch (NumberFormatException ignored) {}
        }

        Date classDate = DateUtil.parseSqlDate(req.getParameter("class_date"));
        if (classDate == null) {
            classDate = new Date(System.currentTimeMillis());
        }

        String[] studentIds = req.getParameterValues("student_id");
        if (studentIds != null) {
            for (String sidStr : studentIds) {
                try {
                    int sid = Integer.parseInt(sidStr);
                    String status = req.getParameter("status_" + sid);
                    if (status == null || status.isEmpty()) {
                        status = "PRESENT";
                    }
                    attendanceDAO.markAttendance(sid, subjectId, sessionId, classDate, status, teacherId);
                } catch (NumberFormatException ignored) {}
            }
        }

        resp.sendRedirect(req.getContextPath() + "/teacher/attendance?subject_id=" + subjectId + "&class_date=" + classDate + "&msg=Attendance+marked+successfully.");
    }

    private int getTeacherId(User user) {
        if (user == null) {
            return 0;
        }
        int teacherId = user.getRelatedId();
        if (teacherId <= 0 && user.getId() > 0) {
            teacherId = user.getId();
        }
        if (teacherId <= 0 && user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            com.apex.model.Teacher t = teacherDAO.getTeacherByEmployeeId(user.getUsername().trim());
            if (t != null) {
                teacherId = t.getId();
                user.setRelatedId(teacherId);
            }
        }
        return teacherId;
    }
}
