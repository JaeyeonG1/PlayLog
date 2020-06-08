package com.quickids.playlog.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quickids.playlog.R;
import com.quickids.playlog.model.MatchVideo;
import com.quickids.playlog.model.TrainingVideo;
import com.quickids.playlog.model.Video;
import com.quickids.playlog.service.Editor;

import java.io.IOException;

public class EditorActivity extends AppCompatActivity {

    TextView videoPath, videoName, type;
    Button tempButton;
    String filePath,name,extn;
    int videoType;
    Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        name = intent.getStringExtra("name");
        extn = intent.getStringExtra("extn");
        videoType = intent.getIntExtra("videoType",0);
        Toast.makeText(getApplicationContext(), "비디오 타입"+videoType+"\n filePath:"+filePath+name+extn, Toast.LENGTH_SHORT).show();
        editor = new Editor(this, 1); //Have to give View Context ok?

        videoPath = (TextView) findViewById(R.id.edit_path);
        type = (TextView) findViewById(R.id.type);
        videoName = (TextView)findViewById(R.id.edit_video_name);
        videoPath.setText(filePath);
        videoName.setText(name);
        tempButton = (Button)findViewById(R.id.tempButton);

        //temp 나중에 수정해야함
        switch (videoType){
            case 200:
                tempButton.setText("훈련 영상 제작");
                type.setText("Edit for Training");
                tempButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        TrainingVideo video = new TrainingVideo();
                        video.setPath(filePath);
                        video.setName(name);
                        video.setExtn(extn);
                        try {
                            editor.processTrainingVideo(video);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case 100:
                tempButton.setText("하이라이트 영상 제작");
                type.setText("Edit for HighLight");
                tempButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(),"하이라이트 영상 제작", Toast.LENGTH_SHORT).show();
                        MatchVideo video = new MatchVideo();
                        video.setPath(filePath);
                        video.setName(name);
                        video.setExtn(extn);
                        try{
                            editor.createHighlightVideo(video);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }

    }
}
