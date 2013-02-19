package com.thebridgestudio.amwayconference.cloudapis;

import java.util.List;

import it.restrung.rest.annotations.JsonProperty;
import it.restrung.rest.marshalling.response.AbstractJSONResponse;

public class SyncScheduleResponse extends AbstractJSONResponse {
    private static final long serialVersionUID = 1L;

    @JsonProperty(value="result")
    private int result;
    
    @JsonProperty(value="data")
    private Data data;
    
    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
    
    public SyncScheduleResponse() {
    }

    @SuppressWarnings("serial")
    public static class Data extends AbstractJSONResponse {
        @JsonProperty(value="need_refresh")
        private int needRefresh;
        
        @JsonProperty(value="schedule")
        private List<Schedule> schedules;
        
        @JsonProperty(value="detail")
        private List<ScheduleDetail> scheduleDetails;

        public int getNeedRefresh() {
            return needRefresh;
        }

        public void setNeedRefresh(int needRefresh) {
            this.needRefresh = needRefresh;
        }

        public List<Schedule> getSchedules() {
            return schedules;
        }

        public void setSchedules(List<Schedule> schedules) {
            this.schedules = schedules;
        }

        public List<ScheduleDetail> getScheduleDetails() {
            return scheduleDetails;
        }

        public void setScheduleDetails(List<ScheduleDetail> scheduleDetails) {
            this.scheduleDetails = scheduleDetails;
        }
        
        public Data() {
        }
    }
    
    @SuppressWarnings("serial")
    public static class Schedule extends AbstractJSONResponse {
        @JsonProperty(value="id")
        private long id;
        
        @JsonProperty(value="date")
        private long date;
        
        @JsonProperty(value="time")
        private String time;
        
        @JsonProperty(value="content")
        private String content;

        @JsonProperty(value="tips")
        private String tips;
        
        @JsonProperty(value="valid")
        private boolean valid;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
        
        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }

        public boolean getValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public Schedule() {
        }
    }
    
    @SuppressWarnings("serial")
    public static class ScheduleDetail extends AbstractJSONResponse {
        @JsonProperty(value="id")
        private long id;
        
        @JsonProperty(value="sid")
        private long sid;
        
        @JsonProperty(value="time")
        private String time;
        
        @JsonProperty(value="content")
        private String content;
        
        @JsonProperty(value="feature")
        private String feature;
        
        @JsonProperty(value="type")
        private int type;
        
        @JsonProperty(value="valid")
        private boolean valid;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getSid() {
            return sid;
        }

        public void setSid(long sid) {
            this.sid = sid;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getFeature() {
            return feature;
        }

        public void setFeature(String feature) {
            this.feature = feature;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public boolean getValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public ScheduleDetail() {
        }
    }
}
