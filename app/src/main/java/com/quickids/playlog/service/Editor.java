package com.quickids.playlog.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.quickids.playlog.model.MatchVideo;
import com.quickids.playlog.model.TrainingVideo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
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
        requestJobCount = 4;
        String filePath = tv.getPath();
        String extn = tv.getExtn();
        String name = tv.getName();
        String currentPath = filePath+name+"."+extn;
        String destPath = filePath+"Processed/"+name+"(SlowMotion)."+extn;
        String top = filePath+"Processed/Temp/"+name+"temp1."+extn;
        String mid = filePath+"Processed/Temp/"+name+"temp2."+extn;
        String bottom = filePath+"Processed/Temp/"+name+"temp4."+extn;

        //객체 움직임 감지 구현부

        //split video 구현부
        ffMpegManager.executeSplitVideoCommand(currentPath,top,0,4500);
        ffMpegManager.executeSplitVideoCommand(currentPath,mid,4500,1000); //슛 감지 후 1초(임시)
        ffMpegManager.executeSplitVideoCommand(currentPath,bottom,5500,0);
        ArrayList<String> tempFile = new ArrayList<>();
        tempFile.add(top);
        tempFile.add(mid);
        tempFile.add(bottom);
        tempFile.add(filePath+"Processed/Temp/"+name+"temp3."+extn);
        ffMpegManager.executeSlowMotionVideoCommand(mid,filePath+"Processed/Temp/"+name+"temp3."+extn);
        //merge video 구현부
        ArrayList<String> cmd = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        mid = filePath+"Processed/Temp/"+name+"temp3."+extn;
        ArrayList<String> tempFileList = new ArrayList<>();
        tempFileList.add(top);
        tempFileList.add(mid);
        tempFileList.add(bottom);
        for(int i = 0; i < tempFileList.size(); i++){
            cmd.add("-i");
            cmd.add(tempFileList.get(i));
            sb.append("[").append(i).append(":0] [").append(i).append(":1]");
        }
        sb.append(" concat=n=").append(tempFileList.size()).append(":v=1:a=1 [v] [a]");
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

        ffMpegManager.executeMergeVideoCommand(command, tempFile);
    }

    // 하이라이트 영상 생성
    public void createHighlightVideo(MatchVideo v){
        String filePath = v.getPath();
        String fileName = v.getName();
        String fileExten = v.getExtn();
        String destPath = filePath+"Temp/";
        String highlightTable = filePath+"HighlightTimeTable/"+fileName+".txt";
        String resultPath = "/storage/emulated/0/PlayLogVideos/Highlight/"+fileName+"(하이라이트)"+"."+fileExten;
        int jobCount = 0;
        ArrayList<String> tempFileList = new ArrayList<String>();

        File file = new File(highlightTable);
        try {
            if(file.exists()){
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line = "";
                while ((line = br.readLine()) != null){
                    String [] time = line.split(" ");
                    ffMpegManager.executeSplitVideoCommand(
                            filePath+fileName+"."+fileExten
                            ,destPath+fileName+"temp"+Integer.toString(jobCount)+"."+fileExten
                            ,Integer.parseInt(time[0])*1000,
                            (Integer.parseInt(time[1])-Integer.parseInt(time[0]))*1000);
                            tempFileList.add(destPath+fileName+"temp"+Integer.toString(jobCount)+"."+fileExten);
                    jobCount++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            requestJobCount = jobCount;
            ArrayList<String> cmd = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            //합병
            for(int i = 0; i < tempFileList.size(); i++){
                cmd.add("-i");
                cmd.add(tempFileList.get(i));
                sb.append("[").append(i).append(":0] [").append(i).append(":1]");
            }
            sb.append(" concat=n=").append(tempFileList.size()).append(":v=1:a=1 [v] [a]");
            cmd.add("-filter_complex");
            cmd.add(sb.toString());
            cmd.add("-map");
            cmd.add("[v]");
            cmd.add("-map");
            cmd.add("[a]");
            cmd.add("-preset");
            cmd.add("ultrafast");
            cmd.add(resultPath);
            System.out.println(cmd);
            sb = new StringBuilder();
            for (String str : cmd)
            {
                sb.append(str).append(" ");
            }
            String[] command = cmd.toArray(new String[cmd.size()]);
            ffMpegManager.executeMergeVideoCommand(command, tempFileList);
        }
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
