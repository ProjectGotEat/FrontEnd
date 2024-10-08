package com.example.projectgoteat.UI.main.board.addPost;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.projectgoteat.R;
import com.example.projectgoteat.UI._globalUtil.UIHelper;
import com.example.projectgoteat.UI._globalUtil.UnitSpinnerUtil;
import com.example.projectgoteat.UI.main.HomeFragment;
import com.example.projectgoteat.UI.main.MainActivity;
import com.example.projectgoteat.common.CommonCode;
import com.example.projectgoteat.model.Board;
import com.example.projectgoteat.network.RetrofitHelper;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class AddpostActivity extends AppCompatActivity {

    private Calendar calendar;
    private TextView timeInputTextView;
    private ImageView uploadIcon1, uploadIcon2;
    private ShapeableImageView image1, image2;
    private TextView placeInput;
    private Uri imageUri1, imageUri2;
    private TextView classificationChoice;

    private String[] mainCategories = {"신선식품", "가공식품", "기타"};
    private String[][] subCategories = {
            {"과일", "채소", "유제품", "양념", "정육 및 계란", "곡물"},
            {"냉동식품", "베이커리", "가공육", "간식 및 음료"}
    };

    private int selectedMainCategory = -1;
    private int selectedSubCategory = -1;

    private boolean postRegistered = false; // 변수 추가

    private String sendMeetingDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);

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

        // 갤러리 열기 버튼 클릭 이벤트 처리
        uploadIcon1.setOnClickListener(v -> UIHelper.openGallery(AddpostActivity.this, CommonCode.GALLERY_REQUEST_CODE_1));
        uploadIcon2.setOnClickListener(v -> UIHelper.openGallery(AddpostActivity.this, CommonCode.GALLERY_REQUEST_CODE_2));

        // 날짜 및 시간 선택 다이얼로그 표시
        timeInputTextView.setOnClickListener(v -> showDateTimePicker());

        // 카테고리 선택 텍스트뷰 클릭 이벤트 처리
        classificationChoice.setOnClickListener(v -> showCategoryDialog());

        // 장소 선택 텍스트뷰 클릭 이벤트
        placeInputTextView.setOnClickListener(v -> {
            Intent placePickerIntent = new Intent(AddpostActivity.this, PlacePickerActivity.class);
            startActivityForResult(placePickerIntent, CommonCode.PLACE_PICKER_REQUEST_CODE); // 다음 화면으로 이동
        });

        // 게시 버튼 클릭 이벤트 처리
        Button postButton = findViewById(R.id.post_button);
        postButton.setOnClickListener(v -> {
            registerPost(); // registerPost 메서드 호출
            if (postRegistered) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            // 현재 액티비티 종료
        });

        // 이전 페이지 이동
        ImageView closeButton = findViewById(R.id.arrow);
        closeButton.setOnClickListener(v -> onBackPressed()); // 이전 페이지로 이동

        // 단위 선택
        UnitSpinnerUtil.setupUnitSpinner(this);
    }

    private void showDateTimePicker() {
        UIHelper.showDateTimePicker(this, calendar, (view, year, month, dayOfMonth) -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(AddpostActivity.this, (view12, hourOfDay, minute1) -> {
                calendar.set(year, month, dayOfMonth, hourOfDay, minute1, 0); // 초를 00으로 설정
                updateTimeInputTextView(); // 시간 선택 후 텍스트 업데이트
            }, hour, minute, false);
            timePickerDialog.show();
        }, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0); // 초를 00으로 설정
            updateTimeInputTextView(); // 시간 선택 후 텍스트 업데이트
        });
    }

    private void updateTimeInputTextView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // UTC 시간대 설정
        sendMeetingDate = dateFormat.format(calendar.getTime());
        // sendMeetingDate = android.text.format.DateFormat.format("yyyy-MM-dd'T'HH:mm:ss", calendar).toString(); // 서버 전송용 약속 시간
        String formattedDateTime = android.text.format.DateFormat.format("yyyy년 MM월 dd일 HH시 mm분", calendar).toString(); // 화면 표시용 약속 시간
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

    private void displaySelectedImage(Uri imageUri, ImageView imageView) {
        imageView.setImageURI(imageUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ActivityResult", "requestCode: " + requestCode + ", resultCode: " + resultCode);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CommonCode.GALLERY_REQUEST_CODE_1) {
                imageUri1 = data.getData();
                displaySelectedImage(imageUri1, image1);
                Log.d("ActivityResult", "Image 1 selected: " + imageUri1.toString());
            } else if (requestCode == CommonCode.GALLERY_REQUEST_CODE_2) {
                imageUri2 = data.getData();
                displaySelectedImage(imageUri2, image2);
                Log.d("ActivityResult", "Image 2 selected: " + imageUri2.toString());
            } else if (requestCode == CommonCode.PLACE_PICKER_REQUEST_CODE && data.hasExtra("address")) {
                String address = data.getStringExtra("address");
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                placeInput.setText(address);
                this.latitude = latitude;
                this.longitude = longitude;
                Log.d("ActivityResult", "Place selected: " + address);
            }
        }
    }

    private double latitude; // Define latitude as a class field
    private double longitude; // Define longitude as a class field

    private void registerPost() {
        // Check if post is already registered
        if (postRegistered) {
            Toast.makeText(this, "이미 포스트를 등록했습니다.", Toast.LENGTH_SHORT).show();
            return; // Do nothing if post is already registered
        }

        // Request necessary permissions
        requestPermissionsIfNeeded();
    }

    // Request necessary permissions method
    private void requestPermissionsIfNeeded() {
        Log.d("Permissions", "Requesting permissions if needed.");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            Log.d("Permissions", "Permissions not granted. Requesting...");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO}, CommonCode.READ_MEDIA_PERMISSION_REQUEST_CODE);
        } else {
            Log.d("Permissions", "Permissions already granted.");
            sendDataToServer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("Permissions", "onRequestPermissionsResult: requestCode=" + requestCode);
        if (requestCode == CommonCode.READ_MEDIA_PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                Log.d("Permissions", "All permissions granted.");
                sendDataToServer();
            } else {
                Log.d("Permissions", "Permissions denied.");
                Toast.makeText(this, "미디어 읽기 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                showPermissionDeniedDialog(); // Show dialog if permissions denied
            }
        }
    }

    private void sendDataToServer() {
        Log.d("Data", "Sending data to server...");
        try {
            if (imageUri1 == null || imageUri2 == null) {
                Toast.makeText(this, "이미지를 선택하세요.", Toast.LENGTH_SHORT).show();
                Log.e("Data", "Images not selected.");
                return;
            }

            // Extract data from UI components
            String categoryId = classificationChoice.getText().toString();
            String itemName = ((EditText) findViewById(R.id.product_choice)).getText().toString();
            int headcnt = Integer.parseInt(((EditText) findViewById(R.id.people_input)).getText().toString());
            int remainHeadcnt = headcnt;
            int totalPrice = Integer.parseInt(((EditText) findViewById(R.id.cost_input)).getText().toString());
            int quantity = Integer.parseInt(((EditText) findViewById(R.id.amount_input)).getText().toString());
            String meetingLocation = placeInput.getText().toString();
            boolean isUp = ((CheckBox) findViewById(R.id.pointuse_checkbox)).isChecked();
            boolean isReusable = ((CheckBox) findViewById(R.id.container_checkbox)).isChecked();
            String scale = ((Spinner) findViewById(R.id.unit_spinner)).getSelectedItem().toString();

            // Convert image URIs to file objects
            File imageFile1 = RetrofitHelper.getFileFromUri(this, imageUri1);
            File imageFile2 = RetrofitHelper.getFileFromUri(this, imageUri2);

            // Create and set data to Board object
            Board board = new Board();
            board.setCategory(categoryId);
            board.setItem_name(itemName);
            board.setHeadcnt(headcnt);
            board.setRemain_headcnt(remainHeadcnt);
            board.setTotal_price(totalPrice);
            board.setQuantity(quantity);
            board.setMeeting_location(meetingLocation);
            board.setMeeting_time(sendMeetingDate);
            board.setIs_up(isUp);
            board.setIs_reusable(isReusable);
            board.setScale(scale);
            board.setLatitude(latitude);
            board.setLongitude(longitude);

            int userId = getUserId(); // Get user ID

            // Call RetrofitHelper method to send data to server
            RetrofitHelper.sendBoardToServer(this, board, imageFile1, imageFile2, userId);

            // Set postRegistered to true after successful registration
            postRegistered = true;

        } catch (NumberFormatException e) {
            Toast.makeText(this, "양, 인원 및 비용에 올바른 숫자를 입력하세요.", Toast.LENGTH_SHORT).show();
            Log.e("Data", "NumberFormatException: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Data", "IllegalArgumentException: " + e.getMessage());
        } catch (Exception e) {
            Toast.makeText(this, "포스트 등록 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            Log.e("Data", "Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("uid", -1);
    }

    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Denied");
        builder.setMessage("This permission is required to perform this action.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Do nothing and dismiss dialog when user clicks OK
            dialog.dismiss();
        });
        builder.show();
    }
}
