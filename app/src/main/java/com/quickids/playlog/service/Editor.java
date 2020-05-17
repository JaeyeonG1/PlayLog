package com.quickids.playlog.service;

import android.content.Context;

import com.quickids.playlog.model.MatchVideo;
import com.quickids.playlog.model.TrainingVideo;

public class Editor {

    private FFMpegManager ffMpegManager;
    private Context context;
    public Editor(Context c){
        ffMpegManager.loadFFMpegBinary(c);
        this.context = c;
    }
    // 훈련 영상 슬로우 모션 효과 적용
    public void processTrainingVideo(TrainingVideo tv){

        String fileName;
        String fileExtn;

        //String currentPath = getPath();
        String destPath = null;

        //ffMpegManager.executeSlowMotionVideoCommand(currentPath,destPath);

    }
    // 하이라이트 영상 생성
    public void createHighlightVideo(MatchVideo v){
    }
}
