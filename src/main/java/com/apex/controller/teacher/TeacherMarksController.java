package com.apex.controller.teacher;

import com.apex.dao.ExaminationDAO;
import com.apex.dao.MarkDAO;
import com.apex.dao.StudentDAO;
import com.apex.dao.TeacherDAO;
import com.apex.model.Examination;
import com.apex.model.Mark;
import com.apex.model.Student;
import com.apex.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/teacher/marks", "/teacher/marks-action"})
public class TeacherMarksController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private TeacherDAO teacherDAO;
    private MarkDAO markDAO;
    private ExaminationDAO examDAO;
    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        teacherDAO = new TeacherDAO();
        markDAO = new MarkDAO();
        examDAO = new ExaminationDAO();
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?role=TEACHER");
            return;
        }
        int teacherId = getTeacherId(user);

        List<Map<String, Object>> assignments = teacherDAO.getAssignedSubjects(teacherId);
        List<Examination> exams = examDAO.getAllExaminations();

        req.setAttribute("assignments", assignments);
        req.setAttribute("exams", exams);

        String examIdStr = req.getParameter("exam_id");
        String subjectIdStr = req.getParameter("subject_id");

        if (examIdStr != null && !examIdStr.trim().isEmpty() && subjectIdStr != null && !subjectIdStr.trim().isEmpty()) {
            int examId;
            int subjectId;
            try {
                examId = Integer.parseInt(examIdStr.trim());
                subjectId = Integer.parseInt(subjectIdStr.trim());
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/teacher/marks?error=Invalid+exam+or+subject+identifier.");
                return;
            }

            // Security check: teacher must be assigned to this subject
            Map<String, Object> selectedAssignment = null;
            for (Map<String, Object> asg : assignments) {
                if (asg.get("subjectId") != null && ((Integer) asg.get("subjectId")) == subjectId) {
                    selectedAssignment = asg;
                    break;
                }
            }

            if (selectedAssignment == null) {
                resp.sendRedirect(req.getContextPath() + "/teacher/marks?error=Unauthorized:+You+are+not+assigned+to+this+subject.");
                return;
            }

            int courseId = selectedAssignment.get("courseId") != null ? (Integer) selectedAssignment.get("courseId") : 0;
            int semesterId = selectedAssignment.get("semesterId") != null ? (Integer) selectedAssignment.get("semesterId") : 0;

            List<Mark> marks = markDAO.getMarksByExamAndSubject(examId, subjectId);
            List<Student> students = studentDAO.getStudentsForSubjectAssignment(subjectId, courseId, semesterId);

            req.setAttribute("selectedExamId", examId);
            req.setAttribute("selectedSubjectId", subjectId);
            req.setAttribute("marks", marks);
            req.setAttribute("students", students);
            req.setAttribute("selectedAssignment", selectedAssignment);
        }

        req.getRequestDispatcher("/WEB-INF/views/teacher/marks.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("CURRENT_USER");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?role=TEACHER");
            return;
        }
        int teacherId = getTeacherId(user);
        if (teacherId <= 0) {
            resp.sendRedirect(req.getContextPath() + "/login?role=TEACHER");
            return;
        }

        int examId;
        int subjectId;
        int studentId;
        double internal;
        try {
            examId = Integer.parseInt(req.getParameter("exam_id"));
            subjectId = Integer.parseInt(req.getParameter("subject_id"));
            studentId = Integer.parseInt(req.getParameter("student_id"));
            internal = Double.parseDouble(req.getParameter("internal_marks"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/teacher/marks?error=Invalid+form+data+submitted.");
            return;
        }

        // Security check: teacher must be assigned to this subject
        if (!teacherDAO.isSubjectAssignedToTeacher(teacherId, subjectId)) {
            resp.sendRedirect(req.getContextPath() + "/teacher/marks?error=Unauthorized:+You+are+not+assigned+to+upload+marks+for+this+subject.");
            return;
        }

        Mark m = new Mark();
        m.setExamId(examId);
        m.setSubjectId(subjectId);
        m.setStudentId(studentId);
        m.setInternalMarks(internal);
        m.setEvaluatedByTeacherId(teacherId);

        // Fetch existing mark if external was already entered
        List<Mark> existing = markDAO.getMarksByExamAndStudent(examId, studentId);
        for (Mark ex : existing) {
            if (ex.getSubjectId() == subjectId) {
                m.setExternalMarks(ex.getExternalMarks());
                break;
            }
        }

        markDAO.saveOrUpdateMark(m);

        resp.sendRedirect(req.getContextPath() + "/teacher/marks?exam_id=" + examId + "&subject_id=" + subjectId + "&msg=Internal+marks+saved.");
    }

    private int getTeacherId(User user) {
        if (user == null) {
            return 0;
        }
        int teacherId = user.getRelatedId();
        if (teacherId <= 0 && user.getId() > 0) {
            teacherId = user.getId();
        }
        if (teacherId <= 0 && user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            com.apex.model.Teacher t = teacherDAO.getTeacherByEmployeeId(user.getUsername().trim());
            if (t != null) {
                teacherId = t.getId();
                user.setRelatedId(teacherId);
            }
        }
        return teacherId;
    }
}
