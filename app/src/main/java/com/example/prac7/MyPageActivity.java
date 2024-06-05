package com.example.prac7;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prac7.network.RetrofitHelper;
import com.example.prac7.network.RetrofitService;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyPageActivity extends AppCompatActivity {

    private static final String TAG = "MyPageActivity";

    private TextView pvNickname;
    private TextView pvRank;
    private TextView pvPoint;

    private ImageView imageView9;
    private ImageView imageView8;
    private TextView tvmypage;
    private Button btn_point;
    private Button btn_scrap;
    private Button btn_review;
    private Button btn_logout;
    private ImageButton btnHome, btnChat, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 액션바 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.my_page);

        pvRank = findViewById(R.id.pvRank);
        pvNickname = findViewById(R.id.pvNickname); // ID 일치 확인
        pvPoint = findViewById(R.id.pvPoint); // ID 일치 확인

        btn_point = findViewById(R.id.btn_point);
        btn_scrap = findViewById(R.id.btn_scrap);
        btn_review = findViewById(R.id.btn_review);
        btn_logout = findViewById(R.id.btn_logout);

        btnHome = findViewById(R.id.btnHome);
        btnChat = findViewById(R.id.btnChat);
        btnProfile = findViewById(R.id.btnProfile);

        // 하단 바 버튼 클릭 리스너 설정
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivity로 이동
                Intent intent = new Intent(MyPageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ChatActivity로 이동
                //Intent intent = new Intent(MyPageActivity.this, ChatActivity.class);
                //startActivity(intent);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MyPageActivity로 이동 (현재 페이지)
                Intent intent = new Intent(MyPageActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });

        getUserInfo();

        btn_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, PointHistoryActivity.class);
                startActivity(intent);
            }
        });

        btn_scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, ScrapActivity.class);
                startActivity(intent);
            }
        });

        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, ReviewActivity.class);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPreferences();
                Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void clearPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


    private void getUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        int uid = sharedPreferences.getInt("uid", -1);

        if (uid == -1) {
            Log.e(TAG, "UID is null");
            return;
        }

        Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        Call<HashMap<String, Object>> call = retrofitService.getUser(String.valueOf(uid));
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful()) {
                    HashMap<String, Object> userInfo = response.body();
                    if (userInfo != null) {
                        pvNickname.setText("닉네임 : " + String.valueOf(userInfo.get("profile_name")));
                        pvRank.setText("등급 : " + String.valueOf(userInfo.get("rank")));
                        pvPoint.setText("포인트 : " + String.valueOf(((Double) userInfo.get("point")).intValue()));
                    }
                } else {
                    Log.e(TAG, "Response not successful");
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
            }
        });
    }
}
