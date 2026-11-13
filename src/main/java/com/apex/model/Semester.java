package com.apex.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Semester implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int courseId;
    private String courseName;
    private int semesterNumber;
    private String semesterName;
    private String status;
    private Timestamp createdAt;

    public Semester() {}

    public Semester(int id, int courseId, int semesterNumber, String semesterName, String status) {
        this.id = id;
        this.courseId = courseId;
        this.semesterNumber = semesterNumber;
        this.semesterName = semesterName;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getSemesterNumber() { return semesterNumber; }
    public void setSemesterNumber(int semesterNumber) { this.semesterNumber = semesterNumber; }

    public String getSemesterName() { return semesterName; }
    public void setSemesterName(String semesterName) { this.semesterName = semesterName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
