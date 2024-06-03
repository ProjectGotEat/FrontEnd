package com.example.addpost;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.addpost.api.ApiCallback;
import com.example.addpost.api.ApiHelper;
import com.example.addpost.model.BoardDetailResponse;

public class CartActivity extends AppCompatActivity {

    private Button postButton; // 신청 버튼
    private Button scrapButton; // 스크랩 버튼
    private int boardId; // 게시물 ID
    private String uid; // 사용자 ID
    private TextView categoryInput;
    private TextView placeInput;
    private TextView timeInput;
    private TextView amountInput;
    private TextView costInput;
    private ImageView itemImage1;
    private ImageView itemImage2;
    private ImageView userImage;
    private TextView userNickname;
    private TextView userRank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        categoryInput = findViewById(R.id.category_input);
        placeInput = findViewById(R.id.place_input);
        timeInput = findViewById(R.id.time_input);
        amountInput = findViewById(R.id.amount_input);
        costInput = findViewById(R.id.cost_input);
        itemImage1 = findViewById(R.id.image);
        itemImage2 = findViewById(R.id.image); // 이미지 2를 위한 아이디로 가정
        userImage = findViewById(R.id.profile_image);
        userNickname = findViewById(R.id.ID_input);
        userRank = findViewById(R.id.rank_input);
        postButton = findViewById(R.id.post_button);
        scrapButton = findViewById(R.id.scrap_button);

        // 게시물 ID와 사용자 ID를 가져옴
        boardId = 123;
        uid = "user123";

        // 신청 버튼 클릭 이벤트 처리
        postButton.setOnClickListener(v -> {
            // 서버로 신청 요청을 보냄
            ApiHelper.requestBoard(boardId, new ApiCallback<Void>() {
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
            ApiHelper.scrapBoard(boardId, uid, new ApiCallback<Void>() {
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

        // 게시물 상세 정보 가져오기
        fetchBoardDetails(boardId, uid);
    }

    private void fetchBoardDetails(int boardId, String userId) {
        ApiHelper.getBoardDetail(this, boardId, userId, new ApiCallback<BoardDetailResponse>() {
            @Override
            public void onSuccess(BoardDetailResponse response) {
                // Update UI with response data
                placeInput.setText(response.getMeeting_location());
                timeInput.setText(response.getMeeting_time());
                amountInput.setText(String.valueOf(response.getEach_quantity()) + " " + response.getScale());
                costInput.setText(String.valueOf(response.getEach_price()));
                categoryInput.setText(response.getCategory());

                // Load images (you can use libraries like Glide or Picasso)
                Glide.with(CartActivity.this).load(response.getItem_image1()).into(itemImage1);
                if (response.getItem_image2() != null) {
                    Glide.with(CartActivity.this).load(response.getItem_image2()).into(itemImage2);
                }
                Glide.with(CartActivity.this).load(response.getUser_image()).into(userImage);
                userNickname.setText(response.getUser_nickname());
                userRank.setText(response.getUser_rank());
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
