package com.example.projectgoteat.network;

import com.example.projectgoteat.model.BoardDetailResponse;

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

}
