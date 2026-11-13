package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubjectDAO {
    private static final Logger LOGGER = Logger.getLogger(SubjectDAO.class.getName());

    public List<Subject> getAllSubjects(Integer courseId, Integer semesterId) {
        List<Subject> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT s.*, c.name AS course_name, sem.semester_number " +
            "FROM subjects s " +
            "JOIN courses c ON s.course_id = c.id " +
            "JOIN semesters sem ON s.semester_id = sem.id " +
            "WHERE 1=1 "
        );
        if (courseId != null && courseId > 0) {
            sql.append(" AND s.course_id = ? ");
        }
        if (semesterId != null && semesterId > 0) {
            sql.append(" AND s.semester_id = ? ");
        }
        sql.append(" ORDER BY s.course_id, sem.semester_number, s.subject_code ASC");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            if (courseId != null && courseId > 0) {
                ps.setInt(idx++, courseId);
            }
            if (semesterId != null && semesterId > 0) {
                ps.setInt(idx++, semesterId);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractSubject(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching subjects", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public Subject getSubjectById(int id) {
        String sql = "SELECT s.*, c.name AS course_name, sem.semester_number FROM subjects s " +
                     "JOIN courses c ON s.course_id = c.id " +
                     "JOIN semesters sem ON s.semester_id = sem.id " +
                     "WHERE s.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractSubject(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching subject by id: " + id, e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    public boolean addSubject(Subject s) {
        String sql = "INSERT INTO subjects (course_id, semester_id, subject_code, name, credit, " +
                     "max_internal_marks, max_external_marks, pass_marks, subject_type, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, s.getCourseId());
            ps.setInt(2, s.getSemesterId());
            ps.setString(3, s.getSubjectCode());
            ps.setString(4, s.getName());
            ps.setDouble(5, s.getCredit() > 0 ? s.getCredit() : 3.0);
            ps.setInt(6, s.getMaxInternalMarks() > 0 ? s.getMaxInternalMarks() : 30);
            ps.setInt(7, s.getMaxExternalMarks() > 0 ? s.getMaxExternalMarks() : 70);
            ps.setInt(8, s.getPassMarks() > 0 ? s.getPassMarks() : 40);
            ps.setString(9, s.getSubjectType() != null ? s.getSubjectType() : "THEORY");
            ps.setString(10, s.getStatus() != null ? s.getStatus() : "ACTIVE");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding subject", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updateSubject(Subject s) {
        String sql = "UPDATE subjects SET course_id = ?, semester_id = ?, subject_code = ?, name = ?, " +
                     "credit = ?, max_internal_marks = ?, max_external_marks = ?, pass_marks = ?, " +
                     "subject_type = ?, status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, s.getCourseId());
            ps.setInt(2, s.getSemesterId());
            ps.setString(3, s.getSubjectCode());
            ps.setString(4, s.getName());
            ps.setDouble(5, s.getCredit());
            ps.setInt(6, s.getMaxInternalMarks());
            ps.setInt(7, s.getMaxExternalMarks());
            ps.setInt(8, s.getPassMarks());
            ps.setString(9, s.getSubjectType());
            ps.setString(10, s.getStatus());
            ps.setInt(11, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating subject", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean deleteSubject(int id) {
        String sql = "DELETE FROM subjects WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting subject", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    private Subject extractSubject(ResultSet rs) throws SQLException {
        Subject s = new Subject();
        s.setId(rs.getInt("id"));
        s.setCourseId(rs.getInt("course_id"));
        s.setCourseName(rs.getString("course_name"));
        s.setSemesterId(rs.getInt("semester_id"));
        s.setSemesterNumber(rs.getInt("semester_number"));
        s.setSubjectCode(rs.getString("subject_code"));
        s.setName(rs.getString("name"));
        s.setCredit(rs.getDouble("credit"));
        s.setMaxInternalMarks(rs.getInt("max_internal_marks"));
        s.setMaxExternalMarks(rs.getInt("max_external_marks"));
        s.setPassMarks(rs.getInt("pass_marks"));
        s.setSubjectType(rs.getString("subject_type"));
        s.setStatus(rs.getString("status"));
        return s;
    }
}
