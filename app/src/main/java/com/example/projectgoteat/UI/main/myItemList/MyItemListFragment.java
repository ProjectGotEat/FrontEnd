package com.example.projectgoteat.UI.main.myItemList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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

public class MyItemListFragment extends Fragment {
    private static final String TAG = "MyItemList";
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<List<Item>> itemLists;
    private RetrofitService retrofitService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_myitemlist, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        uid = sharedPreferences.getInt("uid", -1);
        if (uid == -1) {
            Toast.makeText(getContext(), "로그인 정보가 없습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return view;
        }

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());

        Drawable navigationIcon = toolbar.getNavigationIcon();
        if (navigationIcon != null) {
            DrawableCompat.setTint(navigationIcon, ContextCompat.getColor(getContext(), R.color.purple_700));
            toolbar.setNavigationIcon(navigationIcon);
        }

        createRequiredDirectory();
        initViewPager(view);
        initSwipeRefresh(view);

        Retrofit retrofit = RetrofitHelper.getRetrofitInstance(getContext());
        retrofitService = retrofit.create(RetrofitService.class);

        loadData();

        return view;
    }

    private void createRequiredDirectory() {
        File directory = new File(getContext().getFilesDir(), "recent_tasks");
        if (!directory.exists() && directory.mkdirs()) {
            Log.d(TAG, "Directory created: " + directory.getAbsolutePath());
        } else {
            Log.e(TAG, "Failed to create directory: " + directory.getAbsolutePath());
        }
    }

    private void initViewPager(View view) {
        viewPager = view.findViewById(R.id.viewPager);
        itemLists = new ArrayList<>();
        itemLists.add(new ArrayList<>());
        itemLists.add(new ArrayList<>());
        itemLists.add(new ArrayList<>());
        viewPagerAdapter = new ViewPagerAdapter(getActivity(), itemLists, uid);
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
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

    private void initSwipeRefresh(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadData);
    }

    @Override
    public void onResume() {
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
                            items.add(convertMapToItem(map, listIndex));
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

    private Item convertMapToItem(HashMap<String, Object> map, int listIndex) {
        String title = (String) map.get("title");
        String meetingTime = (String) map.get("meeting_time");
        String message = (String) map.get("message");
        String hasReview = "";
        String hasReport = "";
        if (listIndex == 2) { // 종료된 소분인 경우에만 사용되는 정보(리뷰 작성 완료 여부, 신고 완료 여부)
            hasReview = String.valueOf(((Number) map.get("has_review")).intValue());
            hasReport = String.valueOf(((Number) map.get("has_report")).intValue());
        }
        int id = map.get("id") != null ? ((Number) map.get("id")).intValue() : 0;
        int revieweeId = map.get("reviewee_id") != null ? ((Number) map.get("reviewee_id")).intValue() : 0;
        int organizerId = map.get("organizer_id") != null ? ((Number) map.get("organizer_id")).intValue() : 0;
        int userId = map.get("user_id") != null ? ((Number) map.get("user_id")).intValue() : 0;
        return new Item(title, meetingTime, message, id, revieweeId, organizerId, userId, hasReview, hasReport);
    }

    public void showSuccessDialog(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("소분 성공")
                .setMessage("정말로 이 소분을 성공 처리하시겠습니까?")
                .setPositiveButton("확인", (dialog, id) -> {
                    Log.d(TAG, "Marking item as success with ID: " + item.getParticipantId());
                    markItemSuccess(item.getParticipantId());
                    dialog.dismiss();
                    loadData();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("소분 실패")
                .setMessage("정말로 이 소분을 실패 처리하시겠습니까?")
                .setPositiveButton("확인", (dialog, id) -> {
                    Log.d(TAG, "Marking item as failed with ID: " + item.getParticipantId());
                    markItemFail(item.getParticipantId());
                    dialog.dismiss();
                    loadData();
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
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_review, null);
        EditText reviewContentEditText = dialogView.findViewById(R.id.reviewContent);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        ratingBar.setRating(5F);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView)
                .setPositiveButton("리뷰 제출", (dialog, id) -> {
                    String reviewContent = reviewContentEditText.getText().toString();
                    int rating = (int) ratingBar.getRating();
                    if (rating == 0) {
                        Toast.makeText(getContext(), "평점을 입력하세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.d(TAG, "Review Content: " + reviewContent);
                    Log.d(TAG, "Rating: " + rating);
                    submitReview(participantId, revieweeId, rating, reviewContent);
                    dialog.dismiss();
                    loadData();
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
                    Toast.makeText(getContext(), "리뷰가 성공적으로 제출되었습니다.", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to submit review: " + response.code());
                        Log.e(TAG, "Error body: " + errorBody);
                        Toast.makeText(getContext(), "리뷰 제출에 실패했습니다: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showReportDialog(int participantId, int reporteeId) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_report, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        RadioButton radioNoShow = dialogView.findViewById(R.id.radioNoShow);
        radioNoShow.setChecked(true);
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
                    Toast.makeText(getContext(), "신고가 성공적으로 제출되었습니다.", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to submit report: " + response.code());
                        Log.e(TAG, "Error body: " + errorBody);
                        Toast.makeText(getContext(), "신고 제출에 실패했습니다: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
