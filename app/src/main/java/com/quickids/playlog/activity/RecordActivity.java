package com.quickids.playlog.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;

import com.quickids.playlog.R;
import com.quickids.playlog.model.Classifier;
import com.quickids.playlog.model.ObjectDetector;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressLint("RestrictedApi")
public class RecordActivity extends AppCompatActivity {

    private final static String TAG = "OpenCV";
    // Configuration values for the prepackaged SSD model.
    private static final int TF_OD_API_INPUT_INPUT = 300;
    private static final boolean TF_OD_API_IS_QUANTIZED = false;
    private static final String TF_OD_API_MODEL_FILE = "detect.tflite";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/label_map.txt";

    private TextureView cameraTV;
    private ImageButton btnCapture;
    private CameraX.LensFacing lensFacing;
    private AspectRatio aspectRatio;
    private VideoCapture videoCapture;

    private boolean isRecording;
    private boolean isOpenCvLoaded = false;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private Classifier detector;

    private Handler handler;
    private HandlerThread handlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        cameraTV = findViewById(R.id.view_preview);
        btnCapture = findViewById(R.id.btn_capture);

        lensFacing = CameraX.LensFacing.BACK;
        aspectRatio = AspectRatio.RATIO_16_9;

        isRecording = false;



        try {
            detector =
                    ObjectDetector.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_INPUT_INPUT,
                            TF_OD_API_IS_QUANTIZED);
        } catch (final IOException e) {
            e.printStackTrace();
            Log.e("error", "Exception initializing classifier!");
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        // 타이머 주기에 따라 조건 검사 및 프레임 분석
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                if(isRecording){
                    analyzeFrame();
                }
            }
        };
        Timer timer = new Timer();
        // 2초 단위로 분석
        timer.schedule(tt, 0, 2000);

        // 카메라 프로필 설정
        startCamera();


    }

    /**
     * CameraX Profile Setting & Open Preview
     * */
    private void startCamera() {
        CameraX.unbindAll();

        // Preview Configuration 생성

        // => 정확히 어떻게 작동하는지 알아볼 것
        PreviewConfig pConfig = new PreviewConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setLensFacing(lensFacing)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
        .build();
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

                        Log.i("onUpdated : ", "good");

                        cameraTV.setSurfaceTexture(output.getSurfaceTexture());
                        updateTransform();
                    }
                });

        // VideoCapture Config 생성
        VideoCaptureConfig videoCaptureConfig =
                new VideoCaptureConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setLensFacing(lensFacing)
                .setVideoFrameRate(24)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                .build();

        videoCapture = new VideoCapture(videoCaptureConfig);
        btnCapture.setOnClickListener(setRecordBtn(videoCapture));

        CameraX.bindToLifecycle(this, preview, videoCapture);
    }

    /**
     * Set Record Button Listener
     * */
    private View.OnClickListener setRecordBtn(final VideoCapture videoCapture){
        View.OnClickListener videoBtnListener = new View.OnClickListener(){
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                if(!isRecording){
                    File videoFile = new File(Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".mp4");
                    videoCapture.startRecording(
                            videoFile,
                            executor,
                            new VideoCapture.OnVideoSavedListener() {
                                // 이미지 저장 성공 시
                                @Override
                                public void onVideoSaved(@NonNull final File file) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            String msg = "영상 저장 완료 : " + file.getAbsolutePath();
                                            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                // 이미지 저장 실패 시
                                @Override
                                public void onError(@NonNull VideoCapture.VideoCaptureError videoCaptureError, @NonNull final String message, @Nullable Throwable cause) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            String msg = "영상 저장 실패 : " + message;
                                            Toast.makeText(getBaseContext(), msg,Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    if(cause != null){
                                        cause.printStackTrace();
                                    }
                                }
                            });
                }
                else{
                    videoCapture.stopRecording();
                }
                isRecording = !isRecording;
            }
        };

        return videoBtnListener;
    }

    /**
     * Frame Analyzer Setting
     * */
    private void analyzeFrame(){
        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        Bitmap currentBitmap = cameraTV.getBitmap();

                        int width = 300; // 축소시킬 너비
                        int height = 300; // 축소시킬 높이
                        float bmpWidth = currentBitmap.getWidth();
                        float bmpHeight = currentBitmap.getHeight();

                        Log.d("cameraTV", bmpWidth + ", " + bmpHeight);

                        if (bmpWidth > width) {
                            // 원하는 너비보다 클 경우의 설정
                            float mWidth = bmpWidth / 100;
                            float scale = width/ mWidth;
                            bmpWidth *= (scale / 100);
                        }
                        if (bmpHeight > height) {
                            // 원하는 높이보다 클 경우의 설정
                            float mHeight = bmpHeight / 100;
                            float scale = height/ mHeight;
                            bmpHeight *= (scale / 100);
                        }

                        Bitmap resizedBmp = Bitmap.createScaledBitmap(currentBitmap, (int) bmpWidth, (int) bmpHeight, true);

                        Log.d("resizeBmp", resizedBmp.getWidth() + ", " + resizedBmp.getHeight());

                        final List<Classifier.Recognition> results = detector.recognizeImage(resizedBmp);

                        for (final Classifier.Recognition result : results) {
                            Log.d("DetectorActivity", result.toString());
                        }
                    }
                }
        );

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

    @Override
    public synchronized void onStart() {
        Log.d("", "onStart " + this);
        super.onStart();
    }

    @Override
    public synchronized void onResume() {
        Log.d("", "onResume " + this);
        super.onResume();

        handlerThread = new HandlerThread("inference");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public synchronized void onPause() {
        Log.d("", "onPause " + this);

        handlerThread.quitSafely();
        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (final InterruptedException e) {
            Log.e("", e + "Exception!");
        }

        super.onPause();
    }

    @Override
    public synchronized void onStop() {
        Log.d("", "onStop " + this);
        super.onStop();
    }

    @Override
    public synchronized void onDestroy() {
        Log.d("", "onDestroy " + this);
        super.onDestroy();
    }

    protected synchronized void runInBackground(final Runnable r) {
        if (handler != null) {
            handler.post(r);
        }
    }
}
