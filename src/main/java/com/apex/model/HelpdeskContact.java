package com.apex.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class HelpdeskContact implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String category;
    private String title;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String timing;
    private String address;
    private int sortOrder;
    private String status;
    private Timestamp createdAt;

    public HelpdeskContact() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTiming() { return timing; }
    public void setTiming(String timing) { this.timing = timing; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
