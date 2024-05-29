package com.example.addpost;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    @GET("board/{id}")
    Call<Board> getBoardDetail(@Path("id") int id);

    @Multipart
    @POST("upload")
    Call<Void> sendBoardToServer(
            @Part MultipartBody.Part image,
            @Part("category") RequestBody category,
            @Part("productName") RequestBody productName,
            @Part("totalAmount") RequestBody totalAmount,
            @Part("numberOfPeople") RequestBody numberOfPeople,
            @Part("costPerPerson") RequestBody costPerPerson,
            @Part("meetingPlace") RequestBody meetingPlace,
            @Part("meetingTime") RequestBody meetingTime,
            @Part("isFeatured") RequestBody isFeatured,
            @Part("isContainer") RequestBody isContainer,
            @Part("unit") RequestBody unit
    );

    @GET("board/{id}/request")
    Call<Void> requestBoard(@Path("id") int id);

    @PUT("board/{id}")
    Call<Void> updateBoard(@Path("id") int id, @Body Board board);

    @GET("board/{id}/scrap")
    Call<Void> scrapBoard(@Path("id") int id);
}
