package com.quickids.playlog.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.quickids.playlog.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prepareApp();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // APP 실행 준비 과정
    private void prepareApp(){}
}
