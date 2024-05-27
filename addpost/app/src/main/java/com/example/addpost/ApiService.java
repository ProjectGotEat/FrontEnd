package com.example.addpost;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("board/{id}")
    Call<Board> getBoardDetail(@Path("id") int id);

    @POST("board")
    Call<Void> sendBoardToServer(@Body Board board);

    @GET("board/{id}/request")
    Call<Void> requestBoard(@Path("id") int id);

    @PUT("board/{id}")
    Call<Void> updateBoard(@Path("id") int id, @Body Board board);

    @GET("board/{id}/scrap")
    Call<Void> scrapBoard(@Path("id") int id);
}
