package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.Attendance;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttendanceDAO {
    private static final Logger LOGGER = Logger.getLogger(AttendanceDAO.class.getName());

    public boolean markAttendance(int studentId, int subjectId, int sessionId, Date classDate, String status, Integer teacherId) {
        String sql = "INSERT INTO attendance (student_id, subject_id, session_id, class_date, status, marked_by_teacher_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE status = VALUES(status), marked_by_teacher_id = VALUES(marked_by_teacher_id)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            ps.setInt(3, sessionId);
            ps.setDate(4, classDate);
            ps.setString(5, status);
            if (teacherId != null) ps.setInt(6, teacherId); else ps.setNull(6, Types.INTEGER);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error marking attendance", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public List<Attendance> getAttendanceBySubjectAndDate(int subjectId, Date classDate) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, s.full_name AS student_name, s.registration_no, s.roll_no, " +
                     "sub.subject_code, sub.name AS subject_name, t.full_name AS teacher_name " +
                     "FROM attendance a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN subjects sub ON a.subject_id = sub.id " +
                     "LEFT JOIN teachers t ON a.marked_by_teacher_id = t.id " +
                     "WHERE a.subject_id = ? AND a.class_date = ? ORDER BY s.roll_no ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, subjectId);
            ps.setDate(2, classDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractAttendance(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching attendance by subject and date", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public List<Map<String, Object>> getStudentSubjectWiseSummary(int studentId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT sub.id AS subject_id, sub.subject_code, sub.name AS subject_name, " +
                     "COUNT(a.id) AS total_classes, " +
                     "SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) AS attended_classes " +
                     "FROM subjects sub " +
                     "JOIN students s ON s.course_id = sub.course_id AND s.current_semester_id = sub.semester_id " +
                     "LEFT JOIN attendance a ON a.subject_id = sub.id AND a.student_id = s.id " +
                     "WHERE s.id = ? " +
                     "GROUP BY sub.id, sub.subject_code, sub.name";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("subjectId", rs.getInt("subject_id"));
                map.put("subjectCode", rs.getString("subject_code"));
                map.put("subjectName", rs.getString("subject_name"));
                int total = rs.getInt("total_classes");
                int attended = rs.getInt("attended_classes");
                map.put("totalClasses", total);
                map.put("attendedClasses", attended);
                double pct = total > 0 ? (attended * 100.0) / total : 100.0;
                map.put("percentage", Math.round(pct * 10.0) / 10.0);
                list.add(map);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student attendance summary", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    private Attendance extractAttendance(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getInt("id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setStudentName(rs.getString("student_name"));
        a.setRegistrationNo(rs.getString("registration_no"));
        a.setRollNo(rs.getString("roll_no"));
        a.setSubjectId(rs.getInt("subject_id"));
        a.setSubjectCode(rs.getString("subject_code"));
        a.setSubjectName(rs.getString("subject_name"));
        a.setSessionId(rs.getInt("session_id"));
        a.setClassDate(rs.getDate("class_date"));
        a.setStatus(rs.getString("status"));
        a.setTeacherName(rs.getString("teacher_name"));
        return a;
    }
}
