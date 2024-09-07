package com.example.projectgoteat.UI.main.board;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projectgoteat.R;
import com.example.projectgoteat.model.BoardDetailResponse;
import com.example.projectgoteat.network.ApiCallback;
import com.example.projectgoteat.network.RetrofitHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class CartActivity extends AppCompatActivity {

    private Button postButton; // 신청 버튼

    private TextView titleText;
    private TextView categoryInput;
    private TextView placeInput;
    private TextView timeInput;
    private TextView amountInput;
    private TextView costInput;
    private ImageView itemImage1;

    private ImageView receiptImage;
    private ImageView userImage;
    private TextView userNickname;
    private TextView userRank;

    private BoardDetailResponse boardDetailResponse; // 게시물 상세 정보를 담을 변수

    private int userId;
    private int boardId; // 동적으로 받을 boardId
    private String image_url;
    private String receipt_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        userId = getUserId(); // 사용자 아이디 가져오기
        boardId = getIntent().getIntExtra("BOARD_ID", -1); // HomeActivity에서 전달받은 boardId

        initializeViews();
        fetchBoardDetails();

        // 소분 신청 버튼 클릭 이벤트 처리
        postButton.setOnClickListener(v -> handlePostButtonClick());

        // 이미지 클릭 이벤트 처리
        itemImage1.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, FullscreenImageActivity.class);
            intent.putExtra("imageResId", image_url); // 이미지 리소스 ID 전달
            startActivity(intent);
        });

        receiptImage.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, FullscreenImageActivity.class);
            intent.putExtra("imageResId", receipt_url); // 이미지 리소스 ID 전달
            startActivity(intent);
        });

        // 이전 페이지 이동
        ImageView closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> onBackPressed()); // 이전 페이지로 이동
    }

    private void initializeViews() {
        categoryInput = findViewById(R.id.category_input);
        titleText = findViewById(R.id.titletext);
        placeInput = findViewById(R.id.place_input);
        timeInput = findViewById(R.id.time_input);
        amountInput = findViewById(R.id.amount_input);
        costInput = findViewById(R.id.cost_input);
        itemImage1 = findViewById(R.id.image);
        receiptImage = findViewById(R.id.image_receipt);
        userImage = findViewById(R.id.profile_image);
        userNickname = findViewById(R.id.ID_input);
        userRank = findViewById(R.id.rank_input);
        postButton = findViewById(R.id.post_button);
    }

    private void fetchBoardDetails() {
        RetrofitHelper.getBoardDetail(this, boardId, userId, new ApiCallback<BoardDetailResponse>() {
            @Override
            public void onSuccess(BoardDetailResponse response) {
                updateUI(response);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(BoardDetailResponse response) {
        image_url = response.getItem_image1();
        receipt_url = response.getReceipt_image();
        placeInput.setText(response.getMeeting_location());
        String meetingTime = response.getMeeting_time().substring(0, 4) + "년 "
                + response.getMeeting_time().substring(5, 7) + "월 "
                + response.getMeeting_time().substring(8, 10) + "일  "
                + response.getMeeting_time().substring(11, 13) + "시 "
                + response.getMeeting_time().substring(14, 16) + "분";
        timeInput.setText(meetingTime);
        amountInput.setText(String.valueOf(response.getEach_quantity()) + " " + response.getScale());
        costInput.setText(String.valueOf(response.getEach_price()) + "원");
        categoryInput.setText(response.getCategory());
        titleText.setText(response.getItem_name());
        
        if (response.getIs_full() == 1) { // 선착순 마감된 소분인 경우, 버튼 비활성화 처리
            postButton.setEnabled(false);
            postButton.setBackgroundResource(R.drawable.button_disabled);
            postButton.setText("선착순 마감되었습니다.");
        }
        
        if (response.getIs_requested() == 1) { // 이미 신청한 소분인 경우, 버튼 비활성화 처리
            postButton.setEnabled(false);
            postButton.setBackgroundResource(R.drawable.button_disabled);
            postButton.setText("요청되었습니다.");
        }

        // 위도, 경도를 기반으로 게시물에 등록된 장소와 거래 희망 장소가 5km 이내인 경우, 버튼 활성화. 이외 비활성화
        if (response.getIs_requested() != 1 && response.getIs_full() != 1) { // 이미 요청한 소분이 아니고, 마감된 소분이 아닌 경우
            SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
            String strPreferredLatitude = sharedPreferences.getString("preferredLatitude", "");
            String strPreferredLongitude = sharedPreferences.getString("preferredLongitude", "");

            if (strPreferredLatitude != null && !strPreferredLatitude.isEmpty()
                && strPreferredLongitude != null && !strPreferredLongitude.isEmpty()) {
                double preferredLatitude = Double.parseDouble(strPreferredLatitude);
                double preferredLongitude = Double.parseDouble(strPreferredLongitude);

                LatLng meetLocation = new LatLng(response.getLatitude(), response.getLongitude()); // 게시물에 등록된 거래 장소
                LatLng preferredLocation = new LatLng(preferredLatitude, preferredLongitude); // 사용자가 등록한 거래 희망 장소

                // 두 좌표 간의 거리 계산 (미터 단위)
                double distance = SphericalUtil.computeDistanceBetween(meetLocation, preferredLocation);

                // 5km 이내인지 확인
                double allowedDistance = 5000; // 5000미터(5km)
                if (distance > allowedDistance) { // 거리 초과postButton.setEnabled(false);
                    postButton.setEnabled(false);
                    postButton.setBackgroundResource(R.drawable.button_disabled);
                    postButton.setText("거래 희망 장소와 너무 멀어요.");
                }
            }
        }

        try {
            Glide.with(CartActivity.this).load(response.getItem_image1()).into(itemImage1);
            if (response.getReceipt_image() != null) {
                Glide.with(CartActivity.this).load(response.getReceipt_image()).into(receiptImage);
            }
            Glide.with(CartActivity.this).load(response.getUser_image()).into(userImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        userNickname.setText(response.getUser_nickname());
        userRank.setText(response.getUser_rank());

        if (response.isIs_finished() == 1) {
            postButton.setEnabled(false);
            postButton.setBackgroundResource(R.drawable.button_disabled);
            postButton.setText("종료된 소분입니다.");
        }

        boardDetailResponse = response;
    }

    private void handlePostButtonClick() {
        // 사용자가 주최자인지 확인합니다.
        if (boardDetailResponse.getOrganizerId() == userId) {
            Toast.makeText(CartActivity.this, "본인이 작성한 게시물은 요청할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        int organizerId = boardDetailResponse.getOrganizerId(); // 주최자의 ID를 가져옵니다.
        RetrofitHelper.requestBoard(this, boardId, userId, organizerId, new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                Toast.makeText(CartActivity.this, "요청이 성공적으로 제출되었습니다.", Toast.LENGTH_SHORT).show();
                // 성공적인 요청 후 버튼을 비활성화하고 텍스트를 변경합니다.
                postButton.setEnabled(false);
                postButton.setBackgroundResource(R.drawable.button_disabled);
                postButton.setText("요청 제출됨");
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("uid", -1);
    }
}
