package com.apex.controller.admin;

import com.apex.dao.*;
import com.apex.model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/academics", "/admin/academic-action"})
public class AdminAcademicController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DepartmentDAO departmentDAO;
    private CourseDAO courseDAO;
    private SubjectDAO subjectDAO;
    private AcademicSessionDAO sessionDAO;
    private AuditLogDAO auditDAO;

    @Override
    public void init() throws ServletException {
        departmentDAO = new DepartmentDAO();
        courseDAO = new CourseDAO();
        subjectDAO = new SubjectDAO();
        sessionDAO = new AcademicSessionDAO();
        auditDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tab = req.getParameter("tab");
        if (tab == null || tab.isEmpty()) {
            tab = "departments";
        }

        List<Department> departments = departmentDAO.getAllDepartments();
        List<Course> courses = courseDAO.getAllCourses();
        List<Semester> semesters = courseDAO.getAllSemesters();
        List<Subject> subjects = subjectDAO.getAllSubjects(null, null);
        List<AcademicSession> sessions = sessionDAO.getAllSessions();

        req.setAttribute("activeTab", tab);
        req.setAttribute("departments", departments);
        req.setAttribute("courses", courses);
        req.setAttribute("semesters", semesters);
        req.setAttribute("subjects", subjects);
        req.setAttribute("sessions", sessions);

        req.getRequestDispatcher("/WEB-INF/views/admin/academics.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String entity = req.getParameter("entity"); // dept, course, subject, session
        String action = req.getParameter("action"); // add, edit, delete, set_current
        User admin = (User) req.getSession().getAttribute("CURRENT_USER");

        if ("dept".equalsIgnoreCase(entity)) {
            handleDepartment(req, resp, action, admin);
        } else if ("course".equalsIgnoreCase(entity)) {
            handleCourse(req, resp, action, admin);
        } else if ("subject".equalsIgnoreCase(entity)) {
            handleSubject(req, resp, action, admin);
        } else if ("session".equalsIgnoreCase(entity)) {
            handleSession(req, resp, action, admin);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/academics");
        }
    }

    private void handleDepartment(HttpServletRequest req, HttpServletResponse resp, String action, User admin) throws IOException {
        if ("add".equalsIgnoreCase(action)) {
            Department d = new Department();
            d.setDeptCode(req.getParameter("dept_code"));
            d.setName(req.getParameter("name"));
            d.setDescription(req.getParameter("description"));
            d.setStatus("ACTIVE");
            boolean ok = departmentDAO.addDepartment(d);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "ADD_DEPARTMENT", "departments", null, "Added: " + d.getName(), req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=departments&msg=Department+added+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=departments&error=Failed+to+add+department.+Code+may+already+exist.");
            }
        } else if ("edit".equalsIgnoreCase(action)) {
            String idStr = req.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=departments&error=Invalid+department+ID.");
                return;
            }
            int id = Integer.parseInt(idStr.trim());
            Department d = new Department();
            d.setId(id);
            d.setDeptCode(req.getParameter("dept_code"));
            d.setName(req.getParameter("name"));
            d.setDescription(req.getParameter("description"));
            d.setStatus(req.getParameter("status"));
            boolean ok = departmentDAO.updateDepartment(d);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "EDIT_DEPARTMENT", "departments", id, "Updated: " + d.getName(), req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=departments&msg=Department+updated+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=departments&error=Failed+to+update+department.+Code+may+already+exist.");
            }
        } else if ("delete".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            boolean ok = departmentDAO.deleteDepartment(id);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "DELETE_DEPARTMENT", "departments", id, "Deleted department ID: " + id, req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=departments&msg=Department+deleted+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=departments&error=Failed+to+delete+department.+Existing+courses+or+teachers+may+be+linked+to+it.");
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=departments");
        }
    }

    private void handleCourse(HttpServletRequest req, HttpServletResponse resp, String action, User admin) throws IOException {
        if ("add".equalsIgnoreCase(action)) {
            Course c = new Course();
            c.setDepartmentId(Integer.parseInt(req.getParameter("department_id")));
            c.setCourseCode(req.getParameter("course_code"));
            c.setName(req.getParameter("name"));
            c.setDurationYears(Integer.parseInt(req.getParameter("duration_years")));
            c.setTotalSemesters(Integer.parseInt(req.getParameter("total_semesters")));
            c.setDegreeLevel(req.getParameter("degree_level"));
            c.setStatus("ACTIVE");
            boolean ok = courseDAO.addCourse(c);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "ADD_COURSE", "courses", c.getId(), "Added: " + c.getName(), req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=courses&msg=Course+added+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=courses&error=Failed+to+add+course.+Code+may+already+exist.");
            }
        } else if ("edit".equalsIgnoreCase(action)) {
            String idStr = req.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=courses&error=Invalid+course+ID.");
                return;
            }
            int id = Integer.parseInt(idStr.trim());
            Course c = new Course();
            c.setId(id);
            c.setDepartmentId(Integer.parseInt(req.getParameter("department_id")));
            c.setCourseCode(req.getParameter("course_code"));
            c.setName(req.getParameter("name"));
            c.setDurationYears(Integer.parseInt(req.getParameter("duration_years")));
            c.setTotalSemesters(Integer.parseInt(req.getParameter("total_semesters")));
            c.setDegreeLevel(req.getParameter("degree_level"));
            c.setStatus(req.getParameter("status"));
            boolean ok = courseDAO.updateCourse(c);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "EDIT_COURSE", "courses", id, "Updated: " + c.getName(), req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=courses&msg=Course+updated+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=courses&error=Failed+to+update+course.+Course+code+may+already+exist.");
            }
        } else if ("delete".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            boolean ok = courseDAO.deleteCourse(id);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "DELETE_COURSE", "courses", id, "Deleted course ID: " + id, req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=courses&msg=Course+deleted+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=courses&error=Failed+to+delete+course.+Existing+students+or+subjects+may+be+linked+to+it.");
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=courses");
        }
    }

    private void handleSubject(HttpServletRequest req, HttpServletResponse resp, String action, User admin) throws IOException {
        if ("add".equalsIgnoreCase(action)) {
            Subject s = new Subject();
            s.setCourseId(Integer.parseInt(req.getParameter("course_id")));
            s.setSemesterId(Integer.parseInt(req.getParameter("semester_id")));
            s.setSubjectCode(req.getParameter("subject_code"));
            s.setName(req.getParameter("name"));
            s.setCredit(Double.parseDouble(req.getParameter("credit")));
            s.setMaxInternalMarks(Integer.parseInt(req.getParameter("max_internal_marks")));
            s.setMaxExternalMarks(Integer.parseInt(req.getParameter("max_external_marks")));
            s.setPassMarks(Integer.parseInt(req.getParameter("pass_marks")));
            s.setSubjectType(req.getParameter("subject_type"));
            s.setStatus("ACTIVE");
            boolean ok = subjectDAO.addSubject(s);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "ADD_SUBJECT", "subjects", s.getId(), "Added: " + s.getName(), req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=subjects&msg=Subject+added+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=subjects&error=Failed+to+add+subject.+Subject+code+may+already+exist.");
            }
        } else if ("edit".equalsIgnoreCase(action)) {
            String idStr = req.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=subjects&error=Invalid+subject+ID.");
                return;
            }
            int id = Integer.parseInt(idStr.trim());
            Subject s = new Subject();
            s.setId(id);
            s.setCourseId(Integer.parseInt(req.getParameter("course_id")));
            s.setSemesterId(Integer.parseInt(req.getParameter("semester_id")));
            s.setSubjectCode(req.getParameter("subject_code"));
            s.setName(req.getParameter("name"));
            s.setCredit(Double.parseDouble(req.getParameter("credit")));
            s.setMaxInternalMarks(Integer.parseInt(req.getParameter("max_internal_marks")));
            s.setMaxExternalMarks(Integer.parseInt(req.getParameter("max_external_marks")));
            s.setPassMarks(Integer.parseInt(req.getParameter("pass_marks")));
            s.setSubjectType(req.getParameter("subject_type"));
            s.setStatus(req.getParameter("status"));
            boolean ok = subjectDAO.updateSubject(s);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "EDIT_SUBJECT", "subjects", id, "Updated: " + s.getName(), req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=subjects&msg=Subject+updated+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=subjects&error=Failed+to+update+subject.+Subject+code+may+already+exist.");
            }
        } else if ("delete".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            boolean ok = subjectDAO.deleteSubject(id);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "DELETE_SUBJECT", "subjects", id, "Deleted subject ID: " + id, req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=subjects&msg=Subject+deleted+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=subjects&error=Failed+to+delete+subject.+Existing+marks+or+exam+records+may+be+linked+to+it.");
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=subjects");
        }
    }

    private void handleSession(HttpServletRequest req, HttpServletResponse resp, String action, User admin) throws IOException {
        if ("add".equalsIgnoreCase(action)) {
            String sessionCode = req.getParameter("session_code");
            String startYearStr = req.getParameter("start_year");
            String endYearStr = req.getParameter("end_year");

            if (sessionCode == null || startYearStr == null || endYearStr == null ||
                sessionCode.trim().isEmpty() || startYearStr.trim().isEmpty() || endYearStr.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=sessions&error=Please+fill+in+all+required+fields.");
                return;
            }

            int startYear, endYear;
            try {
                startYear = Integer.parseInt(startYearStr.trim());
                endYear = Integer.parseInt(endYearStr.trim());
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=sessions&error=Invalid+years+entered.");
                return;
            }

            if (endYear < startYear) {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=sessions&error=End+year+cannot+be+earlier+than+start+year.");
                return;
            }

            if (sessionDAO.isSessionCodeExists(sessionCode.trim(), 0)) {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=sessions&error=Session+code+already+exists.");
                return;
            }

            AcademicSession a = new AcademicSession();
            a.setSessionCode(sessionCode.trim());
            a.setStartYear(startYear);
            a.setEndYear(endYear);
            a.setCurrent("1".equals(req.getParameter("is_current")));
            a.setStatus("ACTIVE");
            boolean ok = sessionDAO.addSession(a);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "ADD_SESSION", "academic_sessions", a.getId(), "Added session: " + a.getSessionCode(), req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=sessions&msg=Academic+session+added+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=sessions&error=Failed+to+add+session.+Session+code+may+already+exist.");
            }
        } else if ("edit".equalsIgnoreCase(action)) {
            String idStr = req.getParameter("id");
            String sessionCode = req.getParameter("session_code");
            String startYearStr = req.getParameter("start_year");
            String endYearStr = req.getParameter("end_year");
            String status = req.getParameter("status");
            boolean isCurrent = "1".equals(req.getParameter("is_current"));

            if (idStr == null || sessionCode == null || startYearStr == null || endYearStr == null ||
                sessionCode.trim().isEmpty() || startYearStr.trim().isEmpty() || endYearStr.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=sessions&error=Please+fill+in+all+required+fields.");
                return;
            }

            int id, startYear, endYear;
            try {
                id = Integer.parseInt(idStr.trim());
                startYear = Integer.parseInt(startYearStr.trim());
                endYear = Integer.parseInt(endYearStr.trim());
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=sessions&error=Invalid+numeric+values+for+years+or+session+ID.");
                return;
            }

            if (endYear < startYear) {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=sessions&error=End+year+cannot+be+earlier+than+start+year.");
                return;
            }

            if (sessionDAO.isSessionCodeExists(sessionCode.trim(), id)) {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=sessions&error=Session+code+already+exists.");
                return;
            }

            AcademicSession a = new AcademicSession();
            a.setId(id);
            a.setSessionCode(sessionCode.trim());
            a.setStartYear(startYear);
            a.setEndYear(endYear);
            a.setCurrent(isCurrent);
            a.setStatus(status != null ? status.trim() : "ACTIVE");

            boolean ok = sessionDAO.updateSession(a);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "EDIT_SESSION", "academic_sessions", id, "Updated session: " + a.getSessionCode(), req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=sessions&msg=Academic+session+updated+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=sessions&error=Failed+to+update+academic+session.");
            }
        } else if ("set_current".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            boolean ok = sessionDAO.setCurrentSession(id);
            if (ok) {
                if (admin != null) {
                    auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "SET_CURRENT_SESSION", "academic_sessions", id, "Set active session ID: " + id, req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=sessions&msg=Current+academic+session+updated+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=sessions&error=Failed+to+set+current+session.");
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/academics?tab=sessions");
        }
    }
}
