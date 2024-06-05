package com.example.prac7.network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("board") // 3.1 전체 소분 내역 조회
    Call<List<HashMap<String, Object>>> getBoard(@Header("uid") String uid);

    @POST("scrap/add") //스크랩 내역 조회
    Call<HashMap<String, Object>> addScrap(@Header("uid") String uid, @Query("itemId") String itemId);

    @POST("scrap/remove")
    Call<HashMap<String, Object>> removeScrap(@Header("uid") String uid, @Query("itemId") String itemId);

    @GET("scrap/list")
    Call<List<HashMap<String, Object>>> getScrapList(@Header("uid") String uid);


    @GET("board/search") // 3.2.1 일반 검색어 검색, 3.2.2 필터 적용하여 검색
    Call<List<HashMap<String, Object>>> getBoardSearch(@Header("uid") String uid, @Query("keyword") String keyword, @Query("category") String category);

    @POST("auth/join") // 1.1 회원가입
    Call<Void> postAuthJoin(@Body Map<String, Object> requestBody);

    @POST("auth/log-in") // 1.2 로그인
    Call<HashMap<String, Object>> postAuthLogin(@Body Map<String, Object> requestBody);

    @GET("auth/join/exist") // 1.1.1 아이디 중복확인
    Call<HashMap<String, Object>> getAuthJoinExist(@Query("email") String email);

    @GET("user") // 1.3 마이페이지
    Call<HashMap<String, Object>> getUser(@Header("uid") String uid);

    @GET("user/point") // 1.3.1 포인트 조회
    Call<List<HashMap<String, Object>>> getUserPoint(@Header("uid") String uid);
    @GET("user/scrap") // 1.3.2 스크랩 조회
    Call<List<HashMap<String, Object>>> getScrap(@Header("uid") String uid);


}