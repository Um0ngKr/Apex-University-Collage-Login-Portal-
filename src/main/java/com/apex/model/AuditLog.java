package com.apex.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class AuditLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String userType;
    private Integer userId;
    private String username;
    private String action;
    private String tableAffected;
    private Integer recordId;
    private String details;
    private String ipAddress;
    private Timestamp createdAt;

    public AuditLog() {}

    public AuditLog(String userType, Integer userId, String username, String action, String tableAffected, Integer recordId, String details, String ipAddress) {
        this.userType = userType;
        this.userId = userId;
        this.username = username;
        this.action = action;
        this.tableAffected = tableAffected;
        this.recordId = recordId;
        this.details = details;
        this.ipAddress = ipAddress;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getTableAffected() { return tableAffected; }
    public void setTableAffected(String tableAffected) { this.tableAffected = tableAffected; }

    public Integer getRecordId() { return recordId; }
    public void setRecordId(Integer recordId) { this.recordId = recordId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
