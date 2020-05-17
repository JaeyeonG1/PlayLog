package com.quickids.playlog.model;

import android.graphics.Bitmap;

public class TrainingVideo implements Video{
    Bitmap thumbnail;
    String path; //absolute path
    String name; //file name
    String date; //save date
    String runningTime; //running time of video
    String extn; //file extension of video
    int size; // size for getting byte
    final int videoType = 200;

    public TrainingVideo(Bitmap thumbnail, String path, String name, String date, String runningTime, String extn, int size) {
        this.thumbnail = thumbnail;
        this.path = path;
        this.name = name;
        this.date = date;
        this.runningTime = runningTime;
        this.extn = extn;
        this.size = size;
    }
    @Override
    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(String runningTime) {
        this.runningTime = runningTime;
    }

    public String getExtn() {
        return extn;
    }

    public void setExtn(String extn) {
        this.extn = extn;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getVideoType() {
        return videoType;
    }

}
