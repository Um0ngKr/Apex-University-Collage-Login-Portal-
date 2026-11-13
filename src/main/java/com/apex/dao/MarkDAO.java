package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.Mark;
import com.apex.util.GradeCalculator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MarkDAO {
    private static final Logger LOGGER = Logger.getLogger(MarkDAO.class.getName());

    public boolean saveOrUpdateMark(Mark m) {
        String sql = "INSERT INTO marks (exam_id, student_id, subject_id, internal_marks, external_marks, " +
                     "total_marks, grade_point, letter_grade, is_pass, evaluated_by_teacher_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE internal_marks = VALUES(internal_marks), " +
                     "external_marks = VALUES(external_marks), total_marks = VALUES(total_marks), " +
                     "grade_point = VALUES(grade_point), letter_grade = VALUES(letter_grade), " +
                     "is_pass = VALUES(is_pass), evaluated_by_teacher_id = VALUES(evaluated_by_teacher_id)";

        double total = m.getInternalMarks() + m.getExternalMarks();
        m.setTotalMarks(total);

        // Fetch subject pass marks and max marks
        SubjectDAO subDao = new SubjectDAO();
        var sub = subDao.getSubjectById(m.getSubjectId());
        double maxMarks = sub != null ? (sub.getMaxInternalMarks() + sub.getMaxExternalMarks()) : 100.0;
        int passMarks = sub != null ? sub.getPassMarks() : 40;

        GradeCalculator.GradeInfo gInfo = GradeCalculator.calculateSubjectGrade(total, maxMarks);
        boolean isPass = total >= passMarks;

        m.setGradePoint(gInfo.getGradePoint());
        m.setLetterGrade(gInfo.getLetterGrade());
        m.setPass(isPass);

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, m.getExamId());
            ps.setInt(2, m.getStudentId());
            ps.setInt(3, m.getSubjectId());
            ps.setDouble(4, m.getInternalMarks());
            ps.setDouble(5, m.getExternalMarks());
            ps.setDouble(6, total);
            ps.setDouble(7, gInfo.getGradePoint());
            ps.setString(8, gInfo.getLetterGrade());
            ps.setBoolean(9, isPass);
            if (m.getEvaluatedByTeacherId() != null) {
                ps.setInt(10, m.getEvaluatedByTeacherId());
            } else {
                ps.setNull(10, Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving mark", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public List<Mark> getMarksByExamAndStudent(int examId, int studentId) {
        List<Mark> list = new ArrayList<>();
        String sql = "SELECT m.*, e.exam_name, s.full_name AS student_name, s.registration_no, s.roll_no, " +
                     "sub.subject_code, sub.name AS subject_name, t.full_name AS teacher_name " +
                     "FROM marks m " +
                     "JOIN examinations e ON m.exam_id = e.id " +
                     "JOIN students s ON m.student_id = s.id " +
                     "JOIN subjects sub ON m.subject_id = sub.id " +
                     "LEFT JOIN teachers t ON m.evaluated_by_teacher_id = t.id " +
                     "WHERE m.exam_id = ? AND m.student_id = ? " +
                     "ORDER BY sub.subject_code ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, examId);
            ps.setInt(2, studentId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractMark(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching marks by exam and student", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public List<Mark> getMarksByExamAndSubject(int examId, int subjectId) {
        List<Mark> list = new ArrayList<>();
        String sql = "SELECT m.*, e.exam_name, s.full_name AS student_name, s.registration_no, s.roll_no, " +
                     "sub.subject_code, sub.name AS subject_name, t.full_name AS teacher_name " +
                     "FROM marks m " +
                     "JOIN examinations e ON m.exam_id = e.id " +
                     "JOIN students s ON m.student_id = s.id " +
                     "JOIN subjects sub ON m.subject_id = sub.id " +
                     "LEFT JOIN teachers t ON m.evaluated_by_teacher_id = t.id " +
                     "WHERE m.exam_id = ? AND m.subject_id = ? " +
                     "ORDER BY s.roll_no ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, examId);
            ps.setInt(2, subjectId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractMark(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching marks by exam and subject", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    private Mark extractMark(ResultSet rs) throws SQLException {
        Mark m = new Mark();
        m.setId(rs.getInt("id"));
        m.setExamId(rs.getInt("exam_id"));
        m.setExamName(rs.getString("exam_name"));
        m.setStudentId(rs.getInt("student_id"));
        m.setStudentName(rs.getString("student_name"));
        m.setRegistrationNo(rs.getString("registration_no"));
        m.setRollNo(rs.getString("roll_no"));
        m.setSubjectId(rs.getInt("subject_id"));
        m.setSubjectCode(rs.getString("subject_code"));
        m.setSubjectName(rs.getString("subject_name"));
        m.setInternalMarks(rs.getDouble("internal_marks"));
        m.setExternalMarks(rs.getDouble("external_marks"));
        m.setTotalMarks(rs.getDouble("total_marks"));
        m.setGradePoint(rs.getDouble("grade_point"));
        m.setLetterGrade(rs.getString("letter_grade"));
        m.setPass(rs.getBoolean("is_pass"));
        m.setTeacherName(rs.getString("teacher_name"));
        return m;
    }
}
