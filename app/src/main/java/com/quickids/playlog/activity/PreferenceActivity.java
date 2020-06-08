package com.quickids.playlog.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.quickids.playlog.R;
import com.quickids.playlog.adapter.PresetFragmentAdapter;
import com.quickids.playlog.fragment.BTPrefFragment;
import com.quickids.playlog.fragment.CamPrefFragment;
import com.quickids.playlog.service.BluetoothService;
import com.quickids.playlog.util.NonSwipeViewPager;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class PreferenceActivity extends AppCompatActivity {

    ArrayList<Fragment> fragList;
    ArrayList<String> fragNameList;

    NonSwipeViewPager viewPager;
    PresetFragmentAdapter fragmentAdapter;
    CircleIndicator indicator;

    BluetoothService bts;

    public boolean readyToRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        bts = BluetoothService.getInstance(this);
        bts.setContext(this);

        readyToRecord = false;

        // Viewpager 를 FragmentManager 로 사용하기 위해 설정
        viewPager = findViewById(R.id.viewPager_holder);

        // 어댑터에 추가할 항목 설정
        fragList = new ArrayList<>();
        fragNameList = new ArrayList<>();
        setFragments();

        fragmentAdapter = new PresetFragmentAdapter(getSupportFragmentManager(), fragList, fragNameList);
        viewPager.setAdapter(fragmentAdapter);

        indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
    }

    // SplashActivity 에서 정상적으로 실행되었는지 확인
    private void setFragments(){
        fragList.add(new BTPrefFragment(viewPager, bts));
        fragList.add(new CamPrefFragment(bts));
        fragNameList.add("BlueTooth");
        fragNameList.add("Camera");
    }
}
