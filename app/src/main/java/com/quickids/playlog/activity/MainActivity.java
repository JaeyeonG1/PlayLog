package com.quickids.playlog.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.quickids.playlog.R;
import com.quickids.playlog.adapter.MainFragmentAdapter;
import com.quickids.playlog.dialog.ModeSelectionDialogFragment;
import com.quickids.playlog.fragment.RecordFragment;
import com.quickids.playlog.fragment.HighlightFragment;
import com.quickids.playlog.fragment.MatchFragment;
import com.quickids.playlog.fragment.TrainingFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final static int RIGHT_CODE = 1000;

    ArrayList<Fragment> fragList;
    ArrayList<String> fragNameList;

    Toolbar toolbar;
    ViewPager viewPager;
    MainFragmentAdapter fragmentAdapter;
    TabLayout tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar 를 Activity 의 AppBar 로 설정
        toolbar = findViewById(R.id.toolbar_holder);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("PlayLog");

        // 어댑터에 추가할 항목 설정
        fragList = new ArrayList<>();
        fragNameList = new ArrayList<>();
        setFragments(getIntent().getExtras().getInt("ActivityCode"));

        // Viewpager 를 FragmentManager 로 사용하기 위해 설정
        viewPager = findViewById(R.id.viewPager_holder);
        fragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager(), fragList, fragNameList);
        viewPager.setAdapter(fragmentAdapter);

        // TabLayout 과 Viewpager 연동
        tab = findViewById(R.id.tabLayout_holder);
        tab.setupWithViewPager(viewPager);
    }

    // SplashActivity 에서 정상적으로 실행되었는지 확인
    private void setFragments(int activityCode){
        if(activityCode == RIGHT_CODE){
            fragList.add(new RecordFragment());
            fragList.add(new MatchFragment());
            fragList.add(new HighlightFragment());
            fragList.add(new TrainingFragment());
            fragNameList.add("Record");
            fragNameList.add("Match");
            fragNameList.add("HighLight");
            fragNameList.add("Training");
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(getResources().getColor(R.color.colorWhite)));
        }
        else{ // Code 정상 아닐 시 종료
            finish();
        }
    }

    // 작성한 Custom ActionBar 를 menu 변수에 inflate 하여 AppBar 에 반영
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_custom, menu) ;
        return true ;
    }

    // AppBar 에 선택된 메뉴의 Action 설정
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_record : // 녹화 버튼 클릭
                ModeSelectionDialogFragment ms = ModeSelectionDialogFragment.getInstance();
                ms.show(getSupportFragmentManager(), ModeSelectionDialogFragment.TAG_SELECTION_DIALOG);
                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }
}
