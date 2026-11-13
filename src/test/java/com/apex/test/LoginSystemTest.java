package com.apex.test;

import com.apex.config.DBConnectionManager;
import com.apex.dao.UserDAO;
import com.apex.dao.UserDAO.AuthenticationResult;
import com.apex.model.User;
import com.apex.util.CsrfUtil;
import com.apex.util.PasswordUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Automated Verification Harness for Apex University Login & Authentication System.
 * Tests all 4 roles (Admin, Teacher, Student, College) with real database queries,
 * BCrypt password verification, inactive account checks, incorrect credential checks,
 * role boundary checks, CSRF validation, database failure handling, and dashboard redirection logic.
 */
public class LoginSystemTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("     APEX UNIVERSITY AUTHENTICATION & LOGIN SYSTEM VERIFICATION SUITE           ");
        System.out.println("================================================================================");

        boolean testDbDownMode = args.length > 0 && "testDbDown".equals(args[0]);

        if (testDbDownMode) {
            testDatabaseUnavailableMode();
            return;
        }

        testBcryptDirect();
        testCsrfValidation();
        testDatabaseConnection();

        UserDAO userDAO = new UserDAO();

        // 1. All 4 Roles - Successful Database Authentication
        System.out.println("\n[3] Testing Successful Authentication Across All 4 Roles...");
        testAuth(userDAO, "ADMIN", "admin", "admin123", AuthenticationResult.Status.SUCCESS, "Admin login by username");
        testAuth(userDAO, "ADMIN", "umangkr871170@gmail.com", "admin123", AuthenticationResult.Status.SUCCESS, "Admin login by email");
        testAuth(userDAO, "TEACHER", "TCH101", "teacher123", AuthenticationResult.Status.SUCCESS, "Teacher login by employee_id");
        testAuth(userDAO, "TEACHER", "rajesh.sharma@apex.edu", "teacher123", AuthenticationResult.Status.SUCCESS, "Teacher login by email");
        testAuth(userDAO, "STUDENT", "APEX202200101", "student123", AuthenticationResult.Status.SUCCESS, "Student login by registration_no");
        testAuth(userDAO, "STUDENT", "11500122001", "student123", AuthenticationResult.Status.SUCCESS, "Student login by roll_no");
        testAuth(userDAO, "STUDENT", "umangkr871170@gmail.com", "student123", AuthenticationResult.Status.SUCCESS, "Student login by email");
        testAuth(userDAO, "COLLEGE", "COL101", "college123", AuthenticationResult.Status.SUCCESS, "College login by college_code");
        testAuth(userDAO, "COLLEGE", "info@apextech.edu.in", "college123", AuthenticationResult.Status.SUCCESS, "College login by email");

        // 2. Incorrect Password Rejection
        System.out.println("\n[4] Testing Incorrect Password Rejection (Secure Failure)...");
        testAuth(userDAO, "ADMIN", "admin", "wrongPassword!", AuthenticationResult.Status.INVALID_CREDENTIALS, "Admin incorrect password rejected");
        testAuth(userDAO, "TEACHER", "TCH101", "wrongPassword!", AuthenticationResult.Status.INVALID_CREDENTIALS, "Teacher incorrect password rejected");
        testAuth(userDAO, "STUDENT", "APEX202200101", "wrongPassword!", AuthenticationResult.Status.INVALID_CREDENTIALS, "Student incorrect password rejected");
        testAuth(userDAO, "COLLEGE", "COL101", "wrongPassword!", AuthenticationResult.Status.INVALID_CREDENTIALS, "College incorrect password rejected");

        // 3. Incorrect Identifier / Non-existent User
        System.out.println("\n[5] Testing Non-existent User ID Rejection...");
        testAuth(userDAO, "ADMIN", "nonexistent_admin_xyz", "admin123", AuthenticationResult.Status.ACCOUNT_NOT_FOUND, "Non-existent Admin ID rejected");
        testAuth(userDAO, "TEACHER", "TCH999_UNKNOWN", "teacher123", AuthenticationResult.Status.ACCOUNT_NOT_FOUND, "Non-existent Teacher ID rejected");
        testAuth(userDAO, "STUDENT", "APEX99999999", "student123", AuthenticationResult.Status.ACCOUNT_NOT_FOUND, "Non-existent Student Reg rejected");
        testAuth(userDAO, "COLLEGE", "COL999_UNKNOWN", "college123", AuthenticationResult.Status.ACCOUNT_NOT_FOUND, "Non-existent College Code rejected");

        // 4. Role Cross-Login Isolation (Wrong Selected Role)
        System.out.println("\n[6] Testing Cross-Role Isolation (Wrong Selected Role)...");
        testAuth(userDAO, "STUDENT", "admin", "admin123", AuthenticationResult.Status.ACCOUNT_NOT_FOUND, "Admin credentials on Student portal rejected");
        testAuth(userDAO, "ADMIN", "APEX202200101", "student123", AuthenticationResult.Status.ACCOUNT_NOT_FOUND, "Student credentials on Admin portal rejected");
        testAuth(userDAO, "COLLEGE", "TCH101", "teacher123", AuthenticationResult.Status.ACCOUNT_NOT_FOUND, "Teacher credentials on College portal rejected");
        testAuth(userDAO, "TEACHER", "COL101", "college123", AuthenticationResult.Status.ACCOUNT_NOT_FOUND, "College credentials on Teacher portal rejected");

        // 5. Inactive Accounts Rejection
        System.out.println("\n[7] Testing Inactive Account Rejection...");
        testAuth(userDAO, "ADMIN", "admin_inactive", "admin123", AuthenticationResult.Status.ACCOUNT_INACTIVE, "Inactive Admin account blocked");
        testAuth(userDAO, "TEACHER", "TCH_INACTIVE", "teacher123", AuthenticationResult.Status.ACCOUNT_INACTIVE, "Inactive Teacher account blocked");
        testAuth(userDAO, "STUDENT", "APEX_INACTIVE", "student123", AuthenticationResult.Status.ACCOUNT_INACTIVE, "Inactive Student account blocked");
        testAuth(userDAO, "COLLEGE", "COL_INACTIVE", "college123", AuthenticationResult.Status.ACCOUNT_INACTIVE, "Inactive College account blocked");

        // 6. Invalid Portal Role
        System.out.println("\n[8] Testing Invalid Portal Role Parameter...");
        testAuth(userDAO, "SUPER_ADMIN", "admin", "admin123", AuthenticationResult.Status.INVALID_ROLE, "Invalid role parameter handled");

        // 7. Dashboard Redirection Verification
        System.out.println("\n[9] Verifying Role Dashboard Redirection Mapping...");
        testDashboardRedirection(userDAO);

        printSummaryReport();
    }

    private static void testBcryptDirect() {
        System.out.println("\n[1] Verifying BCrypt Password Compatibility (jBCrypt)...");

        String adminHash = "$2a$12$oDQWHOAgAPYsjI8w0l277.8vy3nYXzwFvBAQwWU4BP8RXKScBlb/G";
        String teacherHash = "$2a$12$msEvlTdgTfMXt7Vr7pBYc.gYWKl.7u/mr7xU0C4rLnHi0liTXtEzG";
        String studentHash = "$2a$12$wNOfl4eTkOfaLrsNAb1r7.iIZOgRPWXbgd1OK7lJl6hZ6kgcBsUWq";
        String collegeHash = "$2a$12$XGYa1caHdrE9hC7X01IW9.USl3we1yTibi.UEU/HTJmCjOFDz9mVC";

        assertCondition(PasswordUtil.checkPassword("admin123", adminHash), "BCrypt checkpw('admin123') matches adminHash");
        assertCondition(PasswordUtil.checkPassword("teacher123", teacherHash), "BCrypt checkpw('teacher123') matches teacherHash");
        assertCondition(PasswordUtil.checkPassword("student123", studentHash), "BCrypt checkpw('student123') matches studentHash");
        assertCondition(PasswordUtil.checkPassword("college123", collegeHash), "BCrypt checkpw('college123') matches collegeHash");

        // Non-matching passwords must return false
        assertCondition(!PasswordUtil.checkPassword("wrongPass", adminHash), "BCrypt checkpw('wrongPass') returns false");
        assertCondition(!PasswordUtil.checkPassword("", adminHash), "BCrypt checkpw('') returns false");
        assertCondition(!PasswordUtil.checkPassword(null, adminHash), "BCrypt checkpw(null) returns false");
    }

    private static void testCsrfValidation() {
        System.out.println("\n[2] Verifying CSRF Token Generation & Protection (CsrfUtil)...");
        try {
            Map<String, Object> sessionAttributes = new HashMap<>();
            HttpSession mockSession = (HttpSession) Proxy.newProxyInstance(
                    HttpSession.class.getClassLoader(),
                    new Class<?>[]{HttpSession.class},
                    (proxy, method, methodArgs) -> {
                        String name = method.getName();
                        if ("getAttribute".equals(name)) {
                            return sessionAttributes.get(methodArgs[0]);
                        } else if ("setAttribute".equals(name)) {
                            sessionAttributes.put((String) methodArgs[0], methodArgs[1]);
                            return null;
                        }
                        return null;
                    }
            );

            // 1. Generate CSRF token
            String token = CsrfUtil.getToken(mockSession);
            assertCondition(token != null && !token.isEmpty() && token.length() >= 24, "CSRF token generated: " + token);

            // 2. Validate matching token via request
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("csrf_token", token);
            HttpServletRequest validRequest = createMockRequest(mockSession, requestParams);
            assertCondition(CsrfUtil.isValid(validRequest), "CsrfUtil.isValid() accepts valid token");

            // 3. Reject invalid token
            requestParams.put("csrf_token", "invalid_forged_token_value_1234");
            HttpServletRequest forgedRequest = createMockRequest(mockSession, requestParams);
            assertCondition(!CsrfUtil.isValid(forgedRequest), "CsrfUtil.isValid() rejects invalid/forged token");

            // 4. Reject missing token
            requestParams.remove("csrf_token");
            HttpServletRequest missingRequest = createMockRequest(mockSession, requestParams);
            assertCondition(!CsrfUtil.isValid(missingRequest), "CsrfUtil.isValid() rejects missing token");

            // 5. Reject request with null session
            HttpServletRequest nullSessionRequest = createMockRequest(null, requestParams);
            assertCondition(!CsrfUtil.isValid(nullSessionRequest), "CsrfUtil.isValid() rejects request without session");

        } catch (Exception e) {
            recordFailure("CSRF validation test failed: " + e.getMessage());
        }
    }

    private static HttpServletRequest createMockRequest(HttpSession session, Map<String, String> params) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, methodArgs) -> {
                    String name = method.getName();
                    if ("getSession".equals(name)) {
                        return session;
                    } else if ("getParameter".equals(name)) {
                        return params.get(methodArgs[0]);
                    } else if ("getHeader".equals(name)) {
                        return null;
                    }
                    return null;
                }
        );
    }

    private static void testDatabaseConnection() {
        System.out.println("\n[*] Verifying MySQL Database Connectivity & Schema Records...");
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            assertCondition(conn != null && !conn.isClosed(), "MySQL Connection established successfully");

            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM admins")) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    assertCondition(count >= 2, "Admins table has seed records (found: " + count + ")");
                }
            }
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM teachers")) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    assertCondition(count >= 4, "Teachers table has seed records (found: " + count + ")");
                }
            }
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students")) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    assertCondition(count >= 5, "Students table has seed records (found: " + count + ")");
                }
            }
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM colleges")) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    assertCondition(count >= 4, "Colleges table has seed records (found: " + count + ")");
                }
            }
        } catch (Exception e) {
            recordFailure("Database connection check threw exception: " + e.getMessage());
        }
    }

    private static void testAuth(UserDAO dao, String role, String identifier, String password, AuthenticationResult.Status expectedStatus, String testDescription) {
        AuthenticationResult result = dao.authenticateWithResult(identifier, password, role);
        boolean statusMatch = (result != null && result.getStatus() == expectedStatus);

        if (statusMatch) {
            if (expectedStatus == AuthenticationResult.Status.SUCCESS) {
                User user = result.getUser();
                boolean userValid = user != null && role.equalsIgnoreCase(user.getRole());
                assertCondition(userValid, testDescription + " [GOT: " + result.getStatus() + ", User=" + (user != null ? user.getUsername() : "null") + "]");
            } else {
                assertCondition(true, testDescription + " [GOT EXPECTED: " + result.getStatus() + "]");
            }
        } else {
            recordFailure(testDescription + " [EXPECTED: " + expectedStatus + ", BUT GOT: " + (result != null ? result.getStatus() : "null") + "]");
        }
    }

    private static void testDashboardRedirection(UserDAO dao) {
        User adminUser = dao.authenticate("admin", "admin123", "ADMIN");
        assertCondition(adminUser != null && getDashboardPath(adminUser).equals("/admin/dashboard"), "Admin redirects to /admin/dashboard");

        User teacherUser = dao.authenticate("TCH101", "teacher123", "TEACHER");
        assertCondition(teacherUser != null && getDashboardPath(teacherUser).equals("/teacher/dashboard"), "Teacher redirects to /teacher/dashboard");

        User studentUser = dao.authenticate("APEX202200101", "student123", "STUDENT");
        assertCondition(studentUser != null && getDashboardPath(studentUser).equals("/student/dashboard"), "Student redirects to /student/dashboard");

        User collegeUser = dao.authenticate("COL101", "college123", "COLLEGE");
        assertCondition(collegeUser != null && getDashboardPath(collegeUser).equals("/college/dashboard"), "College redirects to /college/dashboard");
    }

    private static void testDatabaseUnavailableMode() {
        System.out.println("\n[10] Verifying Failure Behavior When Database Is Unavailable...");
        UserDAO dao = new UserDAO();
        AuthenticationResult result = dao.authenticateWithResult("admin", "admin123", "ADMIN");

        assertCondition(result != null && result.getStatus() == AuthenticationResult.Status.DATABASE_ERROR,
                "When MySQL is offline, returns DATABASE_ERROR (no silent bypass, no hardcoded fallback). Got: " + (result != null ? result.getStatus() : "null"));
        assertCondition(result != null && result.getUser() == null,
                "When MySQL is offline, user is null (no hardcoded mock user returned)");

        printSummaryReport();
    }

    private static String getDashboardPath(User user) {
        if (user.isAdmin()) return "/admin/dashboard";
        if (user.isTeacher()) return "/teacher/dashboard";
        if (user.isCollege()) return "/college/dashboard";
        if (user.isStudent()) return "/student/dashboard";
        return "/";
    }

    private static void assertCondition(boolean condition, String description) {
        if (condition) {
            System.out.println("  [PASS] " + description);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + description);
            testsFailed++;
        }
    }

    private static void recordFailure(String description) {
        System.err.println("  [FAIL] " + description);
        testsFailed++;
    }

    private static void printSummaryReport() {
        System.out.println("================================================================================");
        System.out.println("                         TEST SUMMARY REPORT                                   ");
        System.out.println("================================================================================");
        System.out.println(" TOTAL TESTS EXECUTED: " + (testsPassed + testsFailed));
        System.out.println(" TESTS PASSED        : " + testsPassed);
        System.out.println(" TESTS FAILED        : " + testsFailed);
        System.out.println(" STATUS              : " + (testsFailed == 0 ? "ALL PASS (100% SUCCESS)" : "FAILURES DETECTED"));
        System.out.println("================================================================================");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }
}
