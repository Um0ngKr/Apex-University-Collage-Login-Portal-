package com.apex.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Mark implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int examId;
    private String examName;
    private int studentId;
    private String studentName;
    private String registrationNo;
    private String rollNo;
    private int subjectId;
    private String subjectCode;
    private String subjectName;
    private double internalMarks;
    private double externalMarks;
    private double totalMarks;
    private double gradePoint;
    private String letterGrade;
    private boolean isPass;
    private Integer evaluatedByTeacherId;
    private String teacherName;
    private Timestamp createdAt;

    public Mark() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public double getInternalMarks() { return internalMarks; }
    public void setInternalMarks(double internalMarks) { this.internalMarks = internalMarks; }

    public double getExternalMarks() { return externalMarks; }
    public void setExternalMarks(double externalMarks) { this.externalMarks = externalMarks; }

    public double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(double totalMarks) { this.totalMarks = totalMarks; }

    public double getGradePoint() { return gradePoint; }
    public void setGradePoint(double gradePoint) { this.gradePoint = gradePoint; }

    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }

    public boolean isPass() { return isPass; }
    public void setPass(boolean pass) { isPass = pass; }

    public Integer getEvaluatedByTeacherId() { return evaluatedByTeacherId; }
    public void setEvaluatedByTeacherId(Integer evaluatedByTeacherId) { this.evaluatedByTeacherId = evaluatedByTeacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
