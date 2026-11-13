package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.College;
import com.apex.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CollegeDAO {
    private static final Logger LOGGER = Logger.getLogger(CollegeDAO.class.getName());

    public List<College> getAllColleges(String search) {
        List<College> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT c.*, (SELECT COUNT(*) FROM students s WHERE s.college_id = c.id) AS student_count " +
            "FROM colleges c WHERE 1=1 "
        );
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (c.name LIKE ? OR c.college_code LIKE ? OR c.email LIKE ?) ");
        }
        sql.append(" ORDER BY c.id ASC");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql.toString());
            if (search != null && !search.trim().isEmpty()) {
                String term = "%" + search.trim() + "%";
                ps.setString(1, term);
                ps.setString(2, term);
                ps.setString(3, term);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractCollege(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching colleges", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public College getCollegeById(int id) {
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM students s WHERE s.college_id = c.id) AS student_count " +
                     "FROM colleges c WHERE c.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractCollege(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching college by id: " + id, e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    public boolean addCollege(College c) {
        String sql = "INSERT INTO colleges (college_code, name, email, phone, address, website, principal_name, password_hash, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, c.getCollegeCode());
            ps.setString(2, c.getName());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getAddress());
            ps.setString(6, c.getWebsite());
            ps.setString(7, c.getPrincipalName());
            String hash = c.getPasswordHash() != null && !c.getPasswordHash().isEmpty()
                    ? c.getPasswordHash() : PasswordUtil.hashPassword("college123");
            ps.setString(8, hash);
            ps.setString(9, c.getStatus() != null ? c.getStatus() : "ACTIVE");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding college", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updateCollege(College c) {
        String sql = "UPDATE colleges SET college_code = ?, name = ?, email = ?, phone = ?, address = ?, " +
                     "website = ?, principal_name = ?, status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, c.getCollegeCode());
            ps.setString(2, c.getName());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getAddress());
            ps.setString(6, c.getWebsite());
            ps.setString(7, c.getPrincipalName());
            ps.setString(8, c.getStatus() != null ? c.getStatus() : "ACTIVE");
            ps.setInt(9, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating college", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean isCollegeCodeExists(String collegeCode, int excludeId) {
        if (collegeCode == null || collegeCode.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT id FROM colleges WHERE college_code = ? AND id != ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, collegeCode.trim());
            ps.setInt(2, excludeId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking college code existence", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return false;
    }

    public boolean isEmailExists(String email, int excludeId) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT id FROM colleges WHERE LOWER(email) = LOWER(?) AND id != ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email.trim());
            ps.setInt(2, excludeId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking college email existence", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return false;
    }

    public boolean deleteCollege(int id) {
        String sql = "DELETE FROM colleges WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting college", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    private College extractCollege(ResultSet rs) throws SQLException {
        College c = new College();
        c.setId(rs.getInt("id"));
        c.setCollegeCode(rs.getString("college_code"));
        c.setName(rs.getString("name"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        c.setAddress(rs.getString("address"));
        c.setWebsite(rs.getString("website"));
        c.setPrincipalName(rs.getString("principal_name"));
        c.setStatus(rs.getString("status"));
        c.setStudentCount(rs.getInt("student_count"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
