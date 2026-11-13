package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.Student;
import com.apex.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentDAO {
    private static final Logger LOGGER = Logger.getLogger(StudentDAO.class.getName());

    public List<Student> getAllStudents(Integer collegeId, String search, int offset, int limit) {
        List<Student> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT s.*, c.name AS college_name, d.name AS dept_name, co.name AS course_name, " +
            "sem.semester_number, ses.session_code " +
            "FROM students s " +
            "LEFT JOIN colleges c ON s.college_id = c.id " +
            "LEFT JOIN departments d ON s.department_id = d.id " +
            "LEFT JOIN courses co ON s.course_id = co.id " +
            "LEFT JOIN semesters sem ON s.current_semester_id = sem.id " +
            "LEFT JOIN academic_sessions ses ON s.session_id = ses.id " +
            "WHERE 1=1 "
        );

        if (collegeId != null && collegeId > 0) {
            sql.append(" AND s.college_id = ? ");
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (s.full_name LIKE ? OR s.registration_no LIKE ? OR s.roll_no LIKE ? OR s.email LIKE ?) ");
        }
        sql.append(" ORDER BY s.id DESC LIMIT ? OFFSET ?");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            if (collegeId != null && collegeId > 0) {
                ps.setInt(idx++, collegeId);
            }
            if (search != null && !search.trim().isEmpty()) {
                String term = "%" + search.trim() + "%";
                ps.setString(idx++, term);
                ps.setString(idx++, term);
                ps.setString(idx++, term);
                ps.setString(idx++, term);
            }
            ps.setInt(idx++, limit > 0 ? limit : 50);
            ps.setInt(idx++, Math.max(offset, 0));

            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractStudent(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching students", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    public int getStudentCount(Integer collegeId, String search) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM students s WHERE 1=1 ");
        if (collegeId != null && collegeId > 0) {
            sql.append(" AND s.college_id = ? ");
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (s.full_name LIKE ? OR s.registration_no LIKE ? OR s.roll_no LIKE ? OR s.email LIKE ?) ");
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql.toString());
            int idx = 1;
            if (collegeId != null && collegeId > 0) {
                ps.setInt(idx++, collegeId);
            }
            if (search != null && !search.trim().isEmpty()) {
                String term = "%" + search.trim() + "%";
                ps.setString(idx++, term);
                ps.setString(idx++, term);
                ps.setString(idx++, term);
                ps.setString(idx++, term);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting students", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return 0;
    }

    public Student getStudentById(int id) {
        String sql = "SELECT s.*, c.name AS college_name, d.name AS dept_name, co.name AS course_name, " +
                     "sem.semester_number, ses.session_code " +
                     "FROM students s " +
                     "LEFT JOIN colleges c ON s.college_id = c.id " +
                     "LEFT JOIN departments d ON s.department_id = d.id " +
                     "LEFT JOIN courses co ON s.course_id = co.id " +
                     "LEFT JOIN semesters sem ON s.current_semester_id = sem.id " +
                     "LEFT JOIN academic_sessions ses ON s.session_id = ses.id " +
                     "WHERE s.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractStudent(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student by id: " + id, e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    public Student getStudentByRegOrRoll(String identifier) {
        String sql = "SELECT s.*, c.name AS college_name, d.name AS dept_name, co.name AS course_name, " +
                     "sem.semester_number, ses.session_code " +
                     "FROM students s " +
                     "LEFT JOIN colleges c ON s.college_id = c.id " +
                     "LEFT JOIN departments d ON s.department_id = d.id " +
                     "LEFT JOIN courses co ON s.course_id = co.id " +
                     "LEFT JOIN semesters sem ON s.current_semester_id = sem.id " +
                     "LEFT JOIN academic_sessions ses ON s.session_id = ses.id " +
                     "WHERE s.registration_no = ? OR s.roll_no = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, identifier);
            ps.setString(2, identifier);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractStudent(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student by identifier", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    public boolean addStudent(Student s) {
        String sql = "INSERT INTO students (registration_no, roll_no, full_name, email, phone, dob, gender, " +
                     "blood_group, father_name, mother_name, address, college_id, department_id, course_id, " +
                     "current_semester_id, session_id, password_hash, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, s.getRegistrationNo());
            ps.setString(2, s.getRollNo());
            ps.setString(3, s.getFullName());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setDate(6, s.getDob());
            ps.setString(7, s.getGender() != null ? s.getGender() : "MALE");
            ps.setString(8, s.getBloodGroup());
            ps.setString(9, s.getFatherName());
            ps.setString(10, s.getMotherName());
            ps.setString(11, s.getAddress());
            ps.setInt(12, s.getCollegeId());
            ps.setInt(13, s.getDepartmentId());
            ps.setInt(14, s.getCourseId());
            ps.setInt(15, s.getCurrentSemesterId());
            ps.setInt(16, s.getSessionId());
            String hash = s.getPasswordHash() != null && !s.getPasswordHash().isEmpty()
                    ? s.getPasswordHash() : PasswordUtil.hashPassword("student123");
            ps.setString(17, hash);
            ps.setString(18, s.getStatus() != null ? s.getStatus() : "ACTIVE");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        s.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting student", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean updateStudent(Student s) {
        String sql = "UPDATE students SET registration_no = ?, roll_no = ?, full_name = ?, email = ?, phone = ?, " +
                     "dob = ?, gender = ?, blood_group = ?, father_name = ?, mother_name = ?, address = ?, " +
                     "college_id = ?, department_id = ?, course_id = ?, current_semester_id = ?, session_id = ?, " +
                     "status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, s.getRegistrationNo());
            ps.setString(2, s.getRollNo());
            ps.setString(3, s.getFullName());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setDate(6, s.getDob());
            ps.setString(7, s.getGender());
            ps.setString(8, s.getBloodGroup());
            ps.setString(9, s.getFatherName());
            ps.setString(10, s.getMotherName());
            ps.setString(11, s.getAddress());
            ps.setInt(12, s.getCollegeId());
            ps.setInt(13, s.getDepartmentId());
            ps.setInt(14, s.getCourseId());
            ps.setInt(15, s.getCurrentSemesterId());
            ps.setInt(16, s.getSessionId());
            ps.setString(17, s.getStatus());
            ps.setInt(18, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating student", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting student", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    public List<Student> getStudentsForSubjectAssignment(int subjectId, int courseId, int semesterId) {
        List<Student> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();

            // 1. First attempt: match by exact course_id and semester_id
            if (courseId > 0 && semesterId > 0) {
                String sql = "SELECT s.*, c.name AS college_name, d.name AS dept_name, co.name AS course_name, " +
                             "sem.semester_number, ses.session_code " +
                             "FROM students s " +
                             "LEFT JOIN colleges c ON s.college_id = c.id " +
                             "LEFT JOIN departments d ON s.department_id = d.id " +
                             "LEFT JOIN courses co ON s.course_id = co.id " +
                             "LEFT JOIN semesters sem ON s.current_semester_id = sem.id " +
                             "LEFT JOIN academic_sessions ses ON s.session_id = ses.id " +
                             "WHERE s.course_id = ? AND s.current_semester_id = ? AND s.status = 'ACTIVE' " +
                             "ORDER BY s.roll_no ASC";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, courseId);
                ps.setInt(2, semesterId);
                rs = ps.executeQuery();
                while (rs.next()) {
                    list.add(extractStudent(rs));
                }
            }

            // 2. Second attempt: match by semester_number if list is still empty
            if (list.isEmpty() && semesterId > 0) {
                DBConnectionManager.close(null, ps, rs);
                String semNumSql = "SELECT s.*, c.name AS college_name, d.name AS dept_name, co.name AS course_name, " +
                                   "sem.semester_number, ses.session_code " +
                                   "FROM students s " +
                                   "LEFT JOIN colleges c ON s.college_id = c.id " +
                                   "LEFT JOIN departments d ON s.department_id = d.id " +
                                   "LEFT JOIN courses co ON s.course_id = co.id " +
                                   "LEFT JOIN semesters sem ON s.current_semester_id = sem.id " +
                                   "LEFT JOIN academic_sessions ses ON s.session_id = ses.id " +
                                   "JOIN semesters target_sem ON target_sem.id = ? " +
                                   "WHERE sem.semester_number = target_sem.semester_number AND s.status = 'ACTIVE' " +
                                   "ORDER BY s.roll_no ASC";
                ps = conn.prepareStatement(semNumSql);
                ps.setInt(1, semesterId);
                rs = ps.executeQuery();
                while (rs.next()) {
                    list.add(extractStudent(rs));
                }
            }

            // 3. Fallback: all active students
            if (list.isEmpty()) {
                DBConnectionManager.close(null, ps, rs);
                list = getAllStudents(null, null, 0, 100);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching students for subject assignment: sub=" + subjectId + ", course=" + courseId + ", sem=" + semesterId, e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    private Student extractStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setRegistrationNo(rs.getString("registration_no"));
        s.setRollNo(rs.getString("roll_no"));
        s.setFullName(rs.getString("full_name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setDob(rs.getDate("dob"));
        s.setGender(rs.getString("gender"));
        s.setBloodGroup(rs.getString("blood_group"));
        s.setFatherName(rs.getString("father_name"));
        s.setMotherName(rs.getString("mother_name"));
        s.setAddress(rs.getString("address"));
        s.setCollegeId(rs.getInt("college_id"));
        s.setCollegeName(rs.getString("college_name"));
        s.setDepartmentId(rs.getInt("department_id"));
        s.setDepartmentName(rs.getString("dept_name"));
        s.setCourseId(rs.getInt("course_id"));
        s.setCourseName(rs.getString("course_name"));
        s.setCurrentSemesterId(rs.getInt("current_semester_id"));
        s.setSemesterNumber(rs.getInt("semester_number"));
        s.setSessionId(rs.getInt("session_id"));
        s.setSessionCode(rs.getString("session_code"));
        s.setStatus(rs.getString("status"));
        s.setProfileImage(rs.getString("profile_image"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}
