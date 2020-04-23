package com.quickids.playlog.model;

public class Video {
    private String path;
    private String name;
    private String datetime;
    private String runningTime;
    private int size;

    public Video(String path, String name, String datetime, String runningTime, int size) {
        this.path = path;
        this.name = name;
        this.datetime = datetime;
        this.runningTime = runningTime;
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(String runningTime) {
        this.runningTime = runningTime;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
