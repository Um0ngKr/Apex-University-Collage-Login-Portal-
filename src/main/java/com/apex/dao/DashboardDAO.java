package com.apex.dao;

import com.apex.config.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashboardDAO {
    private static final Logger LOGGER = Logger.getLogger(DashboardDAO.class.getName());

    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        String[] queries = {
            "SELECT COUNT(*) FROM students",
            "SELECT COUNT(*) FROM teachers",
            "SELECT COUNT(*) FROM colleges",
            "SELECT COUNT(*) FROM courses",
            "SELECT COUNT(*) FROM examinations",
            "SELECT COUNT(*) FROM results WHERE is_published = 1",
            "SELECT COUNT(*) FROM payments WHERE payment_status = 'PENDING'",
            "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE payment_status = 'SUCCESS'"
        };
        String[] keys = {
            "totalStudents", "totalTeachers", "totalColleges", "totalCourses",
            "totalExams", "publishedResults", "pendingPayments", "totalCollections"
        };

        Connection conn = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            for (int i = 0; i < queries.length; i++) {
                try (PreparedStatement ps = conn.prepareStatement(queries[i]);
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (i == 7) {
                            stats.put(keys[i], rs.getDouble(1));
                        } else {
                            stats.put(keys[i], rs.getInt(1));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching admin stats", e);
        } finally {
            DBConnectionManager.close(conn, null);
        }
        return stats;
    }

    public Map<String, Object> getTeacherStats(int teacherId) {
        Map<String, Object> stats = new HashMap<>();
        String sqlSubjects = "SELECT COUNT(*) FROM teacher_assignments WHERE teacher_id = ?";
        String sqlMarks = "SELECT COUNT(*) FROM marks WHERE evaluated_by_teacher_id = ?";
        String sqlAtt = "SELECT COUNT(DISTINCT class_date) FROM attendance WHERE marked_by_teacher_id = ?";

        Connection conn = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sqlSubjects)) {
                ps.setInt(1, teacherId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.put("assignedSubjects", rs.getInt(1));
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlMarks)) {
                ps.setInt(1, teacherId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.put("evaluatedMarks", rs.getInt(1));
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlAtt)) {
                ps.setInt(1, teacherId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.put("classesTaken", rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching teacher stats", e);
        } finally {
            DBConnectionManager.close(conn, null);
        }
        return stats;
    }

    public Map<String, Object> getCollegeStats(int collegeId) {
        Map<String, Object> stats = new HashMap<>();
        String sqlStudents = "SELECT COUNT(*) FROM students WHERE college_id = ?";
        String sqlFormsPending = "SELECT COUNT(*) FROM exam_forms WHERE college_id = ? AND college_approval_status = 'PENDING'";
        String sqlFormsApproved = "SELECT COUNT(*) FROM exam_forms WHERE college_id = ? AND college_approval_status = 'APPROVED'";

        Connection conn = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sqlStudents)) {
                ps.setInt(1, collegeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.put("totalStudents", rs.getInt(1));
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlFormsPending)) {
                ps.setInt(1, collegeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.put("pendingForms", rs.getInt(1));
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlFormsApproved)) {
                ps.setInt(1, collegeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.put("approvedForms", rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching college stats", e);
        } finally {
            DBConnectionManager.close(conn, null);
        }
        return stats;
    }

    public Map<String, Object> getStudentStats(int studentId) {
        Map<String, Object> stats = new HashMap<>();
        String sqlForms = "SELECT COUNT(*) FROM exam_forms WHERE student_id = ?";
        String sqlPayments = "SELECT COUNT(*) FROM payments WHERE student_id = ? AND payment_status = 'SUCCESS'";
        String sqlResults = "SELECT COUNT(*) FROM results WHERE student_id = ? AND is_published = 1";

        Connection conn = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sqlForms)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.put("submittedForms", rs.getInt(1));
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlPayments)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.put("successfulPayments", rs.getInt(1));
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlResults)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) stats.put("publishedResults", rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student stats", e);
        } finally {
            DBConnectionManager.close(conn, null);
        }
        return stats;
    }
}
