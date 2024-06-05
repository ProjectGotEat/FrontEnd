package com.example.prac7;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prac7.network.RetrofitHelper;
import com.example.prac7.network.RetrofitService;

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

public class ReviewActivity extends AppCompatActivity {

    private static final String TAG = "ReviewActivity";
    Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
    RetrofitService retrofitService = retrofit.create(RetrofitService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 액션바 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.review_main);

        getReview();

    }

    private void getReview() { // 1.3.3 리뷰 조회
        // 백엔드로 넘겨 줄 파라미터
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String uid = String.valueOf(sharedPreferences.getInt("uid", -1));

        // API를 호출할 객체 생성
        Call<List<HashMap<String, Object>>> call = retrofitService.getReview(uid);

        // API 호출
        call.enqueue(new Callback<List<HashMap<String, Object>>>() {
            @Override
            public void onResponse(Call<List<HashMap<String, Object>>> call, Response<List<HashMap<String, Object>>> response) {
                if (response.isSuccessful()) { // 200(정상적인 응답)이 왔을 때는 response.isSuccessful() 가 true
                    List<HashMap<String, Object>> responseMapList = response.body(); // 백엔드에서 넘겨준 결과를 받아옴

                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ReviewActivity.this));

                    List<ReviewItem> itemList = new ArrayList<>();
                    for (int i = 0; i < responseMapList.size(); i++) {
                        HashMap<String, Object> responseMap = responseMapList.get(i);

                        String itemName = String.valueOf(responseMap.get("item_name"));
                        int rate = ((Double) responseMap.get("rate")).intValue();
                        String strRate = "";
                        for (int j = 0; j < rate; j++) {
                            strRate += "★";
                        }
                        String createdAt = String.valueOf(responseMap.get("created_at"));
                        String reivewer = String.valueOf(responseMap.get("reviewer_nickname"));
                        String content = String.valueOf(responseMap.get("content"));
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date = inputFormat.parse(createdAt);
                            outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                            createdAt = outputFormat.format(date);
                            itemList.add(new ReviewItem(itemName, createdAt, strRate, reivewer, content));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    ReviewAdapter adapter = new ReviewAdapter(ReviewActivity.this, itemList);
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