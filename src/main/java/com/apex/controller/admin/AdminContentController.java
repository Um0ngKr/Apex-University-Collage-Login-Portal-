package com.apex.controller.admin;

import com.apex.dao.AuditLogDAO;
import com.apex.dao.ContentDAO;
import com.apex.model.HelpdeskContact;
import com.apex.model.Instruction;
import com.apex.model.Notice;
import com.apex.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/content", "/admin/content-action"})
public class AdminContentController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ContentDAO contentDAO;
    private AuditLogDAO auditDAO;

    @Override
    public void init() throws ServletException {
        contentDAO = new ContentDAO();
        auditDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tab = req.getParameter("tab");
        if (tab == null || tab.isEmpty()) {
            tab = "instructions";
        }

        List<Instruction> instructions = contentDAO.getAllInstructions(false);
        List<Notice> notices = contentDAO.getNotices(null, false);
        List<HelpdeskContact> helpdeskList = contentDAO.getAllHelpdeskContacts(false);

        req.setAttribute("activeTab", tab);
        req.setAttribute("instructions", instructions);
        req.setAttribute("notices", notices);
        req.setAttribute("helpdeskList", helpdeskList);

        req.getRequestDispatcher("/WEB-INF/views/admin/content.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type"); // instruction, notice, helpdesk
        String action = req.getParameter("action"); // add, edit, delete
        User admin = (User) req.getSession().getAttribute("CURRENT_USER");

        if ("instruction".equalsIgnoreCase(type)) {
            handleInstruction(req, resp, action, admin);
        } else if ("notice".equalsIgnoreCase(type)) {
            handleNotice(req, resp, action, admin);
        } else if ("helpdesk".equalsIgnoreCase(type)) {
            handleHelpdesk(req, resp, action, admin);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/content");
        }
    }

    private void handleInstruction(HttpServletRequest req, HttpServletResponse resp, String action, User admin) throws IOException {
        if ("add".equalsIgnoreCase(action)) {
            Instruction i = new Instruction();
            i.setCategory(req.getParameter("category"));
            i.setTitle(req.getParameter("title"));
            i.setContent(req.getParameter("content"));
            i.setSortOrder(Integer.parseInt(req.getParameter("sort_order")));
            i.setPublished("1".equals(req.getParameter("is_published")));
            boolean ok = contentDAO.addInstruction(i);
            if (ok && admin != null) {
                auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "ADD_INSTRUCTION", "instructions", null, "Added: " + i.getTitle(), req.getRemoteAddr());
            }
            resp.sendRedirect(req.getContextPath() + "/admin/content?tab=instructions&msg=Instruction+added+successfully.");
        } else if ("edit".equalsIgnoreCase(action)) {
            Instruction i = new Instruction();
            i.setId(Integer.parseInt(req.getParameter("id")));
            i.setCategory(req.getParameter("category"));
            i.setTitle(req.getParameter("title"));
            i.setContent(req.getParameter("content"));
            i.setSortOrder(Integer.parseInt(req.getParameter("sort_order")));
            i.setPublished("1".equals(req.getParameter("is_published")));
            boolean ok = contentDAO.updateInstruction(i);
            if (ok && admin != null) {
                auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "EDIT_INSTRUCTION", "instructions", i.getId(), "Updated: " + i.getTitle(), req.getRemoteAddr());
            }
            resp.sendRedirect(req.getContextPath() + "/admin/content?tab=instructions&msg=Instruction+updated+successfully.");
        } else if ("delete".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            boolean ok = contentDAO.deleteInstruction(id);
            if (ok && admin != null) {
                auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "DELETE_INSTRUCTION", "instructions", id, "Deleted ID: " + id, req.getRemoteAddr());
            }
            resp.sendRedirect(req.getContextPath() + "/admin/content?tab=instructions&msg=Instruction+deleted+successfully.");
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/content?tab=instructions");
        }
    }

    private void handleNotice(HttpServletRequest req, HttpServletResponse resp, String action, User admin) throws IOException {
        if ("add".equalsIgnoreCase(action)) {
            Notice n = new Notice();
            n.setTitle(req.getParameter("title"));
            n.setCategory(req.getParameter("category"));
            n.setDescription(req.getParameter("description"));
            n.setAttachmentUrl(req.getParameter("attachment_url"));
            n.setTargetAudience(req.getParameter("target_audience"));
            n.setPublished("1".equals(req.getParameter("is_published")));
            if (admin != null) {
                n.setCreatedByAdminId(admin.getId());
            }
            boolean ok = contentDAO.addNotice(n);
            if (ok && admin != null) {
                auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "ADD_NOTICE", "notices", null, "Added notice: " + n.getTitle(), req.getRemoteAddr());
            }
            resp.sendRedirect(req.getContextPath() + "/admin/content?tab=notices&msg=Notice+published+successfully.");
        } else if ("edit".equalsIgnoreCase(action)) {
            Notice n = new Notice();
            n.setId(Integer.parseInt(req.getParameter("id")));
            n.setTitle(req.getParameter("title"));
            n.setCategory(req.getParameter("category"));
            n.setDescription(req.getParameter("description"));
            n.setAttachmentUrl(req.getParameter("attachment_url"));
            n.setTargetAudience(req.getParameter("target_audience"));
            n.setPublished("1".equals(req.getParameter("is_published")));
            boolean ok = contentDAO.updateNotice(n);
            if (ok && admin != null) {
                auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "EDIT_NOTICE", "notices", n.getId(), "Updated notice: " + n.getTitle(), req.getRemoteAddr());
            }
            resp.sendRedirect(req.getContextPath() + "/admin/content?tab=notices&msg=Notice+updated+successfully.");
        } else if ("delete".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            boolean ok = contentDAO.deleteNotice(id);
            if (ok && admin != null) {
                auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "DELETE_NOTICE", "notices", id, "Deleted ID: " + id, req.getRemoteAddr());
            }
            resp.sendRedirect(req.getContextPath() + "/admin/content?tab=notices&msg=Notice+deleted+successfully.");
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/content?tab=notices");
        }
    }

    private void handleHelpdesk(HttpServletRequest req, HttpServletResponse resp, String action, User admin) throws IOException {
        if ("add".equalsIgnoreCase(action)) {
            HelpdeskContact c = new HelpdeskContact();
            c.setCategory(req.getParameter("category"));
            c.setTitle(req.getParameter("title"));
            c.setContactPerson(req.getParameter("contact_person"));
            c.setPhoneNumber(req.getParameter("phone_number"));
            c.setEmail(req.getParameter("email"));
            c.setTiming(req.getParameter("timing"));
            c.setAddress(req.getParameter("address"));
            c.setSortOrder(Integer.parseInt(req.getParameter("sort_order")));
            c.setStatus("ACTIVE");
            boolean ok = contentDAO.addHelpdeskContact(c);
            if (ok && admin != null) {
                auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "ADD_HELPDESK_CONTACT", "helpdesk_contacts", null, "Added: " + c.getTitle(), req.getRemoteAddr());
            }
            resp.sendRedirect(req.getContextPath() + "/admin/content?tab=helpdesk&msg=Helpdesk+contact+added+successfully.");
        } else if ("edit".equalsIgnoreCase(action)) {
            HelpdeskContact c = new HelpdeskContact();
            c.setId(Integer.parseInt(req.getParameter("id")));
            c.setCategory(req.getParameter("category"));
            c.setTitle(req.getParameter("title"));
            c.setContactPerson(req.getParameter("contact_person"));
            c.setPhoneNumber(req.getParameter("phone_number"));
            c.setEmail(req.getParameter("email"));
            c.setTiming(req.getParameter("timing"));
            c.setAddress(req.getParameter("address"));
            c.setSortOrder(Integer.parseInt(req.getParameter("sort_order")));
            String status = req.getParameter("status");
            c.setStatus(status != null ? status : "ACTIVE");
            boolean ok = contentDAO.updateHelpdeskContact(c);
            if (ok && admin != null) {
                auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "EDIT_HELPDESK_CONTACT", "helpdesk_contacts", c.getId(), "Updated: " + c.getTitle(), req.getRemoteAddr());
            }
            resp.sendRedirect(req.getContextPath() + "/admin/content?tab=helpdesk&msg=Helpdesk+contact+updated+successfully.");
        } else if ("delete".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            boolean ok = contentDAO.deleteHelpdeskContact(id);
            if (ok && admin != null) {
                auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "DELETE_HELPDESK_CONTACT", "helpdesk_contacts", id, "Deleted ID: " + id, req.getRemoteAddr());
            }
            resp.sendRedirect(req.getContextPath() + "/admin/content?tab=helpdesk&msg=Helpdesk+contact+deleted+successfully.");
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/content?tab=helpdesk");
        }
    }
}
