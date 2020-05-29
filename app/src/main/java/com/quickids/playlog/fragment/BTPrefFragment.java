package com.quickids.playlog.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.quickids.playlog.R;
import com.quickids.playlog.adapter.DeviceListAdapter;
import com.quickids.playlog.service.BluetoothService;
import com.quickids.playlog.util.NonSwipeViewPager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;
import static android.app.Activity.RESULT_CANCELED;

public class BTPrefFragment extends Fragment implements CompoundButton.OnCheckedChangeListener,DeviceListAdapter.OnItemClickListener, View.OnClickListener {

    ViewPager parentVP;

    Switch switchBt;
    Button btnChecker;
    TextView tvTest;
    RecyclerView recyclerView;
    DeviceListAdapter adapter;

    BluetoothService bts;
    android.os.Handler bluetoothUIHandler;

    ArrayList<String> deviceList;
    boolean canSelect;

    public BTPrefFragment(NonSwipeViewPager parentVP, BluetoothService bts) {
        this.parentVP = parentVP;
        this.bts = bts;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        canSelect = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_btpref, container, false);

        switchBt = v.findViewById(R.id.switch_bt);
        btnChecker = v.findViewById(R.id.button_checker);
        tvTest = v.findViewById(R.id.textView_test);
        recyclerView = v.findViewById(R.id.recycler_device);

        adapter = new DeviceListAdapter(deviceList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));

        switchBt.setOnCheckedChangeListener(this);
        btnChecker.setOnClickListener(this);
        adapter.setOnItemClickListener(this);

        doByBluetoothStatus();

        // 타이머에서 UI 조정을 위한 Handler Init
        bluetoothUIHandler = new android.os.Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                changeCheckerStat(true);
                tvTest.setText("테스트 성공!!");
                btnChecker.setText("카메라 설정");
            }
        };

        return v;
    }

    /**
     * 스위치 Check 상태 변화 시 Listener
     * */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(!isChecked){
            bts.disable();
            Toast.makeText(getContext(), "블루투스가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show();
            while(bts.isEnabled());
            doByBluetoothStatus();
        }
        else{
            Intent bluetoothIntent = bts.enable();
            startActivityForResult(bluetoothIntent, BluetoothService.BT_REQUEST_ENABLE);
        }
    }

    /**
     * 리사이클러뷰 아이템 Listener
     * */
    @Override
    public void onItemClick(View v, int position) {
        if(canSelect){
            String name = adapter.getItem(position);
            Log.i("Name", name);

            if(bts.connectSelectedDevice(name)){
                changeCheckerStat(true);
                switchBt.setEnabled(false);
                recyclerView.setEnabled(false);
                tvTest.setText("연결 성공! 테스트를 위해 버튼을 눌러주세요.");
                canSelect = false;
            }
            else
                Toast.makeText(getContext(), "연결 실패. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 연결 확인 버튼 Listener
     * */
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_checker:
                if(btnChecker.getText().toString().equals("연결 확인")){
                    tvTest.setText("테스트 실행 중...");
                    changeCheckerStat(false);

                    Timer timer = new Timer();
                    TimerTask tt = new TimerTask() {
                        int counter = 0;
                        @Override
                        public void run() {
                            if(counter < 2)
                                bts.sendMsg("left");
                            else if(counter >= 2 && counter < 4)
                                bts.sendMsg("right");
                            else{
                                bluetoothUIHandler.sendEmptyMessage(1);
                                timer.cancel();
                            }
                            counter++;
                        }
                    };
                    timer.schedule(tt, 0, 1000);
                }
                else{
                    // 다음 프래그먼트로 전환
                    parentVP.setCurrentItem(1, true);
                }
        }
    }

    /**
     * 블루투스 활성화 스위치 Intent 응답
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BluetoothService.BT_REQUEST_ENABLE:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getContext(), "블루투스 활성화", Toast.LENGTH_LONG).show();
                    doByBluetoothStatus();
                } else if (resultCode == RESULT_CANCELED) {
                    switchBt.setChecked(false);
                    Toast.makeText(getContext(), "취소", Toast.LENGTH_LONG).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 블루투스 전원 상태 변화 시 실행하는 함수
     * */
    public void doByBluetoothStatus(){
        if(bts.exists()){
            if(bts.isEnabled()){
                switchBt.setChecked(true);
            }
            else{
                switchBt.setChecked(false);
            }
        }
        deviceList = bts.getPairedDevices();
        adapter.setDeviceList(deviceList);
    }

    /**
     * 연결 확인 버튼 상태 조정 함수
     * */
    public void changeCheckerStat(boolean stat){
        if(stat){
            btnChecker.setEnabled(true);
            btnChecker.setBackgroundColor(getResources().getColor(R.color.colorBTMain));
        }
        else {
            btnChecker.setEnabled(false);
            btnChecker.setBackgroundColor(getResources().getColor(R.color.colorBTDisabled));
        }
    }
}
