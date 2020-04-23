package com.quickids.playlog.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.quickids.playlog.R;
import com.quickids.playlog.adapter.ViewPagerAdapter;
import com.quickids.playlog.fragment.FullFragment;
import com.quickids.playlog.fragment.HighlightFragment;

import java.util.ArrayList;

public class HolderActivity extends AppCompatActivity {

    public static final int MATCH_CODE = 1000;
    public static final int TRAINING_CODE = 2000;

    int activityCode;
    ArrayList<Fragment> fragList;
    ArrayList<String> fragNameList;
    Toolbar tb;
    ViewPager vp;
    ViewPagerAdapter adapter;
    TabLayout tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holder);

        // Toolbar 를 Activity 의 AppBar 로 설정
        tb = findViewById(R.id.toolbar_holder);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 홈 버튼 활성화
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp); // 아이콘 대체

        // 어댑터에 추가할 항목 설정
        fragList = new ArrayList<>();
        fragNameList = new ArrayList<>();
        activityCode = getIntent().getExtras().getInt("ActivityCode");
        setByActivityCode(activityCode);

        // Viewpager 를 FragmentManager 로 사용하기 위해 설정
        vp = findViewById(R.id.viewPager_holder);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragList, fragNameList);
        vp.setAdapter(adapter);

        // TabLayout 과 Viewpager 연동
        tab = findViewById(R.id.tabLayout_holder);
        tab.setupWithViewPager(vp);
    }

    // MainActivity 에서 전달받은 ActivityCode 에 따라 기본 설정
    private void setByActivityCode(int activityCode){
        if(activityCode == MATCH_CODE){
            fragList.add(new FullFragment());
            fragList.add(new HighlightFragment());
            fragNameList.add("FullMatch");
            fragNameList.add("Highlight");
            getSupportActionBar().setTitle("Match");
        }
        else if(activityCode == TRAINING_CODE){
            fragList.add(new FullFragment());
            fragList.add(new HighlightFragment());
            fragNameList.add("Original");
            fragNameList.add("SlowMotion");
            getSupportActionBar().setTitle("Training");
        }
        else{
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
            case android.R.id.home :
                finish();
                return true ;
            case R.id.action_record :
                Intent intentMatch = new Intent(this, RecordActivity.class);
                startActivity(intentMatch);
                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }
}
