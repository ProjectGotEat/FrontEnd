package com.example.addpost;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Calendar calendar;
    private TextView timeInputTextView;
    private ImageView uploadIcon;
    private ShapeableImageView image;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addpost), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        timeInputTextView = findViewById(R.id.time_input);
        calendar = Calendar.getInstance();

        uploadIcon = findViewById(R.id.uploadicon);
        image = findViewById(R.id.image);

        uploadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        Button postButton = findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPost(v); // 포스트 등록 메소드 호출
            }
        });
    }


    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                image.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showCategoryDialog(View view) {
        final String[] categories = {"카테고리 1", "카테고리 2", "카테고리 3"}; // 카테고리 목록을 정의합니다.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("카테고리 선택");
        builder.setItems(categories, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedCategory = categories[which];
                // 선택된 카테고리에 대한 작업을 수행합니다.
                // 예: 선택된 카테고리를 TextView에 표시합니다.
                ((TextView) view).setText(selectedCategory); // 선택된 카테고리를 TextView에 표시합니다.
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDateTimePicker(View view) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                        updateTimeInputTextView();
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void updateTimeInputTextView() {
        String formattedDateTime = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", calendar).toString();
        timeInputTextView.setText(formattedDateTime);
    }

    public void registerPost(View view) {
        // 사용자가 입력한 값을 가져오기
        String imageUrl = ""; // 이미지 URL을 여기에 할당
        String category = ((TextView) findViewById(R.id.classification_choice)).getText().toString();
        String productName = ((EditText) findViewById(R.id.product_choice)).getText().toString();
        int totalAmount = Integer.parseInt(((EditText) findViewById(R.id.amount_input)).getText().toString());
        int numberOfPeople = Integer.parseInt(((EditText) findViewById(R.id.people_input)).getText().toString());
        int costPerPerson = Integer.parseInt(((EditText) findViewById(R.id.cost_input)).getText().toString());
        String meetingPlace = ((TextView) findViewById(R.id.place_input)).getText().toString();
        String meetingTime = ((TextView) findViewById(R.id.time_input)).getText().toString();
        boolean isFeatured = ((CheckBox) findViewById(R.id.pointuse_checkbox)).isChecked();
        boolean isContainer = ((CheckBox) findViewById(R.id.container_checkbox)).isChecked();

        // 입력 값을 사용하여 새로운 Post 객체 생성
        Post newPost = new Post(imageUrl, category, productName, totalAmount, numberOfPeople, costPerPerson, meetingPlace, meetingTime, isFeatured, isContainer);
        Log.d("Post Data", "Image URL: " + newPost.getImageUrl());
        Log.d("Post Data", "Category: " + newPost.getCategory());
        Log.d("Post Data", "Product Name: " + newPost.getProductName());
        Log.d("Post Data", "Total Amount: " + newPost.getTotalAmount());
        Log.d("Post Data", "Number of People: " + newPost.getNumberOfPeople());
        Log.d("Post Data", "Cost Per Person: " + newPost.getCostPerPerson());
        Log.d("Post Data", "Meeting Place: " + newPost.getMeetingPlace());
        Log.d("Post Data", "Meeting Time: " + newPost.getMeetingTime());
        Log.d("Post Data", "Is Featured: " + newPost.isFeatured());
        Log.d("Post Data", "Is Container: " + newPost.isContainer());
    }

}
