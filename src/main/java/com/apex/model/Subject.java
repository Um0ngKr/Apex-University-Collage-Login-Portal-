package com.apex.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Subject implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int courseId;
    private String courseName;
    private int semesterId;
    private int semesterNumber;
    private String subjectCode;
    private String name;
    private double credit;
    private int maxInternalMarks;
    private int maxExternalMarks;
    private int passMarks;
    private String subjectType;
    private String status;
    private Timestamp createdAt;

    public Subject() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getSemesterId() { return semesterId; }
    public void setSemesterId(int semesterId) { this.semesterId = semesterId; }

    public int getSemesterNumber() { return semesterNumber; }
    public void setSemesterNumber(int semesterNumber) { this.semesterNumber = semesterNumber; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getCredit() { return credit; }
    public void setCredit(double credit) { this.credit = credit; }

    public int getMaxInternalMarks() { return maxInternalMarks; }
    public void setMaxInternalMarks(int maxInternalMarks) { this.maxInternalMarks = maxInternalMarks; }

    public int getMaxExternalMarks() { return maxExternalMarks; }
    public void setMaxExternalMarks(int maxExternalMarks) { this.maxExternalMarks = maxExternalMarks; }

    public int getPassMarks() { return passMarks; }
    public void setPassMarks(int passMarks) { this.passMarks = passMarks; }

    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
