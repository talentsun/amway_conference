package com.thebridgestudio.amwayconference.models;

import java.util.Date;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="schedules")
public class Schedule {
    @DatabaseField(id = true, columnName="_id")
    private long id;

    @DatabaseField
    private String content;
    
    @DatabaseField
    private long date;
    
    @DatabaseField
    private String time;
    
    @ForeignCollectionField(eager=false)
    private ForeignCollection<ScheduleDetail> scheduleDetails;
    
    public Schedule() {
    }
    
    public Schedule(long id, String content, long date, String time) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ForeignCollection<ScheduleDetail> getScheduleDetails() {
        return scheduleDetails;
    }

    public void setDetails(ForeignCollection<ScheduleDetail> details) {
        this.scheduleDetails = details;
    }
}
