package com.example.prac7;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText pvName;
    private EditText pvNickname;
    private EditText pvLevel;

    private ImageView imageView9;
    private ImageView imageView8;
    private TextView tvmypage;
    private Button btn_point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_page);

        btn_point = findViewById(R.id.btn_point);
        pvName = findViewById(R.id.pvName);
        pvNickname = findViewById(R.id.pvNickname); // ID 일치 확인
        pvLevel = findViewById(R.id.pvLevel); // ID 일치 확인

        getUserInfo();

        btn_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, PointHistoryActivity.class);
                startActivity(intent);
            }
        });
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
                        pvName.setText(String.valueOf(userInfo.get("name")));
                        pvNickname.setText(String.valueOf(userInfo.get("nickname")));
                        pvLevel.setText(String.valueOf(userInfo.get("level")));
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
