package com.apex.test;

import com.apex.controller.teacher.TeacherAttendanceController;
import com.apex.controller.teacher.TeacherMarksController;
import com.apex.dao.StudentDAO;
import com.apex.dao.TeacherDAO;
import com.apex.model.Student;
import com.apex.model.Teacher;
import com.apex.model.User;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Automated Test Suite for Teacher Portal Assigned Subjects, Student Roster Loading,
 * and Cross-Teacher Subject Access Authorization.
 */
public class TeacherAssignmentTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   APEX UNIVERSITY TEACHER ASSIGNMENTS & AUTHORIZATION TEST SUITE");
        System.out.println("================================================================================");

        try {
            testTch101AssignedSubjects();
            testTeacherIsolation();
            testUnassignedTeacher();
            testStudentRosterLoading();
            testEmployeeIdResolution();
            testAttendanceControllerAuthorization();
            testMarksControllerAuthorization();
        } catch (Exception e) {
            e.printStackTrace();
            testsFailed++;
        }

        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("TEST SUMMARY: " + testsPassed + " Passed, " + testsFailed + " Failed");
        System.out.println("--------------------------------------------------------------------------------");
        if (testsFailed > 0) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (condition) {
            System.out.println("  [PASS] " + message);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + message);
            testsFailed++;
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) {
            System.out.println("  [PASS] " + message);
            testsPassed++;
        } else if (expected != null && expected.equals(actual)) {
            System.out.println("  [PASS] " + message);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + message + " (Expected: " + expected + ", Got: " + actual + ")");
            testsFailed++;
        }
    }

    private static void testTch101AssignedSubjects() {
        System.out.println("\n[1] Testing Teacher TCH101 Assigned Subjects Retrieval...");
        TeacherDAO teacherDAO = new TeacherDAO();
        Teacher t101 = teacherDAO.getTeacherByEmployeeId("TCH101");
        assertTrue(t101 != null, "Teacher TCH101 exists in database");
        if (t101 == null) return;

        assertEquals("TCH101", t101.getEmployeeId(), "Employee ID matches TCH101");

        List<Map<String, Object>> assignments = teacherDAO.getAssignedSubjects(t101.getId());
        assertTrue(assignments != null && !assignments.isEmpty(), "TCH101 has assigned subjects returned (Count: " + (assignments != null ? assignments.size() : 0) + ")");

        boolean hasCS501 = false;
        boolean hasCS502 = false;
        boolean hasCS591 = false;
        if (assignments != null) {
            for (Map<String, Object> asg : assignments) {
                String code = (String) asg.get("subjectCode");
                if ("CS501".equalsIgnoreCase(code)) hasCS501 = true;
                if ("CS502".equalsIgnoreCase(code)) hasCS502 = true;
                if ("CS591".equalsIgnoreCase(code)) hasCS591 = true;
                // Verify all required assignment metadata is populated
                assertTrue(asg.get("courseName") != null, "Assignment has courseName populated");
                assertTrue(asg.get("semesterNumber") != null, "Assignment has semesterNumber populated");
                assertTrue(asg.get("sessionCode") != null, "Assignment has sessionCode populated");
            }
        }
        assertTrue(hasCS501, "TCH101 is assigned CS501 (Database Management Systems)");
        assertTrue(hasCS502, "TCH101 is assigned CS502 (Design and Analysis of Algorithms)");
        assertTrue(hasCS591, "TCH101 is assigned CS591 (Database Management Systems Lab)");
    }

    private static void testTeacherIsolation() {
        System.out.println("\n[2] Testing Teacher Subject Isolation & Cross-Teacher Authorization...");
        TeacherDAO teacherDAO = new TeacherDAO();
        Teacher t101 = teacherDAO.getTeacherByEmployeeId("TCH101");
        Teacher t102 = teacherDAO.getTeacherByEmployeeId("TCH102");
        assertTrue(t101 != null && t102 != null, "Both TCH101 and TCH102 exist");
        if (t101 == null || t102 == null) return;

        // Subject 1 (CS501) assigned to TCH101
        assertTrue(teacherDAO.isSubjectAssignedToTeacher(t101.getId(), 1), "TCH101 can access subject 1");
        assertTrue(!teacherDAO.isSubjectAssignedToTeacher(t102.getId(), 1), "TCH102 is BLOCKED from accessing subject 1");

        // Subject 3 (CS503) assigned to TCH102
        assertTrue(teacherDAO.isSubjectAssignedToTeacher(t102.getId(), 3), "TCH102 can access subject 3");
        assertTrue(!teacherDAO.isSubjectAssignedToTeacher(t101.getId(), 3), "TCH101 is BLOCKED from accessing subject 3");
    }

    private static void testUnassignedTeacher() {
        System.out.println("\n[3] Testing Unassigned Teacher (TCH103)...");
        TeacherDAO teacherDAO = new TeacherDAO();
        Teacher t103 = teacherDAO.getTeacherByEmployeeId("TCH103");
        assertTrue(t103 != null, "Teacher TCH103 exists");
        if (t103 == null) return;

        List<Map<String, Object>> assignments = teacherDAO.getAssignedSubjects(t103.getId());
        assertTrue(assignments != null && assignments.isEmpty(), "Unassigned teacher TCH103 has 0 assigned subjects");
        assertTrue(!teacherDAO.isSubjectAssignedToTeacher(t103.getId(), 1), "TCH103 cannot access subject 1");
        assertTrue(!teacherDAO.isSubjectAssignedToTeacher(t103.getId(), 2), "TCH103 cannot access subject 2");
        assertTrue(!teacherDAO.isSubjectAssignedToTeacher(t103.getId(), 3), "TCH103 cannot access subject 3");
    }

    private static void testStudentRosterLoading() {
        System.out.println("\n[4] Testing Student Roster Loading for Assigned Subject...");
        StudentDAO studentDAO = new StudentDAO();
        // Load students for subject 1, course 4 (BTECH-IT), semester 13 (Sem 5)
        List<Student> students = studentDAO.getStudentsForSubjectAssignment(1, 4, 13);
        assertTrue(students != null && !students.isEmpty(), "Student roster loads enrolled students (Count: " + (students != null ? students.size() : 0) + ")");

        boolean foundUmang = false;
        if (students != null) {
            for (Student s : students) {
                if ("Umang Kumar".equalsIgnoreCase(s.getFullName()) || "11500224088".equals(s.getRollNo())) {
                    foundUmang = true;
                    break;
                }
            }
        }
        assertTrue(foundUmang, "Enrolled student Umang Kumar (Roll: 11500224088) is present in class roster");
    }

    private static void testEmployeeIdResolution() {
        System.out.println("\n[5] Testing Resolution via Employee ID...");
        TeacherDAO teacherDAO = new TeacherDAO();
        List<Map<String, Object>> asgs = teacherDAO.getAssignedSubjectsByEmployeeId("TCH101");
        assertTrue(asgs != null && asgs.size() == 3, "getAssignedSubjectsByEmployeeId('TCH101') returned 3 assignments");
    }

    private static void testAttendanceControllerAuthorization() throws Exception {
        System.out.println("\n[6] Testing Teacher Attendance Controller Authorization Enforcement...");
        TeacherAttendanceController controller = new TeacherAttendanceController();
        controller.init();

        TeacherDAO teacherDAO = new TeacherDAO();
        Teacher t101 = teacherDAO.getTeacherByEmployeeId("TCH101");
        Teacher t102 = teacherDAO.getTeacherByEmployeeId("TCH102");

        // Test 6a: Authorized access by TCH101 to Subject 1
        User user101 = new User();
        user101.setId(t101.getId());
        user101.setRelatedId(t101.getId());
        user101.setUsername("TCH101");
        user101.setRole("TEACHER");

        MockSession session101 = new MockSession();
        session101.setAttribute("CURRENT_USER", user101);

        MockRequest req101 = new MockRequest("/teacher/attendance", "GET");
        req101.setSession(session101);
        req101.setParameter("subject_id", "1");
        req101.setParameter("class_date", "2024-11-12");

        MockResponse resp101 = new MockResponse();
        controller.service(req101.getProxy(), resp101.getProxy());

        assertTrue(req101.getAttribute("assignments") != null, "Attendance controller set 'assignments' attribute");
        assertTrue(req101.getAttribute("students") != null, "Attendance controller loaded 'students' roster");
        assertEquals("/WEB-INF/views/teacher/attendance.jsp", req101.getForwardedPath(), "Forwarded to attendance JSP for authorized teacher");

        // Test 6b: Unauthorized access by TCH102 to Subject 1 (not assigned)
        User user102 = new User();
        user102.setId(t102.getId());
        user102.setRelatedId(t102.getId());
        user102.setUsername("TCH102");
        user102.setRole("TEACHER");

        MockSession session102 = new MockSession();
        session102.setAttribute("CURRENT_USER", user102);

        MockRequest req102 = new MockRequest("/teacher/attendance", "GET");
        req102.setSession(session102);
        req102.setParameter("subject_id", "1"); // Subject 1 belongs to TCH101!

        MockResponse resp102 = new MockResponse();
        controller.service(req102.getProxy(), resp102.getProxy());

        assertTrue(resp102.getRedirectLocation() != null && resp102.getRedirectLocation().contains("error=Unauthorized"),
                "TCH102 accessing unassigned subject 1 was REJECTED with Unauthorized error redirect");

        // Test 6c: Unauthorized POST by TCH102 to Subject 1
        MockRequest reqPost = new MockRequest("/teacher/attendance-action", "POST");
        reqPost.setSession(session102);
        reqPost.setParameter("subject_id", "1");
        reqPost.setParameter("session_id", "2");
        reqPost.setParameter("class_date", "2024-11-12");

        MockResponse respPost = new MockResponse();
        controller.service(reqPost.getProxy(), respPost.getProxy());

        assertTrue(respPost.getRedirectLocation() != null && respPost.getRedirectLocation().contains("error=Unauthorized"),
                "TCH102 POST to unassigned subject 1 was REJECTED with Unauthorized error redirect");
    }

    private static void testMarksControllerAuthorization() throws Exception {
        System.out.println("\n[7] Testing Teacher Marks Controller Authorization Enforcement...");
        TeacherMarksController controller = new TeacherMarksController();
        controller.init();

        TeacherDAO teacherDAO = new TeacherDAO();
        Teacher t101 = teacherDAO.getTeacherByEmployeeId("TCH101");
        Teacher t102 = teacherDAO.getTeacherByEmployeeId("TCH102");

        User user101 = new User();
        user101.setId(t101.getId());
        user101.setRelatedId(t101.getId());
        user101.setUsername("TCH101");
        user101.setRole("TEACHER");

        MockSession session101 = new MockSession();
        session101.setAttribute("CURRENT_USER", user101);

        // Test 7a: Authorized access by TCH101 to Subject 1
        MockRequest req101 = new MockRequest("/teacher/marks", "GET");
        req101.setSession(session101);
        req101.setParameter("exam_id", "1");
        req101.setParameter("subject_id", "1");

        MockResponse resp101 = new MockResponse();
        controller.service(req101.getProxy(), resp101.getProxy());

        assertTrue(req101.getAttribute("assignments") != null, "Marks controller set 'assignments' attribute");
        assertTrue(req101.getAttribute("students") != null, "Marks controller loaded 'students' roster");
        assertEquals("/WEB-INF/views/teacher/marks.jsp", req101.getForwardedPath(), "Forwarded to marks JSP for authorized teacher");

        // Test 7b: Unauthorized access by TCH102 to Subject 1
        User user102 = new User();
        user102.setId(t102.getId());
        user102.setRelatedId(t102.getId());
        user102.setUsername("TCH102");
        user102.setRole("TEACHER");

        MockSession session102 = new MockSession();
        session102.setAttribute("CURRENT_USER", user102);

        MockRequest req102 = new MockRequest("/teacher/marks", "GET");
        req102.setSession(session102);
        req102.setParameter("exam_id", "1");
        req102.setParameter("subject_id", "1"); // Subject 1 belongs to TCH101!

        MockResponse resp102 = new MockResponse();
        controller.service(req102.getProxy(), resp102.getProxy());

        assertTrue(resp102.getRedirectLocation() != null && resp102.getRedirectLocation().contains("error=Unauthorized"),
                "TCH102 accessing unassigned subject 1 marks was REJECTED with Unauthorized error redirect");

        // Test 7c: Unauthorized POST marks by TCH102 to Subject 1
        MockRequest reqPost = new MockRequest("/teacher/marks-action", "POST");
        reqPost.setSession(session102);
        reqPost.setParameter("exam_id", "1");
        reqPost.setParameter("subject_id", "1");
        reqPost.setParameter("student_id", "1");
        reqPost.setParameter("internal_marks", "25");

        MockResponse respPost = new MockResponse();
        controller.service(reqPost.getProxy(), respPost.getProxy());

        assertTrue(respPost.getRedirectLocation() != null && respPost.getRedirectLocation().contains("error=Unauthorized"),
                "TCH102 POST marks to unassigned subject 1 was REJECTED with Unauthorized error redirect");
    }

    // -------------------------------------------------------------------------
    // Mock Helpers using Reflection Proxies
    // -------------------------------------------------------------------------

    static class MockSession {
        private final Map<String, Object> attributes = new HashMap<>();

        public void setAttribute(String name, Object value) { attributes.put(name, value); }
        public Object getAttribute(String name) { return attributes.get(name); }

        public HttpSession getProxy() {
            return (HttpSession) Proxy.newProxyInstance(
                    HttpSession.class.getClassLoader(),
                    new Class<?>[]{HttpSession.class},
                    (proxy, method, args) -> {
                        String name = method.getName();
                        if ("getAttribute".equals(name)) return getAttribute((String) args[0]);
                        if ("setAttribute".equals(name)) { setAttribute((String) args[0], args[1]); return null; }
                        return null;
                    }
            );
        }
    }

    static class MockRequest {
        private final String servletPath;
        private final String method;
        private final Map<String, String> parameters = new HashMap<>();
        private final Map<String, Object> attributes = new HashMap<>();
        private MockSession session;
        private String forwardedPath;

        public MockRequest(String servletPath, String method) {
            this.servletPath = servletPath;
            this.method = method;
        }

        public void setSession(MockSession session) { this.session = session; }
        public void setParameter(String key, String value) { parameters.put(key, value); }
        public Object getAttribute(String name) { return attributes.get(name); }
        public String getForwardedPath() { return forwardedPath; }

        public HttpServletRequest getProxy() {
            return (HttpServletRequest) Proxy.newProxyInstance(
                    HttpServletRequest.class.getClassLoader(),
                    new Class<?>[]{HttpServletRequest.class},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
                            String name = m.getName();
                            if ("getServletPath".equals(name)) return servletPath;
                            if ("getMethod".equals(name)) return method;
                            if ("getParameter".equals(name)) return parameters.get((String) args[0]);
                            if ("getParameterValues".equals(name)) {
                                String val = parameters.get((String) args[0]);
                                return val != null ? new String[]{val} : null;
                            }
                            if ("getAttribute".equals(name)) return attributes.get((String) args[0]);
                            if ("setAttribute".equals(name)) { attributes.put((String) args[0], args[1]); return null; }
                            if ("getSession".equals(name)) return session != null ? session.getProxy() : null;
                            if ("getContextPath".equals(name)) return "/apex";
                            if ("getRequestDispatcher".equals(name)) {
                                final String path = (String) args[0];
                                return (RequestDispatcher) Proxy.newProxyInstance(
                                        RequestDispatcher.class.getClassLoader(),
                                        new Class<?>[]{RequestDispatcher.class},
                                        (dProxy, dMethod, dArgs) -> {
                                            if ("forward".equals(dMethod.getName())) {
                                                forwardedPath = path;
                                            }
                                            return null;
                                        }
                                );
                            }
                            return null;
                        }
                    }
            );
        }
    }

    static class MockResponse {
        private String redirectLocation;

        public String getRedirectLocation() { return redirectLocation; }

        public HttpServletResponse getProxy() {
            return (HttpServletResponse) Proxy.newProxyInstance(
                    HttpServletResponse.class.getClassLoader(),
                    new Class<?>[]{HttpServletResponse.class},
                    (proxy, method, args) -> {
                        String name = method.getName();
                        if ("sendRedirect".equals(name)) {
                            redirectLocation = (String) args[0];
                            return null;
                        }
                        return null;
                    }
            );
        }
    }
}
