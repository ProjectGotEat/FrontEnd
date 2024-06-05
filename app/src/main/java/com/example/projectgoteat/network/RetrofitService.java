package com.example.projectgoteat.network;

import com.example.projectgoteat.Review;
import com.example.projectgoteat.Report;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Header;

public interface RetrofitService {

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

    @POST("participant/{id}")
    Call<String> sendMessage(@Path("id") int id, @Body HashMap<String, Object> message);

    @GET("participant/{id}")
    Call<HashMap<String, Object>> getMessageDetails(@Path("id") int id);

    @POST("participant/{id}/review")
    Call<Void> submitReview(@Path("id") int participantId, @Body Review review);

    @POST("participant/{id}/report")
    Call<Void> submitReport(@Path("id") int participantId, @Body Report report);
}
