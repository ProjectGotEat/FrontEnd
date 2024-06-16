package com.example.projectgoteat.UI.main.board;

import android.content.Context;
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

public class CartActivity extends AppCompatActivity {

    private Button postButton; // 신청 버튼

    private TextView titleText;
    private TextView categoryInput;
    private TextView placeInput;
    private TextView timeInput;
    private TextView amountInput;
    private TextView costInput;
    private ImageView itemImage1;
    private ImageView itemImage2;

    private ImageView receiptImage;
    private ImageView userImage;
    private TextView userNickname;
    private TextView userRank;

    private BoardDetailResponse boardDetailResponse; // 게시물 상세 정보를 담을 변수

    private int userId;
    private int boardId; // 동적으로 받을 boardId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        userId = getUserId(); // 사용자 아이디 가져오기
        boardId = getIntent().getIntExtra("BOARD_ID", -1); // MainActivity에서 전달받은 boardId

        initializeViews();
        fetchBoardDetails();

        // 소분 신청 버튼 클릭 이벤트 처리
        postButton.setOnClickListener(v -> handlePostButtonClick());

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
        itemImage2 = findViewById(R.id.image); // 이미지 2를 위한 아이디로 가정
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
        placeInput.setText(response.getMeeting_location());
        timeInput.setText(response.getMeeting_time());
        amountInput.setText(String.valueOf(response.getEach_quantity()) + " " + response.getScale());
        costInput.setText(String.valueOf(response.getEach_price()));
        categoryInput.setText(response.getCategory());
        titleText.setText(response.getItem_name());

        if (response.getIs_requested() == 1) { // 이미 신청한 소분인 경우, 버튼 비활성화 처리
            postButton.setEnabled(false);
            postButton.setText("요청 제출됨");
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
            postButton.setText("소분 완료됨");
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
                saveRequestedStatus();
                Toast.makeText(CartActivity.this, "요청이 성공적으로 제출되었습니다.", Toast.LENGTH_SHORT).show();
                // 성공적인 요청 후 버튼을 비활성화하고 텍스트를 변경합니다.
                postButton.setEnabled(false);
                postButton.setText("요청 제출됨");
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveRequestedStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("RequestedStatus", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isRequested_" + boardId + "_" + userId, true);
        editor.apply();
    }

    private boolean isAlreadyRequested() {
        SharedPreferences sharedPreferences = getSharedPreferences("RequestedStatus", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isRequested_" +  boardId + "_" + userId, false);
    }

    private int getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("uid", -1);
    }
}
