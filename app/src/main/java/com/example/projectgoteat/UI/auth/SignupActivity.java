package com.example.projectgoteat.UI.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectgoteat.R;
import com.example.projectgoteat.UI._globalUtil.UIHelper;
import com.example.projectgoteat.UI.main.board.addPost.AddpostActivity;
import com.example.projectgoteat.UI.main.board.addPost.PlacePickerActivity;
import com.example.projectgoteat.common.CommonCode;
import com.example.projectgoteat.model.Users;
import com.example.projectgoteat.network.RetrofitHelper;
import com.example.projectgoteat.network.RetrofitService;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText etName, etNickname, etId, etPass;
    private Button btnCheckId, btnRegister;
    private ImageView uploadIcon;
    private ShapeableImageView profileImage;
    private Uri imageUri;
    private TextView tvPlace;
    private double latitude = 0.0;
    private double longitude = 0.0;

    private Switch switchNotification;
    Boolean isChecked = false;

    Retrofit retrofit;
    RetrofitService retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        retrofit = RetrofitHelper.getRetrofitInstance(this);
        retrofitService = retrofit.create(RetrofitService.class);

        etName = findViewById(R.id.et_name);
        etNickname = findViewById(R.id.et_nickname);
        etId = findViewById(R.id.et_id);
        etPass = findViewById(R.id.et_pass);
        btnCheckId = findViewById(R.id.btn_check_id);
        btnRegister = findViewById(R.id.btn_register);
        uploadIcon = findViewById(R.id.uploadicon);
        profileImage = findViewById(R.id.image);
        tvPlace = findViewById(R.id.tv_place);

        switchNotification = findViewById(R.id.switch_notification);

        btnCheckId.setOnClickListener(v -> checkIdAvailability());

        btnRegister.setOnClickListener(v -> registerUser());

        // 갤러리 열기 버튼 클릭 이벤트 처리
        uploadIcon.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            SignupActivity.this.startActivityForResult(galleryIntent, CommonCode.GALLERY_REQUEST_CODE_1);
        });

        // 장소 선택 텍스트뷰 클릭 이벤트
        tvPlace.setOnClickListener(v -> {
            Intent placePickerIntent = new Intent(SignupActivity.this, PlacePickerActivity.class);
            startActivityForResult(placePickerIntent, CommonCode.PLACE_PICKER_REQUEST_CODE); // 다음 화면으로 이동
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ActivityResult", "requestCode: " + requestCode + ", resultCode: " + resultCode);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CommonCode.GALLERY_REQUEST_CODE_1) {
                imageUri = data.getData();
                displaySelectedImage(imageUri, profileImage);
                Log.d("ActivityResult", "Image 1 selected: " + imageUri.toString());
            } else if (requestCode == CommonCode.PLACE_PICKER_REQUEST_CODE && data.hasExtra("address")) {
                String address = data.getStringExtra("address");
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                tvPlace.setText(address);
                this.latitude = latitude;
                this.longitude = longitude;
                Log.d("ActivityResult", "Place selected: " + address);
            }
        }
    }

    private void displaySelectedImage(Uri imageUri, ImageView imageView) {
        imageView.setImageURI(imageUri);
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
        double preferredLatitude = latitude;
        double preferredLongitude = longitude;

        if (name.isEmpty() || nickname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isChecked) {
            Toast.makeText(this, "아이디 중복체크를 해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (preferredLatitude == 0.0 || preferredLongitude == 0.0) {
            Toast.makeText(this, "거래 희망 장소를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            MultipartBody.Part body;
            if (imageUri != null) {
                File imageFile = RetrofitHelper.getFileFromUri(this, imageUri);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                body = MultipartBody.Part.createFormData("profile_image", imageFile.getName(), requestFile);
            } else {
                RequestBody emptyRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), new byte[0]);
                body = MultipartBody.Part.createFormData("profile_image", "", emptyRequestBody);
            }

            Map<String, Object> requestBody = new HashMap<>();
            Users user = new Users();
            user.setName(name);
            user.setProfileName(nickname);
            user.setEmail(email);
            user.setPassword(password);
            user.setNotiAllow(isNotificationEnabled ? 1 : 0);
            user.setPreferredLatitude(preferredLatitude);
            user.setPreferredLongitude(preferredLongitude);
            user.setPreferredLocation(tvPlace.getText().toString());

            Gson gson = new GsonBuilder().create();
            String userJson = gson.toJson(user);
            RequestBody userRequestBody = RequestBody.create(MediaType.parse("application/json"), userJson);

            Call<Void> call = retrofitService.postAuthJoin(body, userRequestBody);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        } catch (Exception e) {
            Toast.makeText(this, "회원가입 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            Log.e("Data", "Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveLoginInfo(int uid) {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("uid", uid);
        editor.apply();
    }
}
