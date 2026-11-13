package com.apex.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class AcademicSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String sessionCode;
    private int startYear;
    private int endYear;
    private boolean isCurrent;
    private String status;
    private Timestamp createdAt;

    public AcademicSession() {}

    public AcademicSession(int id, String sessionCode, int startYear, int endYear, boolean isCurrent, String status) {
        this.id = id;
        this.sessionCode = sessionCode;
        this.startYear = startYear;
        this.endYear = endYear;
        this.isCurrent = isCurrent;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSessionCode() { return sessionCode; }
    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }

    public int getStartYear() { return startYear; }
    public void setStartYear(int startYear) { this.startYear = startYear; }

    public int getEndYear() { return endYear; }
    public void setEndYear(int endYear) { this.endYear = endYear; }

    public boolean isCurrent() { return isCurrent; }
    public void setCurrent(boolean current) { isCurrent = current; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
