package com.apex.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class Examination implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String examName;
    private String examCode;
    private int sessionId;
    private String sessionCode;
    private int semesterNumber;
    private Date startDate;
    private Date endDate;
    private String examType; // REGULAR, BACKLOG, SPECIAL
    private double feeAmount;
    private String status; // UPCOMING, ACTIVE, FORM_FILLUP, CLOSED, COMPLETED
    private boolean isResultPublished;
    private Timestamp createdAt;

    public Examination() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public String getSessionCode() { return sessionCode; }
    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }

    public int getSemesterNumber() { return semesterNumber; }
    public void setSemesterNumber(int semesterNumber) { this.semesterNumber = semesterNumber; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public double getFeeAmount() { return feeAmount; }
    public void setFeeAmount(double feeAmount) { this.feeAmount = feeAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isResultPublished() { return isResultPublished; }
    public void setResultPublished(boolean resultPublished) { isResultPublished = resultPublished; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
