package com.example.projectgoteat.UI.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectgoteat.R;
import com.example.projectgoteat.UI.main.HomeFragment;
import com.example.projectgoteat.UI.main.MainActivity;
import com.example.projectgoteat.network.RetrofitHelper;
import com.example.projectgoteat.network.RetrofitService;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_id);
        etPassword = findViewById(R.id.et_pass);
        btnLogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.btn_signup);

        btnLogin.setOnClickListener(v -> loginUser());

        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // 엔터키로 submit
        etPassword.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                btnLogin.callOnClick();
                return true;
            }
            return false;
        });

        checkLoginStatus();
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = RetrofitHelper.getRetrofitInstance(this);
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);

        Call<HashMap<String, Object>> call = retrofitService.postAuthLogin(requestBody);
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HashMap<String, Object> responseBody = response.body();
                    int uid = ((Double) responseBody.get("uid")).intValue();
                    double preferredLatitude = ((Double) responseBody.get("preferred_latitude")).doubleValue();
                    double preferredLongitude = ((Double) responseBody.get("preferred_longitude")).doubleValue();
                    String token = (String) responseBody.get("token"); // Assuming the response contains a token

                    saveLoginInfo(uid, preferredLatitude, preferredLongitude, token);

                    Toast.makeText(LoginActivity.this, "환영합니다!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "아이디나 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginInfo(int uid, double preferredLatitude, double preferredLongitude, String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("uid", uid);
        editor.putString("preferredLatitude", String.valueOf(preferredLatitude)); // double형을 저장하는 메소드를 지원하지 않으므로, String으로 변환하여 저장
        editor.putString("preferredLongitude", String.valueOf(preferredLongitude)); // double형을 저장하는 메소드를 지원하지 않으므로, String으로 변환하여 저장
        editor.putString("token", token); // Save the token
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
