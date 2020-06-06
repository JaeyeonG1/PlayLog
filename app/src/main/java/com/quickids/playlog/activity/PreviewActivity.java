package com.quickids.playlog.activity;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.quickids.playlog.R;
import com.quickids.playlog.adapter.HighlightListAdapter;

import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {

    VideoView previewView;
    ArrayList<String> highlights;
    String path, name, extn;
    String tempPath;
    String timeTable;
    RecyclerView recyclerView;
    HighlightListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        path = getIntent().getStringExtra("path");
        name = getIntent().getStringExtra("name");
        extn = getIntent().getStringExtra("extn");
        tempPath = "/storage/emulated/0/PlayLogVideos/Match/HighlightTimeTable/";
        timeTable = name+".txt";
        Toast.makeText(this, path+name+extn, Toast.LENGTH_SHORT).show();
    }
    public void initView(){


        recyclerView = (RecyclerView) findViewById(R.id.higlightList);
        adapter = new HighlightListAdapter(highlights);
    }

    public void getHighlightTimes(String path){

    }
}
