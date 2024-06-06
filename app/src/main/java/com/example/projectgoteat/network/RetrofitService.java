package com.example.projectgoteat.network;

import com.example.projectgoteat.Review;
import com.example.projectgoteat.Report;
import com.example.projectgoteat.model.BoardDetailResponse;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

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

    @PUT("board/{id}/request")
    Call<Void> requestBoard(
            @Path("id") int boardId
    );

    @GET("participant/organize")
    Call<List<HashMap<String, Object>>> getOrganizingItems(@Header("uid") String uid);

    @GET("participant/participate")
    Call<List<HashMap<String, Object>>> getParticipatingItems(@Header("uid") String uid);

    @GET("participant/end")
    Call<List<HashMap<String, Object>>> getCompletedItems(@Header("uid") String uid);

    @PUT("participant/{id}/success")
    Call<Void> markItemSuccess(@Path("id") int id);

    @PUT("participant/{id}/fail")
    Call<Void> markItemFail(@Path("id") int id);

    @GET("participant/{id}")
    Call<HashMap<String, Object>> getMessageDetails(@Path("id") int id);

    @POST("participant/{id}")
    Call<String> sendMessage(@Path("id") int id, @Body HashMap<String, Object> message);

    @POST("participant/{id}/review")
    Call<Void> submitReview(@Path("id") int participantId, @Body Review review);

    @POST("participant/{id}/report")
    Call<Void> submitReport(@Path("id") int participantId, @Header("uid") int uid, @Body Report report);
}
