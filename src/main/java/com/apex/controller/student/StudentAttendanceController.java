package com.apex.controller.student;

import com.apex.dao.AttendanceDAO;
import com.apex.dao.StudentDAO;
import com.apex.model.Student;
import com.apex.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/student/attendance"})
public class StudentAttendanceController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private AttendanceDAO attendanceDAO;
    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        attendanceDAO = new AttendanceDAO();
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        int studentId = user.getRelatedId();

        Student student = studentDAO.getStudentById(studentId);
        List<Map<String, Object>> attendanceList = attendanceDAO.getStudentSubjectWiseSummary(studentId);

        int totalClassesAll = 0;
        int totalAttendedAll = 0;
        for (Map<String, Object> map : attendanceList) {
            totalClassesAll += (Integer) map.get("totalClasses");
            totalAttendedAll += (Integer) map.get("attendedClasses");
        }

        double overallPct = totalClassesAll > 0 ? (totalAttendedAll * 100.0) / totalClassesAll : 100.0;
        overallPct = Math.round(overallPct * 10.0) / 10.0;

        req.setAttribute("student", student);
        req.setAttribute("attendanceList", attendanceList);
        req.setAttribute("totalClassesAll", totalClassesAll);
        req.setAttribute("totalAttendedAll", totalAttendedAll);
        req.setAttribute("overallPct", overallPct);

        req.getRequestDispatcher("/WEB-INF/views/student/attendance.jsp").forward(req, resp);
    }
}
