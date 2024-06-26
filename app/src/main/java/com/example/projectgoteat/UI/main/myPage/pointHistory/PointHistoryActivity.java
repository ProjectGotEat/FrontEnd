package com.example.projectgoteat.UI.main.myPage.pointHistory;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectgoteat.R;
import com.example.projectgoteat.model.PointHistoryItem;
import com.example.projectgoteat.network.RetrofitHelper;
import com.example.projectgoteat.network.RetrofitService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PointHistoryActivity extends AppCompatActivity {

    private static final String TAG = "PointHistoryActivity";
    Retrofit retrofit;
    RetrofitService retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 액션바 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.point_history_main);

        retrofit = RetrofitHelper.getRetrofitInstance(this);
        retrofitService = retrofit.create(RetrofitService.class);

        getUserPoint();

    }

    private void getUserPoint() { // 1.3.1 포인트 조회
        // 백엔드로 넘겨 줄 파라미터
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String uid = String.valueOf(sharedPreferences.getInt("uid", -1));

        // API를 호출할 객체 생성
        Call<List<HashMap<String, Object>>> call = retrofitService.getUserPoint(uid);

        // API 호출
        call.enqueue(new Callback<List<HashMap<String, Object>>>() {
            @Override
            public void onResponse(Call<List<HashMap<String, Object>>> call, Response<List<HashMap<String, Object>>> response) {
                if (response.isSuccessful()) { // 200(정상적인 응답)이 왔을 때는 response.isSuccessful() 가 true
                    List<HashMap<String, Object>> responseMapList = response.body(); // 백엔드에서 넘겨준 결과를 받아옴

                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(PointHistoryActivity.this));

                    List<PointHistoryItem> itemList = new ArrayList<>();
                    for (int i = 0; i < responseMapList.size(); i++) {
                        HashMap<String, Object> responseMap = responseMapList.get(i);

                        String strChangeReason = String.valueOf(responseMap.get("change_reason"));
                        String strChangePoint = String.valueOf(((Double) responseMap.get("change_point")).intValue());
                        String strCreatedAt = String.valueOf(responseMap.get("created_at"));
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date = inputFormat.parse(strCreatedAt);
                            outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                            long timeInMillis = date.getTime();
                            timeInMillis += 9 * 60 * 60 * 1000; // 18시간을 밀리초로 변환하여 더함
                            date = new Date(timeInMillis);
                            strCreatedAt = outputFormat.format(date);
                            itemList.add(new PointHistoryItem(strChangeReason, strChangePoint, strCreatedAt));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    PointHistoryAdapter adapter = new PointHistoryAdapter(PointHistoryActivity.this, itemList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<HashMap<String, Object>>> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
            }
        });
    }
}
