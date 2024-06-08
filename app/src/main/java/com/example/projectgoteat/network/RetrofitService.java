package com.example.projectgoteat.network;

import com.example.projectgoteat.Review;
import com.example.projectgoteat.Report;
import com.example.projectgoteat.model.BoardDetailResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("board/{id}")
    Call<BoardDetailResponse> getBoardDetail(
            @Path("id") int id,
            @Header("uid") int userId
    );

    @Multipart
    @POST("board")
    Call<Void> sendBoardToServer(
            @Header("uid") int userId,
            @Part MultipartBody.Part item_image1,
            @Part MultipartBody.Part receipt_image,
            @Part("board") RequestBody boardJson
    );

    @HTTP(method = "POST", path = "board/{id}/request", hasBody = true)
    Call<Void> requestBoard(
            @Path("id") int boardId,
            @Header("uid") int userId,
            @Body Map<String, Integer> body
    );

    @GET("participant/organize")
    Call<List<HashMap<String, Object>>> getOrganizingItems(@Header("uid") String uid);

    @GET("participant/participate")
    Call<List<HashMap<String, Object>>> getParticipatingItems(@Header("uid") String uid);

    @GET("participant/end")
    Call<List<HashMap<String, Object>>> getCompletedItems(@Header("uid") String uid);

    @PUT("participant/{id}/success")
    Call<Void> markItemSuccess(@Path("id") int participantId);

    @PUT("participant/{id}/fail")
    Call<Void> markItemFail(@Path("id") int participantId);

    @GET("participant/{id}")
    Call<HashMap<String, Object>> getMessageDetails(@Path("id") int id);

    @POST("participant/{id}")
    Call<String> sendMessage(@Path("id") int id, @Body HashMap<String, Object> message);

    @POST("participant/{id}/review")
    Call<Void> submitReview(@Path("id") int participantId, @Body Review review);

    @POST("participant/{id}/report")
    Call<Void> submitReport(@Path("id") int participantId, @Header("uid") int uid, @Body Report report);

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

    @GET("user/review") // 1.3.3 리뷰 조회
    Call<List<HashMap<String, Object>>> getReview(@Header("uid") String uid);

    @POST("board/{id}/scrap")
    Call<Void> postScrap(@Header("uid") String uid, @Path("id") int bid);

    @GET("auth/user-id")
    Call<Integer> getUserId(@Header("Authorization") String token);

    @POST("auth/log-in")
    Call<HashMap<String, Object>> postAuthLogin(@Body HashMap<String, String> requestBody);
}