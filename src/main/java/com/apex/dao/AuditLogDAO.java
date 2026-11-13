package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.AuditLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuditLogDAO {
    private static final Logger LOGGER = Logger.getLogger(AuditLogDAO.class.getName());

    public void log(String userType, Integer userId, String username, String action, String tableAffected, Integer recordId, String details, String ip) {
        String sql = "INSERT INTO audit_logs (user_type, user_id, username, action, table_affected, record_id, details, ip_address) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, userType);
            if (userId != null) ps.setInt(2, userId); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, username);
            ps.setString(4, action);
            ps.setString(5, tableAffected);
            if (recordId != null) ps.setInt(6, recordId); else ps.setNull(6, Types.INTEGER);
            ps.setString(7, details);
            ps.setString(8, ip != null ? ip : "127.0.0.1");
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to write audit log", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
    }

    public List<AuditLog> getRecentLogs(int limit) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs ORDER BY id DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, limit > 0 ? limit : 50);
            rs = ps.executeQuery();
            while (rs.next()) {
                AuditLog a = new AuditLog();
                a.setId(rs.getInt("id"));
                a.setUserType(rs.getString("user_type"));
                a.setUserId(rs.getInt("user_id"));
                a.setUsername(rs.getString("username"));
                a.setAction(rs.getString("action"));
                a.setTableAffected(rs.getString("table_affected"));
                a.setRecordId(rs.getInt("record_id"));
                a.setDetails(rs.getString("details"));
                a.setIpAddress(rs.getString("ip_address"));
                a.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(a);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching audit logs", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }
}
