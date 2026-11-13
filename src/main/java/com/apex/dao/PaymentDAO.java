package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaymentDAO {
    private static final Logger LOGGER = Logger.getLogger(PaymentDAO.class.getName());

    public List<Payment> getAllPayments(String statusFilter) {
        List<Payment> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, s.full_name AS student_name, s.registration_no, s.roll_no, e.exam_name " +
            "FROM payments p " +
            "JOIN students s ON p.student_id = s.id " +
            "LEFT JOIN examinations e ON p.exam_id = e.id " +
            "WHERE 1=1 "
        );
        if (statusFilter != null && !statusFilter.isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
            sql.append(" AND p.payment_status = ? ");
        }
        sql.append(" ORDER BY p.id DESC");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql.toString());
            if (statusFilter != null && !statusFilter.isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
                ps.setString(1, statusFilter);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractPayment(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching payments", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public List<Payment> getPaymentsByStudent(int studentId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, s.full_name AS student_name, s.registration_no, s.roll_no, e.exam_name " +
                     "FROM payments p " +
                     "JOIN students s ON p.student_id = s.id " +
                     "LEFT JOIN examinations e ON p.exam_id = e.id " +
                     "WHERE p.student_id = ? ORDER BY p.id DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractPayment(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student payments", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public Payment getPaymentByReceiptOrTxn(String query) {
        String sql = "SELECT p.*, s.full_name AS student_name, s.registration_no, s.roll_no, e.exam_name " +
                     "FROM payments p " +
                     "JOIN students s ON p.student_id = s.id " +
                     "LEFT JOIN examinations e ON p.exam_id = e.id " +
                     "WHERE p.receipt_no = ? OR p.transaction_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, query);
            ps.setString(2, query);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractPayment(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching payment by receipt/txn", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    public boolean createSandboxPayment(int studentId, Integer examId, Integer examFormId, double amount, String method, String remarks) {
        String sql = "INSERT INTO payments (student_id, exam_id, exam_form_id, transaction_id, amount, payment_method, payment_status, receipt_no, remarks) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'SUCCESS', ?, ?)";
        String txnId = "TXN_APEX_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 9000 + 1000);
        String receiptNo = "RCPT-" + (10000 + (int)(Math.random() * 89999));

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            if (examId != null) ps.setInt(2, examId); else ps.setNull(2, Types.INTEGER);
            if (examFormId != null) ps.setInt(3, examFormId); else ps.setNull(3, Types.INTEGER);
            ps.setString(4, txnId);
            ps.setDouble(5, amount);
            ps.setString(6, method != null ? method : "SANDBOX_GATEWAY");
            ps.setString(7, receiptNo);
            ps.setString(8, remarks != null ? remarks : "Examination Fee Payment (Sandbox Mode)");

            int affected = ps.executeUpdate();
            if (affected > 0 && examFormId != null && examFormId > 0) {
                // Update exam form status as PAID
                new ExamFormDAO().updatePaymentStatus(examFormId, "PAID");
            }
            return affected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating sandbox payment", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updatePaymentVerification(int paymentId, int adminId, String status) {
        String sql = "UPDATE payments SET verified_by_admin_id = ?, payment_status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, adminId);
            ps.setString(2, status);
            ps.setInt(3, paymentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error verifying payment", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    private Payment extractPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getInt("id"));
        p.setStudentId(rs.getInt("student_id"));
        p.setStudentName(rs.getString("student_name"));
        p.setRegistrationNo(rs.getString("registration_no"));
        p.setRollNo(rs.getString("roll_no"));
        p.setExamId(rs.getInt("exam_id"));
        p.setExamName(rs.getString("exam_name"));
        p.setExamFormId(rs.getInt("exam_form_id"));
        p.setTransactionId(rs.getString("transaction_id"));
        p.setAmount(rs.getDouble("amount"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setPaymentDate(rs.getTimestamp("payment_date"));
        p.setPaymentStatus(rs.getString("payment_status"));
        p.setReceiptNo(rs.getString("receipt_no"));
        p.setRemarks(rs.getString("remarks"));
        p.setVerifiedByAdminId(rs.getInt("verified_by_admin_id"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
