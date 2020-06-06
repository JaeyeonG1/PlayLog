package com.quickids.playlog.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.quickids.playlog.R;
import com.quickids.playlog.model.Video;

public class VideoPlayerActivity extends AppCompatActivity {

    VideoView videoView;
    ImageView pause;
    SeekBar seekBar;
    TextView current, total;
    double currentPos, totalDuration;
    LinearLayout showProgress;
    Handler mHandler, handler;
    boolean isVisible = true;
    RelativeLayout relativeLayout;
    int videoIndex = 0;
    public static final int PERMISSION_READ = 0;

    String videoPath;
    Video video;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        setVideo();
    }

    public void setVideo(){
        videoView = (VideoView) findViewById(R.id.videoView);
        pause = (ImageView) findViewById(R.id.pause);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        current = (TextView) findViewById(R.id.current);
        total = (TextView) findViewById(R.id.total);
        showProgress = (LinearLayout) findViewById(R.id.showProgress);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);

        videoIndex = getIntent().getIntExtra("pos", 0);
        videoPath = getIntent().getStringExtra("path");

        handler = new Handler();
        mHandler = new Handler();

        //재생 끝남
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoIndex++;
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setVideoProgress();
            }
        });
        playVideo(videoIndex);
        setPause();
        hideLayout();

    }

    //재생 함수

    public void playVideo(int pos){
        try{
            videoView.setVideoPath(videoPath);
            videoView.start();
            pause.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
            videoIndex = pos;

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setVideoProgress(){
        currentPos = videoView.getCurrentPosition();
        totalDuration = videoView.getDuration();

        total.setText(getTime((long) totalDuration));
        current.setText(getTime((long) currentPos));
        seekBar.setMax((int) totalDuration);
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    currentPos = videoView.getCurrentPosition();
                    current.setText(getTime((long) currentPos));
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
                videoView.seekTo((int) currentPos);
            }
        });
    }

    public void setPause(){
        pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(videoView.isPlaying()){
                    pause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                }else{
                    videoView.start();
                    pause.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                }
            }
        });
    }

    public void hideLayout(){

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                showProgress.setVisibility(View.GONE);
                isVisible = false;
            }
        };
        handler.postDelayed(runnable, 5000);

        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mHandler.removeCallbacks(runnable);
                if(isVisible){
                    showProgress.setVisibility(View.GONE);
                    isVisible= false;
                }else{
                    showProgress.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(runnable, 5000);
                    isVisible = true;
                }
            }
        });
    }

    //
    public String getTime(long value) {
        String playTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            playTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            playTime = String.format("%02d:%02d", mns, scs);
        }
        return playTime;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        finish();
    }
}
