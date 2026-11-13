package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.ExamSchedule;
import com.apex.model.Examination;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExaminationDAO {
    private static final Logger LOGGER = Logger.getLogger(ExaminationDAO.class.getName());

    public List<Examination> getAllExaminations() {
        List<Examination> list = new ArrayList<>();
        String sql = "SELECT e.*, ses.session_code FROM examinations e " +
                     "JOIN academic_sessions ses ON e.session_id = ses.id " +
                     "ORDER BY e.id DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractExam(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching examinations", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public Examination getExaminationById(int id) {
        String sql = "SELECT e.*, ses.session_code FROM examinations e " +
                     "JOIN academic_sessions ses ON e.session_id = ses.id WHERE e.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractExam(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching exam: " + id, e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    public List<Examination> getActiveFormFillupExams() {
        List<Examination> list = new ArrayList<>();
        String sql = "SELECT e.*, ses.session_code FROM examinations e " +
                     "JOIN academic_sessions ses ON e.session_id = ses.id " +
                     "WHERE e.status IN ('ACTIVE', 'FORM_FILLUP') ORDER BY e.id DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractExam(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching active exams", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public boolean addExamination(Examination e) {
        String sql = "INSERT INTO examinations (exam_name, exam_code, session_id, semester_number, start_date, end_date, exam_type, fee_amount, status, is_result_published) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, e.getExamName());
            ps.setString(2, e.getExamCode());
            ps.setInt(3, e.getSessionId());
            ps.setInt(4, e.getSemesterNumber());
            ps.setDate(5, e.getStartDate());
            ps.setDate(6, e.getEndDate());
            ps.setString(7, e.getExamType() != null ? e.getExamType() : "REGULAR");
            ps.setDouble(8, e.getFeeAmount() > 0 ? e.getFeeAmount() : 1200.00);
            ps.setString(9, e.getStatus() != null ? e.getStatus() : "ACTIVE");
            ps.setBoolean(10, e.isResultPublished());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error adding examination", ex);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updateExamination(Examination e) {
        String sql = "UPDATE examinations SET exam_name = ?, exam_code = ?, session_id = ?, semester_number = ?, " +
                     "start_date = ?, end_date = ?, exam_type = ?, fee_amount = ?, status = ?, is_result_published = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, e.getExamName());
            ps.setString(2, e.getExamCode());
            ps.setInt(3, e.getSessionId());
            ps.setInt(4, e.getSemesterNumber());
            ps.setDate(5, e.getStartDate());
            ps.setDate(6, e.getEndDate());
            ps.setString(7, e.getExamType());
            ps.setDouble(8, e.getFeeAmount());
            ps.setString(9, e.getStatus());
            ps.setBoolean(10, e.isResultPublished());
            ps.setInt(11, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating examination", ex);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean deleteExamination(int id) {
        String sql = "DELETE FROM examinations WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting exam", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean toggleResultPublication(int examId, boolean isPublished) {
        String sqlExam = "UPDATE examinations SET is_result_published = ? WHERE id = ?";
        String sqlResults = "UPDATE results SET is_published = ?, published_at = ? WHERE exam_id = ?";
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            ps1 = conn.prepareStatement(sqlExam);
            ps1.setBoolean(1, isPublished);
            ps1.setInt(2, examId);
            ps1.executeUpdate();

            ps2 = conn.prepareStatement(sqlResults);
            ps2.setBoolean(1, isPublished);
            if (isPublished) {
                ps2.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            } else {
                ps2.setNull(2, Types.TIMESTAMP);
            }
            ps2.setInt(3, examId);
            ps2.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            LOGGER.log(Level.SEVERE, "Error toggling result publication", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            DBConnectionManager.close(null, ps1);
            DBConnectionManager.close(conn, ps2);
        }
        return false;
    }

    public List<ExamSchedule> getSchedulesByExam(int examId) {
        List<ExamSchedule> list = new ArrayList<>();
        String sql = "SELECT es.*, sub.subject_code, sub.name AS subject_name " +
                     "FROM exam_schedules es " +
                     "JOIN subjects sub ON es.subject_id = sub.id " +
                     "WHERE es.exam_id = ? ORDER BY es.exam_date, es.start_time ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, examId);
            rs = ps.executeQuery();
            while (rs.next()) {
                ExamSchedule s = new ExamSchedule();
                s.setId(rs.getInt("id"));
                s.setExamId(rs.getInt("exam_id"));
                s.setSubjectId(rs.getInt("subject_id"));
                s.setSubjectCode(rs.getString("subject_code"));
                s.setSubjectName(rs.getString("subject_name"));
                s.setExamDate(rs.getDate("exam_date"));
                s.setStartTime(rs.getTime("start_time"));
                s.setEndTime(rs.getTime("end_time"));
                s.setRoomNo(rs.getString("room_no"));
                list.add(s);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching exam schedules", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public boolean addExamSchedule(ExamSchedule s) {
        String sql = "INSERT INTO exam_schedules (exam_id, subject_id, exam_date, start_time, end_time, room_no) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, s.getExamId());
            ps.setInt(2, s.getSubjectId());
            ps.setDate(3, s.getExamDate());
            ps.setTime(4, s.getStartTime());
            ps.setTime(5, s.getEndTime());
            ps.setString(6, s.getRoomNo() != null ? s.getRoomNo() : "Main Examination Hall");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding exam schedule", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean deleteExamSchedule(int scheduleId) {
        String sql = "DELETE FROM exam_schedules WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, scheduleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting exam schedule", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updateExamSchedule(ExamSchedule s) {
        String sql = "UPDATE exam_schedules SET subject_id = ?, exam_date = ?, start_time = ?, end_time = ?, room_no = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, s.getSubjectId());
            ps.setDate(2, s.getExamDate());
            ps.setTime(3, s.getStartTime());
            ps.setTime(4, s.getEndTime());
            ps.setString(5, s.getRoomNo() != null ? s.getRoomNo() : "Main Examination Hall");
            ps.setInt(6, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating exam schedule", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean isExamCodeExists(String examCode, int excludeId) {
        if (examCode == null || examCode.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT id FROM examinations WHERE exam_code = ? AND id != ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, examCode.trim());
            ps.setInt(2, excludeId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking exam code existence", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return false;
    }

    private Examination extractExam(ResultSet rs) throws SQLException {
        Examination e = new Examination();
        e.setId(rs.getInt("id"));
        e.setExamName(rs.getString("exam_name"));
        e.setExamCode(rs.getString("exam_code"));
        e.setSessionId(rs.getInt("session_id"));
        e.setSessionCode(rs.getString("session_code"));
        e.setSemesterNumber(rs.getInt("semester_number"));
        e.setStartDate(rs.getDate("start_date"));
        e.setEndDate(rs.getDate("end_date"));
        e.setExamType(rs.getString("exam_type"));
        e.setFeeAmount(rs.getDouble("fee_amount"));
        e.setStatus(rs.getString("status"));
        e.setResultPublished(rs.getBoolean("is_result_published"));
        e.setCreatedAt(rs.getTimestamp("created_at"));
        return e;
    }
}
