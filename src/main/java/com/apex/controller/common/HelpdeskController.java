package com.apex.controller.common;

import com.apex.dao.ContentDAO;
import com.apex.model.HelpdeskContact;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/helpdesk", "/contact"})
public class HelpdeskController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ContentDAO contentDAO;

    @Override
    public void init() throws ServletException {
        contentDAO = new ContentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<HelpdeskContact> contacts = contentDAO.getAllHelpdeskContacts(true);
        req.setAttribute("contacts", contacts);
        req.getRequestDispatcher("/WEB-INF/views/public/helpdesk.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String subject = req.getParameter("subject");
        String message = req.getParameter("message");

        // Flash message acknowledging enquiry
        req.setAttribute("successMessage", "Thank you, " + (name != null ? name : "Student") + "! Your query regarding \"" + subject + "\" has been logged. Our helpdesk team will respond to " + email + " promptly.");
        doGet(req, resp);
    }
}
