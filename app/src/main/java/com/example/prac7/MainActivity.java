package com.example.prac7;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prac7.network.RetrofitService;
import com.example.prac7.network.RetrofitHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
    RetrofitService retrofitService = retrofit.create(RetrofitService.class);
    private ImageView btnSearch;
    private ImageView btnWrite;
    private EditText editKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_main);

        btnSearch = findViewById(R.id.btnSearch);
        btnWrite = findViewById(R.id.btnWrite);

        //Spinner객체 생성
        final Spinner spinner_field = (Spinner) findViewById(R.id.category);

        //1번에서 생성한 categories.xml의 item을 String 배열로 가져오기
        String[] str = getResources().getStringArray(R.array.categoriesArray);

        //2번에서 생성한 spinner_item.xml과 str을 인자로 어댑터 생성.
        final ArrayAdapter<String> adapter= new ArrayAdapter<String>(MainActivity.this, R.layout.categories_item,str);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner_field.setAdapter(adapter);

        //spinner 이벤트 리스너. 카테고리 선택시
        spinner_field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinner_field.getSelectedItemPosition() > 0){
                    //선택된 항목
                    Toast toast = Toast.makeText(MainActivity.this, spinner_field.getSelectedItem().toString(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBoardSearch(spinner_field.getSelectedItem().toString());
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 게시물 작성 화면으로 이동하는 처리 구현
            }
        });

        getBoard();

    }

    private void getBoard() { // 3.1 전체 소분 내역 조회
        // 백엔드로 넘겨 줄 파라미터
        String uid = "10";

        // API를 호출할 객체 생성
        Call<List<HashMap<String, Object>>> call = retrofitService.getBoard(uid);

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

    private void printBoardData(List<HashMap<String, Object>> boardList) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

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

            String title = String.valueOf(boardMap.get("item_name")) + " | 총 " + strQuantity + String.valueOf(boardMap.get("scale"));
            String eachPrice = strEachQuantity + String.valueOf(boardMap.get("scale")) + " 당 " + strEachPrice + "원";
            String location = "장소 : " + String.valueOf(boardMap.get("meeting_location"));
            String participants = "인원 : " + strPartHeadcnt + "/" + strHeadcnt;
            String category = String.valueOf(boardMap.get("category"));
            boolean isBookmarked = "1".equals(strIsScraped);
            String imageUrl = String.valueOf(boardMap.get("item_image1"));
            itemList.add(new BoardItem(imageUrl, title, eachPrice, location, participants, category, isBookmarked, isFinished));
        }

        BoardAdapter adapter = new BoardAdapter(MainActivity.this, itemList);
        recyclerView.setAdapter(adapter);
    }

    private void getBoardSearch(String category) { // 3.2.1 일반 검색어 검색, 3.2.2 필터 적용하여 검색
        // 백엔드로 넘겨 줄 파라미터
        String uid = "10"; // TODO: 로그인한 사용자의 uid를 저장하도록 변경
        editKeyword = findViewById(R.id.editKeyword);
        String keyword = String.valueOf(editKeyword.getText());
        switch(category) {
            case "전체": category = ""; break;
            case "과일": category = "0101"; break;
            case "채소": category = "0102"; break;
            case "유제품": category = "0103"; break;
            case "양념": category = "0104"; break;
            case "정육 및 계란": category = "0105"; break;
            case "곡물": category = "0106"; break;
            case "냉동식품": category = "0201"; break;
            case "베이커리": category = "0202"; break;
            case "가공육": category = "0203"; break;
            case "간식 및 음료": category = "0204"; break;
            case "기타": category = "0301"; break;
            default: break;
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



    private void getUser() { // 1.3 마이페이지
        // 백엔드로 넘겨 줄 파라미터
        String uid = "";

        // API를 호출할 객체 생성
        Call<HashMap<String, Object>> call = retrofitService.getUser(uid);

        // API 호출
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful()) { // 200(정상적인 응답)이 왔을 때는 response.isSuccessful() 가 true
                    HashMap<String, Object> responseMap = response.body(); // 백엔드에서 넘겨준 결과를 받아옴
                    /** TODO: 화면에 출력하는 처리 구현 */
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
            }
        });
    }

    private void getUserPoint() { // 1.3.1 포인트 조회
        // 백엔드로 넘겨 줄 파라미터
        String uid = "";

        // API를 호출할 객체 생성
        Call<List<HashMap<String, Object>>> call = retrofitService.getUserPoint(uid);

        // API 호출
        call.enqueue(new Callback<List<HashMap<String, Object>>>() {
            @Override
            public void onResponse(Call<List<HashMap<String, Object>>> call, Response<List<HashMap<String, Object>>> response) {
                if (response.isSuccessful()) { // 200(정상적인 응답)이 왔을 때는 response.isSuccessful() 가 true
                    List<HashMap<String, Object>> responseMapList = response.body(); // 백엔드에서 넘겨준 결과를 받아옴
                    /** TODO: 화면에 출력하는 처리 구현 */
                }
            }

            @Override
            public void onFailure(Call<List<HashMap<String, Object>>> call, Throwable t) {
                Log.e(TAG, "Network Error: " + t.getMessage());
            }
        });
    }
}