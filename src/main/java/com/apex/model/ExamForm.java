package com.apex.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ExamForm implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int examId;
    private String examName;
    private String examCode;
    private int studentId;
    private String studentName;
    private String registrationNo;
    private String rollNo;
    private int collegeId;
    private String collegeName;
    private String courseName;
    private int semesterNumber;
    private Timestamp submissionDate;
    private String collegeApprovalStatus; // PENDING, APPROVED, REJECTED
    private String adminApprovalStatus;   // PENDING, APPROVED, REJECTED
    private String paymentStatus;         // PENDING, PAID, WAIVED
    private String rejectionReason;
    private boolean admitCardIssued;
    private double feeAmount;
    private String transactionId;

    public ExamForm() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public int getCollegeId() { return collegeId; }
    public void setCollegeId(int collegeId) { this.collegeId = collegeId; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getSemesterNumber() { return semesterNumber; }
    public void setSemesterNumber(int semesterNumber) { this.semesterNumber = semesterNumber; }

    public Timestamp getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(Timestamp submissionDate) { this.submissionDate = submissionDate; }

    public String getCollegeApprovalStatus() { return collegeApprovalStatus; }
    public void setCollegeApprovalStatus(String collegeApprovalStatus) { this.collegeApprovalStatus = collegeApprovalStatus; }

    public String getAdminApprovalStatus() { return adminApprovalStatus; }
    public void setAdminApprovalStatus(String adminApprovalStatus) { this.adminApprovalStatus = adminApprovalStatus; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public boolean isAdmitCardIssued() { return admitCardIssued; }
    public void setAdmitCardIssued(boolean admitCardIssued) { this.admitCardIssued = admitCardIssued; }

    public double getFeeAmount() { return feeAmount; }
    public void setFeeAmount(double feeAmount) { this.feeAmount = feeAmount; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}
