package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.ExamForm;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamFormDAO {
    private static final Logger LOGGER = Logger.getLogger(ExamFormDAO.class.getName());

    public List<ExamForm> getExamForms(Integer examId, Integer collegeId, String statusFilter) {
        List<ExamForm> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT ef.*, e.exam_name, e.exam_code, e.fee_amount, s.full_name AS student_name, " +
            "s.registration_no, s.roll_no, c.name AS college_name, co.name AS course_name, " +
            "sem.semester_number, p.transaction_id " +
            "FROM exam_forms ef " +
            "JOIN examinations e ON ef.exam_id = e.id " +
            "JOIN students s ON ef.student_id = s.id " +
            "JOIN colleges c ON ef.college_id = c.id " +
            "JOIN courses co ON s.course_id = co.id " +
            "JOIN semesters sem ON s.current_semester_id = sem.id " +
            "LEFT JOIN payments p ON p.exam_form_id = ef.id AND p.payment_status = 'SUCCESS' " +
            "WHERE 1=1 "
        );

        if (examId != null && examId > 0) {
            sql.append(" AND ef.exam_id = ? ");
        }
        if (collegeId != null && collegeId > 0) {
            sql.append(" AND ef.college_id = ? ");
        }
        if (statusFilter != null && !statusFilter.isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
            sql.append(" AND (ef.college_approval_status = ? OR ef.admin_approval_status = ?) ");
        }
        sql.append(" ORDER BY ef.id DESC");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            if (examId != null && examId > 0) {
                ps.setInt(idx++, examId);
            }
            if (collegeId != null && collegeId > 0) {
                ps.setInt(idx++, collegeId);
            }
            if (statusFilter != null && !statusFilter.isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
                ps.setString(idx++, statusFilter);
                ps.setString(idx++, statusFilter);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractExamForm(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching exam forms", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public ExamForm getFormById(int formId) {
        String sql = "SELECT ef.*, e.exam_name, e.exam_code, e.fee_amount, s.full_name AS student_name, " +
                     "s.registration_no, s.roll_no, c.name AS college_name, co.name AS course_name, " +
                     "sem.semester_number, p.transaction_id " +
                     "FROM exam_forms ef " +
                     "JOIN examinations e ON ef.exam_id = e.id " +
                     "JOIN students s ON ef.student_id = s.id " +
                     "JOIN colleges c ON ef.college_id = c.id " +
                     "JOIN courses co ON s.course_id = co.id " +
                     "JOIN semesters sem ON s.current_semester_id = sem.id " +
                     "LEFT JOIN payments p ON p.exam_form_id = ef.id AND p.payment_status = 'SUCCESS' " +
                     "WHERE ef.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, formId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractExamForm(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching exam form: " + formId, e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    public ExamForm getFormByStudentAndExam(int studentId, int examId) {
        String sql = "SELECT ef.*, e.exam_name, e.exam_code, e.fee_amount, s.full_name AS student_name, " +
                     "s.registration_no, s.roll_no, c.name AS college_name, co.name AS course_name, " +
                     "sem.semester_number, p.transaction_id " +
                     "FROM exam_forms ef " +
                     "JOIN examinations e ON ef.exam_id = e.id " +
                     "JOIN students s ON ef.student_id = s.id " +
                     "JOIN colleges c ON ef.college_id = c.id " +
                     "JOIN courses co ON s.course_id = co.id " +
                     "JOIN semesters sem ON s.current_semester_id = sem.id " +
                     "LEFT JOIN payments p ON p.exam_form_id = ef.id AND p.payment_status = 'SUCCESS' " +
                     "WHERE ef.student_id = ? AND ef.exam_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractExamForm(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching exam form by student and exam", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    public List<ExamForm> getFormsByStudent(int studentId) {
        List<ExamForm> list = new ArrayList<>();
        String sql = "SELECT ef.*, e.exam_name, e.exam_code, e.fee_amount, s.full_name AS student_name, " +
                     "s.registration_no, s.roll_no, c.name AS college_name, co.name AS course_name, " +
                     "sem.semester_number, p.transaction_id " +
                     "FROM exam_forms ef " +
                     "JOIN examinations e ON ef.exam_id = e.id " +
                     "JOIN students s ON ef.student_id = s.id " +
                     "JOIN colleges c ON ef.college_id = c.id " +
                     "JOIN courses co ON s.course_id = co.id " +
                     "JOIN semesters sem ON s.current_semester_id = sem.id " +
                     "LEFT JOIN payments p ON p.exam_form_id = ef.id AND p.payment_status = 'SUCCESS' " +
                     "WHERE ef.student_id = ? ORDER BY ef.id DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractExamForm(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student exam forms", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public boolean submitForm(int examId, int studentId, int collegeId) {
        String sql = "INSERT INTO exam_forms (exam_id, student_id, college_id, college_approval_status, admin_approval_status, payment_status) " +
                     "VALUES (?, ?, ?, 'PENDING', 'PENDING', 'PENDING') " +
                     "ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, examId);
            ps.setInt(2, studentId);
            ps.setInt(3, collegeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error submitting exam form", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updateCollegeApproval(int formId, String status, String reason) {
        String sql = "UPDATE exam_forms SET college_approval_status = ?, rejection_reason = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, reason);
            ps.setInt(3, formId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating college approval", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updateAdminApproval(int formId, String status, String reason) {
        String sql = "UPDATE exam_forms SET admin_approval_status = ?, " +
                     "admit_card_issued = CASE WHEN ? = 'APPROVED' AND payment_status = 'PAID' THEN 1 ELSE admit_card_issued END, " +
                     "rejection_reason = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setString(3, reason);
            ps.setInt(4, formId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating admin approval", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updatePaymentStatus(int formId, String paymentStatus) {
        String sql = "UPDATE exam_forms SET payment_status = ?, " +
                     "admit_card_issued = CASE WHEN ? = 'PAID' AND admin_approval_status = 'APPROVED' THEN 1 ELSE admit_card_issued END " +
                     "WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, paymentStatus);
            ps.setString(2, paymentStatus);
            ps.setInt(3, formId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating form payment status", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updateExamForm(ExamForm form) {
        String sql = "UPDATE exam_forms SET college_approval_status = ?, admin_approval_status = ?, " +
                     "payment_status = ?, rejection_reason = ?, admit_card_issued = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, form.getCollegeApprovalStatus() != null ? form.getCollegeApprovalStatus() : "PENDING");
            ps.setString(2, form.getAdminApprovalStatus() != null ? form.getAdminApprovalStatus() : "PENDING");
            ps.setString(3, form.getPaymentStatus() != null ? form.getPaymentStatus() : "PENDING");
            ps.setString(4, form.getRejectionReason());
            ps.setBoolean(5, form.isAdmitCardIssued());
            ps.setInt(6, form.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating exam form", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    private ExamForm extractExamForm(ResultSet rs) throws SQLException {
        ExamForm ef = new ExamForm();
        ef.setId(rs.getInt("id"));
        ef.setExamId(rs.getInt("exam_id"));
        ef.setExamName(rs.getString("exam_name"));
        ef.setExamCode(rs.getString("exam_code"));
        ef.setFeeAmount(rs.getDouble("fee_amount"));
        ef.setStudentId(rs.getInt("student_id"));
        ef.setStudentName(rs.getString("student_name"));
        ef.setRegistrationNo(rs.getString("registration_no"));
        ef.setRollNo(rs.getString("roll_no"));
        ef.setCollegeId(rs.getInt("college_id"));
        ef.setCollegeName(rs.getString("college_name"));
        ef.setCourseName(rs.getString("course_name"));
        ef.setSemesterNumber(rs.getInt("semester_number"));
        ef.setSubmissionDate(rs.getTimestamp("submission_date"));
        ef.setCollegeApprovalStatus(rs.getString("college_approval_status"));
        ef.setAdminApprovalStatus(rs.getString("admin_approval_status"));
        ef.setPaymentStatus(rs.getString("payment_status"));
        ef.setRejectionReason(rs.getString("rejection_reason"));
        ef.setAdmitCardIssued(rs.getBoolean("admit_card_issued"));
        ef.setTransactionId(rs.getString("transaction_id"));
        return ef;
    }
}
