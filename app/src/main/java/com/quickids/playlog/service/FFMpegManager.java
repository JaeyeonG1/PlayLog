package com.quickids.playlog.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

public class FFMpegManager {
    private FFmpeg ffmpeg;
    private Context context;
    private ProgressDialog progressDialog;

    public void loadFFMpegBinary(Context c){
        this.context = c;
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
                "60",
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
        String[] complexCommand = {"-i", originalPath, "-ss", "" + startMs / 1000, "-t", "" + endMs / 1000, destPath};
        execFFmpegBinary(complexCommand);
    }

    public void executeMergeVideoCommand(){

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
                    Toast.makeText(context.getApplicationContext(),"변환완료", Toast.LENGTH_SHORT).show();

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
                    progressDialog.dismiss();
                }
            });
        }catch (FFmpegCommandAlreadyRunningException e){
            System.out.println("에러메시지");
            e.printStackTrace();
        }
    }

}
