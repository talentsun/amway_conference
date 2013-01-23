package com.thebridgestudio.amwayconference.models;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="meetings")
public class Meeting {
    @DatabaseField(id = true, columnName="_id")
    private int id;
    
    @DatabaseField
    private String name;
    
    @DatabaseField
    private String engName;
    
    @DatabaseField
    private int duration;
    
    @DatabaseField
    private String address;
    
    @DatabaseField
    private Date startTime;
    
    @DatabaseField
    private Date endTime;
    
    public Meeting() {
    }
    
    public Meeting(int id, String name, String engName, int duration,
            String address, Date startTime, Date endTime) {
        this.id = id;
        this.name = name;
        this.engName = engName;
        this.duration = duration;
        this.address = address;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    protected int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected String getEngName() {
        return engName;
    }

    protected void setEngName(String engName) {
        this.engName = engName;
    }

    protected int getDuration() {
        return duration;
    }

    protected void setDuration(int duration) {
        this.duration = duration;
    }

    protected String getAddress() {
        return address;
    }

    protected void setAddress(String address) {
        this.address = address;
    }

    protected Date getStartTime() {
        return startTime;
    }

    protected void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    protected Date getEndTime() {
        return endTime;
    }

    protected void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
