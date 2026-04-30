package com.simplenotes.app;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Note {
    private int id;
    private String title;
    private String content;
    private String createdDate;
    private long reminderTime;
    
    public Note() {
        this.createdDate = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    
    public long getReminderTime() { return reminderTime; }
    public void setReminderTime(long reminderTime) { this.reminderTime = reminderTime; }
}
