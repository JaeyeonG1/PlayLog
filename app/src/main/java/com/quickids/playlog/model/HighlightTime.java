package com.quickids.playlog.model;

public class HighlightTime {
    private long startTime;
    private long endTime;

    public HighlightTime() {
        this.startTime = 0;
        this.endTime = 0;
    }

    public HighlightTime(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
