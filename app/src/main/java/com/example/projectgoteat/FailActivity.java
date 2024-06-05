package com.example.projectgoteat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.projectgoteat.network.RetrofitHelper;
import com.example.projectgoteat.network.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FailActivity extends AppCompatActivity {

    private RetrofitService retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fail);

        // RetrofitService 초기화
        Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
        retrofitService = retrofit.create(RetrofitService.class);

        // Intent로 전달된 participantId를 가져옴
        int participantId = getIntent().getIntExtra("participantId", 0);

        // 실패를 서버에 전송하는 메소드 호출
        sendFailToServer(participantId);
    }

    // 서버로 실패를 전송하는 메소드
    private void sendFailToServer(int participantId) {
        retrofitService.markItemFail(participantId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FailActivity.this, "실패가 서버로 전송되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FailActivity.this, "실패 전송에 문제가 발생했습니다: " + response.code(), Toast.LENGTH_SHORT).show();
                }
                // MainActivity로 돌아가기
                Intent intent = new Intent(FailActivity.this, MyItemList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(FailActivity.this, "서버 통신에 실패했습니다: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // MainActivity로 돌아가기
                Intent intent = new Intent(FailActivity.this, MyItemList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
