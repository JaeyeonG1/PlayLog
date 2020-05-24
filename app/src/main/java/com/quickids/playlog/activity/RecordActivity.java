package com.quickids.playlog.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
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
import com.quickids.playlog.model.HighlightTime;
import com.quickids.playlog.model.ObjectDetector;
import com.quickids.playlog.painter.ImageUtils;
import com.quickids.playlog.painter.MultiBoxTracker;
import com.quickids.playlog.painter.OverlayView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
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
    // minimumConfidence
    private static final double MINIMUM_CONFIDENCE = 0.5f;
    // preview size
    private static final int PREVIEW_WIDTH = 1080;
    private static final int PREVIEW_HEIGHT = 2019;
    // 줄이는 size
    private static final int RESIZE_WIDTH = 300;
    private static final int RESIZE_HEIGHT = 300;

    private TextureView cameraTV;
    private ImageButton btnCapture;
    private CameraX.LensFacing lensFacing;
    private AspectRatio aspectRatio;
    private VideoCapture videoCapture;

    private boolean isRecording;
    private boolean isOpenCvLoaded = false;

    private long recordStartTime;
    private TreeSet<Long> highlightTimes = new TreeSet<Long>();

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private Classifier detector;

    private Handler handler;
    private HandlerThread handlerThread;

    // for painter
    private MultiBoxTracker tracker;

    OverlayView trackingOverlay;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

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

        // painter 를 위한 선언
        // preview(1080x2019) 를 (300x300) 으로 변환하는 matrix
        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        PREVIEW_WIDTH, PREVIEW_HEIGHT,
                        RESIZE_WIDTH, RESIZE_HEIGHT,
                        0, false);

        // (300x300) 를 preview 로 변환하는 matrix
        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        tracker.draw(canvas);
                    }
                });

        tracker = new MultiBoxTracker(this);
        tracker.setFrameConfiguration(PREVIEW_WIDTH, PREVIEW_HEIGHT, 0);

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
        timer.schedule(tt, 0, 200);

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
                    recordStartTime = System.currentTimeMillis();
                    String filepath = Environment.getExternalStorageDirectory() + "/PlayLogVideos/Match/";
                    File videoFile = new File(filepath + recordStartTime  + ".mp4");
                    File highlightFile = new File(filepath + "HighlightTimeTable/" + recordStartTime + ".txt");
                    videoCapture.startRecording(
                            videoFile,
                            executor,
                            new VideoCapture.OnVideoSavedListener() {
                                // 이미지 저장 성공 시
                                @Override
                                public void onVideoSaved(@NonNull final File file) {
                                    saveHighlightTimes(highlightFile);
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
                        Bitmap previewBitmap = cameraTV.getBitmap();

                        Log.d("previewBitmap", previewBitmap.getWidth() + ", " + previewBitmap.getHeight());

                        // 카메라 촬영 size 를 모델의 input size(300x300) 으로 변환하는 과정
                        Bitmap resizedBmp = Bitmap.createScaledBitmap(previewBitmap, RESIZE_WIDTH, RESIZE_HEIGHT, true);

                        // object detection
                        final List<Classifier.Recognition> results = detector.recognizeImage(resizedBmp);

                        // 일정 조건을 만족하는 결과(minmum confidence 이상)만 저장하는 list
                        final List<Classifier.Recognition> mappedRecognitions = new LinkedList<Classifier.Recognition>();
                        boolean isFirst = true;

                        for (final Classifier.Recognition result : results) {
                            // 실시간으로 확인하기 위해 화면에 detecting 된 것을 그려줌
                            final RectF location = result.getLocation();
                            Log.d("DetectorActivity", result.toString());
                            // location 이 존재하고 minimumConfidence 보다 큰 경우
                            if (location != null && result.getConfidence() >= MINIMUM_CONFIDENCE) {
                                // (300x300)에서의 location 을 preview(1080x2019)로 변환
                                cropToFrameTransform.mapRect(location);

                                // 변환된 location 으로 변경
                                result.setLocation(location);

                                mappedRecognitions.add(result);

                                if(result.getTitle() == "ball") {
                                    // raspberryPi 와 통신
                                    // float ball_x_location = (location.left + location.right) / 2;
                                }

                                else {
                                    if(isFirst) {
                                        // 하이라이트 구간 추가
                                        long currentTime = System.currentTimeMillis();

                                        long videoTime = currentTime - recordStartTime; // 초단위로 변경

                                        highlightTimes.add(videoTime);

                                        isFirst = false;
                                    }
                                }
                            }
                        }

                        // detection 된거 overlay 에 그리기
                        tracker.trackResults(mappedRecognitions, 0);
                        trackingOverlay.postInvalidate();
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

    private void saveHighlightTimes(File saveFile) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile, true));

            Iterator<Long> highlightTimeIter = highlightTimes.iterator();
            while(highlightTimeIter.hasNext()) {
                bw.write(String.valueOf(highlightTimeIter.next()));
                bw.newLine();
            }
            bw.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Long> summaryHighlight(Iterator<Long> highlightTimeIter) {
        ArrayList<Long> highlightTimes = new ArrayList<Long>();

        HighlightTime highlightTime = new HighlightTime();
        while(highlightTimeIter.hasNext()) {

        }

        return highlightTimes;
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
