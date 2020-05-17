package com.quickids.playlog.service;

import android.content.Context;

import com.quickids.playlog.model.MatchVideo;
import com.quickids.playlog.model.TrainingVideo;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Editor {

    private FFMpegManager ffMpegManager;
    private Context context;
    public Editor(Context c){
        this.context = c;
        ffMpegManager = new FFMpegManager();
        ffMpegManager.loadFFMpegBinary(c);
    }
    // 훈련 영상 슬로우 모션 효과 적용
    public void processTrainingVideo(TrainingVideo tv) throws IOException {

        String filePath = tv.getPath();
        String extn = tv.getExtn();
        String name = tv.getName();
        String currentPath = filePath+name+"."+extn;
        String destPath = filePath+"Processed/"+name+"(SlowMotion)."+extn;
        String top = filePath+"Processed/Temp/"+name+"temp01."+extn;
        String mid = filePath+"Processed/Temp/"+name+"temp02."+extn;
        String bottom = filePath+"Processed/Temp/"+name+"temp04."+extn;
//        ffMpegManager.executeSplitVideoCommand(currentPath,top,0,3000);
//        ffMpegManager.executeSplitVideoCommand(currentPath,mid,3000,4000); //슛 감지 후 1초
//        ffMpegManager.executeSplitVideoCommand(currentPath,bottom,4000,0);
//
//        ffMpegManager.executeSlowMotionVideoCommand(mid,filePath+"Processed/Temp/"+name+"temp03."+extn);
//
        FileOutputStream fos = new FileOutputStream(filePath+"Processed/Temp/join.txt",true);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
        writer.write("file "+"'"+top+"'"+"\n");
        writer.write("file "+"'"+filePath+"Processed/Temp/"+name+"temp03."+extn+"'"+"\n");
        writer.write("file "+"'"+bottom+"'");
        writer.flush();
        writer.close();
        fos.close();
        ffMpegManager.executeMergeVideoCommand(destPath,filePath+"Processed/Temp/join.txt");

    }
    // 하이라이트 영상 생성
    public void createHighlightVideo(MatchVideo v){
    }
}
