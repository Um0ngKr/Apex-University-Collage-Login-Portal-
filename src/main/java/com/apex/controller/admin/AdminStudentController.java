package com.apex.controller.admin;

import com.apex.dao.*;
import com.apex.model.*;
import com.apex.util.DateUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/students", "/admin/student-action"})
public class AdminStudentController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private StudentDAO studentDAO;
    private CollegeDAO collegeDAO;
    private DepartmentDAO departmentDAO;
    private CourseDAO courseDAO;
    private AcademicSessionDAO sessionDAO;
    private AuditLogDAO auditDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
        collegeDAO = new CollegeDAO();
        departmentDAO = new DepartmentDAO();
        courseDAO = new CourseDAO();
        sessionDAO = new AcademicSessionDAO();
        auditDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("search");
        String collegeIdStr = req.getParameter("college_id");
        Integer collegeId = (collegeIdStr != null && !collegeIdStr.isEmpty()) ? Integer.parseInt(collegeIdStr) : null;

        List<Student> students = studentDAO.getAllStudents(collegeId, search, 0, 100);
        List<College> colleges = collegeDAO.getAllColleges(null);
        List<Department> departments = departmentDAO.getAllDepartments();
        List<Course> courses = courseDAO.getAllCourses();
        List<AcademicSession> sessions = sessionDAO.getAllSessions();
        List<Semester> semesters = courseDAO.getAllSemesters();

        req.setAttribute("students", students);
        req.setAttribute("colleges", colleges);
        req.setAttribute("departments", departments);
        req.setAttribute("courses", courses);
        req.setAttribute("sessions", sessions);
        req.setAttribute("semesters", semesters);
        req.setAttribute("currentSearch", search);
        req.setAttribute("selectedCollegeId", collegeId);

        req.getRequestDispatcher("/WEB-INF/views/admin/students.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        User admin = (User) req.getSession().getAttribute("CURRENT_USER");

        if ("add".equalsIgnoreCase(action)) {
            Student s = new Student();
            String regNo = req.getParameter("registration_no");
            if (regNo == null || regNo.trim().isEmpty()) {
                regNo = "APEX" + java.time.Year.now().getValue() + (10000 + (int)(Math.random() * 89999));
            }
            String rollNo = req.getParameter("roll_no");
            if (rollNo == null || rollNo.trim().isEmpty()) {
                rollNo = "115" + System.currentTimeMillis() % 100000000;
            }

            s.setRegistrationNo(regNo.trim());
            s.setRollNo(rollNo.trim());
            s.setFullName(req.getParameter("full_name"));
            s.setEmail(req.getParameter("email"));
            s.setPhone(req.getParameter("phone"));
            s.setDob(DateUtil.parseSqlDate(req.getParameter("dob")));
            s.setGender(req.getParameter("gender"));
            s.setBloodGroup(req.getParameter("blood_group"));
            s.setFatherName(req.getParameter("father_name"));
            s.setMotherName(req.getParameter("mother_name"));
            s.setAddress(req.getParameter("address"));
            s.setCollegeId(Integer.parseInt(req.getParameter("college_id")));
            s.setDepartmentId(Integer.parseInt(req.getParameter("department_id")));
            s.setCourseId(Integer.parseInt(req.getParameter("course_id")));
            s.setCurrentSemesterId(Integer.parseInt(req.getParameter("semester_id")));
            s.setSessionId(Integer.parseInt(req.getParameter("session_id")));
            s.setStatus("ACTIVE");

            boolean ok = studentDAO.addStudent(s);
            if (ok) {
                auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "ADD_STUDENT", "students", s.getId(), "Added student: " + s.getFullName(), req.getRemoteAddr());
                resp.sendRedirect(req.getContextPath() + "/admin/students?msg=Student+added+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/students?error=Failed+to+add+student.+Registration/Roll+number+may+already+exist.");
            }
        } else if ("edit".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            Student s = studentDAO.getStudentById(id);
            if (s != null) {
                s.setRegistrationNo(req.getParameter("registration_no"));
                s.setRollNo(req.getParameter("roll_no"));
                s.setFullName(req.getParameter("full_name"));
                s.setEmail(req.getParameter("email"));
                s.setPhone(req.getParameter("phone"));
                s.setDob(DateUtil.parseSqlDate(req.getParameter("dob")));
                s.setGender(req.getParameter("gender"));
                s.setBloodGroup(req.getParameter("blood_group"));
                s.setFatherName(req.getParameter("father_name"));
                s.setMotherName(req.getParameter("mother_name"));
                s.setAddress(req.getParameter("address"));
                s.setCollegeId(Integer.parseInt(req.getParameter("college_id")));
                s.setDepartmentId(Integer.parseInt(req.getParameter("department_id")));
                s.setCourseId(Integer.parseInt(req.getParameter("course_id")));
                s.setCurrentSemesterId(Integer.parseInt(req.getParameter("semester_id")));
                s.setSessionId(Integer.parseInt(req.getParameter("session_id")));
                s.setStatus(req.getParameter("status"));

                studentDAO.updateStudent(s);
                auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "EDIT_STUDENT", "students", id, "Updated student: " + s.getFullName(), req.getRemoteAddr());
            }
            resp.sendRedirect(req.getContextPath() + "/admin/students?msg=Student+updated+successfully.");
        } else if ("delete".equalsIgnoreCase(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            studentDAO.deleteStudent(id);
            auditDAO.log("ADMIN", admin.getId(), admin.getUsername(), "DELETE_STUDENT", "students", id, "Deleted student ID: " + id, req.getRemoteAddr());
            resp.sendRedirect(req.getContextPath() + "/admin/students?msg=Student+deleted+successfully.");
        }
    }
}
