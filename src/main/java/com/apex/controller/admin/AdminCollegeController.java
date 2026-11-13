package com.apex.controller.admin;

import com.apex.dao.AuditLogDAO;
import com.apex.dao.CollegeDAO;
import com.apex.dao.StudentDAO;
import com.apex.model.College;
import com.apex.model.Student;
import com.apex.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/colleges", "/admin/college-action"})
public class AdminCollegeController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private CollegeDAO collegeDAO;
    private StudentDAO studentDAO;
    private AuditLogDAO auditDAO;

    @Override
    public void init() throws ServletException {
        collegeDAO = new CollegeDAO();
        studentDAO = new StudentDAO();
        auditDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("search");
        List<College> colleges = collegeDAO.getAllColleges(search);
        req.setAttribute("colleges", colleges);

        String collegeIdStr = req.getParameter("view_students");
        if (collegeIdStr != null && !collegeIdStr.isEmpty()) {
            int cid = Integer.parseInt(collegeIdStr);
            College selected = collegeDAO.getCollegeById(cid);
            List<Student> students = studentDAO.getAllStudents(cid, null, 0, 100);
            req.setAttribute("selectedCollege", selected);
            req.setAttribute("collegeStudents", students);
        }

        req.getRequestDispatcher("/WEB-INF/views/admin/colleges.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        User admin = (User) req.getSession().getAttribute("CURRENT_USER");

        if ("add".equalsIgnoreCase(action)) {
            College c = new College();
            String code = req.getParameter("college_code");
            if (code == null || code.trim().isEmpty()) {
                code = "COL" + (100 + (int)(Math.random() * 899));
            }
            c.setCollegeCode(code.trim());
            c.setName(req.getParameter("name"));
            c.setEmail(req.getParameter("email"));
            c.setPhone(req.getParameter("phone"));
            c.setAddress(req.getParameter("address"));
            c.setWebsite(req.getParameter("website"));
            c.setPrincipalName(req.getParameter("principal_name"));
            c.setStatus("ACTIVE");

            boolean ok = collegeDAO.addCollege(c);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "ADD_COLLEGE", "colleges", c.getId(), "Added college: " + c.getName(), req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/colleges?msg=College+added+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/colleges?error=Failed+to+add+college.+College+code+or+email+may+already+exist.");
            }
        } else if ("edit".equalsIgnoreCase(action)) {
            String idStr = req.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/colleges?error=Invalid+college+ID.");
                return;
            }
            int id;
            try {
                id = Integer.parseInt(idStr.trim());
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/admin/colleges?error=Invalid+college+ID.");
                return;
            }

            College c = collegeDAO.getCollegeById(id);
            if (c == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/colleges?error=College+not+found.");
                return;
            }

            String collegeCode = req.getParameter("college_code");
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            String address = req.getParameter("address");
            String website = req.getParameter("website");
            String principalName = req.getParameter("principal_name");
            String status = req.getParameter("status");

            if (collegeCode == null || collegeCode.trim().isEmpty() ||
                name == null || name.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty() ||
                address == null || address.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/colleges?error=Please+fill+in+all+required+fields.");
                return;
            }

            collegeCode = collegeCode.trim();
            email = email.trim();

            if (collegeDAO.isCollegeCodeExists(collegeCode, id)) {
                resp.sendRedirect(req.getContextPath() + "/admin/colleges?error=College+code+already+exists+for+another+institution.");
                return;
            }

            if (collegeDAO.isEmailExists(email, id)) {
                resp.sendRedirect(req.getContextPath() + "/admin/colleges?error=Official+email+already+exists+for+another+institution.");
                return;
            }

            c.setCollegeCode(collegeCode);
            c.setName(name.trim());
            c.setEmail(email);
            c.setPhone(phone.trim());
            c.setAddress(address.trim());
            c.setWebsite(website != null && !website.trim().isEmpty() ? website.trim() : null);
            c.setPrincipalName(principalName != null && !principalName.trim().isEmpty() ? principalName.trim() : null);
            c.setStatus(status != null && "INACTIVE".equalsIgnoreCase(status.trim()) ? "INACTIVE" : "ACTIVE");

            boolean updated = collegeDAO.updateCollege(c);
            if (updated) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "EDIT_COLLEGE", "colleges", id, "Updated college: " + c.getName(), req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/colleges?msg=College+updated+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/colleges?error=Failed+to+update+college.+Please+verify+details.");
            }
        } else if ("delete".equalsIgnoreCase(action)) {
            String idStr = req.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/colleges?error=Invalid+college+ID.");
                return;
            }
            int id;
            try {
                id = Integer.parseInt(idStr.trim());
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/admin/colleges?error=Invalid+college+ID.");
                return;
            }
            boolean deleted = collegeDAO.deleteCollege(id);
            if (deleted) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "DELETE_COLLEGE", "colleges", id, "Deleted college ID: " + id, req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/colleges?msg=College+deleted+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/colleges?error=Failed+to+delete+college.+Existing+students+or+records+may+be+linked+to+it.");
            }
        }
    }
}
