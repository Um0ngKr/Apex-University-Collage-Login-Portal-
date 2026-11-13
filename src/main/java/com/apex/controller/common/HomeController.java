package com.apex.controller.common;

import com.apex.dao.ContentDAO;
import com.apex.dao.DashboardDAO;
import com.apex.model.HelpdeskContact;
import com.apex.model.Instruction;
import com.apex.model.Notice;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"", "/home", "/index"})
public class HomeController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ContentDAO contentDAO;
    private DashboardDAO dashboardDAO;

    @Override
    public void init() throws ServletException {
        contentDAO = new ContentDAO();
        dashboardDAO = new DashboardDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Load dynamic updates, instructions, helpdesk and notices
        List<Instruction> instructions = contentDAO.getAllInstructions(true);
        List<Notice> notices = contentDAO.getNotices("ALL", true);
        List<HelpdeskContact> helpdeskList = contentDAO.getAllHelpdeskContacts(true);
        Map<String, Object> stats = dashboardDAO.getAdminStats();

        req.setAttribute("instructions", instructions);
        req.setAttribute("notices", notices);
        req.setAttribute("helpdeskList", helpdeskList);
        req.setAttribute("stats", stats);

        req.getRequestDispatcher("/WEB-INF/views/public/index.jsp").forward(req, resp);
    }
}
