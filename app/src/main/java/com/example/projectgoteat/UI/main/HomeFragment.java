package com.example.projectgoteat.UI.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectgoteat.R;
import com.example.projectgoteat.UI.auth.LoginActivity;
import com.example.projectgoteat.UI.main.board.BoardAdapter;
import com.example.projectgoteat.UI.main.board.CartActivity;
import com.example.projectgoteat.UI.main.board.addPost.AddpostActivity;
import com.example.projectgoteat.model.BoardItem;
import com.example.projectgoteat.network.RetrofitService;
import com.example.projectgoteat.network.RetrofitHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private Retrofit retrofit;
    private RetrofitService retrofitService;
    private ImageView btnSearch;
    private ImageView btnWrite;
    private EditText editKeyword;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.listview_main, container, false);

        checkLoginStatus();

        retrofit = RetrofitHelper.getRetrofitInstance(getContext());
        retrofitService = retrofit.create(RetrofitService.class);

        btnSearch = view.findViewById(R.id.btnSearch);
        btnWrite = view.findViewById(R.id.btnWrite);

        //Spinner객체 생성
        final Spinner spinner_field = view.findViewById(R.id.category);

        //1번에서 생성한 categories.xml의 item을 String 배열로 가져오기
        String[] str = getResources().getStringArray(R.array.categoriesArray);

        //2번에서 생성한 spinner_item.xml과 str을 인자로 어댑터 생성.
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.categories_item, str);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner_field.setAdapter(adapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBoardSearch(spinner_field.getSelectedItem().toString());
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 게시물 작성 화면으로 이동하는 처리 구현
                Intent intent = new Intent(getContext(), AddpostActivity.class);
                startActivity(intent);
            }
        });

        // 엔터키로 submit
        editKeyword = view.findViewById(R.id.editKeyword);
        editKeyword.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                btnSearch.callOnClick();
                return true;
            }
            return false;
        });

        // 카테고리 변경시 자동 검색
        spinner_field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                btnSearch.callOnClick();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        getBoard();

        return view;
    }

    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void getBoard() { // 3.1 전체 소분 내역 조회
        // SharedPreferences에서 사용자 ID 가져오기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        int uid = sharedPreferences.getInt("uid", -1);

        if (uid != -1) {
            // 백엔드로 넘겨 줄 파라미터
            String userId = String.valueOf(uid);

            // API를 호출할 객체 생성
            Call<List<HashMap<String, Object>>> call = retrofitService.getBoard(userId);

            // API 호출
            call.enqueue(new Callback<List<HashMap<String, Object>>>() {
                @Override
                public void onResponse(Call<List<HashMap<String, Object>>> call, Response<List<HashMap<String, Object>>> response) {
                    if (response.isSuccessful()) { // 200(정상적인 응답)이 왔을 때는 response.isSuccessful() 가 true
                        List<HashMap<String, Object>> boardList = response.body();
                        printBoardData(boardList);
                    }
                }

                @Override
                public void onFailure(Call<List<HashMap<String, Object>>> call, Throwable t) {
                    Log.e(TAG, "Network Error: " + t.getMessage());
                }
            });
        } else {
            Log.e(TAG, "User ID not found in SharedPreferences");
        }
    }

    private void printBoardData(List<HashMap<String, Object>> boardList) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<BoardItem> itemList = new ArrayList<>();
        for (int i = 0; i < boardList.size(); i++) {
            HashMap<String, Object> boardMap = boardList.get(i);

            String strQuantity = String.valueOf(((Double) boardMap.get("quantity")).intValue());
            String strEachQuantity = String.valueOf(((Double) boardMap.get("each_quantity")).intValue());
            String strEachPrice = String.valueOf(((Double) boardMap.get("each_price")).intValue());
            String strPartHeadcnt = String.valueOf(((Double) boardMap.get("participants_headcnt")).intValue());
            String strHeadcnt = String.valueOf(((Double) boardMap.get("headcnt")).intValue());
            String strIsScraped = String.valueOf(((Double) boardMap.get("is_scraped")).intValue());
            boolean isFinished = "1".equals(String.valueOf(((Double) boardMap.get("is_finished")).intValue()));
            String bid = String.valueOf(boardMap.get("id"));

            String title = String.valueOf(boardMap.get("item_name")) + " | 총 " + strQuantity + String.valueOf(boardMap.get("scale"));
            String eachPrice = strEachQuantity + String.valueOf(boardMap.get("scale")) + " 당 " + strEachPrice + "원";
            String location = "장소 : " + String.valueOf(boardMap.get("meeting_location"));
            String participantsStatus = "인원 : " + strPartHeadcnt + "/" + strHeadcnt;
            String category = String.valueOf(boardMap.get("category"));
            boolean isBookmarked = "1".equals(strIsScraped);
            String imageUrl = String.valueOf(boardMap.get("item_image1"));
            itemList.add(new BoardItem(bid, imageUrl, title, eachPrice, location, participantsStatus, category, isBookmarked, isFinished, strHeadcnt, strPartHeadcnt));
        }

        BoardAdapter adapter = new BoardAdapter(getContext(), itemList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BoardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int boardId) {
                Intent intent = new Intent(getContext(), CartActivity.class);
                intent.putExtra("BOARD_ID", boardId);
                startActivity(intent);
            }
        });
    }

    private void getBoardSearch(String category) { // 3.2.1 일반 검색어 검색, 3.2.2 필터 적용하여 검색
        // 백엔드로 넘겨 줄 파라미터
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String uid = String.valueOf(sharedPreferences.getInt("uid", -1));
        editKeyword = view.findViewById(R.id.editKeyword);
        String keyword = String.valueOf(editKeyword.getText());
        switch (category) {
            case "전체":
                category = "";
                break;
            case "과일":
                category = "0101";
                break;
            case "채소":
                category = "0102";
                break;
            case "유제품":
                category = "0103";
                break;
            case "양념":
                category = "0104";
                break;
            case "정육 및 계란":
                category = "0105";
                break;
            case "곡물":
                category = "0106";
                break;
            case "냉동식품":
                category = "0201";
                break;
            case "베이커리":
                category = "0202";
                break;
            case "가공육":
                category = "0203";
                break;
            case "간식 및 음료":
                category = "0204";
                break;
            case "기타":
                category = "0301";
                break;
            default:
                break;
        }

        // API를 호출할 객체 생성
        Call<List<HashMap<String, Object>>> call = retrofitService.getBoardSearch(uid, keyword, category);

        // API 호출
        call.enqueue(new Callback<List<HashMap<String, Object>>>() {
            @Override
            public void onResponse(Call<List<HashMap<String, Object>>> call, Response<List<HashMap<String, Object>>> response) {
                if (response.isSuccessful()) { // 200(정상적인 응답)이 왔을 때는 response.isSuccessful() 가 true
                    List<HashMap<String, Object>> boardList = response.body();
                    printBoardData(boardList);
                }
            }

            @Override
            public void onFailure(Call<List<HashMap<String, Object>>> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
            }
        });
    }
}
