package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.Course;
import com.apex.model.Semester;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CourseDAO {
    private static final Logger LOGGER = Logger.getLogger(CourseDAO.class.getName());

    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, d.name AS dept_name FROM courses c " +
                     "LEFT JOIN departments d ON c.department_id = d.id " +
                     "ORDER BY c.id ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Course c = new Course();
                c.setId(rs.getInt("id"));
                c.setDepartmentId(rs.getInt("department_id"));
                c.setDepartmentName(rs.getString("dept_name"));
                c.setCourseCode(rs.getString("course_code"));
                c.setName(rs.getString("name"));
                c.setDurationYears(rs.getInt("duration_years"));
                c.setTotalSemesters(rs.getInt("total_semesters"));
                c.setDegreeLevel(rs.getString("degree_level"));
                c.setStatus(rs.getString("status"));
                c.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(c);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching courses", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public boolean addCourse(Course c) {
        String sql = "INSERT INTO courses (department_id, course_code, name, duration_years, total_semesters, degree_level, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, c.getDepartmentId());
            ps.setString(2, c.getCourseCode());
            ps.setString(3, c.getName());
            ps.setInt(4, c.getDurationYears() > 0 ? c.getDurationYears() : 4);
            ps.setInt(5, c.getTotalSemesters() > 0 ? c.getTotalSemesters() : 8);
            ps.setString(6, c.getDegreeLevel() != null ? c.getDegreeLevel() : "UG");
            ps.setString(7, c.getStatus() != null ? c.getStatus() : "ACTIVE");
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int courseId = keys.getInt(1);
                        c.setId(courseId);
                        // Auto-generate semesters
                        generateSemestersForCourse(conn, courseId, c.getTotalSemesters());
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding course", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    private void generateSemestersForCourse(Connection conn, int courseId, int totalSemesters) {
        String semSql = "INSERT IGNORE INTO semesters (course_id, semester_number, semester_name, status) VALUES (?, ?, ?, 'ACTIVE')";
        try (PreparedStatement psSem = conn.prepareStatement(semSql)) {
            for (int i = 1; i <= totalSemesters; i++) {
                psSem.setInt(1, courseId);
                psSem.setInt(2, i);
                psSem.setString(3, "Semester " + i);
                psSem.addBatch();
            }
            psSem.executeBatch();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error auto-generating semesters for course: " + courseId, e);
        }
    }

    public boolean updateCourse(Course c) {
        String sql = "UPDATE courses SET department_id = ?, course_code = ?, name = ?, duration_years = ?, " +
                     "total_semesters = ?, degree_level = ?, status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, c.getDepartmentId());
            ps.setString(2, c.getCourseCode());
            ps.setString(3, c.getName());
            ps.setInt(4, c.getDurationYears());
            ps.setInt(5, c.getTotalSemesters());
            ps.setString(6, c.getDegreeLevel());
            ps.setString(7, c.getStatus());
            ps.setInt(8, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating course", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean deleteCourse(int id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting course", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public List<Semester> getSemestersByCourse(int courseId) {
        List<Semester> list = new ArrayList<>();
        String sql = "SELECT * FROM semesters WHERE course_id = ? ORDER BY semester_number ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Semester s = new Semester();
                s.setId(rs.getInt("id"));
                s.setCourseId(rs.getInt("course_id"));
                s.setSemesterNumber(rs.getInt("semester_number"));
                s.setSemesterName(rs.getString("semester_name"));
                s.setStatus(rs.getString("status"));
                list.add(s);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching semesters for course: " + courseId, e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public List<Semester> getAllSemesters() {
        List<Semester> list = new ArrayList<>();
        String sql = "SELECT s.*, c.name AS course_name FROM semesters s " +
                     "JOIN courses c ON s.course_id = c.id ORDER BY s.course_id, s.semester_number ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Semester s = new Semester();
                s.setId(rs.getInt("id"));
                s.setCourseId(rs.getInt("course_id"));
                s.setCourseName(rs.getString("course_name"));
                s.setSemesterNumber(rs.getInt("semester_number"));
                s.setSemesterName(rs.getString("semester_name"));
                s.setStatus(rs.getString("status"));
                list.add(s);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all semesters", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }
}
