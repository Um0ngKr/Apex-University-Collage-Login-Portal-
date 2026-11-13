package com.apex.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Instruction implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String category;
    private String title;
    private String content;
    private int sortOrder;
    private boolean isPublished;
    private Timestamp createdAt;

    public Instruction() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
