package com.apex.dao;

import com.apex.config.DBConnectionManager;
import com.apex.model.Result;
import com.apex.util.GradeCalculator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResultDAO {
    private static final Logger LOGGER = Logger.getLogger(ResultDAO.class.getName());

    public Result getResultByStudentAndExam(int studentId, int examId, boolean onlyPublished) {
        String sql = "SELECT r.*, e.exam_name, e.exam_code, s.full_name AS student_name, " +
                     "s.registration_no, s.roll_no, c.name AS course_name, ses.session_code " +
                     "FROM results r " +
                     "JOIN examinations e ON r.exam_id = e.id " +
                     "JOIN students s ON r.student_id = s.id " +
                     "JOIN courses c ON r.course_id = c.id " +
                     "JOIN academic_sessions ses ON r.session_id = ses.id " +
                     "WHERE r.student_id = ? AND r.exam_id = ?" +
                     (onlyPublished ? " AND r.is_published = 1" : "");

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
                return extractResult(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student result", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    public List<Result> getResultsByStudent(int studentId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, e.exam_name, e.exam_code, s.full_name AS student_name, " +
                     "s.registration_no, s.roll_no, c.name AS course_name, ses.session_code " +
                     "FROM results r " +
                     "JOIN examinations e ON r.exam_id = e.id " +
                     "JOIN students s ON r.student_id = s.id " +
                     "JOIN courses c ON r.course_id = c.id " +
                     "JOIN academic_sessions ses ON r.session_id = ses.id " +
                     "WHERE r.student_id = ? AND r.is_published = 1 ORDER BY r.id DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractResult(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student results", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * Public search by registration number, roll number, optional exam id or semester.
     * Enforces strictly: ONLY published results!
     */
    public Result searchPublicResult(String regNo, String rollNo, Integer examId, Integer semesterNumber) {
        StringBuilder sql = new StringBuilder(
            "SELECT r.*, e.exam_name, e.exam_code, s.full_name AS student_name, " +
            "s.registration_no, s.roll_no, c.name AS course_name, ses.session_code " +
            "FROM results r " +
            "JOIN examinations e ON r.exam_id = e.id " +
            "JOIN students s ON r.student_id = s.id " +
            "JOIN courses c ON r.course_id = c.id " +
            "JOIN academic_sessions ses ON r.session_id = ses.id " +
            "WHERE r.is_published = 1 AND (s.registration_no = ? OR s.roll_no = ?) "
        );

        if (examId != null && examId > 0) {
            sql.append(" AND r.exam_id = ? ");
        }
        if (semesterNumber != null && semesterNumber > 0) {
            sql.append(" AND r.semester_number = ? ");
        }
        sql.append(" ORDER BY r.id DESC LIMIT 1");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, regNo != null ? regNo.trim() : "");
            ps.setString(2, rollNo != null ? rollNo.trim() : "");
            int idx = 3;
            if (examId != null && examId > 0) {
                ps.setInt(idx++, examId);
            }
            if (semesterNumber != null && semesterNumber > 0) {
                ps.setInt(idx++, semesterNumber);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractResult(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in public result search", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    public List<Result> getResultsByExam(int examId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, e.exam_name, e.exam_code, s.full_name AS student_name, " +
                     "s.registration_no, s.roll_no, c.name AS course_name, ses.session_code " +
                     "FROM results r " +
                     "JOIN examinations e ON r.exam_id = e.id " +
                     "JOIN students s ON r.student_id = s.id " +
                     "JOIN courses c ON r.course_id = c.id " +
                     "JOIN academic_sessions ses ON r.session_id = ses.id " +
                     "WHERE r.exam_id = ? ORDER BY s.roll_no ASC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, examId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractResult(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching exam results", e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * Calculates and compiles complete semester results for all students in an examination.
     */
    public int compileExamResults(int examId) {
        String fetchStudentsSql = "SELECT DISTINCT student_id FROM marks WHERE exam_id = ?";
        String fetchMarksSql = "SELECT m.total_marks, m.grade_point, m.is_pass, sub.credit, " +
                               "(sub.max_internal_marks + sub.max_external_marks) AS max_sub_marks " +
                               "FROM marks m JOIN subjects sub ON m.subject_id = sub.id " +
                               "WHERE m.exam_id = ? AND m.student_id = ?";
        String fetchStudentInfo = "SELECT s.course_id, s.session_id, sem.semester_number " +
                                 "FROM students s JOIN semesters sem ON s.current_semester_id = sem.id " +
                                 "WHERE s.id = ?";
        String insertResultSql = "INSERT INTO results (exam_id, student_id, course_id, semester_number, " +
                                 "session_id, total_obtained, total_max, percentage, sgpa, result_status, is_published) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1) " +
                                 "ON DUPLICATE KEY UPDATE total_obtained = VALUES(total_obtained), " +
                                 "total_max = VALUES(total_max), percentage = VALUES(percentage), " +
                                 "sgpa = VALUES(sgpa), result_status = VALUES(result_status), is_published = 1";

        Connection conn = null;
        PreparedStatement psStudents = null;
        PreparedStatement psMarks = null;
        PreparedStatement psInfo = null;
        PreparedStatement psInsert = null;
        ResultSet rsStudents = null;
        int count = 0;

        try {
            conn = DBConnectionManager.getInstance().getConnection();
            psStudents = conn.prepareStatement(fetchStudentsSql);
            psStudents.setInt(1, examId);
            rsStudents = psStudents.executeQuery();

            psMarks = conn.prepareStatement(fetchMarksSql);
            psInfo = conn.prepareStatement(fetchStudentInfo);
            psInsert = conn.prepareStatement(insertResultSql);

            while (rsStudents.next()) {
                int studentId = rsStudents.getInt("student_id");

                // Get course & sem info
                psInfo.setInt(1, studentId);
                int courseId = 1, sessionId = 2, semNum = 1;
                try (ResultSet rsInfo = psInfo.executeQuery()) {
                    if (rsInfo.next()) {
                        courseId = rsInfo.getInt("course_id");
                        sessionId = rsInfo.getInt("session_id");
                        semNum = rsInfo.getInt("semester_number");
                    }
                }

                // Compute marks aggregate
                psMarks.setInt(1, examId);
                psMarks.setInt(2, studentId);
                double totalObtained = 0.0;
                double totalMax = 0.0;
                double totalCreditPoints = 0.0;
                double totalCredits = 0.0;
                boolean hasFailed = false;

                try (ResultSet rsMarks = psMarks.executeQuery()) {
                    while (rsMarks.next()) {
                        double tot = rsMarks.getDouble("total_marks");
                        double max = rsMarks.getDouble("max_sub_marks");
                        double gp = rsMarks.getDouble("grade_point");
                        double cr = rsMarks.getDouble("credit");
                        boolean pass = rsMarks.getBoolean("is_pass");

                        totalObtained += tot;
                        totalMax += max;
                        totalCreditPoints += (gp * cr);
                        totalCredits += cr;
                        if (!pass) hasFailed = true;
                    }
                }

                double percentage = totalMax > 0 ? (totalObtained / totalMax) * 100.0 : 0.0;
                double sgpa = totalCredits > 0 ? (totalCreditPoints / totalCredits) : 0.0;
                String status = hasFailed ? "FAIL" : "PASS";

                psInsert.setInt(1, examId);
                psInsert.setInt(2, studentId);
                psInsert.setInt(3, courseId);
                psInsert.setInt(4, semNum);
                psInsert.setInt(5, sessionId);
                psInsert.setDouble(6, GradeCalculator.round(totalObtained, 2));
                psInsert.setDouble(7, GradeCalculator.round(totalMax, 2));
                psInsert.setDouble(8, GradeCalculator.round(percentage, 2));
                psInsert.setDouble(9, GradeCalculator.round(sgpa, 2));
                psInsert.setString(10, status);
                psInsert.executeUpdate();
                count++;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error compiling exam results", e);
        } finally {
            DBConnectionManager.close(null, psStudents, rsStudents);
            DBConnectionManager.close(null, psMarks);
            DBConnectionManager.close(null, psInfo);
            DBConnectionManager.close(conn, psInsert);
        }
        return count;
    }

    public Result getResultById(int id) {
        String sql = "SELECT r.*, e.exam_name, e.exam_code, s.full_name AS student_name, " +
                     "s.registration_no, s.roll_no, c.name AS course_name, ses.session_code " +
                     "FROM results r " +
                     "JOIN examinations e ON r.exam_id = e.id " +
                     "JOIN students s ON r.student_id = s.id " +
                     "JOIN courses c ON r.course_id = c.id " +
                     "JOIN academic_sessions ses ON r.session_id = ses.id " +
                     "WHERE r.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extractResult(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching result by id: " + id, e);
        } finally {
            DBConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    public boolean updateResult(Result r) {
        String sql = "UPDATE results SET total_obtained = ?, total_max = ?, percentage = ?, sgpa = ?, result_status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            ps = conn.prepareStatement(sql);
            ps.setDouble(1, r.getTotalObtained());
            ps.setDouble(2, r.getTotalMax());
            ps.setDouble(3, r.getPercentage());
            ps.setDouble(4, r.getSgpa());
            ps.setString(5, r.getResultStatus() != null ? r.getResultStatus() : "PASS");
            ps.setInt(6, r.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating result", e);
        } finally {
            DBConnectionManager.close(conn, ps);
        }
        return false;
    }

    private Result extractResult(ResultSet rs) throws SQLException {
        Result r = new Result();
        r.setId(rs.getInt("id"));
        r.setExamId(rs.getInt("exam_id"));
        r.setExamName(rs.getString("exam_name"));
        r.setExamCode(rs.getString("exam_code"));
        r.setStudentId(rs.getInt("student_id"));
        r.setStudentName(rs.getString("student_name"));
        r.setRegistrationNo(rs.getString("registration_no"));
        r.setRollNo(rs.getString("roll_no"));
        r.setCourseId(rs.getInt("course_id"));
        r.setCourseName(rs.getString("course_name"));
        r.setSemesterNumber(rs.getInt("semester_number"));
        r.setSessionId(rs.getInt("session_id"));
        r.setSessionCode(rs.getString("session_code"));
        r.setTotalObtained(rs.getDouble("total_obtained"));
        r.setTotalMax(rs.getDouble("total_max"));
        r.setPercentage(rs.getDouble("percentage"));
        r.setSgpa(rs.getDouble("sgpa"));
        r.setResultStatus(rs.getString("result_status"));
        r.setPublished(rs.getBoolean("is_published"));
        r.setPublishedAt(rs.getTimestamp("published_at"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
}
