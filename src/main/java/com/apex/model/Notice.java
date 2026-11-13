package com.apex.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Notice implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String category; // GENERAL, EXAMINATION, ADMISSION, RESULT, URGENT
    private String description;
    private String attachmentUrl;
    private String targetAudience; // ALL, STUDENTS, TEACHERS, COLLEGES
    private boolean isPublished;
    private Timestamp publishedAt;
    private Integer createdByAdminId;
    private Timestamp createdAt;

    public Notice() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }

    public Timestamp getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Timestamp publishedAt) { this.publishedAt = publishedAt; }

    public Integer getCreatedByAdminId() { return createdByAdminId; }
    public void setCreatedByAdminId(Integer createdByAdminId) { this.createdByAdminId = createdByAdminId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
