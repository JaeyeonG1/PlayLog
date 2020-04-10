package com.quickids.playlog.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
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

        btnMatch.setOnClickListener(this);
        btnTraining.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_match:
                Intent intentMatch = new Intent(this, MatchActivity.class);
                startActivity(intentMatch);
                break;
            case R.id.btn_training:
                Intent intentTraining = new Intent(this, TrainingActivity.class);
                startActivity(intentTraining);
                break;
        }
    }
}
