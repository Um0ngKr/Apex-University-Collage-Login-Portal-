package com.apex.controller.student;

import com.apex.dao.AttendanceDAO;
import com.apex.dao.ContentDAO;
import com.apex.dao.DashboardDAO;
import com.apex.dao.ExamFormDAO;
import com.apex.dao.StudentDAO;
import com.apex.model.ExamForm;
import com.apex.model.Notice;
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

@WebServlet(urlPatterns = {"/student/dashboard", "/student"})
public class StudentDashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private StudentDAO studentDAO;
    private DashboardDAO dashboardDAO;
    private ExamFormDAO examFormDAO;
    private ContentDAO contentDAO;
    private AttendanceDAO attendanceDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
        dashboardDAO = new DashboardDAO();
        examFormDAO = new ExamFormDAO();
        contentDAO = new ContentDAO();
        attendanceDAO = new AttendanceDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        int studentId = user.getRelatedId();

        Student student = studentDAO.getStudentById(studentId);
        Map<String, Object> stats = dashboardDAO.getStudentStats(studentId);
        List<ExamForm> myForms = examFormDAO.getFormsByStudent(studentId);
        List<Notice> notices = contentDAO.getNotices("STUDENTS", true);
        List<Map<String, Object>> attendanceSummary = attendanceDAO.getStudentSubjectWiseSummary(studentId);

        req.setAttribute("student", student);
        req.setAttribute("stats", stats);
        req.setAttribute("myForms", myForms);
        req.setAttribute("notices", notices);
        req.setAttribute("attendanceSummary", attendanceSummary);

        req.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp").forward(req, resp);
    }
}
