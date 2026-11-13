package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DepartmentDAO {
    private static final Logger LOGGER = Logger.getLogger(DepartmentDAO.class.getName());

    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY dept_code ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Department d = new Department();
                d.setId(rs.getInt("id"));
                d.setDeptCode(rs.getString("dept_code"));
                d.setName(rs.getString("name"));
                d.setDescription(rs.getString("description"));
                d.setStatus(rs.getString("status"));
                d.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(d);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching departments", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public boolean addDepartment(Department d) {
        String sql = "INSERT INTO departments (dept_code, name, description, status) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, d.getDeptCode());
            ps.setString(2, d.getName());
            ps.setString(3, d.getDescription());
            ps.setString(4, d.getStatus() != null ? d.getStatus() : "ACTIVE");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding department", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updateDepartment(Department d) {
        String sql = "UPDATE departments SET dept_code = ?, name = ?, description = ?, status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, d.getDeptCode());
            ps.setString(2, d.getName());
            ps.setString(3, d.getDescription());
            ps.setString(4, d.getStatus());
            ps.setInt(5, d.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating department", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean deleteDepartment(int id) {
        String sql = "DELETE FROM departments WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting department", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }
}
