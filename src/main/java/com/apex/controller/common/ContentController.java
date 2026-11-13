package com.apex.controller.common;

import com.apex.dao.ContentDAO;
import com.apex.model.Instruction;
import com.apex.model.Notice;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/instructions", "/notices", "/about", "/privacy", "/terms", "/refund"})
public class ContentController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ContentDAO contentDAO;

    @Override
    public void init() throws ServletException {
        contentDAO = new ContentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        switch (path) {
            case "/instructions":
                List<Instruction> instructions = contentDAO.getAllInstructions(true);
                req.setAttribute("instructions", instructions);
                req.getRequestDispatcher("/WEB-INF/views/public/instructions.jsp").forward(req, resp);
                break;
            case "/notices":
                List<Notice> notices = contentDAO.getNotices("ALL", true);
                req.setAttribute("notices", notices);
                req.getRequestDispatcher("/WEB-INF/views/public/notices.jsp").forward(req, resp);
                break;
            case "/about":
                req.getRequestDispatcher("/WEB-INF/views/public/about.jsp").forward(req, resp);
                break;
            case "/privacy":
                req.getRequestDispatcher("/WEB-INF/views/public/privacy.jsp").forward(req, resp);
                break;
            case "/terms":
                req.getRequestDispatcher("/WEB-INF/views/public/terms.jsp").forward(req, resp);
                break;
            case "/refund":
                req.getRequestDispatcher("/WEB-INF/views/public/refund.jsp").forward(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/");
                break;
        }
    }
}
