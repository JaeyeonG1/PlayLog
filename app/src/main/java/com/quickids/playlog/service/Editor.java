package com.quickids.playlog.service;

import android.content.Context;

import com.quickids.playlog.model.MatchVideo;
import com.quickids.playlog.model.TrainingVideo;

public class Editor {

    private FFMpegManager ffMpegManager;
    private Context context;
    public Editor(Context c){
        this.context = c;
        ffMpegManager = new FFMpegManager();
        ffMpegManager.loadFFMpegBinary(c);
    }
    // 훈련 영상 슬로우 모션 효과 적용
    public void processTrainingVideo(TrainingVideo tv){

        String filePath = tv.getPath();
        String extn = tv.getExtn();
        String name = tv.getName();
        String currentPath = filePath+name+"."+extn;
        String destPath = filePath+"Processed/"+name+"(SlowMotion)."+extn;
        String tempPath = filePath+"Processed/Temp/"+name+"temp."+extn;
        System.out.println(tempPath);
        ffMpegManager.executeSplitVideoCommand(currentPath,tempPath,800,3000); //슛 감지 후 1초간 잘라내기
        //ffMpegManager.executeSlowMotionVideoCommand(currentPath,destPath);

    }
    // 하이라이트 영상 생성
    public void createHighlightVideo(MatchVideo v){
    }
}
