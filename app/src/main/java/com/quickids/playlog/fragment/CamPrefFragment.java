package com.quickids.playlog.fragment;

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.fragment.app.Fragment;

import com.quickids.playlog.R;
import com.quickids.playlog.activity.RecordActivity;
import com.quickids.playlog.service.BluetoothService;

public class CamPrefFragment extends Fragment implements View.OnClickListener {

    TextureView cameraTextureView;
    ImageButton btnTurnLeft;
    Button btnSubmitLeft;
    ImageButton btnTurnRight;
    Button btnSubmitRight;

    Preview preview;
    CameraX.LensFacing lensFacing;
    AspectRatio aspectRatio;

    BluetoothService bts;

    boolean isLeftMoving;
    boolean isRightMoving;
    boolean isLeftSubmit;
    boolean isRightSubmit;

    public CamPrefFragment(BluetoothService bts) {
        this.bts = bts;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        lensFacing = CameraX.LensFacing.BACK;
        aspectRatio = AspectRatio.RATIO_16_9;

        isLeftMoving = false;
        isRightMoving = false;
        isLeftSubmit = false;
        isRightSubmit = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_campref, container, false);

        cameraTextureView = v.findViewById(R.id.textureView_preview);
        btnTurnLeft = v.findViewById(R.id.fabLeft);
        btnSubmitLeft = v.findViewById(R.id.button_left_ok);
        btnTurnRight = v.findViewById(R.id.fabRight);
        btnSubmitRight = v.findViewById(R.id.button_right_ok);


        btnTurnLeft.setOnClickListener(this);
        btnSubmitLeft.setOnClickListener(this);
        btnTurnRight.setOnClickListener(this);
        btnSubmitRight.setOnClickListener(this);

        startCamera();

        return v;
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
                .build();
        preview = new Preview(pConfig); // Build Preview

        // 뷰 파인더가 업데이트 될 때마다 레이아웃 다시 계산
        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    // 화면 업데이트를 위해 SurfaceTexture 제거 후 다시 추가
                    @Override
                    public void onUpdated(@NonNull Preview.PreviewOutput output) {
                        ViewGroup parent = (ViewGroup) cameraTextureView.getParent();
                        parent.removeView(cameraTextureView);
                        parent.addView(cameraTextureView, 0);

                        Log.i("onUpdated : ", "good");

                        cameraTextureView.setSurfaceTexture(output.getSurfaceTexture());
                        updateTransform(output);
                    }
                });

        CameraX.bindToLifecycle(this, preview);
    }

    private void updateTransform(Preview.PreviewOutput output){
        DisplayMetrics ctvMetrics = new DisplayMetrics();
        cameraTextureView.getDisplay().getRealMetrics(ctvMetrics);
        float previewWidth = ctvMetrics.widthPixels;
        float previewHeight = ctvMetrics.heightPixels;
        float width = output.getTextureSize().getWidth();
        float height = output.getTextureSize().getHeight();
        float centerX = cameraTextureView.getMeasuredWidth() / 2f;
        float centerY = cameraTextureView.getMeasuredHeight() / 2f;

        int rotationDgr;
        int rotation = (int)cameraTextureView.getRotation(); // 스위치문을 위해 int로 casting

        // landscape 모드에 맞게 90도 추가
        switch(rotation){
            case Surface.ROTATION_0:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 270;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 0;
                break;
            default:
                return;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(-(float)rotationDgr, centerX, centerY);
        matrix.postScale(previewWidth / height, previewHeight / width, centerX, centerY);

        cameraTextureView.setTransform(matrix);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_left_ok:
                bts.sendMsg("setLimitLeft");
                isLeftSubmit = true;
                break;
            case R.id.button_right_ok:
                bts.sendMsg("setLimitRight");
                isRightSubmit = true;
                break;
            case R.id.fabLeft:
                if(!isLeftMoving)
                    bts.sendMsg("left");
                else
                    bts.sendMsg("stop");
                isLeftMoving = !isLeftMoving;
                break;
            case R.id.fabRight:
                if(!isRightMoving)
                    bts.sendMsg("right");
                else
                    bts.sendMsg("stop");
                isRightMoving = !isRightMoving;
                break;
        }

        if(isLeftSubmit && isRightSubmit){
            Intent intentMatch = new Intent(getActivity(), RecordActivity.class);
            intentMatch.putExtra("prefInfo", "prefDone");
            startActivity(intentMatch);
            getActivity().finish();
        }
    }
}
