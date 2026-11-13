package com.apex.model;

import java.io.Serializable;

/**
 * Universal User session representation for Apex University System.
 * Distinguishes roles: ADMIN, TEACHER, STUDENT, COLLEGE.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String fullName;
    private String email;
    private String role; // "ADMIN", "TEACHER", "STUDENT", "COLLEGE"
    private String status;
    private int relatedId; // student_id, teacher_id, college_id, admin_id
    private Integer collegeId;
    private Integer departmentId;
    private Integer courseId;
    private Integer semesterId;
    private String registrationNo;
    private String rollNo;
    private String designation;
    private String collegeCode;

    public User() {}

    public User(int id, String username, String fullName, String email, String role, int relatedId) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.relatedId = relatedId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getRelatedId() { return relatedId; }
    public void setRelatedId(int relatedId) { this.relatedId = relatedId; }

    public Integer getCollegeId() { return collegeId; }
    public void setCollegeId(Integer collegeId) { this.collegeId = collegeId; }

    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public Integer getSemesterId() { return semesterId; }
    public void setSemesterId(Integer semesterId) { this.semesterId = semesterId; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getCollegeCode() { return collegeCode; }
    public void setCollegeCode(String collegeCode) { this.collegeCode = collegeCode; }

    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }
    public boolean isTeacher() { return "TEACHER".equalsIgnoreCase(role); }
    public boolean isStudent() { return "STUDENT".equalsIgnoreCase(role); }
    public boolean isCollege() { return "COLLEGE".equalsIgnoreCase(role); }
}
