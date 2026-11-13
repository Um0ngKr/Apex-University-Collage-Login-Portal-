package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.Subject;
import com.apex.model.Teacher;
import com.apex.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeacherDAO {
    private static final Logger LOGGER = Logger.getLogger(TeacherDAO.class.getName());

    public List<Teacher> getAllTeachers(Integer departmentId, String search) {
        List<Teacher> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT t.*, d.name AS dept_name " +
            "FROM teachers t " +
            "LEFT JOIN departments d ON t.department_id = d.id " +
            "WHERE 1=1 "
        );
        if (departmentId != null && departmentId > 0) {
            sql.append(" AND t.department_id = ? ");
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (t.full_name LIKE ? OR t.employee_id LIKE ? OR t.email LIKE ?) ");
        }
        sql.append(" ORDER BY t.id DESC");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            if (departmentId != null && departmentId > 0) {
                ps.setInt(idx++, departmentId);
            }
            if (search != null && !search.trim().isEmpty()) {
                String term = "%" + search.trim() + "%";
                ps.setString(idx++, term);
                ps.setString(idx++, term);
                ps.setString(idx++, term);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractTeacher(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching teachers", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public Teacher getTeacherById(int id) {
        String sql = "SELECT t.*, d.name AS dept_name FROM teachers t " +
                     "LEFT JOIN departments d ON t.department_id = d.id WHERE t.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractTeacher(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching teacher: " + id, e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    public Teacher getTeacherByEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT t.*, d.name AS dept_name FROM teachers t " +
                     "LEFT JOIN departments d ON t.department_id = d.id WHERE t.employee_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, employeeId.trim());
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractTeacher(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching teacher by employeeId: " + employeeId, e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    public boolean addTeacher(Teacher t) {
        String sql = "INSERT INTO teachers (employee_id, full_name, email, phone, designation, department_id, qualification, password_hash, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getEmployeeId());
            ps.setString(2, t.getFullName());
            ps.setString(3, t.getEmail());
            ps.setString(4, t.getPhone());
            ps.setString(5, t.getDesignation());
            ps.setInt(6, t.getDepartmentId());
            ps.setString(7, t.getQualification());
            String hash = t.getPasswordHash() != null && !t.getPasswordHash().isEmpty()
                    ? t.getPasswordHash() : PasswordUtil.hashPassword("teacher123");
            ps.setString(8, hash);
            ps.setString(9, t.getStatus() != null ? t.getStatus() : "ACTIVE");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting teacher", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updateTeacher(Teacher t) {
        String sql = "UPDATE teachers SET employee_id = ?, full_name = ?, email = ?, phone = ?, designation = ?, " +
                     "department_id = ?, qualification = ?, status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getEmployeeId());
            ps.setString(2, t.getFullName());
            ps.setString(3, t.getEmail());
            ps.setString(4, t.getPhone());
            ps.setString(5, t.getDesignation());
            ps.setInt(6, t.getDepartmentId());
            ps.setString(7, t.getQualification());
            ps.setString(8, t.getStatus());
            ps.setInt(9, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating teacher", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean deleteTeacher(int id) {
        String sql = "DELETE FROM teachers WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting teacher", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public List<Map<String, Object>> getAssignedSubjects(int teacherId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT ta.id AS assignment_id, ta.teacher_id, ta.section, ta.course_id, ta.semester_id, ta.session_id, " +
                     "c.name AS course_name, c.course_code, sem.semester_number, sem.semester_name, " +
                     "sub.id AS subject_id, sub.subject_code, sub.name AS subject_name, sub.credit, " +
                     "ses.session_code, ses.is_current " +
                     "FROM teacher_assignments ta " +
                     "JOIN courses c ON ta.course_id = c.id " +
                     "JOIN semesters sem ON ta.semester_id = sem.id " +
                     "JOIN subjects sub ON ta.subject_id = sub.id " +
                     "JOIN academic_sessions ses ON ta.session_id = ses.id " +
                     "WHERE ta.teacher_id = ? " +
                     "ORDER BY sem.semester_number, sub.subject_code";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, teacherId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("assignmentId", rs.getInt("assignment_id"));
                map.put("teacherId", rs.getInt("teacher_id"));
                map.put("section", rs.getString("section"));
                map.put("courseId", rs.getInt("course_id"));
                map.put("courseName", rs.getString("course_name"));
                map.put("courseCode", rs.getString("course_code"));
                map.put("semesterId", rs.getInt("semester_id"));
                map.put("semesterNumber", rs.getInt("semester_number"));
                map.put("semesterName", rs.getString("semester_name"));
                map.put("subjectId", rs.getInt("subject_id"));
                map.put("subjectCode", rs.getString("subject_code"));
                map.put("subjectName", rs.getString("subject_name"));
                map.put("credit", rs.getDouble("credit"));
                map.put("sessionId", rs.getInt("session_id"));
                map.put("sessionCode", rs.getString("session_code"));
                map.put("isCurrent", rs.getBoolean("is_current"));
                list.add(map);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching assigned subjects for teacher: " + teacherId, e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public List<Map<String, Object>> getAssignedSubjectsByEmployeeId(String employeeId) {
        Teacher t = getTeacherByEmployeeId(employeeId);
        if (t != null) {
            return getAssignedSubjects(t.getId());
        }
        return new ArrayList<>();
    }

    public boolean isSubjectAssignedToTeacher(int teacherId, int subjectId) {
        String sql = "SELECT 1 FROM teacher_assignments WHERE teacher_id = ? AND subject_id = ? LIMIT 1";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, teacherId);
            ps.setInt(2, subjectId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking assignment for teacher: " + teacherId + ", subject: " + subjectId, e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return false;
    }

    public Map<String, Object> getAssignment(int teacherId, int subjectId) {
        List<Map<String, Object>> assignments = getAssignedSubjects(teacherId);
        for (Map<String, Object> asg : assignments) {
            if (asg.get("subjectId") != null && ((Integer) asg.get("subjectId")) == subjectId) {
                return asg;
            }
        }
        return null;
    }

    public boolean assignSubject(int teacherId, int courseId, int semesterId, int subjectId, int sessionId, String section) {
        String sql = "INSERT INTO teacher_assignments (teacher_id, course_id, semester_id, subject_id, session_id, section) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, teacherId);
            ps.setInt(2, courseId);
            ps.setInt(3, semesterId);
            ps.setInt(4, subjectId);
            ps.setInt(5, sessionId);
            ps.setString(6, section != null ? section : "A");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error assigning subject to teacher", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean removeAssignment(int assignmentId) {
        String sql = "DELETE FROM teacher_assignments WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, assignmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting assignment", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    private Teacher extractTeacher(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setId(rs.getInt("id"));
        t.setEmployeeId(rs.getString("employee_id"));
        t.setFullName(rs.getString("full_name"));
        t.setEmail(rs.getString("email"));
        t.setPhone(rs.getString("phone"));
        t.setDesignation(rs.getString("designation"));
        t.setDepartmentId(rs.getInt("department_id"));
        t.setDepartmentName(rs.getString("dept_name"));
        t.setQualification(rs.getString("qualification"));
        t.setStatus(rs.getString("status"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        return t;
    }
}
