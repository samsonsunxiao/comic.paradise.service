package com.mdd.admin.service.impl.comic.model;

import java.util.List;

public class ComicResponse {
    private int code;
    private String msg;
    private String page;
    private int pagecount;
    private int total;
    private List<Comic> list;
    
    // Getters and setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    
    public String getPage() { return page; }
    public void setPage(String page) { this.page = page; }
    
    public int getPagecount() { return pagecount; }
    public void setPagecount(int pagecount) { this.pagecount = pagecount; }
    
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    
    public List<Comic> getList() { return list; }
    public void setList(List<Comic> list) { this.list = list; }
}