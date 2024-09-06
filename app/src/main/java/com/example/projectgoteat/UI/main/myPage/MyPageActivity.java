package com.example.projectgoteat.UI.main.myPage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projectgoteat.R;
import com.example.projectgoteat.UI.auth.LoginActivity;
import com.example.projectgoteat.UI.auth.SignupActivity;
import com.example.projectgoteat.UI.main.MainActivity;
import com.example.projectgoteat.UI.main.board.addPost.PlacePickerActivity;
import com.example.projectgoteat.UI.main.myItemList.MyItemList;
import com.example.projectgoteat.UI.main.myPage.pointHistory.PointHistoryActivity;
import com.example.projectgoteat.UI.main.myPage.review.ReviewActivity;
import com.example.projectgoteat.UI.main.myPage.scrap.ScrapActivity;
import com.example.projectgoteat.common.CommonCode;
import com.example.projectgoteat.network.RetrofitHelper;
import com.example.projectgoteat.network.RetrofitService;

import java.io.IOException;
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
    private TextView tvPlace;

    private Button btn_point;
    private Button btn_scrap;
    private Button btn_review;
    private Button btn_logout;
    private ImageButton btnHome, btnChat, btnProfile;
    private ImageView profileImage;

    private double latitude = 0.0;
    private double longitude = 0.0;

    Retrofit retrofit;
    RetrofitService retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 액션바 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.my_page);

        retrofit = RetrofitHelper.getRetrofitInstance(this);
        retrofitService = retrofit.create(RetrofitService.class);

        pvRank = findViewById(R.id.pvRank);
        pvNickname = findViewById(R.id.pvNickname);
        pvPoint = findViewById(R.id.pvPoint);
        tvPlace = findViewById(R.id.tv_place);

        profileImage = findViewById(R.id.profile_image);
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
                Intent intent = new Intent(MyPageActivity.this, MyItemList.class);
                startActivity(intent);
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

        // 거래 희망 장소 텍스트뷰 클릭 이벤트
        tvPlace.setOnClickListener(v -> {
            Intent placePickerIntent = new Intent(MyPageActivity.this, PlacePickerActivity.class);
            startActivityForResult(placePickerIntent, CommonCode.PLACE_PICKER_REQUEST_CODE); // 다음 화면으로 이동
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ActivityResult", "requestCode: " + requestCode + ", resultCode: " + resultCode);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CommonCode.PLACE_PICKER_REQUEST_CODE && data.hasExtra("address")) {
                String address = data.getStringExtra("address");
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                this.latitude = latitude;
                this.longitude = longitude;
                updatePreferredLocation(address);
                Log.d("ActivityResult", "Place selected: " + address);
            }
        }
    }

    private void updatePreferredLocation(String address) {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        int uid = sharedPreferences.getInt("uid", -1);

        if (uid != -1) {
            String userId = String.valueOf(uid);

            HashMap<String, Object> requestBody = new HashMap<>();
            requestBody.put("preferred_location", address);
            requestBody.put("preferred_latitude", latitude);
            requestBody.put("preferred_longitude", longitude);

            retrofitService.putUserLocation(userId, requestBody).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "preferred location updated successfully");
                        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("preferredLatitude", String.valueOf(latitude)); // double형을 저장하는 메소드를 지원하지 않으므로, String으로 변환하여 저장
                        editor.putString("preferredLongitude", String.valueOf(longitude)); // double형을 저장하는 메소드를 지원하지 않으므로, String으로 변환하여 저장
                        editor.apply();
                        tvPlace.setText(address);
                        Toast.makeText(MyPageActivity.this, "변경되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyPageActivity.this, "변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Network error: " + t.getMessage());
                    Toast.makeText(MyPageActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void clearPreferences() {
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

        Retrofit retrofit = RetrofitHelper.getRetrofitInstance(this);
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        Call<HashMap<String, Object>> call = retrofitService.getUser(String.valueOf(uid));
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful()) {
                    HashMap<String, Object> userInfo = response.body();
                    if (userInfo != null) {
                        pvNickname.setText(String.valueOf(userInfo.get("profile_name")));
                        pvRank.setText(String.valueOf(userInfo.get("rank")));
                        pvPoint.setText(String.valueOf(((Double) userInfo.get("point")).intValue()));
                        tvPlace.setText(String.valueOf(userInfo.get("preferred_location")));
                        Glide.with(MyPageActivity.this).load(userInfo.get("image")).into(profileImage);
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
