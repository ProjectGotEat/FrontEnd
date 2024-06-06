package com.example.projectgoteat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projectgoteat.api.ApiCallback;
import com.example.projectgoteat.api.RetrofitHelper;
import com.example.projectgoteat.model.BoardDetailResponse;

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

    private int userId = 10;
    private int boardId = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

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
        itemImage2 = findViewById(R.id.image);// 이미지 2를 위한 아이디로 가정
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
        if (!isAlreadyRequested()) {
            RetrofitHelper.requestBoard(boardId, new ApiCallback<Void>() {
                @Override
                public void onSuccess(Void response) {
                    saveRequestedStatus();
                    Toast.makeText(CartActivity.this, "소분 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(CartActivity.this, "이미 소분 신청한 게시물입니다.", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveRequestedStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("RequestedStatus", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isRequested_" + boardId, true);
        editor.apply();
    }

    private boolean isAlreadyRequested() {
        SharedPreferences sharedPreferences = getSharedPreferences("RequestedStatus", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isRequested_" + boardId, false);
    }
}
