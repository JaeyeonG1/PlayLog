package com.quickids.playlog.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickids.playlog.R;
import com.quickids.playlog.adapter.HighlightListAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static androidx.camera.core.CameraX.getContext;

public class PreviewActivity extends AppCompatActivity implements HighlightListAdapter.OnItemClickListener {

    VideoView previewView;
    ArrayList<String> highlights;
    String path, name, extn, tempPath, timeTable, videoPath;
    RecyclerView recyclerView;
    HighlightListAdapter adapter;
    TextView currentTime, runningTime;
    SeekBar seekBar;
    int videoIndex;
    double currentPos, totalDuration;
    Handler mHandler, handler;
    Button extractButton, playButton, preButton, nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        path = getIntent().getStringExtra("path");
        name = getIntent().getStringExtra("name");
        extn = getIntent().getStringExtra("extn");

        extractButton = (Button) findViewById(R.id.button_extract);
        playButton = findViewById(R.id.button_play);
        playButton.setText("정지");
        preButton = findViewById(R.id.button_pre);
        nextButton = findViewById(R.id.button_next);
        extractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
                intent.putExtra("filePath", path);
                intent.putExtra("videoType", 100);
                intent.putExtra("name",name);
                intent.putExtra("extn",extn);
                startActivity(intent);
            }
        });
        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setProgress(seekBar.getProgress()-5*1000);
                currentPos = seekBar.getProgress();
                previewView.seekTo((int)currentPos);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setProgress(seekBar.getProgress()+5*1000);
                currentPos = seekBar.getProgress();
                previewView.seekTo((int)currentPos);
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(previewView.isPlaying()){
                    previewView.pause();
                    playButton.setText("재생");
                }else{
                    previewView.start();
                    playButton.setText("정지");
                }
            }
        });
        videoPath = path+name+"."+extn;
        tempPath = "/storage/emulated/0/PlayLogVideos/Match/HighlightTimeTable/";
        timeTable = name+".txt";
        videoIndex = 0;
        highlights = new ArrayList<>();
        getHighlightTimes(tempPath+timeTable);
        initView();
        setVideo();
    }
    public void initView(){
        //하이라이트 리스트 초기화
        recyclerView = findViewById(R.id.higlightList);
        recyclerView.setHasFixedSize(true);
        adapter = new HighlightListAdapter(highlights);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        //하이라이트 리스트 초기화


    }
    public void setVideo(){
        //비디오 뷰 초기화
        previewView = findViewById(R.id.view_preview);
        playVideo(videoIndex);
        //비디오 뷰 초기화
        seekBar = findViewById(R.id.preview_seek_bar);
        handler = new Handler();
        mHandler = new Handler();
        currentTime = findViewById(R.id.current_pos);
        runningTime = findViewById(R.id.running_time);
        previewView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoIndex++;
            }
        });

        previewView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setVideoProgress();
            }
        });
    }

    public void setVideoProgress(){
        currentPos = previewView.getCurrentPosition();
        totalDuration = previewView.getDuration();
        runningTime.setText(getTime((long) totalDuration));
        currentTime.setText(getTime((long) currentPos));
        seekBar.setMax((int) totalDuration);
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    currentPos = previewView.getCurrentPosition();
                    currentTime.setText(getTime((long) currentPos));
                    seekBar.setProgress((int)currentPos);
                    handler.postDelayed(this, 1000);
                }catch(IllegalStateException  e){
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable,1000);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentPos = seekBar.getProgress();
                previewView.seekTo((int) currentPos);
            }
        });
    }

    public void playVideo(int pos){
        try{
            previewView.setVideoPath(videoPath);
            previewView.start();
            videoIndex= pos;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getHighlightTimes(String path){
        //저장된 하이라이트 구간 로드
        File file = new File(path);
        if(file.exists()){
            try{
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line = "";
                while((line = bufferedReader.readLine()) != null){
                    String [] section = line.split(" ");
                    String highlight = getTime(Integer.parseInt(section[0]))+ " - " + getTime(Integer.parseInt(section[1]));
                    highlights.add(highlight);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getTime(int sec){
        //초를 시간(String)으로 변환
        String time ="";
        int hour, min;
        min = sec / 60;
        hour = min / 60;
        sec = sec % 60;
        min = min % 60;
        String sHour, sMin, sSec;
        sHour = Integer.toString(hour);
        sMin = Integer.toString(min);
        sSec = Integer.toString(sec);
        if(sHour.length() == 1) sHour = "0"+sHour;
        if(sMin.length() == 1) sMin = "0"+sMin;
        if(sSec.length() == 1) sSec = "0"+sSec;
        time = sHour+":"+sMin+":"+sSec;
        return time;
    }

    public String getTime(long value) {
        String playTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        playTime = String.format("%02d:%02d:%02d", hrs, mns, scs);

        return playTime;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        finish();
    }

    @Override
    public void onItemClick(View view, int pos) {
        String selectedPos = highlights.get(pos);
        String[] initTime = selectedPos.split(" -");
        String [] highlight = initTime[0].split(":");
        highlight[2].replaceAll(" ", "");
        int hour = Integer.parseInt(highlight[0]);
        int min =  Integer.parseInt(highlight[1]);
        int sec = Integer.parseInt(highlight[2]);
        int duration = hour*3600 + min * 60 + sec;
        seekBar.setProgress(duration*1000);
        currentPos = seekBar.getProgress();
        previewView.seekTo((int)currentPos);
    }
}
