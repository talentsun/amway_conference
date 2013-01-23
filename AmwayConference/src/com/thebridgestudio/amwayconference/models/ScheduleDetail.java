package com.thebridgestudio.amwayconference.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="schedule_details")
public class ScheduleDetail {
    @DatabaseField(id=true, columnName="_id")
    private long id;
    
    @DatabaseField(canBeNull=false, foreign=true)
    private Schedule schedule;
    
    @DatabaseField
    private String content;
    
    @DatabaseField
    private String time;
    
    @DatabaseField
    private String feature;
    
    @DatabaseField
    private int type;
    
    public ScheduleDetail() {
    }

    public ScheduleDetail(long id, Schedule schedule, String content, String time, String feature, int type) {
        this.id = id;
        this.schedule = schedule;
        this.content = content;
        this.time = time;
        this.feature = feature;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }
}
