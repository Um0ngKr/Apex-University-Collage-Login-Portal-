package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.AcademicSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AcademicSessionDAO {
    private static final Logger LOGGER = Logger.getLogger(AcademicSessionDAO.class.getName());

    public List<AcademicSession> getAllSessions() {
        List<AcademicSession> list = new ArrayList<>();
        String sql = "SELECT * FROM academic_sessions ORDER BY start_year DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                AcademicSession a = new AcademicSession();
                a.setId(rs.getInt("id"));
                a.setSessionCode(rs.getString("session_code"));
                a.setStartYear(rs.getInt("start_year"));
                a.setEndYear(rs.getInt("end_year"));
                a.setCurrent(rs.getBoolean("is_current"));
                a.setStatus(rs.getString("status"));
                list.add(a);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching sessions", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public AcademicSession getCurrentSession() {
        String sql = "SELECT * FROM academic_sessions WHERE is_current = 1 LIMIT 1";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                AcademicSession a = new AcademicSession();
                a.setId(rs.getInt("id"));
                a.setSessionCode(rs.getString("session_code"));
                a.setStartYear(rs.getInt("start_year"));
                a.setEndYear(rs.getInt("end_year"));
                a.setCurrent(true);
                a.setStatus(rs.getString("status"));
                return a;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching current session", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    public boolean addSession(AcademicSession a) {
        String sql = "INSERT INTO academic_sessions (session_code, start_year, end_year, is_current, status) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement psClear = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            if (a.isCurrent()) {
                conn.setAutoCommit(false);
                psClear = conn.prepareStatement("UPDATE academic_sessions SET is_current = 0");
                psClear.executeUpdate();
            }
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, a.getSessionCode());
            ps.setInt(2, a.getStartYear());
            ps.setInt(3, a.getEndYear());
            ps.setBoolean(4, a.isCurrent());
            ps.setString(5, a.getStatus() != null ? a.getStatus() : "ACTIVE");
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        a.setId(keys.getInt(1));
                    }
                }
            }
            if (a.isCurrent()) {
                conn.commit();
            }
            return affected > 0;
        } catch (SQLException e) {
            if (conn != null && a.isCurrent()) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            LOGGER.log(Level.SEVERE, "Error adding academic session", e);
        } finally {
            if (conn != null && a.isCurrent()) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            DBConnectionManager.close(null, psClear);
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updateSession(AcademicSession a) {
        Connection conn = null;
        PreparedStatement psCheck = null;
        PreparedStatement psClear = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            boolean currentlyIsCurrent = false;
            psCheck = conn.prepareStatement("SELECT is_current FROM academic_sessions WHERE id = ?");
            psCheck.setInt(1, a.getId());
            rs = psCheck.executeQuery();
            if (rs.next()) {
                currentlyIsCurrent = rs.getBoolean("is_current");
            }

            // If it is already current, preserve current status; or if requested to be current, make it current.
            boolean shouldBeCurrent = a.isCurrent() || currentlyIsCurrent;

            if (shouldBeCurrent) {
                psClear = conn.prepareStatement("UPDATE academic_sessions SET is_current = 0");
                psClear.executeUpdate();
            }

            String sql = "UPDATE academic_sessions SET session_code = ?, start_year = ?, end_year = ?, is_current = ?, status = ? WHERE id = ?";
            psUpdate = conn.prepareStatement(sql);
            psUpdate.setString(1, a.getSessionCode());
            psUpdate.setInt(2, a.getStartYear());
            psUpdate.setInt(3, a.getEndYear());
            psUpdate.setBoolean(4, shouldBeCurrent);
            psUpdate.setString(5, a.getStatus() != null ? a.getStatus() : "ACTIVE");
            psUpdate.setInt(6, a.getId());
            int rows = psUpdate.executeUpdate();

            conn.commit();
            return rows > 0;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            LOGGER.log(Level.SEVERE, "Error updating academic session", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            DBConnectionManager.close(null, psCheck, rs);
            DBConnectionManager.close(null, psClear);
            DBConnectionManager.close(conn, psUpdate);
        }
        return false;
    }

    public boolean isSessionCodeExists(String sessionCode, int excludeId) {
        if (sessionCode == null || sessionCode.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT id FROM academic_sessions WHERE session_code = ? AND id != ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, sessionCode.trim());
            ps.setInt(2, excludeId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking session code existence", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return false;
    }

    public boolean setCurrentSession(int id) {
        Connection conn = null;
        PreparedStatement psClear = null;
        PreparedStatement psSet = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            psClear = conn.prepareStatement("UPDATE academic_sessions SET is_current = 0");
            psClear.executeUpdate();

            psSet = conn.prepareStatement("UPDATE academic_sessions SET is_current = 1 WHERE id = ?");
            psSet.setInt(1, id);
            psSet.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            LOGGER.log(Level.SEVERE, "Error setting current session", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            DBConnectionManager.close(null, psClear);
            DBConnectionManager.close(conn, psSet);
        }
        return false;
    }
}
