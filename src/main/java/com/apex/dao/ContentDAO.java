package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.HelpdeskContact;
import com.apex.model.Instruction;
import com.apex.model.Notice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContentDAO {
    private static final Logger LOGGER = Logger.getLogger(ContentDAO.class.getName());

    // -------------------------------------------------------------
    // Instructions CRUD
    // -------------------------------------------------------------

    public List<Instruction> getAllInstructions(boolean onlyPublished) {
        List<Instruction> list = new ArrayList<>();
        String sql = "SELECT * FROM instructions " + (onlyPublished ? "WHERE is_published = 1 " : "") +
                     "ORDER BY sort_order ASC, id ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Instruction i = new Instruction();
                i.setId(rs.getInt("id"));
                i.setCategory(rs.getString("category"));
                i.setTitle(rs.getString("title"));
                i.setContent(rs.getString("content"));
                i.setSortOrder(rs.getInt("sort_order"));
                i.setPublished(rs.getBoolean("is_published"));
                i.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(i);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching instructions", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public boolean addInstruction(Instruction i) {
        String sql = "INSERT INTO instructions (category, title, content, sort_order, is_published) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, i.getCategory());
            ps.setString(2, i.getTitle());
            ps.setString(3, i.getContent());
            ps.setInt(4, i.getSortOrder() > 0 ? i.getSortOrder() : 1);
            ps.setBoolean(5, i.isPublished());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding instruction", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updateInstruction(Instruction i) {
        String sql = "UPDATE instructions SET category = ?, title = ?, content = ?, sort_order = ?, is_published = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, i.getCategory());
            ps.setString(2, i.getTitle());
            ps.setString(3, i.getContent());
            ps.setInt(4, i.getSortOrder());
            ps.setBoolean(5, i.isPublished());
            ps.setInt(6, i.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating instruction", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean deleteInstruction(int id) {
        String sql = "DELETE FROM instructions WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting instruction", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    // -------------------------------------------------------------
    // Notices CRUD
    // -------------------------------------------------------------

    public List<Notice> getNotices(String audienceFilter, boolean onlyPublished) {
        List<Notice> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM notices WHERE 1=1 ");
        if (onlyPublished) {
            sql.append(" AND is_published = 1 ");
        }
        if (audienceFilter != null && !audienceFilter.isEmpty() && !"ALL".equalsIgnoreCase(audienceFilter)) {
            sql.append(" AND (target_audience = 'ALL' OR target_audience = ?) ");
        }
        sql.append(" ORDER BY published_at DESC, id DESC");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql.toString());
            if (audienceFilter != null && !audienceFilter.isEmpty() && !"ALL".equalsIgnoreCase(audienceFilter)) {
                ps.setString(1, audienceFilter.toUpperCase());
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                Notice n = new Notice();
                n.setId(rs.getInt("id"));
                n.setTitle(rs.getString("title"));
                n.setCategory(rs.getString("category"));
                n.setDescription(rs.getString("description"));
                n.setAttachmentUrl(rs.getString("attachment_url"));
                n.setTargetAudience(rs.getString("target_audience"));
                n.setPublished(rs.getBoolean("is_published"));
                n.setPublishedAt(rs.getTimestamp("published_at"));
                n.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(n);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching notices", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public boolean addNotice(Notice n) {
        String sql = "INSERT INTO notices (title, category, description, attachment_url, target_audience, is_published, published_at, created_by_admin_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, n.getTitle());
            ps.setString(2, n.getCategory() != null ? n.getCategory() : "GENERAL");
            ps.setString(3, n.getDescription());
            ps.setString(4, n.getAttachmentUrl());
            ps.setString(5, n.getTargetAudience() != null ? n.getTargetAudience() : "ALL");
            ps.setBoolean(6, n.isPublished());
            ps.setTimestamp(7, n.getPublishedAt() != null ? n.getPublishedAt() : new Timestamp(System.currentTimeMillis()));
            if (n.getCreatedByAdminId() != null) ps.setInt(8, n.getCreatedByAdminId()); else ps.setNull(8, Types.INTEGER);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding notice", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updateNotice(Notice n) {
        String sql = "UPDATE notices SET title = ?, category = ?, description = ?, attachment_url = ?, " +
                     "target_audience = ?, is_published = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, n.getTitle());
            ps.setString(2, n.getCategory());
            ps.setString(3, n.getDescription());
            ps.setString(4, n.getAttachmentUrl());
            ps.setString(5, n.getTargetAudience());
            ps.setBoolean(6, n.isPublished());
            ps.setInt(7, n.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating notice", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean deleteNotice(int id) {
        String sql = "DELETE FROM notices WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting notice", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    // -------------------------------------------------------------
    // Helpdesk Contacts CRUD
    // -------------------------------------------------------------

    public List<HelpdeskContact> getAllHelpdeskContacts(boolean onlyActive) {
        List<HelpdeskContact> list = new ArrayList<>();
        String sql = "SELECT * FROM helpdesk_contacts " + (onlyActive ? "WHERE status = 'ACTIVE' " : "") +
                     "ORDER BY sort_order ASC, id ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                HelpdeskContact c = new HelpdeskContact();
                c.setId(rs.getInt("id"));
                c.setCategory(rs.getString("category"));
                c.setTitle(rs.getString("title"));
                c.setContactPerson(rs.getString("contact_person"));
                c.setPhoneNumber(rs.getString("phone_number"));
                c.setEmail(rs.getString("email"));
                c.setTiming(rs.getString("timing"));
                c.setAddress(rs.getString("address"));
                c.setSortOrder(rs.getInt("sort_order"));
                c.setStatus(rs.getString("status"));
                list.add(c);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching helpdesk contacts", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public boolean addHelpdeskContact(HelpdeskContact c) {
        String sql = "INSERT INTO helpdesk_contacts (category, title, contact_person, phone_number, email, timing, address, sort_order, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, c.getCategory());
            ps.setString(2, c.getTitle());
            ps.setString(3, c.getContactPerson());
            ps.setString(4, c.getPhoneNumber());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getTiming());
            ps.setString(7, c.getAddress());
            ps.setInt(8, c.getSortOrder() > 0 ? c.getSortOrder() : 1);
            ps.setString(9, c.getStatus() != null ? c.getStatus() : "ACTIVE");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding helpdesk contact", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updateHelpdeskContact(HelpdeskContact c) {
        String sql = "UPDATE helpdesk_contacts SET category = ?, title = ?, contact_person = ?, " +
                     "phone_number = ?, email = ?, timing = ?, address = ?, sort_order = ?, status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, c.getCategory());
            ps.setString(2, c.getTitle());
            ps.setString(3, c.getContactPerson());
            ps.setString(4, c.getPhoneNumber());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getTiming());
            ps.setString(7, c.getAddress());
            ps.setInt(8, c.getSortOrder());
            ps.setString(9, c.getStatus());
            ps.setInt(10, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating helpdesk contact", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean deleteHelpdeskContact(int id) {
        String sql = "DELETE FROM helpdesk_contacts WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting helpdesk contact", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }
}
