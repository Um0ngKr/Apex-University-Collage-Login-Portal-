package com.apex.test;

import com.apex.controller.college.CollegeStudentController;
import com.apex.dao.StudentDAO;
import com.apex.model.Student;
import com.apex.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Automated Test Suite for College Portal Student Edit Functionality & Ownership Authorization.
 */
public class CollegeStudentEditTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("     APEX UNIVERSITY COLLEGE STUDENT EDIT & AUTHORIZATION TEST SUITE             ");
        System.out.println("================================================================================");

        try {
            testAuthorizedCollegeEdit();
            testCrossCollegeEditRejection();
            testNonExistentStudentEdit();
            testInvalidStudentId();
        } catch (Exception e) {
            e.printStackTrace();
            testsFailed++;
        }

        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("TEST SUMMARY: " + testsPassed + " Passed, " + testsFailed + " Failed");
        System.out.println("--------------------------------------------------------------------------------");
        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void testAuthorizedCollegeEdit() throws Exception {
        System.out.println("\n[1] Testing Authorized Student Edit by Own College...");
        StudentDAO studentDAO = new StudentDAO();
        Student s1 = studentDAO.getStudentById(1);

        if (s1 == null) {
            System.out.println("    [SKIP] Student ID 1 not found in database.");
            return;
        }

        int studentCollegeId = s1.getCollegeId();
        String originalAddress = s1.getAddress();
        String originalPhone = s1.getPhone();
        String updatedPhone = "9876543210";
        String updatedAddress = "Updated Campus Hostel Block A";

        CollegeStudentController controller = new CollegeStudentController();
        controller.init();

        Map<String, Object> sessionAttrs = new HashMap<>();
        User collegeUser = new User();
        collegeUser.setId(10);
        collegeUser.setUsername("COL101");
        collegeUser.setRole("COLLEGE");
        collegeUser.setCollegeId(studentCollegeId);
        collegeUser.setRelatedId(studentCollegeId);
        sessionAttrs.put("CURRENT_USER", collegeUser);

        Map<String, String> params = new HashMap<>();
        params.put("action", "edit");
        params.put("id", String.valueOf(s1.getId()));
        params.put("full_name", s1.getFullName());
        params.put("email", s1.getEmail());
        params.put("phone", updatedPhone);
        params.put("dob", s1.getDob() != null ? s1.getDob().toString() : "2004-05-15");
        params.put("gender", s1.getGender());
        params.put("blood_group", s1.getBloodGroup());
        params.put("father_name", s1.getFatherName());
        params.put("mother_name", s1.getMotherName());
        params.put("address", updatedAddress);
        params.put("department_id", String.valueOf(s1.getDepartmentId()));
        params.put("course_id", String.valueOf(s1.getCourseId()));
        params.put("semester_id", String.valueOf(s1.getCurrentSemesterId()));
        params.put("session_id", String.valueOf(s1.getSessionId()));
        params.put("status", s1.getStatus());

        final String[] redirected = new String[1];
        HttpServletRequest req = createMockRequest(params, sessionAttrs);
        HttpServletResponse resp = createMockResponse(redirected);

        Method doPost = CollegeStudentController.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(controller, req, resp);

        assertCondition(redirected[0] != null && redirected[0].contains("msg=Student+details+updated+successfully."),
                "College successfully updated own student (Redirected to: " + redirected[0] + ")");

        // Verify update in database
        Student reloaded = studentDAO.getStudentById(1);
        assertCondition(reloaded != null && updatedPhone.equals(reloaded.getPhone()) && updatedAddress.equals(reloaded.getAddress()),
                "Database record updated with new phone and address");

        // Restore original values
        reloaded.setPhone(originalPhone);
        reloaded.setAddress(originalAddress);
        studentDAO.updateStudent(reloaded);
        System.out.println("    [OK] Restored original student record values.");
    }

    private static void testCrossCollegeEditRejection() throws Exception {
        System.out.println("\n[2] Testing Cross-College Unauthorized Edit Rejection...");
        StudentDAO studentDAO = new StudentDAO();
        Student s1 = studentDAO.getStudentById(1);

        if (s1 == null) {
            System.out.println("    [SKIP] Student ID 1 not found in database.");
            return;
        }

        // College ID 999 is different from s1.getCollegeId()
        int unauthorizedCollegeId = s1.getCollegeId() + 999;
        String originalAddress = s1.getAddress();

        CollegeStudentController controller = new CollegeStudentController();
        controller.init();

        Map<String, Object> sessionAttrs = new HashMap<>();
        User unauthorizedCollegeUser = new User();
        unauthorizedCollegeUser.setId(99);
        unauthorizedCollegeUser.setUsername("COL_OTHER");
        unauthorizedCollegeUser.setRole("COLLEGE");
        unauthorizedCollegeUser.setCollegeId(unauthorizedCollegeId);
        unauthorizedCollegeUser.setRelatedId(unauthorizedCollegeId);
        sessionAttrs.put("CURRENT_USER", unauthorizedCollegeUser);

        Map<String, String> params = new HashMap<>();
        params.put("action", "edit");
        params.put("id", String.valueOf(s1.getId()));
        params.put("full_name", "Hacked Student Name");
        params.put("email", s1.getEmail());
        params.put("phone", "0000000000");
        params.put("address", "Hacked Address");

        final String[] redirected = new String[1];
        HttpServletRequest req = createMockRequest(params, sessionAttrs);
        HttpServletResponse resp = createMockResponse(redirected);

        Method doPost = CollegeStudentController.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(controller, req, resp);

        assertCondition(redirected[0] != null && redirected[0].contains("error=Unauthorized"),
                "Unauthorized college edit rejected with error (Redirected to: " + redirected[0] + ")");

        // Verify database was NOT changed
        Student reloaded = studentDAO.getStudentById(1);
        assertCondition(reloaded != null && reloaded.getAddress().equals(originalAddress) && !reloaded.getFullName().equals("Hacked Student Name"),
                "Database record remained untouched after unauthorized attempt");
    }

    private static void testNonExistentStudentEdit() throws Exception {
        System.out.println("\n[3] Testing Non-existent Student Edit Handling...");
        CollegeStudentController controller = new CollegeStudentController();
        controller.init();

        Map<String, Object> sessionAttrs = new HashMap<>();
        User collegeUser = new User();
        collegeUser.setId(10);
        collegeUser.setRole("COLLEGE");
        collegeUser.setCollegeId(1);
        sessionAttrs.put("CURRENT_USER", collegeUser);

        Map<String, String> params = new HashMap<>();
        params.put("action", "edit");
        params.put("id", "999999");
        params.put("full_name", "Non Existent");
        params.put("email", "nonexistent@apex.edu");

        final String[] redirected = new String[1];
        HttpServletRequest req = createMockRequest(params, sessionAttrs);
        HttpServletResponse resp = createMockResponse(redirected);

        Method doPost = CollegeStudentController.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(controller, req, resp);

        assertCondition(redirected[0] != null && redirected[0].contains("error=Student+record+not+found."),
                "Non-existent student ID rejected with not found error");
    }

    private static void testInvalidStudentId() throws Exception {
        System.out.println("\n[4] Testing Malformed Student ID Handling...");
        CollegeStudentController controller = new CollegeStudentController();
        controller.init();

        Map<String, Object> sessionAttrs = new HashMap<>();
        User collegeUser = new User();
        collegeUser.setId(10);
        collegeUser.setRole("COLLEGE");
        collegeUser.setCollegeId(1);
        sessionAttrs.put("CURRENT_USER", collegeUser);

        Map<String, String> params = new HashMap<>();
        params.put("action", "edit");
        params.put("id", "abc_invalid_id");

        final String[] redirected = new String[1];
        HttpServletRequest req = createMockRequest(params, sessionAttrs);
        HttpServletResponse resp = createMockResponse(redirected);

        Method doPost = CollegeStudentController.class.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
        doPost.setAccessible(true);
        doPost.invoke(controller, req, resp);

        assertCondition(redirected[0] != null && redirected[0].contains("error=Invalid+student+ID"),
                "Malformed student ID rejected with invalid ID error");
    }

    private static void assertCondition(boolean condition, String description) {
        if (condition) {
            testsPassed++;
            System.out.println("    [PASS] " + description);
        } else {
            testsFailed++;
            System.err.println("    [FAIL] " + description);
        }
    }

    @SuppressWarnings("unchecked")
    private static HttpServletRequest createMockRequest(Map<String, String> params, Map<String, Object> sessionAttrs) {
        final HttpSession session = (HttpSession) Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(),
                new Class<?>[]{HttpSession.class},
                (proxy, method, mArgs) -> {
                    if ("getAttribute".equals(method.getName())) {
                        return sessionAttrs.get(mArgs[0]);
                    }
                    if ("setAttribute".equals(method.getName())) {
                        sessionAttrs.put((String) mArgs[0], mArgs[1]);
                        return null;
                    }
                    return null;
                }
        );

        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, mArgs) -> {
                    if ("getParameter".equals(method.getName())) {
                        return params.get(mArgs[0]);
                    }
                    if ("getSession".equals(method.getName())) {
                        return session;
                    }
                    if ("getContextPath".equals(method.getName())) {
                        return "/apex-university";
                    }
                    if ("getRemoteAddr".equals(method.getName())) {
                        return "127.0.0.1";
                    }
                    return null;
                }
        );
    }

    private static HttpServletResponse createMockResponse(String[] redirectedOut) {
        return (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class<?>[]{HttpServletResponse.class},
                (proxy, method, mArgs) -> {
                    if ("sendRedirect".equals(method.getName())) {
                        redirectedOut[0] = (String) mArgs[0];
                        return null;
                    }
                    return null;
                }
        );
    }
}
