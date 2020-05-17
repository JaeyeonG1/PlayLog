package com.quickids.playlog.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.quickids.playlog.R;
import com.quickids.playlog.adapter.ViewPagerAdapter;
import com.quickids.playlog.fragment.FullFragment;
import com.quickids.playlog.fragment.HighlightFragment;
import com.quickids.playlog.fragment.OriginalFragment;
import com.quickids.playlog.fragment.SlowMotionFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final static int RIGHT_CODE = 1000;

    ArrayList<Fragment> fragList;
    ArrayList<String> fragNameList;

    DrawerLayout drawerLayout;
    View drawerView;
    Toolbar toolbar;
    ViewPager viewPager;
    ViewPagerAdapter vpAdapter;
    TabLayout tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar 를 Activity 의 AppBar 로 설정
        toolbar = findViewById(R.id.toolbar_holder);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerView = findViewById(R.id.drawerView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 홈 버튼 활성화
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_24dp); // 아이콘 대체

        // 어댑터에 추가할 항목 설정
        fragList = new ArrayList<>();
        fragNameList = new ArrayList<>();
        setFragments(getIntent().getExtras().getInt("ActivityCode"));

        // Viewpager 를 FragmentManager 로 사용하기 위해 설정
        viewPager = findViewById(R.id.viewPager_holder);
        vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragList, fragNameList);
        viewPager.setAdapter(vpAdapter);

        // TabLayout 과 Viewpager 연동
        tab = findViewById(R.id.tabLayout_holder);
        tab.setupWithViewPager(viewPager);
    }

    // SplashActivity 에서 정상적으로 실행되었는지 확인
    private void setFragments(int activityCode){
        if(activityCode == RIGHT_CODE){
            fragList.add(new FullFragment());
            fragList.add(new HighlightFragment());
            fragList.add(new OriginalFragment());
            fragList.add(new SlowMotionFragment());
            fragNameList.add("Match");
            fragNameList.add("Highlight");
            fragNameList.add("Original");
            fragNameList.add("Slow");
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(getResources().getColor(R.color.colorWhite)));
            getSupportActionBar().setTitle("");
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
            case android.R.id.home : // 뒤로가기 버튼 클릭
                drawerLayout.openDrawer(drawerView);
                return true ;
            case R.id.action_record : // 녹화 버튼 클릭
                Intent intentMatch = new Intent(this, RecordActivity.class);
                startActivity(intentMatch);
                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }

    // 뒤로가기 버튼 눌렸을 때 동작
    @Override
    public void onBackPressed() {
        // Drawer 열려있으면 닫기
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
