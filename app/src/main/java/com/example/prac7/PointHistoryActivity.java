package com.example.prac7;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prac7.network.RetrofitHelper;
import com.example.prac7.network.RetrofitService;
import com.example.prac7.network.RetrofitHelper;
import com.example.prac7.network.RetrofitService;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PointHistoryActivity extends AppCompatActivity {

    private static final String TAG = "PointHistoryActivity";
    Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
    RetrofitService retrofitService = retrofit.create(RetrofitService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_history_main);

        getUserPoint();

    }

    private void getUserPoint() { // 1.3.1 포인트 조회
        // 백엔드로 넘겨 줄 파라미터
        String uid = "10"; // TODO: 로그인한 사용자의 uid를 설정하도록 수정. dummy data의 uid는 10

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
                            outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
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