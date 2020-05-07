package com.quickids.playlog.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;

import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.quickids.playlog.R;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordActivity extends AppCompatActivity {

    private TextureView cameraTV;
    private ImageButton btnCapture;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        cameraTV = findViewById(R.id.view_preview);
        btnCapture = findViewById(R.id.btn_capture);

        startCamera();
    }

    private void startCamera() {
        CameraX.unbindAll();

        /** Preview Viewfinder Setting**/
        // Preview Configuration 생성

        /* 화면 크기로 결정
        int aspRatioW = cameraTV.getWidth();
        int aspRatioH = cameraTV.getHeight();
        // 해상도 설정에 사용
        Size screen = new Size(aspRatioW,aspRatioH);
        */

        // => 정확히 어떻게 작동하는지 알아볼 것
        PreviewConfig pConfig = new PreviewConfig.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9).build();
        Preview preview = new Preview(pConfig); // Build Preview

        // 뷰 파인더가 업데이트 될 때마다 레이아웃 다시 계산
        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    // 화면 업데이트를 위해 SurfaceTexture 제거 후 다시 추가
                    @Override
                    public void onUpdated(@NonNull Preview.PreviewOutput output) {
                        ViewGroup parent = (ViewGroup) cameraTV.getParent();
                        parent.removeView(cameraTV);
                        parent.addView(cameraTV, 0);

                        cameraTV.setSurfaceTexture(output.getSurfaceTexture());
                        updateTransform();
                    }
                });

        /** Image Capture **/
        // ImageCapture Configuration 생성
        ImageCaptureConfig imgCapConfig =
                new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        final ImageCapture imgCap = new ImageCapture(imgCapConfig);

        // 캡처 버튼에 익명 리스너 설정
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".jpg");
                imgCap.takePicture(file, executor, new ImageCapture.OnImageSavedListener() {
                    // 이미지 저장 성공 시
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                String msg = "사진 캡처 완료 : " + file.getAbsolutePath();
                                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    // 이미지 저장 실패 시
                    @Override
                    public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                String msg = "사진 캡쳐 실패 : " + message;
                                Toast.makeText(getBaseContext(), msg,Toast.LENGTH_LONG).show();
                            }
                        });
                        if(cause != null){
                            cause.printStackTrace();
                        }
                    }
                });
            }
        });

        /** Video Record **/



        /** Image Analyser **/
        // ImageAnalysis Configuration 생성
        ImageAnalysisConfig imgAConfig = new ImageAnalysisConfig.Builder().setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE).build();
        ImageAnalysis analysis = new ImageAnalysis(imgAConfig);

        analysis.setAnalyzer(executor,
                new ImageAnalysis.Analyzer(){
                    @Override
                    public void analyze(ImageProxy image, int rotationDegrees){
                        //y'all can add code to analyse stuff here idek go wild.
                    }
                }
        );

        //
        CameraX.bindToLifecycle(this, preview, imgCap, analysis);
    }

    private void updateTransform(){
        Matrix matrix = new Matrix();
        float centerX = cameraTV.getMeasuredWidth() / 2f;
        float centerY = cameraTV.getMeasuredHeight() / 2f;

        int rotationDgr;
        int rotation = (int)cameraTV.getRotation(); // 스위치문을 위해 int로 casting

        switch(rotation){ //correct output to account for display rotation
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        matrix.postRotate((float)rotationDgr, centerX, centerY);
        cameraTV.setTransform(matrix);
    }
}
