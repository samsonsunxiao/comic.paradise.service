package com.mdd.admin.service.impl.comic.model;

import java.util.List;

public class Comic {
    private String id;
    private String title;
    private String descript;
    private String author;
    private int state;
    private String cover_image;
    private String tags;
    private String category;
    private String type;
    private List<Article> articles;
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescript() { return descript; }
    public void setDescript(String descript) { this.descript = descript; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public int getState() { return state; }
    public void setState(int state) { this.state = state; }
    
    public String getCover_image() { return cover_image; }
    public void setCover_image(String cover_image) { this.cover_image = cover_image; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public List<Article> getArticle() { return articles; }
    public void setArticle(List<Article> articles) { this.articles = articles; }
}