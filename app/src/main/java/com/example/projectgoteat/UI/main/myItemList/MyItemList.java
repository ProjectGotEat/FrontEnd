package com.example.projectgoteat.UI.main.myItemList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectgoteat.R;
import com.example.projectgoteat.model.Item;
import com.example.projectgoteat.model.Report;
import com.example.projectgoteat.model.Review;
import com.example.projectgoteat.network.RetrofitHelper;
import com.example.projectgoteat.network.RetrofitService;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.graphics.drawable.Drawable;
import androidx.core.graphics.drawable.DrawableCompat;

public class MyItemList extends AppCompatActivity {
    private static final String TAG = "MyItemList";
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<List<Item>> itemLists;
    private RetrofitService retrofitService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myitemlist);

        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        uid = sharedPreferences.getInt("uid", -1);
        if (uid == -1) {
            Toast.makeText(this, "로그인 정보가 없습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");

            Drawable navigationIcon = toolbar.getNavigationIcon();
            if (navigationIcon != null) {
                DrawableCompat.setTint(navigationIcon, ContextCompat.getColor(this, R.color.purple_700));
                toolbar.setNavigationIcon(navigationIcon);
            }
        }

        createRequiredDirectory();
        initViewPager();
        initSwipeRefresh();

        Retrofit retrofit = RetrofitHelper.getRetrofitInstance(this);
        retrofitService = retrofit.create(RetrofitService.class);

        loadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createRequiredDirectory() {
        File directory = new File(getFilesDir(), "recent_tasks");
        if (!directory.exists() && directory.mkdirs()) {
            Log.d(TAG, "Directory created: " + directory.getAbsolutePath());
        } else {
            Log.e(TAG, "Failed to create directory: " + directory.getAbsolutePath());
        }
    }

    private void initViewPager() {
        viewPager = findViewById(R.id.viewPager);
        itemLists = new ArrayList<>();
        itemLists.add(new ArrayList<>());
        itemLists.add(new ArrayList<>());
        itemLists.add(new ArrayList<>());
        viewPagerAdapter = new ViewPagerAdapter(this, itemLists, uid);
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("나눔중인 소분");
                    break;
                case 1:
                    tab.setText("참여중인 소분");
                    break;
                case 2:
                    tab.setText("종료된 소분");
                    break;
            }
        }).attach();
    }

    private void initSwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadData);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        fetchItems(retrofitService.getOrganizingItems(String.valueOf(uid)), 0);
        fetchItems(retrofitService.getParticipatingItems(String.valueOf(uid)), 1);
        fetchItems(retrofitService.getCompletedItems(String.valueOf(uid)), 2);
    }

    // 수정된 fetchItems 메소드
    private void fetchItems(Call<List<HashMap<String, Object>>> call, int listIndex) {
        call.enqueue(new Callback<List<HashMap<String, Object>>>() {
            @Override
            public void onResponse(Call<List<HashMap<String, Object>>> call, Response<List<HashMap<String, Object>>> response) {
                if (response.isSuccessful()) {
                    List<Item> items = new ArrayList<>();
                    if (response.code() != 204 && response.body() != null) {
                        for (HashMap<String, Object> map : response.body()) {
                            items.add(convertMapToItem(map));
                        }
                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        itemLists.get(listIndex).clear();
                        itemLists.get(listIndex).addAll(items);
                        viewPagerAdapter.updateFragment(listIndex, items);
                        swipeRefreshLayout.setRefreshing(false);
                    });
                } else {
                    Log.e(TAG, "Response not successful: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Response body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<HashMap<String, Object>>> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private Item convertMapToItem(HashMap<String, Object> map) {
        String title = (String) map.get("title");
        String meetingTime = (String) map.get("meeting_time");
        String message = (String) map.get("message");
        int id = map.get("id") != null ? ((Number) map.get("id")).intValue() : 0;
        int revieweeId = map.get("reviewee_id") != null ? ((Number) map.get("reviewee_id")).intValue() : 0;
        int organizerId = map.get("organizer_id") != null ? ((Number) map.get("organizer_id")).intValue() : 0;
        int userId = map.get("user_id") != null ? ((Number) map.get("user_id")).intValue() : 0;
        return new Item(title, meetingTime, message, id, revieweeId, organizerId, userId);
    }

    public void showSuccessDialog(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("소분 성공")
                .setMessage("정말로 이 소분을 성공 처리하시겠습니까?")
                .setPositiveButton("확인", (dialog, id) -> {
                    Log.d(TAG, "Marking item as success with ID: " + item.getParticipantId());
                    markItemSuccess(item.getParticipantId());
                    dialog.dismiss();
                })
                .setNegativeButton("취소", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

    private void markItemSuccess(int participantId) {
        retrofitService.markItemSuccess(participantId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Item marked as success");
                    loadData();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to mark item as success: " + response.code());
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
            }
        });
    }

    public void showFailDialog(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("소분 실패")
                .setMessage("정말로 이 소분을 실패 처리하시겠습니까?")
                .setPositiveButton("확인", (dialog, id) -> {
                    Log.d(TAG, "Marking item as failed with ID: " + item.getParticipantId());
                    markItemFail(item.getParticipantId());
                    dialog.dismiss();
                })
                .setNegativeButton("취소", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

    private void markItemFail(int participantId) {
        retrofitService.markItemFail(participantId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Item marked as fail");
                    loadData();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to mark item as fail: " + response.code());
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
            }
        });
    }

    public void showReviewDialog(int participantId, int revieweeId) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_review, null);
        EditText reviewContentEditText = dialogView.findViewById(R.id.reviewContent);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton("리뷰 제출", (dialog, id) -> {
                    String reviewContent = reviewContentEditText.getText().toString();
                    int rating = (int) ratingBar.getRating();
                    if (rating == 0) {
                        Toast.makeText(this, "평점을 입력하세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.d(TAG, "Review Content: " + reviewContent);
                    Log.d(TAG, "Rating: " + rating);
                    submitReview(participantId, revieweeId, rating, reviewContent);
                    dialog.dismiss();
                })
                .setNegativeButton("취소", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

    private void submitReview(int participantId, int revieweeId, int rating, String content) {
        Review review = new Review(revieweeId, rating, content);
        Log.d(TAG, "Submitting review: " + review.toString());

        retrofitService.submitReview(participantId, review).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Review submitted successfully");
                    Toast.makeText(MyItemList.this, "리뷰가 성공적으로 제출되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to submit review: " + response.code());
                        Log.e(TAG, "Error body: " + errorBody);
                        Toast.makeText(MyItemList.this, "리뷰 제출에 실패했습니다: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(MyItemList.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showReportDialog(int participantId, int reporteeId) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_report, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton("신고 제출", (dialog, id) -> {
                    RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
                    EditText otherReasonEditText = dialogView.findViewById(R.id.otherReasonEditText);
                    int selectedCategoryId = getSelectedCategoryId(radioGroup);
                    String otherReason = otherReasonEditText.getText().toString();

                    String content = selectedCategoryId == 4 ? otherReason : null;
                    submitReport(participantId, reporteeId, selectedCategoryId, content);
                    dialog.dismiss();
                })
                .setNegativeButton("취소", (dialog, id) -> dialog.dismiss())
                .create()
                .show();

        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
        EditText otherReasonEditText = dialogView.findViewById(R.id.otherReasonEditText);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioOther) {
                otherReasonEditText.setVisibility(View.VISIBLE);
            } else {
                otherReasonEditText.setVisibility(View.GONE);
            }
        });
    }


    private int getSelectedCategoryId(RadioGroup radioGroup) {
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radioNoShow) {
            return 1;
        } else if (checkedId == R.id.radioBadCondition) {
            return 2;
        } else if (checkedId == R.id.radioBadAttitude) {
            return 3;
        } else if (checkedId == R.id.radioOther) {
            return 4;
        } else {
            return 0;
        }
    }

    private void submitReport(int participantId, int reporteeId, int categoryId, String content) {
        Report report = new Report(reporteeId, categoryId, content != null ? content : ""); // 빈 문자열로 기본값 설정
        Log.d(TAG, "Submitting report: " + report.toString());

        retrofitService.submitReport(participantId, uid, report).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Report submitted successfully");
                    Toast.makeText(MyItemList.this, "신고가 성공적으로 제출되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to submit report: " + response.code());
                        Log.e(TAG, "Error body: " + errorBody);
                        Toast.makeText(MyItemList.this, "신고 제출에 실패했습니다: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(MyItemList.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
