package com.example.addpost;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Calendar calendar;
    private TextView timeInputTextView;
    private ImageView uploadIcon;
    private ShapeableImageView image;
    private TextView placeInput;
    private Uri imageUri;
    private TextView classificationChoice;

    private static final int GALLERY_REQUEST_CODE = 1001;
    private static final int PLACE_PICKER_REQUEST_CODE = 123;

    private String[] mainCategories = {"신선식품", "가공식품"};
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
        uploadIcon = findViewById(R.id.uploadicon);
        image = findViewById(R.id.image);
        placeInput = findViewById(R.id.place_input);
        calendar = Calendar.getInstance();
        classificationChoice = findViewById(R.id.classification_choice);
        TextView placeInputTextView = findViewById(R.id.place_input); // 장소 선택 텍스트뷰

        // 갤러리 열기 버튼 클릭 이벤트 처리
        uploadIcon.setOnClickListener(v -> UIHelper.openGallery(MainActivity.this, GALLERY_REQUEST_CODE));

        // 날짜 및 시간 선택 다이얼로그 표시
        timeInputTextView.setOnClickListener(v -> showDateTimePicker());

        // 카테고리 선택 텍스트뷰 클릭 이벤트 처리
        classificationChoice.setOnClickListener(v -> showCategoryDialog());

        // 장소 선택 텍스트뷰 클릭 이벤트
        placeInputTextView.setOnClickListener(v -> {
            Intent placePickerIntent = new Intent(MainActivity.this, PlacePickerActivity.class);
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
            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, (view12, hourOfDay, minute1) -> {
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
            String selectedCategory = mainCategories[selectedMainCategory] + " - " + subCategories[selectedMainCategory][selectedSubCategory];
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
                placeInput.setText(address);
            }
        }
    }

    private void registerPost() {
        try {
            // 이미지 URI 가져오기


            // 이미지 URI 가져오기
            if (imageUri == null) {
                Toast.makeText(this, "이미지를 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            String category = classificationChoice.getText().toString();
            String productName = ((EditText) findViewById(R.id.product_choice)).getText().toString();
            int totalAmount = Integer.parseInt(((EditText) findViewById(R.id.amount_input)).getText().toString());
            int numberOfPeople = Integer.parseInt(((EditText) findViewById(R.id.people_input)).getText().toString());
            int costPerPerson = Integer.parseInt(((EditText) findViewById(R.id.cost_input)).getText().toString());
            String meetingPlace = placeInput.getText().toString();
            String meetingTime = timeInputTextView.getText().toString();
            boolean isFeatured = ((CheckBox) findViewById(R.id.pointuse_checkbox)).isChecked();
            boolean isContainer = ((CheckBox) findViewById(R.id.container_checkbox)).isChecked();
            String unit = ((Spinner) findViewById(R.id.unit_spinner)).getSelectedItem().toString();

            Board newPost = new Board(imageUri, category, productName, totalAmount, numberOfPeople, costPerPerson, meetingPlace, meetingTime, isFeatured, isContainer, unit);

            // 서버에 포스트 전송
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
