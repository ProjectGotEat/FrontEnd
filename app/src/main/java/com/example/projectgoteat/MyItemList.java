package com.example.projectgoteat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

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

public class MyItemList extends AppCompatActivity {
    public static final int UID = 1;
    private static final String TAG = "MyItemList";
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<List<Item>> itemLists;
    private RetrofitService retrofitService;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createRequiredDirectory();
        initViewPager();
        initSwipeRefresh();  // 올바르게 작동하도록 수정된 initSwipeRefresh 메서드 호출

        Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
        retrofitService = retrofit.create(RetrofitService.class);

        loadData();
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
        viewPagerAdapter = new ViewPagerAdapter(this, itemLists);
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
        fetchItems(retrofitService.getOrganizingItems("1"), 0);
        fetchItems(retrofitService.getParticipatingItems("1"), 1);
        fetchItems(retrofitService.getCompletedItems("1"), 2);
    }

    private void fetchItems(Call<List<HashMap<String, Object>>> call, int listIndex) {
        call.enqueue(new Callback<List<HashMap<String, Object>>>() {
            @Override
            public void onResponse(Call<List<HashMap<String, Object>>> call, Response<List<HashMap<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Item> items = new ArrayList<>();
                    for (HashMap<String, Object> map : response.body()) {
                        items.add(convertMapToItem(map));
                    }
                    Log.d(TAG, "Fetched items: " + items.size() + " for listIndex: " + listIndex);
                    runOnUiThread(() -> {
                        itemLists.get(listIndex).clear();
                        itemLists.get(listIndex).addAll(items);
                        viewPagerAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);  // 새로고침 완료 후 스피너 숨기기
                    });
                } else {
                    Log.e(TAG, "Response not successful: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Response body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(false);  // 실패한 경우에도 스피너 숨기기
                }
            }

            @Override
            public void onFailure(Call<List<HashMap<String, Object>>> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
                swipeRefreshLayout.setRefreshing(false);  // 실패한 경우에도 스피너 숨기기
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
                    loadData(); // 데이터 새로고침
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
                    loadData(); // 데이터 새로고침
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
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to submit review: " + response.code());
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

    public void showReportDialog(int participantId) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_report, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton("신고 제출", (dialog, id) -> {
                    RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
                    EditText otherReasonEditText = dialogView.findViewById(R.id.otherReasonEditText);
                    int selectedCategoryId = getSelectedCategoryId(radioGroup);
                    String otherReason = otherReasonEditText.getText().toString();

                    String content = selectedCategoryId == 4 ? otherReason : null;
                    submitReport(participantId, selectedCategoryId, content);
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

    private void submitReport(int participantId, int categoryId, String content) {
        Report report = new Report(participantId, categoryId, content);
        Log.d(TAG, "Submitting report: " + report.toString());
        retrofitService.submitReport(participantId, UID, report).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Report submitted successfully");
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to submit report: " + response.code());
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
}
