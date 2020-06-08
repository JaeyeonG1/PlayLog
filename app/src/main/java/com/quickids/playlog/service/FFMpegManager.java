package com.quickids.playlog.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.util.ArrayList;

public class FFMpegManager {
    private FFmpeg ffmpeg;
    private Context context;
    private ProgressDialog progressDialog;
    private Editor editor;
    private String tempPath, mainTitle;
    private int fileCount;
    private ArrayList<String> tempFileList;
    private String [] mergeCommand;
    public void loadFFMpegBinary(Context c, Editor editor){
        this.context = c;
        this.editor = editor;
        this.progressDialog = new ProgressDialog(context);
        if(ffmpeg == null){
            ffmpeg = FFmpeg.getInstance(c);
        }
        try{
            ffmpeg.loadBinary(new LoadBinaryResponseHandler(){
                @Override
                public void onFinish(){
                    super.onFinish();
                }
                @Override
                public void onSuccess(){
                    super.onSuccess();
                }
                @Override
                public void onFailure(){
                    super.onFailure();
                }
                @Override
                public void onStart(){
                    super.onStart();
                }
            });
        }catch (FFmpegNotSupportedException e){
            e.printStackTrace();
        }
    }

    public void executeSlowMotionVideoCommand(String currentPath, String destPath){
        String[] complexCommand = {"-y",
                "-i",
                currentPath,
                "-filter_complex",
                "[0:v]setpts=2.0*PTS[v];[0:a]atempo=0.5[a]",
                "-map",
                "[v]",
                "-map",
                "[a]",
                "-b:v",
                "2097k",
                "-r",
                "30",
                "-vcodec",
                "mpeg4",
                destPath};
        execFFmpegBinary(complexCommand);

    }

    public void executeSplitVideoCommand(String originalPath, String destPath, int startMs, int endMs){
//        String[] complexCommand = {"-ss",
//                "" + startMs / 1000,
//                "-y",
//                "-i",
//                originalPath,
//                "-t",
//                "" + (endMs - startMs) / 1000,
//                "-vcodec",
//                "mpeg4",
//                "-b:v",
//                "2097152",
//                "-b:a",
//                "48000",
//                "-ac",
//                "2",
//                "-ar",
//                "22050",
//                destPath
//        };

        if(endMs==0){
            String[] complexCommand = {"-i", originalPath, "-ss", "" + startMs / 1000, "-vcodec", "copy", "-acodec", "copy", destPath};
            execFFmpegBinary(complexCommand);

        }else{
            String[] complexCommand = {"-i", originalPath, "-ss", "" + startMs / 1000, "-t", "" + endMs / 1000, "-vcodec", "copy", "-acodec", "copy", destPath};
            execFFmpegBinary(complexCommand);
        }
    }

    public void executeMergeVideoCommand(String[] command, ArrayList<String> tempFileList){
        this.mergeCommand = command;
        this.tempFileList = tempFileList;
    }

    public void execFFmpegBinary(final String[] command){
        try{
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler(){

                @Override
                public void onFailure(String s) {
                    Toast.makeText(context.getApplicationContext(),"변환실패",Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onSuccess(String s) {
                    editor.increaseJobCount();
                    Toast.makeText(context.getApplicationContext(),"작업 완료 :"+Integer.toString(editor.getJobCount())+"/"+Integer.toString(editor.getRequestJobCount()), Toast.LENGTH_SHORT).show();
                    if(editor.getRequestJobCount() == editor.getJobCount()){
                        Toast.makeText(context.getApplicationContext(), "분할 작업 완료", Toast.LENGTH_SHORT).show();
                        execFFmpegBinary(mergeCommand);
                    }
                }
                @Override
                public void onProgress(String s) {
                    progressDialog.setMessage("progress : " + s);
                }
                @Override
                public void onStart() {
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }
                @Override
                public void onFinish() {
                    if(editor.getJobCount()-1 == editor.getRequestJobCount()){
                        try{
                            for(int i = 0 ; i < tempFileList.size(); i ++){
                                File file = new File(tempFileList.get(i));
                                if(file.exists()){
                                    file.delete();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }
            });
        }catch (FFmpegCommandAlreadyRunningException e){
            System.out.println("에러메시지");
            e.printStackTrace();
        }
    }
}
