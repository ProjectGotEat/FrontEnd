package com.example.projectgoteat.UI.main.myPage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.projectgoteat.R;
import com.example.projectgoteat.UI.auth.LoginActivity;
import com.example.projectgoteat.UI.main.HomeFragment;
import com.example.projectgoteat.UI.main.board.addPost.PlacePickerActivity;
import com.example.projectgoteat.UI.main.myItemList.MyItemListFragment;
import com.example.projectgoteat.UI.main.myPage.pointHistory.PointHistoryActivity;
import com.example.projectgoteat.UI.main.myPage.review.ReviewActivity;
import com.example.projectgoteat.UI.main.myPage.scrap.ScrapActivity;
import com.example.projectgoteat.common.CommonCode;
import com.example.projectgoteat.network.RetrofitHelper;
import com.example.projectgoteat.network.RetrofitService;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyPageFragment extends Fragment {

    private static final String TAG = "MyPageFragment";

    private TextView pvNickname;
    private TextView pvRank;
    private TextView pvPoint;
    private TextView tvPlace;

    private Button btn_point;
    private Button btn_scrap;
    private Button btn_review;
    private Button btn_logout;
    private ImageView profileImage;

    private double latitude = 0.0;
    private double longitude = 0.0;

    Retrofit retrofit;
    RetrofitService retrofitService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_page, container, false);

        retrofit = RetrofitHelper.getRetrofitInstance(getContext());
        retrofitService = retrofit.create(RetrofitService.class);

        pvRank = view.findViewById(R.id.pvRank);
        pvNickname = view.findViewById(R.id.pvNickname);
        pvPoint = view.findViewById(R.id.pvPoint);
        tvPlace = view.findViewById(R.id.tv_place);

        profileImage = view.findViewById(R.id.profile_image);
        btn_point = view.findViewById(R.id.btn_point);
        btn_scrap = view.findViewById(R.id.btn_scrap);
        btn_review = view.findViewById(R.id.btn_review);
        btn_logout = view.findViewById(R.id.btn_logout);

        getUserInfo();

        btn_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PointHistoryActivity.class);
                startActivity(intent);
            }
        });

        btn_scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ScrapActivity.class);
                startActivity(intent);
            }
        });

        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ReviewActivity.class);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPreferences();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // 거래 희망 장소 텍스트뷰 클릭 이벤트
        tvPlace.setOnClickListener(v -> {
            Intent placePickerIntent = new Intent(getContext(), PlacePickerActivity.class);
            startActivityForResult(placePickerIntent, CommonCode.PLACE_PICKER_REQUEST_CODE); // 다음 화면으로 이동
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ActivityResult", "requestCode: " + requestCode + ", resultCode: " + resultCode);
        if (resultCode == -1 && data != null) {
            if (requestCode == CommonCode.PLACE_PICKER_REQUEST_CODE && data.hasExtra("address")) {
                String address = data.getStringExtra("address");
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                this.latitude = latitude;
                this.longitude = longitude;
                updatePreferredLocation(address);
                Log.d("ActivityResult", "Place selected: " + address);
            }
        }
    }

    private void updatePreferredLocation(String address) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        int uid = sharedPreferences.getInt("uid", -1);

        if (uid != -1) {
            String userId = String.valueOf(uid);

            HashMap<String, Object> requestBody = new HashMap<>();
            requestBody.put("preferred_location", address);
            requestBody.put("preferred_latitude", latitude);
            requestBody.put("preferred_longitude", longitude);

            retrofitService.putUserLocation(userId, requestBody).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "preferred location updated successfully");
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("preferredLatitude", String.valueOf(latitude)); // double형을 저장하는 메소드를 지원하지 않으므로, String으로 변환하여 저장
                        editor.putString("preferredLongitude", String.valueOf(longitude)); // double형을 저장하는 메소드를 지원하지 않으므로, String으로 변환하여 저장
                        editor.apply();
                        tvPlace.setText(address);
                        Toast.makeText(getContext(), "변경되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Network error: " + t.getMessage());
                    Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void clearPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void getUserInfo() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        int uid = sharedPreferences.getInt("uid", -1);

        if (uid == -1) {
            Log.e(TAG, "UID is null");
            return;
        }

        Retrofit retrofit = RetrofitHelper.getRetrofitInstance(getContext());
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        Call<HashMap<String, Object>> call = retrofitService.getUser(String.valueOf(uid));
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful()) {
                    HashMap<String, Object> userInfo = response.body();
                    if (userInfo != null) {
                        pvNickname.setText(String.valueOf(userInfo.get("profile_name")));
                        pvRank.setText(String.valueOf(userInfo.get("rank")));
                        pvPoint.setText(String.valueOf(((Double) userInfo.get("point")).intValue()));
                        tvPlace.setText(String.valueOf(userInfo.get("preferred_location")));
                        Glide.with(MyPageFragment.this).load(userInfo.get("image")).into(profileImage);
                    }
                } else {
                    Log.e(TAG, "Response not successful");
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
            }
        });
    }
}
