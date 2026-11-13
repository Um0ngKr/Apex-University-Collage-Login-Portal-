package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.User;
import com.apex.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Universal Authentication & Account Management DAO.
 * Strictly verifies accounts against the database using BCrypt hashing.
 * Provides granular authentication status diagnosis without plaintext password leaks.
 */
public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    /**
     * Authentication result wrapper with detailed status classification.
     */
    public static class AuthenticationResult {
        public enum Status {
            SUCCESS,
            ACCOUNT_NOT_FOUND,
            ACCOUNT_INACTIVE,
            INVALID_CREDENTIALS,
            DATABASE_ERROR,
            INVALID_ROLE
        }

        private final Status status;
        private final User user;
        private final String message;

        public AuthenticationResult(Status status, String message) {
            this(status, null, message);
        }

        public AuthenticationResult(Status status, User user) {
            this(status, user, status.name());
        }

        public AuthenticationResult(Status status, User user, String message) {
            this.status = status;
            this.user = user;
            this.message = message;
        }

        public Status getStatus() {
            return status;
        }

        public User getUser() {
            return user;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return status == Status.SUCCESS && user != null;
        }
    }

    /**
     * Authenticates credentials across roles: ADMIN, TEACHER, STUDENT, COLLEGE
     * returning detailed status (SUCCESS, ACCOUNT_NOT_FOUND, ACCOUNT_INACTIVE, INVALID_CREDENTIALS, DATABASE_ERROR, INVALID_ROLE).
     */
    public AuthenticationResult authenticateWithResult(String identifier, String password, String expectedRole) {
        if (identifier == null || identifier.trim().isEmpty() || password == null || password.isEmpty()) {
            return new AuthenticationResult(AuthenticationResult.Status.INVALID_CREDENTIALS, "Identifier and password required");
        }
        if (expectedRole == null || expectedRole.trim().isEmpty()) {
            return new AuthenticationResult(AuthenticationResult.Status.INVALID_ROLE, "Portal role not specified");
        }

        identifier = identifier.trim();
        String roleUpper = expectedRole.trim().toUpperCase();

        switch (roleUpper) {
            case "ADMIN":
                return authenticateAdminResult(identifier, password);
            case "TEACHER":
                return authenticateTeacherResult(identifier, password);
            case "STUDENT":
                return authenticateStudentResult(identifier, password);
            case "COLLEGE":
                return authenticateCollegeResult(identifier, password);
            default:
                LOGGER.warning("Authentication failed: invalid portal role requested: " + expectedRole);
                return new AuthenticationResult(AuthenticationResult.Status.INVALID_ROLE, "Invalid role: " + expectedRole);
        }
    }

    /**
     * Standard authentication returning User or null for backwards compatibility.
     */
    public User authenticate(String identifier, String password, String expectedRole) {
        AuthenticationResult result = authenticateWithResult(identifier, password, expectedRole);
        return (result != null && result.isSuccess()) ? result.getUser() : null;
    }

    private AuthenticationResult authenticateAdminResult(String usernameOrEmail, String password) {
        String sql = "SELECT id, username, password_hash, full_name, email, role, status FROM admins WHERE (username = ? OR email = ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, usernameOrEmail);
            ps.setString(2, usernameOrEmail);
            rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                if (!"ACTIVE".equalsIgnoreCase(status)) {
                    LOGGER.warning("Admin authentication failed: account inactive for identifier '" + usernameOrEmail + "'");
                    return new AuthenticationResult(AuthenticationResult.Status.ACCOUNT_INACTIVE, "Admin account is inactive");
                }

                String hash = rs.getString("password_hash");
                if (PasswordUtil.checkPassword(password, hash)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setRelatedId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setRole("ADMIN");
                    user.setStatus(status);
                    LOGGER.info("Admin authentication successful for user: " + user.getUsername());
                    return new AuthenticationResult(AuthenticationResult.Status.SUCCESS, user);
                } else {
                    LOGGER.warning("Admin authentication failed: password mismatch for identifier '" + usernameOrEmail + "'");
                    return new AuthenticationResult(AuthenticationResult.Status.INVALID_CREDENTIALS, "Invalid password");
                }
            } else {
                LOGGER.warning("Admin authentication failed: account not found for identifier '" + usernameOrEmail + "'");
                return new AuthenticationResult(AuthenticationResult.Status.ACCOUNT_NOT_FOUND, "Admin account not found");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error during Admin authentication for '" + usernameOrEmail + "'", e);
            return new AuthenticationResult(AuthenticationResult.Status.DATABASE_ERROR, "Database connection failure: " + e.getMessage());
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
    }

    private AuthenticationResult authenticateTeacherResult(String empIdOrEmail, String password) {
        String sql = "SELECT t.id, t.employee_id, t.password_hash, t.full_name, t.email, t.designation, t.department_id, t.status FROM teachers t WHERE (t.employee_id = ? OR t.email = ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, empIdOrEmail);
            ps.setString(2, empIdOrEmail);
            rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                if (!"ACTIVE".equalsIgnoreCase(status)) {
                    LOGGER.warning("Teacher authentication failed: account inactive for identifier '" + empIdOrEmail + "'");
                    return new AuthenticationResult(AuthenticationResult.Status.ACCOUNT_INACTIVE, "Teacher account is inactive");
                }

                String hash = rs.getString("password_hash");
                if (PasswordUtil.checkPassword(password, hash)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setRelatedId(rs.getInt("id"));
                    user.setUsername(rs.getString("employee_id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setDesignation(rs.getString("designation"));
                    user.setDepartmentId(rs.getInt("department_id"));
                    user.setRole("TEACHER");
                    user.setStatus(status);
                    LOGGER.info("Teacher authentication successful for user: " + user.getUsername());
                    return new AuthenticationResult(AuthenticationResult.Status.SUCCESS, user);
                } else {
                    LOGGER.warning("Teacher authentication failed: password mismatch for identifier '" + empIdOrEmail + "'");
                    return new AuthenticationResult(AuthenticationResult.Status.INVALID_CREDENTIALS, "Invalid password");
                }
            } else {
                LOGGER.warning("Teacher authentication failed: account not found for identifier '" + empIdOrEmail + "'");
                return new AuthenticationResult(AuthenticationResult.Status.ACCOUNT_NOT_FOUND, "Teacher account not found");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error during Teacher authentication for '" + empIdOrEmail + "'", e);
            return new AuthenticationResult(AuthenticationResult.Status.DATABASE_ERROR, "Database connection failure: " + e.getMessage());
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
    }

    private AuthenticationResult authenticateStudentResult(String regOrRollOrEmail, String password) {
        String sql = "SELECT s.id, s.registration_no, s.roll_no, s.password_hash, s.full_name, s.email, s.college_id, s.department_id, s.course_id, s.current_semester_id, s.status FROM students s WHERE (s.registration_no = ? OR s.roll_no = ? OR s.email = ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, regOrRollOrEmail);
            ps.setString(2, regOrRollOrEmail);
            ps.setString(3, regOrRollOrEmail);
            rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                if (!"ACTIVE".equalsIgnoreCase(status)) {
                    LOGGER.warning("Student authentication failed: account inactive for identifier '" + regOrRollOrEmail + "'");
                    return new AuthenticationResult(AuthenticationResult.Status.ACCOUNT_INACTIVE, "Student account is inactive");
                }

                String hash = rs.getString("password_hash");
                if (PasswordUtil.checkPassword(password, hash)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setRelatedId(rs.getInt("id"));
                    user.setUsername(rs.getString("registration_no"));
                    user.setRegistrationNo(rs.getString("registration_no"));
                    user.setRollNo(rs.getString("roll_no"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setCollegeId(rs.getInt("college_id"));
                    user.setDepartmentId(rs.getInt("department_id"));
                    user.setCourseId(rs.getInt("course_id"));
                    user.setSemesterId(rs.getInt("current_semester_id"));
                    user.setRole("STUDENT");
                    user.setStatus(status);
                    LOGGER.info("Student authentication successful for user: " + user.getUsername());
                    return new AuthenticationResult(AuthenticationResult.Status.SUCCESS, user);
                } else {
                    LOGGER.warning("Student authentication failed: password mismatch for identifier '" + regOrRollOrEmail + "'");
                    return new AuthenticationResult(AuthenticationResult.Status.INVALID_CREDENTIALS, "Invalid password");
                }
            } else {
                LOGGER.warning("Student authentication failed: account not found for identifier '" + regOrRollOrEmail + "'");
                return new AuthenticationResult(AuthenticationResult.Status.ACCOUNT_NOT_FOUND, "Student account not found");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error during Student authentication for '" + regOrRollOrEmail + "'", e);
            return new AuthenticationResult(AuthenticationResult.Status.DATABASE_ERROR, "Database connection failure: " + e.getMessage());
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
    }

    private AuthenticationResult authenticateCollegeResult(String codeOrEmail, String password) {
        String sql = "SELECT id, college_code, name, email, password_hash, status FROM colleges WHERE (college_code = ? OR email = ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, codeOrEmail);
            ps.setString(2, codeOrEmail);
            rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                if (!"ACTIVE".equalsIgnoreCase(status)) {
                    LOGGER.warning("College authentication failed: account inactive for identifier '" + codeOrEmail + "'");
                    return new AuthenticationResult(AuthenticationResult.Status.ACCOUNT_INACTIVE, "College account is inactive");
                }

                String hash = rs.getString("password_hash");
                if (PasswordUtil.checkPassword(password, hash)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setRelatedId(rs.getInt("id"));
                    user.setUsername(rs.getString("college_code"));
                    user.setCollegeCode(rs.getString("college_code"));
                    user.setCollegeId(rs.getInt("id"));
                    user.setFullName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setRole("COLLEGE");
                    user.setStatus(status);
                    LOGGER.info("College authentication successful for user: " + user.getUsername());
                    return new AuthenticationResult(AuthenticationResult.Status.SUCCESS, user);
                } else {
                    LOGGER.warning("College authentication failed: password mismatch for identifier '" + codeOrEmail + "'");
                    return new AuthenticationResult(AuthenticationResult.Status.INVALID_CREDENTIALS, "Invalid password");
                }
            } else {
                LOGGER.warning("College authentication failed: account not found for identifier '" + codeOrEmail + "'");
                return new AuthenticationResult(AuthenticationResult.Status.ACCOUNT_NOT_FOUND, "College account not found");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error during College authentication for '" + codeOrEmail + "'", e);
            return new AuthenticationResult(AuthenticationResult.Status.DATABASE_ERROR, "Database connection failure: " + e.getMessage());
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
    }

    /**
     * Records login history in audit trail.
     */
    public void recordLogin(String userType, Integer userId, String username, String ip, String userAgent, String status) {
        String sql = "INSERT INTO login_history (user_type, user_id, username, ip_address, user_agent, status) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, userType);
            if (userId != null) ps.setInt(2, userId); else ps.setNull(2, java.sql.Types.INTEGER);
            ps.setString(3, username);
            ps.setString(4, ip != null ? ip : "127.0.0.1");
            ps.setString(5, userAgent);
            ps.setString(6, status);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to record login history", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
    }

    /**
     * Changes password for any role.
     */
    public boolean changePassword(String role, int userId, String oldPassword, String newPassword) {
        String table;
        switch (role.toUpperCase()) {
            case "ADMIN": table = "admins"; break;
            case "TEACHER": table = "teachers"; break;
            case "STUDENT": table = "students"; break;
            case "COLLEGE": table = "colleges"; break;
            default: return false;
        }

        String fetchSql = "SELECT password_hash FROM " + table + " WHERE id = ?";
        String updateSql = "UPDATE " + table + " SET password_hash = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement psFetch = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            psFetch = conn.prepareStatement(fetchSql);
            psFetch.setInt(1, userId);
            rs = psFetch.executeQuery();
            if (rs.next()) {
                String currentHash = rs.getString("password_hash");
                if (PasswordUtil.checkPassword(oldPassword, currentHash)) {
                    psUpdate = conn.prepareStatement(updateSql);
                    psUpdate.setString(1, PasswordUtil.hashPassword(newPassword));
                    psUpdate.setInt(2, userId);
                    return psUpdate.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error changing password for " + role, e);
        } finally {
            DBConnectionManager.close(null, psFetch, rs);
            DBConnectionManager.close(conn, psUpdate);
        }
        return false;
    }
}
