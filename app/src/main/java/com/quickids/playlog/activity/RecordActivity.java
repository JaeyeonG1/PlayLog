package com.quickids.playlog.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.quickids.playlog.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressLint("RestrictedApi")
public class RecordActivity extends AppCompatActivity {

    private final static String TAG = "OpenCV";

    private TextureView cameraTV;
    private ImageButton btnCapture;
    private CameraX.LensFacing lensFacing;
    private AspectRatio aspectRatio;
    private VideoCapture videoCapture;

    private boolean isRecording;
    private boolean isOpenCvLoaded = false;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        cameraTV = findViewById(R.id.view_preview);
        btnCapture = findViewById(R.id.btn_capture);

        lensFacing = CameraX.LensFacing.BACK;
        aspectRatio = AspectRatio.RATIO_16_9;

        isRecording = false;

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
        Mat mat = new Mat();
        Utils.bitmapToMat(cameraTV.getBitmap(), mat);

        File path = new File(""+Environment.getExternalStorageDirectory());
        File file = new File(path, System.currentTimeMillis() + ".png");

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2GRAY);

        Imgcodecs.imwrite(file.toString(), mat);
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

    /**
     * Overrides for OpenCV Settings
     * */
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            isOpenCvLoaded = true;
        }
    }
}
