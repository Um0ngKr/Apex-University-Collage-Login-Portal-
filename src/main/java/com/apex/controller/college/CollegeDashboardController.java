package com.apex.controller.college;

import com.apex.dao.CollegeDAO;
import com.apex.dao.ContentDAO;
import com.apex.dao.DashboardDAO;
import com.apex.dao.ExamFormDAO;
import com.apex.dao.StudentDAO;
import com.apex.model.College;
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

@WebServlet(urlPatterns = {"/college/dashboard", "/college"})
public class CollegeDashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private CollegeDAO collegeDAO;
    private DashboardDAO dashboardDAO;
    private StudentDAO studentDAO;
    private ExamFormDAO examFormDAO;
    private ContentDAO contentDAO;

    @Override
    public void init() throws ServletException {
        collegeDAO = new CollegeDAO();
        dashboardDAO = new DashboardDAO();
        studentDAO = new StudentDAO();
        examFormDAO = new ExamFormDAO();
        contentDAO = new ContentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        int collegeId = user.getCollegeId() != null ? user.getCollegeId() : user.getRelatedId();

        College college = collegeDAO.getCollegeById(collegeId);
        Map<String, Object> stats = dashboardDAO.getCollegeStats(collegeId);
        List<Student> recentStudents = studentDAO.getAllStudents(collegeId, null, 0, 10);
        List<ExamForm> pendingForms = examFormDAO.getExamForms(null, collegeId, "PENDING");
        List<Notice> notices = contentDAO.getNotices("COLLEGES", true);

        req.setAttribute("college", college);
        req.setAttribute("stats", stats);
        req.setAttribute("recentStudents", recentStudents);
        req.setAttribute("pendingForms", pendingForms);
        req.setAttribute("notices", notices);

        req.getRequestDispatcher("/WEB-INF/views/college/dashboard.jsp").forward(req, resp);
    }
}
