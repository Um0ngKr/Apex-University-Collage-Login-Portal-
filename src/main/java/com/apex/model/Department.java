package com.apex.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String deptCode;
    private String name;
    private String description;
    private String status;
    private Timestamp createdAt;

    public Department() {}

    public Department(int id, String deptCode, String name, String description, String status) {
        this.id = id;
        this.deptCode = deptCode;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDeptCode() { return deptCode; }
    public void setDeptCode(String deptCode) { this.deptCode = deptCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
