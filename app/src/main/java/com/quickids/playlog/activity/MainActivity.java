package com.quickids.playlog.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.quickids.playlog.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnMatch;
    Button btnTraining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMatch = findViewById(R.id.btn_match);
        btnTraining = findViewById(R.id.btn_training);
    }

    @Override
    public void onClick(View view) {

    }
}
