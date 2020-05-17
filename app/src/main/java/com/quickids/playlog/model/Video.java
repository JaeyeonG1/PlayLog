package com.quickids.playlog.model;

import android.graphics.Bitmap;

//Video Type:
//100 : Match
//101 : Highlight
//200 : Training
//201 : Processed
public interface Video {
    public void setPath(String path);
    public void setName(String name);
    public void setDate(String date);
    public void setRunningTime(String runningTime);
    public void setExtn(String extn);
    public void setSize(int size);
    public void setThumbnail(Bitmap thumbnail);
    String getPath();
    String getName();
    String getDate();
    String getRunningTime();
    String getExtn();
    int getSize();
    Bitmap getThumbnail();
    int getVideoType();
}
