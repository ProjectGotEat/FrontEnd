package com.example.projectgoteat.UI.main.board;

import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projectgoteat.R;
import com.github.chrisbanes.photoview.PhotoView;

public class FullscreenImageActivity extends AppCompatActivity {

    private PhotoView fullscreenImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_image);

        fullscreenImageView = findViewById(R.id.fullscreenImageView);

        // Intent에서 이미지 주소를 받아오기
        String imageResId = getIntent().getStringExtra("imageResId");
        Glide.with(FullscreenImageActivity.this).load(imageResId).into(fullscreenImageView);
    }

}
