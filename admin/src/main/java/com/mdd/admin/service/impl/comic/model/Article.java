package com.mdd.admin.service.impl.comic.model;

import java.util.List;
//章节
public class Article {
    private int id;
    private String title;
    private List<String> items;
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public List<String> getItems() { return items; }
    public void setItems(List<String> items) { this.items = items; }
}