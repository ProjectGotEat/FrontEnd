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

public class SuccessActivity extends AppCompatActivity {

    private RetrofitService retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        // RetrofitService 초기화
        Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
        retrofitService = retrofit.create(RetrofitService.class);

        // Intent로 전달된 participantId를 가져옴
        int participantId = getIntent().getIntExtra("participantId", 0);

        // 성공을 서버에 전송하는 메소드 호출
        sendSuccessToServer(participantId);
    }

    // 서버로 성공을 전송하는 메소드
    private void sendSuccessToServer(int participantId) {
        retrofitService.markItemSuccess(participantId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SuccessActivity.this, "성공이 서버로 전송되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SuccessActivity.this, "성공 전송에 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
                // MainActivity로 돌아가기
                Intent intent = new Intent(SuccessActivity.this, MyItemList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SuccessActivity.this, "서버 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                // MainActivity로 돌아가기
                Intent intent = new Intent(SuccessActivity.this, MyItemList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
