package com.quickids.playlog.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.quickids.playlog.R;
import com.quickids.playlog.service.Editor;

public class EditorActivity extends AppCompatActivity {

    Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editor = new Editor(this); //Have to give View Context ok?

    }
}
