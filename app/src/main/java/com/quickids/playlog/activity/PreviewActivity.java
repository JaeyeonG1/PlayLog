package com.quickids.playlog.activity;

import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.quickids.playlog.R;

import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {

    VideoView previewView;
    ArrayList<String> highlights;
    String path, name, extn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        path = getIntent().getStringExtra("path");
        name = getIntent().getStringExtra("name");
        extn = getIntent().getStringExtra("extn");
        

    }
}
