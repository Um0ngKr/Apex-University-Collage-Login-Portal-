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
import java.util.Map;

@WebServlet(urlPatterns = {"/admin/teachers", "/admin/teacher-action"})
public class AdminTeacherController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private TeacherDAO teacherDAO;
    private DepartmentDAO departmentDAO;
    private CourseDAO courseDAO;
    private SubjectDAO subjectDAO;
    private AcademicSessionDAO sessionDAO;
    private AuditLogDAO auditDAO;

    @Override
    public void init() throws ServletException {
        teacherDAO = new TeacherDAO();
        departmentDAO = new DepartmentDAO();
        courseDAO = new CourseDAO();
        subjectDAO = new SubjectDAO();
        sessionDAO = new AcademicSessionDAO();
        auditDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("search");
        List<Teacher> teachers = teacherDAO.getAllTeachers(null, search);
        List<Department> departments = departmentDAO.getAllDepartments();
        List<Course> courses = courseDAO.getAllCourses();
        List<Semester> semesters = courseDAO.getAllSemesters();
        List<Subject> subjects = subjectDAO.getAllSubjects(null, null);
        List<AcademicSession> sessions = sessionDAO.getAllSessions();

        req.setAttribute("teachers", teachers);
        req.setAttribute("departments", departments);
        req.setAttribute("courses", courses);
        req.setAttribute("semesters", semesters);
        req.setAttribute("subjects", subjects);
        req.setAttribute("sessions", sessions);

        String teacherIdStr = req.getParameter("view_id");
        if (teacherIdStr != null && !teacherIdStr.isEmpty()) {
            int tid = Integer.parseInt(teacherIdStr);
            List<Map<String, Object>> assignments = teacherDAO.getAssignedSubjects(tid);
            Teacher selectedTeacher = teacherDAO.getTeacherById(tid);
            req.setAttribute("selectedTeacher", selectedTeacher);
            req.setAttribute("assignments", assignments);
        }

        req.getRequestDispatcher("/WEB-INF/views/admin/teachers.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        User admin = (User) req.getSession().getAttribute("CURRENT_USER");

        if ("add".equalsIgnoreCase(action)) {
            Teacher t = new Teacher();
            String empId = req.getParameter("employee_id");
            if (empId == null || empId.trim().isEmpty()) {
                empId = "TCH" + (100 + (int)(Math.random() * 899));
            }
            t.setEmployeeId(empId.trim());
            t.setFullName(req.getParameter("full_name"));
            t.setEmail(req.getParameter("email"));
            t.setPhone(req.getParameter("phone"));
            t.setDesignation(req.getParameter("designation"));
            t.setDepartmentId(Integer.parseInt(req.getParameter("department_id")));
            t.setQualification(req.getParameter("qualification"));
            t.setStatus("ACTIVE");

            boolean ok = teacherDAO.addTeacher(t);
            if (ok) {
                auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "ADD_TEACHER", "teachers", t.getId(), "Added teacher: " + t.getFullName(), req.getRemoteAddr());
                resp.sendRedirect(req.getContextPath() + "/admin/teachers?msg=Teacher+added+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/teachers?error=Failed+to+add+teacher.+Employee+ID+or+Email+may+already+exist.");
            }
        } else if ("edit".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            Teacher t = teacherDAO.getTeacherById(id);
            if (t != null) {
                t.setEmployeeId(req.getParameter("employee_id"));
                t.setFullName(req.getParameter("full_name"));
                t.setEmail(req.getParameter("email"));
                t.setPhone(req.getParameter("phone"));
                t.setDesignation(req.getParameter("designation"));
                t.setDepartmentId(Integer.parseInt(req.getParameter("department_id")));
                t.setQualification(req.getParameter("qualification"));
                t.setStatus(req.getParameter("status"));

                teacherDAO.updateTeacher(t);
                auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "EDIT_TEACHER", "teachers", id, "Updated teacher: " + t.getFullName(), req.getRemoteAddr());
            }
            resp.sendRedirect(req.getContextPath() + "/admin/teachers?msg=Teacher+updated+successfully.");
        } else if ("delete".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            teacherDAO.deleteTeacher(id);
            auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "DELETE_TEACHER", "teachers", id, "Deleted teacher ID: " + id, req.getRemoteAddr());
            resp.sendRedirect(req.getContextPath() + "/admin/teachers?msg=Teacher+deleted+successfully.");
        } else if ("assign".equalsIgnoreCase(action)) {
            int teacherId = Integer.parseInt(req.getParameter("teacher_id"));
            int courseId = Integer.parseInt(req.getParameter("course_id"));
            int semesterId = Integer.parseInt(req.getParameter("semester_id"));
            int subjectId = Integer.parseInt(req.getParameter("subject_id"));
            int sessionId = Integer.parseInt(req.getParameter("session_id"));
            String section = req.getParameter("section");

            teacherDAO.assignSubject(teacherId, courseId, semesterId, subjectId, sessionId, section);
            auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "ASSIGN_SUBJECT", "teacher_assignments", null, "Assigned subject ID " + subjectId + " to teacher ID " + teacherId, req.getRemoteAddr());
            resp.sendRedirect(req.getContextPath() + "/admin/teachers?view_id=" + teacherId + "&msg=Subject+assigned+successfully.");
        } else if ("remove_assignment".equalsIgnoreCase(action)) {
            int assignmentId = Integer.parseInt(req.getParameter("assignment_id"));
            int teacherId = Integer.parseInt(req.getParameter("teacher_id"));
            teacherDAO.removeAssignment(assignmentId);
            resp.sendRedirect(req.getContextPath() + "/admin/teachers?view_id=" + teacherId + "&msg=Assignment+removed.");
        }
    }
}
