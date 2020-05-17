package com.quickids.playlog.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quickids.playlog.R;
import com.quickids.playlog.model.TrainingVideo;
import com.quickids.playlog.model.Video;
import com.quickids.playlog.service.Editor;

import java.io.IOException;

public class EditorActivity extends AppCompatActivity {

    TextView tempJob;
    TextView tempFilePath;
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
        editor = new Editor(this); //Have to give View Context ok?

        tempJob = (TextView) findViewById(R.id.tempJob);
        tempFilePath = (TextView)findViewById(R.id.tempFilePath);
        tempButton = (Button)findViewById(R.id.tempButton);

        //temp 나중에 수정해야함
        switch (videoType){
            case 200:
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
                tempButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(),"하이라이트 영상 제작", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }

    }
}
