package com.quickids.playlog.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.quickids.playlog.R;

public class SplashActivity extends AppCompatActivity {

    final static int PERMISSIONS_REQ_CODE = 1000;
    // 필요한 권한 목록 리스트
    String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 퍼미션 상태 확인
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!allPermissionsGranted(PERMISSIONS)){
                requestPermissions(PERMISSIONS, PERMISSIONS_REQ_CODE);
            }
            else{
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private boolean allPermissionsGranted(String[] permissions){
        int result;

        // 퍼미션 목록의 허가 여부 확인
        for(String permission : permissions){
            result = ContextCompat.checkSelfPermission(this, permission);

            // 허가되지 않은 퍼미션 발견
            if(result == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        // 모든 퍼미션 허가 됨
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSIONS_REQ_CODE:
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;
                    boolean diskAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;

                    if(!cameraAccepted || !diskAccepted){
                        showPermissionDialog("앱을 실행하기 위해 권한이 필요합니다.");
                    }
                    else{
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showPermissionDialog(String msg){
        AlertDialog.Builder builder
                = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("권한 알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQ_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
    }
}
