package com.example.projectgoteat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectgoteat.network.RetrofitHelper;
import com.example.projectgoteat.network.RetrofitService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScrapActivity extends AppCompatActivity {

    private static final String TAG = "ScrapActivity";
    Retrofit retrofit;
    RetrofitService retrofitService;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 액션바 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.scrap_main);

        retrofit = RetrofitHelper.getRetrofitInstance(this);
        retrofitService = retrofit.create(RetrofitService.class);

        getScrap();
    }

    private void getScrap() { // 1.3.2 스크랩 조회
        // 백엔드로 넘겨 줄 파라미터
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String uid = String.valueOf(sharedPreferences.getInt("uid", -1));

        // API를 호출할 객체 생성
        Call<List<HashMap<String, Object>>> call = retrofitService.getScrap(uid);

        // API 호출
        call.enqueue(new Callback<List<HashMap<String, Object>>>() {
            @Override
            public void onResponse(Call<List<HashMap<String, Object>>> call, Response<List<HashMap<String, Object>>> response) {
                if (response.isSuccessful()) { // 200(정상적인 응답)이 왔을 때는 response.isSuccessful() 가 true
                    List<HashMap<String, Object>> scrapList = response.body();

                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ScrapActivity.this));

                    List<BoardItem> itemList = new ArrayList<>();
                    for (int i = 0; i < scrapList.size(); i++) {
                        HashMap<String, Object> ScrapMap = scrapList.get(i);

                        String strQuantity = String.valueOf(((Double) ScrapMap.get("quantity")).intValue());
                        String strEachQuantity = String.valueOf(((Double) ScrapMap.get("each_quantity")).intValue());
                        String strEachPrice = String.valueOf(((Double) ScrapMap.get("each_price")).intValue());
                        String strPartHeadcnt = String.valueOf(((Double) ScrapMap.get("participantsHeadcnt")).intValue());
                        String strHeadcnt = String.valueOf(((Double) ScrapMap.get("headcnt")).intValue());
                        String strIsScraped = String.valueOf(((Double) ScrapMap.get("isScraped")).intValue());
                        boolean isFinished = "1".equals(String.valueOf(((Double) ScrapMap.get("isFinished")).intValue()));
                        String bid = String.valueOf(ScrapMap.get("id"));

                        String title = String.valueOf(ScrapMap.get("itemName")) + " | 총 " + strQuantity + String.valueOf(ScrapMap.get("scale"));
                        String eachPrice = strEachQuantity + String.valueOf(ScrapMap.get("scale")) + " 당 " + strEachPrice + "원";
                        String location = "장소 : " + String.valueOf(ScrapMap.get("meetingLocation"));
                        String participants = "인원 : " + strPartHeadcnt + "/" + strHeadcnt;
                        String category = String.valueOf(ScrapMap.get("category"));
                        boolean isBookmarked = "1".equals(strIsScraped);
                        String imageUrl = String.valueOf(ScrapMap.get("item_image1"));
                        itemList.add(new BoardItem(bid, imageUrl, title, eachPrice, location, participants, category, isBookmarked, isFinished));
                    }

                    BoardAdapter adapter = new BoardAdapter(ScrapActivity.this, itemList);
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
