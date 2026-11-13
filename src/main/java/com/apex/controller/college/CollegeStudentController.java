package com.apex.controller.college;

import com.apex.dao.AcademicSessionDAO;
import com.apex.dao.AuditLogDAO;
import com.apex.dao.CourseDAO;
import com.apex.dao.DepartmentDAO;
import com.apex.dao.StudentDAO;
import com.apex.model.*;
import com.apex.util.DateUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/college/students", "/college/student-action"})
public class CollegeStudentController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private StudentDAO studentDAO;
    private DepartmentDAO departmentDAO;
    private CourseDAO courseDAO;
    private AcademicSessionDAO sessionDAO;
    private AuditLogDAO auditDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
        departmentDAO = new DepartmentDAO();
        courseDAO = new CourseDAO();
        sessionDAO = new AcademicSessionDAO();
        auditDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=Session+expired.+Please+sign+in.");
            return;
        }
        int collegeId = user.getCollegeId() != null ? user.getCollegeId() : user.getRelatedId();
        String search = req.getParameter("search");

        List<Student> students = studentDAO.getAllStudents(collegeId, search, 0, 100);
        List<Department> departments = departmentDAO.getAllDepartments();
        List<Course> courses = courseDAO.getAllCourses();
        List<Semester> semesters = courseDAO.getAllSemesters();
        List<AcademicSession> sessions = sessionDAO.getAllSessions();

        req.setAttribute("students", students);
        req.setAttribute("departments", departments);
        req.setAttribute("courses", courses);
        req.setAttribute("semesters", semesters);
        req.setAttribute("sessions", sessions);

        req.getRequestDispatcher("/WEB-INF/views/college/students.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=Session+expired.+Please+sign+in.");
            return;
        }
        int collegeId = user.getCollegeId() != null ? user.getCollegeId() : user.getRelatedId();

        String action = req.getParameter("action");

        if ("add".equalsIgnoreCase(action)) {
            Student s = new Student();
            String regNo = "APEX" + java.time.Year.now().getValue() + (10000 + (int)(Math.random() * 89999));
            String rollNo = "115" + System.currentTimeMillis() % 100000000;

            s.setRegistrationNo(regNo);
            s.setRollNo(rollNo);
            s.setFullName(req.getParameter("full_name"));
            s.setEmail(req.getParameter("email"));
            s.setPhone(req.getParameter("phone"));
            s.setDob(DateUtil.parseSqlDate(req.getParameter("dob")));
            s.setGender(req.getParameter("gender"));
            s.setBloodGroup(req.getParameter("blood_group"));
            s.setFatherName(req.getParameter("father_name"));
            s.setMotherName(req.getParameter("mother_name"));
            s.setAddress(req.getParameter("address"));
            s.setCollegeId(collegeId); // Strictly assign logged in college ID
            s.setDepartmentId(Integer.parseInt(req.getParameter("department_id")));
            s.setCourseId(Integer.parseInt(req.getParameter("course_id")));
            s.setCurrentSemesterId(Integer.parseInt(req.getParameter("semester_id")));
            s.setSessionId(Integer.parseInt(req.getParameter("session_id")));
            s.setStatus("ACTIVE");

            boolean ok = studentDAO.addStudent(s);
            if (ok) {
                if (auditDAO != null) {
                    auditDAO.log("COLLEGE", user.getId(), user.getUsername(), "ADD_STUDENT", "students", s.getId(),
                            "Registered student: " + s.getFullName() + " (" + regNo + ")", req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/college/students?msg=Student+enrolled+successfully.+Registration+No:+" + regNo);
            } else {
                resp.sendRedirect(req.getContextPath() + "/college/students?error=Failed+to+enroll+student.+Email+may+already+be+in+use.");
            }
        } else if ("edit".equalsIgnoreCase(action)) {
            String idStr = req.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/college/students?error=Invalid+student+ID.");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idStr.trim());
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/college/students?error=Invalid+student+ID+format.");
                return;
            }

            Student s = studentDAO.getStudentById(id);
            if (s == null) {
                resp.sendRedirect(req.getContextPath() + "/college/students?error=Student+record+not+found.");
                return;
            }

            // CRITICAL: Strictly verify student belongs to this college
            if (s.getCollegeId() != collegeId) {
                if (auditDAO != null) {
                    auditDAO.log("COLLEGE", user.getId(), user.getUsername(), "UNAUTHORIZED_EDIT_ATTEMPT", "students", id,
                            "Unauthorized attempt to edit student ID " + id + " belonging to college ID " + s.getCollegeId(), req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/college/students?error=Unauthorized:+You+can+only+modify+students+enrolled+in+your+own+college.");
                return;
            }

            String fullName = req.getParameter("full_name");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");

            if (fullName == null || fullName.trim().isEmpty() || email == null || email.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/college/students?error=Full+name+and+email+are+required+fields.");
                return;
            }

            s.setFullName(fullName.trim());
            s.setEmail(email.trim());
            s.setPhone(phone != null ? phone.trim() : "");
            s.setDob(DateUtil.parseSqlDate(req.getParameter("dob")));
            s.setGender(req.getParameter("gender"));
            s.setBloodGroup(req.getParameter("blood_group"));
            s.setFatherName(req.getParameter("father_name"));
            s.setMotherName(req.getParameter("mother_name"));
            s.setAddress(req.getParameter("address"));

            String deptStr = req.getParameter("department_id");
            if (deptStr != null && !deptStr.trim().isEmpty()) {
                s.setDepartmentId(Integer.parseInt(deptStr.trim()));
            }
            String courseStr = req.getParameter("course_id");
            if (courseStr != null && !courseStr.trim().isEmpty()) {
                s.setCourseId(Integer.parseInt(courseStr.trim()));
            }
            String semStr = req.getParameter("semester_id");
            if (semStr != null && !semStr.trim().isEmpty()) {
                s.setCurrentSemesterId(Integer.parseInt(semStr.trim()));
            }
            String sessionStr = req.getParameter("session_id");
            if (sessionStr != null && !sessionStr.trim().isEmpty()) {
                s.setSessionId(Integer.parseInt(sessionStr.trim()));
            }
            String status = req.getParameter("status");
            if (status != null && !status.trim().isEmpty()) {
                s.setStatus(status.trim());
            }

            // Preserve registration_no and roll_no unless explicitly updated
            String regNo = req.getParameter("registration_no");
            if (regNo != null && !regNo.trim().isEmpty()) {
                s.setRegistrationNo(regNo.trim());
            }
            String rollNo = req.getParameter("roll_no");
            if (rollNo != null && !rollNo.trim().isEmpty()) {
                s.setRollNo(rollNo.trim());
            }

            // Always enforce authenticated college ID
            s.setCollegeId(collegeId);

            boolean ok = studentDAO.updateStudent(s);
            if (ok) {
                if (auditDAO != null) {
                    auditDAO.log("COLLEGE", user.getId(), user.getUsername(), "EDIT_STUDENT", "students", id,
                            "College updated student: " + s.getFullName() + " (" + s.getRegistrationNo() + ")", req.getRemoteAddr());
                }
                resp.sendRedirect(req.getContextPath() + "/college/students?msg=Student+details+updated+successfully.");
            } else {
                resp.sendRedirect(req.getContextPath() + "/college/students?error=Failed+to+update+student+details.+Email+may+already+be+in+use.");
            }
        }
    }
}
