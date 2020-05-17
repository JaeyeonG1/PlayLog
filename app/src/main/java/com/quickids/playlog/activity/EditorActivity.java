package com.quickids.playlog.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.quickids.playlog.R;
import com.quickids.playlog.service.Editor;
import com.quickids.playlog.service.FFMpegManager;

public class EditorActivity extends AppCompatActivity {

    Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editor = new Editor(this); //Have to give View Context ok?

    }
}
