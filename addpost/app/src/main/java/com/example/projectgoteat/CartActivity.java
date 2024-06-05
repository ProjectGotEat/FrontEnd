package com.example.projectgoteat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.example.projectgoteat.api.ApiCallback;
import com.example.projectgoteat.api.RetrofitHelper;
import com.example.projectgoteat.model.BoardDetailResponse;

public class CartActivity extends AppCompatActivity {

    private Button postButton; // 신청 버튼
    private Button scrapButton; // 스크랩 버튼
    private int boardId; // 게시물 ID
    private String uid; // 사용자 ID
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        categoryInput = findViewById(R.id.category_input);
        titleText =findViewById(R.id.titletext);
        placeInput = findViewById(R.id.place_input);
        timeInput = findViewById(R.id.time_input);
        amountInput = findViewById(R.id.amount_input);
        costInput = findViewById(R.id.cost_input);
        itemImage1 = findViewById(R.id.image);
        itemImage2 = findViewById(R.id.image);// 이미지 2를 위한 아이디로 가정
        receiptImage =findViewById(R.id.image_receipt);
        userImage = findViewById(R.id.profile_image);
        userNickname = findViewById(R.id.ID_input);
        userRank = findViewById(R.id.rank_input);
        postButton = findViewById(R.id.post_button);
        scrapButton = findViewById(R.id.scrap_button);


         int userId= 10;
         int boardId= 5;

        // 게시물 상세 정보 가져오기
        fetchBoardDetails(boardId, userId);

        // 신청 버튼 클릭 이벤트 처리
        postButton.setOnClickListener(v -> {
            // 소분 가능 여부를 확인하고 메시지를 표시
            if (boardDetailResponse != null && boardDetailResponse.isIs_reusable()==1) {
                Toast.makeText(getApplicationContext(), "소분 시 다회용기를 지참해주세요", Toast.LENGTH_SHORT).show();
            }

            // 서버로 신청 요청을 보냄
            RetrofitHelper.requestBoard(boardId, new ApiCallback<Void>() {
                @Override
                public void onSuccess(Void response) {
                    Toast.makeText(CartActivity.this, "소분 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // 스크랩 버튼 클릭 이벤트 처리
        scrapButton.setOnClickListener(v -> {
            // 서버로 스크랩 요청을 보냄
            RetrofitHelper.scrapBoard(boardId, userId, new ApiCallback<Void>() {
                @Override
                public void onSuccess(Void response) {
                    // 버튼 텍스트를 변경하여 스크랩 상태를 나타냄
                    scrapButton.setText("♥");
                    Toast.makeText(CartActivity.this, "게시물을 스크랩했습니다.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });


        //이전 페이지 이동
        ImageView closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> onBackPressed()); // 이전 페이지로 이동
    }

    private void fetchBoardDetails(int boardId, int userId) {
        RetrofitHelper.getBoardDetail(this, boardId, userId, new ApiCallback<BoardDetailResponse>() {
            @Override
            public void onSuccess(BoardDetailResponse response) {
                // Update UI with response data
                placeInput.setText(response.getMeeting_location());
                timeInput.setText(response.getMeeting_time());
                amountInput.setText(String.valueOf(response.getEach_quantity()) + " " + response.getScale());
                costInput.setText(String.valueOf(response.getEach_price()));
                categoryInput.setText(response.getCategory());
                categoryInput.setText(response.getCategory());
                titleText.setText(response.getItem_name());

                Log.d("ImageLoading", "Item Image 1: " + response.getItem_image1());
                Log.d("ImageLoading", "Item Image 2: " + response.getItem_image2());
                Log.d("ImageLoading", "Receipt Image: " + response.getReceipt_image());
                Log.d("ImageLoading", "User Image: " + response.getUser_image());

                try {
                    // 이미지 로드
                    Glide.with(CartActivity.this)
                            .load(response.getItem_image1())
                            .into(itemImage1);

                    if (response.getReceipt_image() != null) {
                        Glide.with(CartActivity.this)
                                .load(response.getReceipt_image())
                                .into(receiptImage);
                    }

                    if (response.getItem_image2() != null) {
                        Glide.with(CartActivity.this)
                                .load(response.getItem_image2())
                                .into(itemImage2);
                    }

                    Glide.with(CartActivity.this)
                            .load(response.getUser_image())
                            .into(userImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                userNickname.setText(response.getUser_nickname());
                userRank.setText(response.getUser_rank());

                // 소분 완료 상태인 경우 버튼 비활성화
                if (response.isIs_finished()==1) {
                    postButton.setEnabled(false);
                    postButton.setText("소분 완료됨");
                }

                // 가져온 게시물 상세 정보를 response 변수에 저장
                boardDetailResponse = response;;
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
