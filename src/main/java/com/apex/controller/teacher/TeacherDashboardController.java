package com.apex.controller.teacher;

import com.apex.dao.ContentDAO;
import com.apex.dao.DashboardDAO;
import com.apex.dao.TeacherDAO;
import com.apex.model.Notice;
import com.apex.model.Teacher;
import com.apex.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/teacher/dashboard", "/teacher"})
public class TeacherDashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private TeacherDAO teacherDAO;
    private DashboardDAO dashboardDAO;
    private ContentDAO contentDAO;

    @Override
    public void init() throws ServletException {
        teacherDAO = new TeacherDAO();
        dashboardDAO = new DashboardDAO();
        contentDAO = new ContentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?role=TEACHER");
            return;
        }
        int teacherId = getTeacherId(user);

        Teacher teacher = teacherDAO.getTeacherById(teacherId);
        List<Map<String, Object>> assignments = teacherDAO.getAssignedSubjects(teacherId);
        Map<String, Object> stats = dashboardDAO.getTeacherStats(teacherId);
        List<Notice> notices = contentDAO.getNotices("TEACHERS", true);

        req.setAttribute("teacher", teacher);
        req.setAttribute("assignments", assignments);
        req.setAttribute("stats", stats);
        req.setAttribute("notices", notices);

        req.getRequestDispatcher("/WEB-INF/views/teacher/dashboard.jsp").forward(req, resp);
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
            Teacher t = teacherDAO.getTeacherByEmployeeId(user.getUsername().trim());
            if (t != null) {
                teacherId = t.getId();
                user.setRelatedId(teacherId);
            }
        }
        return teacherId;
    }
}
