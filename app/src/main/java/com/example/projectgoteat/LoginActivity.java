package com.example.projectgoteat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projectgoteat.network.RetrofitHelper;
import com.example.projectgoteat.network.RetrofitService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private EditText etId;
    private EditText etPass;
    private Button btnLogin;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // 액션바 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_login);

        etId = findViewById(R.id.et_id);
        etPass = findViewById(R.id.et_pass);
        btnLogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.btn_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String id = etId.getText().toString().trim();
            String password = etPass.getText().toString().trim();
            validateLogin(id, password);
        });

        // 자동 로그인
        checkLoginStatus();
    }

    private void validateLogin(String id, String password) {
        Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
        RetrofitService service = retrofit.create(RetrofitService.class);

        Map<String, Object> loginRequest = new HashMap<>();
        loginRequest.put("email", id);
        loginRequest.put("password", password);

        Call<HashMap<String, Object>> call = service.postAuthLogin(loginRequest);
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HashMap<String, Object> responseMap = response.body();
                    Log.d("LoginActivity", "Response Body: " + responseMap);
                    if (responseMap.containsKey("uid")) {
                        int uid = ((Double) responseMap.get("uid")).intValue();
                        saveLoginInfo(uid);  // UID를 SharedPreferences에 저장
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("uid", uid);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "로그인 실패: uid 없음", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("LoginActivity", "Login failed: " + response.code());
                    Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "로그인 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginInfo(int uid) {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("uid", uid);
        editor.apply();
    }

    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        int uid = sharedPreferences.getInt("uid", -1);
        if (uid != -1) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
            finish();
        }
    }
}
