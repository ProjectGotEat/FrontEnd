package com.example.projectgotit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectgotit.network.RetrofitHelper;
import com.example.projectgotit.network.RetrofitService;

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
    Boolean isChecked = false;

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
                    Boolean isExist = (Boolean) responseMap.get("isExist");

                    if (isExist != null && isExist) {
                        Toast.makeText(SignupActivity.this, "아이디가 이미 존재합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignupActivity.this, "아이디를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        isChecked = true;
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
        if (!isChecked) {
            Toast.makeText(this, "아이디 중복체크를 해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("profile_name", nickname);
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("noti_allow", isNotificationEnabled ? 1 : 0);

        Call<Void> call = retrofitService.postAuthJoin(requestBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
                Toast.makeText(SignupActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginInfo(int uid) {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("uid", uid);
        editor.apply();
    }
}
