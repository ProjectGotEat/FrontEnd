package com.example.projectgoteat.UI.main;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectgoteat.R;
import com.example.projectgoteat.UI.main.myItemList.MyItemListFragment;
import com.example.projectgoteat.UI.main.myPage.MyPageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment homefragment;
    MyItemListFragment myItemListFragment;
    MyPageFragment mypageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //초기 세팅
        init();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.fragment_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new HomeFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.fragment_myitemlist) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new MyItemListFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.fragment_mypage) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new MyPageFragment())
                        .commit();
                return true;
            }
            return false;
        });
    }

    private void init(){
        // fragment 객체 생성
        homefragment = new HomeFragment();
        myItemListFragment = new MyItemListFragment();
        mypageFragment = new MyPageFragment();

        // activity_main.xml의 bottomNavigationView 연결
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        // 초기 표시 뷰 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, homefragment).commitAllowingStateLoss();

    }
}