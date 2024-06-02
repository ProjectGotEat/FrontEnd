package com.example.addpost;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.Calendar;

public class AddpostActivity extends AppCompatActivity {

    private Calendar calendar;
    private TextView timeInputTextView;
    private ImageView uploadIcon1,uploadIcon2;
    private ShapeableImageView image1,image2;
    private TextView placeInput;
    private Uri imageUri1,imageUri2;
    private TextView classificationChoice;

    private static final int GALLERY_REQUEST_CODE_1 = 1001;
    private static final int GALLERY_REQUEST_CODE_2 = 1002;
    private static final int PLACE_PICKER_REQUEST_CODE = 123;

    private String[] mainCategories = {"신선식품", "가공식품","기타"};
    private String[][] subCategories = {
            {"과일", "채소", "유제품", "양념", "정육 및 계란", "곡물"},
            {"냉동식품", "베이커리", "가공육", "간식 및 음료"}
    };

    private int selectedMainCategory = -1;
    private int selectedSubCategory = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);

        // 주소 값을 받아옴
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("address")) {
            String address = intent.getStringExtra("address");

            // 받아온 주소 값을 TextView에 설정
            TextView addressTextView = findViewById(R.id.place_input);
            addressTextView.setText(address);
        }

        // UI 요소 초기화
        timeInputTextView = findViewById(R.id.time_input);
        uploadIcon1 = findViewById(R.id.uploadicon);
        uploadIcon2 = findViewById(R.id.uploadicon_2);
        image1 = findViewById(R.id.image);
        image2 = findViewById(R.id.image_receipt);
        placeInput = findViewById(R.id.place_input);
        calendar = Calendar.getInstance();
        classificationChoice = findViewById(R.id.classification_choice);
        TextView placeInputTextView = findViewById(R.id.place_input); // 장소 선택 텍스트뷰
// 장소 선택 텍스트뷰

        // 갤러리 열기 버튼 클릭 이벤트 처리
        uploadIcon1.setOnClickListener(v -> UIHelper.openGallery(AddpostActivity.this, GALLERY_REQUEST_CODE_1));
        uploadIcon2.setOnClickListener(v -> UIHelper.openGallery(AddpostActivity.this, GALLERY_REQUEST_CODE_2));

        // 날짜 및 시간 선택 다이얼로그 표시
        timeInputTextView.setOnClickListener(v -> showDateTimePicker());

        // 카테고리 선택 텍스트뷰 클릭 이벤트 처리
        classificationChoice.setOnClickListener(v -> showCategoryDialog());

        // 장소 선택 텍스트뷰 클릭 이벤트
        placeInputTextView.setOnClickListener(v -> {
            Intent placePickerIntent = new Intent(AddpostActivity.this, PlacePickerActivity.class);
            startActivityForResult(placePickerIntent, PLACE_PICKER_REQUEST_CODE); // 다음 화면으로 이동
        });

        // 게시 버튼 클릭 이벤트 처리
        Button postButton = findViewById(R.id.post_button);
        postButton.setOnClickListener(v -> registerPost());
    }

    private void showDateTimePicker() {
        UIHelper.showDateTimePicker(this, calendar, (view, year, month, dayOfMonth) -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(AddpostActivity.this, (view12, hourOfDay, minute1) -> {
                calendar.set(year, month, dayOfMonth, hourOfDay, minute1);
                updateTimeInputTextView(); // 시간 선택 후 텍스트 업데이트
            }, hour, minute, false);
            timePickerDialog.show();
        }, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            updateTimeInputTextView(); // 시간 선택 후 텍스트 업데이트
        });
    }

    private void updateTimeInputTextView() {
        String formattedDateTime = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", calendar).toString();
        timeInputTextView.setText(formattedDateTime);
    }

    private void showCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("카테고리 선택");

        builder.setItems(mainCategories, (dialog, which) -> {
            // 선택한 주 카테고리의 인덱스 저장
            selectedMainCategory = which;
            // 해당 주 카테고리에 대한 서브 카테고리 다이얼로그 표시
            showSubCategoryDialog(which);
        });
        builder.show();
    }

    private void showSubCategoryDialog(int mainCategoryIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("서브 카테고리 선택");

        builder.setItems(subCategories[mainCategoryIndex], (dialog, which) -> {
            // 선택한 서브 카테고리의 인덱스 저장
            selectedSubCategory = which;
            // 선택한 카테고리 텍스트뷰에 표시
            String selectedCategory = subCategories[selectedMainCategory][selectedSubCategory];
            classificationChoice.setText(selectedCategory);
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("address")) {
                String address = data.getStringExtra("address");
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                placeInput.setText(address);
            }
        }
    }

    private double latitude; // Define latitude as a class field
    private double longitude; // Define longitude as a class field

    private void registerPost() {
        try {
            // Check if image URIs are null
            if (imageUri1 == null || imageUri2 == null) {
                Toast.makeText(this, "이미지를 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Extract values from UI elements
            String category = classificationChoice.getText().toString();
            String item_name = ((EditText) findViewById(R.id.product_choice)).getText().toString();
            int headcnt = Integer.parseInt(((EditText) findViewById(R.id.people_input)).getText().toString());
            int remain_headcnt = headcnt; // Set remain_headcnt to headcnt
            int total_price = Integer.parseInt(((EditText) findViewById(R.id.cost_input)).getText().toString());
            String meeting_location = placeInput.getText().toString();
            String meeting_time = timeInputTextView.getText().toString();
            boolean is_up = ((CheckBox) findViewById(R.id.pointuse_checkbox)).isChecked();
            boolean is_reusable = ((CheckBox) findViewById(R.id.container_checkbox)).isChecked();
            String scale = ((Spinner) findViewById(R.id.unit_spinner)).getSelectedItem().toString();

            // Create a new Board object
            Board newPost = new Board(imageUri1, imageUri2, category, item_name, headcnt, remain_headcnt, total_price, meeting_location, meeting_time, is_up, is_reusable, scale, latitude, longitude);

            // Send the post to the server
            ApiHelper.sendBoardToServer(this, newPost);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "양, 인원 및 비용에 올바른 숫자를 입력하세요.", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "포스트 등록 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }


}
