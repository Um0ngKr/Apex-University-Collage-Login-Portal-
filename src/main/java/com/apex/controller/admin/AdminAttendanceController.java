package com.apex.controller.admin;

import com.apex.dao.AttendanceDAO;
import com.apex.dao.SubjectDAO;
import com.apex.model.Attendance;
import com.apex.model.Subject;
import com.apex.util.DateUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/attendance", "/admin/attendance-action"})
public class AdminAttendanceController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private AttendanceDAO attendanceDAO;
    private SubjectDAO subjectDAO;

    @Override
    public void init() throws ServletException {
        attendanceDAO = new AttendanceDAO();
        subjectDAO = new SubjectDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Subject> subjects = subjectDAO.getAllSubjects(null, null);
        req.setAttribute("subjects", subjects);

        String subjectIdStr = req.getParameter("subject_id");
        String dateStr = req.getParameter("class_date");

        if (subjectIdStr != null && !subjectIdStr.isEmpty()) {
            int subId = Integer.parseInt(subjectIdStr);
            Date classDate = (dateStr != null && !dateStr.isEmpty())
                    ? DateUtil.parseSqlDate(dateStr)
                    : new Date(System.currentTimeMillis());

            List<Attendance> records = attendanceDAO.getAttendanceBySubjectAndDate(subId, classDate);
            req.setAttribute("records", records);
            req.setAttribute("selectedSubjectId", subId);
            req.setAttribute("selectedDate", classDate);
        }

        req.getRequestDispatcher("/WEB-INF/views/admin/attendance.jsp").forward(req, resp);
    }
}
