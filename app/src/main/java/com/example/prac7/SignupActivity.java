package com.example.prac7;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prac7.network.RetrofitHelper;
import com.example.prac7.network.RetrofitService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText etName, etNickname, etId, etPass;
    private Button btnCheckId, btnRegister;

    private Switch switchNotification;

    Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
    RetrofitService retrofitService = retrofit.create(RetrofitService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.et_name);
        etNickname = findViewById(R.id.et_nickname);
        etId = findViewById(R.id.et_id);
        etPass = findViewById(R.id.et_pass);
        btnCheckId = findViewById(R.id.btn_check_id);
        btnRegister = findViewById(R.id.btn_register);

        switchNotification = findViewById(R.id.switch_notification);

        btnCheckId.setOnClickListener(v -> checkIdAvailability());

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void checkIdAvailability() {
        String email = etId.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<HashMap<String, Object>> call = retrofitService.getAuthJoinExist(email);
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HashMap<String, Object> responseMap = response.body();
                    boolean isExist = (boolean) responseMap.get("isExist");
                    if (isExist) {
                        Toast.makeText(SignupActivity.this, "아이디가 이미 존재합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignupActivity.this, "아이디를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "중복확인 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
                t.printStackTrace();
                Toast.makeText(SignupActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String nickname = etNickname.getText().toString().trim();
        String email = etId.getText().toString().trim();
        String password = etPass.getText().toString().trim();
        boolean isNotificationEnabled = switchNotification.isChecked();

        if (name.isEmpty() || nickname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("profile_name", nickname);
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("noti_allow", isNotificationEnabled ? 1 : 0);

        Call<HashMap<String, Object>> call = retrofitService.postAuthJoin(requestBody);
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                    // 회원가입 성공 시 로그인 화면으로 이동
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
                Toast.makeText(SignupActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
