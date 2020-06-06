package com.quickids.playlog.service;

import android.app.ProgressDialog;
import android.content.Context;

import com.quickids.playlog.model.MatchVideo;
import com.quickids.playlog.model.TrainingVideo;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Editor {

    static int TRAINING = 1;
    static int HIGHLIGHT = 2;
    private FFMpegManager ffMpegManager;
    private Context context;
    private int jobCount ;
    private int requestJobCount;
    private boolean isFinish;
    public Editor(Context c, int job){
        this.context = c;
        jobCount = 0;
        isFinish = false;
        ffMpegManager = new FFMpegManager();
        ffMpegManager.loadFFMpegBinary(c, this);
    }
    // 훈련 영상 슬로우 모션 효과 적용
    public void processTrainingVideo(TrainingVideo tv) throws IOException {

        //처리해야할 작업량
        requestJobCount = 5;
        String filePath = tv.getPath();
        String extn = tv.getExtn();
        String name = tv.getName();
        String currentPath = filePath+name+"."+extn;
        String destPath = filePath+"Processed/"+name+"(SlowMotion)."+extn;
        String top = filePath+"Processed/Temp/"+name+"temp01."+extn;
        String mid = filePath+"Processed/Temp/"+name+"temp02."+extn;
        String bottom = filePath+"Processed/Temp/"+name+"temp04."+extn;

        //객체 움직임 감지 구현부

        //split video 구현부
        ffMpegManager.executeSplitVideoCommand(currentPath,top,0,4500);

        ffMpegManager.executeSplitVideoCommand(currentPath,mid,4500,1000); //슛 감지 후 1초(임시)
        ffMpegManager.executeSplitVideoCommand(currentPath,bottom,5500,0);
        ffMpegManager.executeSlowMotionVideoCommand(mid,filePath+"Processed/Temp/"+name+"temp03."+extn);
        //merge video 구현부
        ArrayList<String> cmd = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        mid = filePath+"Processed/Temp/"+name+"temp03."+extn;
        String fileList[] = {top, mid, bottom};
        for(int i = 0; i < fileList.length; i++){
            cmd.add("-i");
            cmd.add(fileList[i]);
            sb.append("[").append(i).append(":0] [").append(i).append(":1]");
        }
        sb.append(" concat=n=").append(fileList.length).append(":v=1:a=1 [v] [a]");
        cmd.add("-filter_complex");
        cmd.add(sb.toString());
        cmd.add("-map");
        cmd.add("[v]");
        cmd.add("-map");
        cmd.add("[a]");
        cmd.add("-preset");
        cmd.add("ultrafast");
        cmd.add(destPath);

        sb = new StringBuilder();
        for (String str : cmd)
        {
            sb.append(str).append(" ");
        }
        String[] command = cmd.toArray(new String[cmd.size()]);
        ffMpegManager.executeMergeVideoCommand(command);
    }

    // 하이라이트 영상 생성
    public void createHighlightVideo(MatchVideo v){

    }
    public int getRequestJobCount(){
        return requestJobCount;
    }
    public void increaseJobCount(){
        jobCount ++;
    }
    public int getJobCount(){
        return jobCount;
    }
}
