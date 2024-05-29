package com.example.addpost;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CartActivity extends AppCompatActivity {

    private TextView placeInputTextView;
    private TextView timeInputTextView;
    private TextView amountInputTextView;
    private TextView costInputTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // XML 파일에서 TextView를 찾아 변수에 할당합니다.
        placeInputTextView = findViewById(R.id.place_input);
        timeInputTextView = findViewById(R.id.time_input);
        amountInputTextView = findViewById(R.id.amount_input);
        costInputTextView = findViewById(R.id.cost_input);

        // API를 통해 데이터를 가져옵니다. 원하는 게시물 ID를 전달합니다.
        //ApiHelper.getBoardDetail(this, boardId, new ApiCallback<Board>() {
            //@Override
            //public void onSuccess(Board board) {
                // 가져온 데이터를 TextView에 설정합니다.
                //placeInputTextView.setText(board.getMeetingPlace());
                //timeInputTextView.setText(board.getMeetingTime());
                //amountInputTextView.setText(String.valueOf(board.getTotalAmount()));
                //costInputTextView.setText(String.valueOf(board.getCostPerPerson()));
            //}

            //@Override
            //public void onFailure(String errorMessage) {
                // 실패할 경우 처리
                //Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            //}
        //});
    }
}

