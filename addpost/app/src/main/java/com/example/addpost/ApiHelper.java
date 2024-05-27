package com.example.addpost;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHelper {
    private static ApiService apiService;
    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://172.20.39.225:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


        public static void sendBoardToServer (Board board){
            if (apiService == null) {
                apiService = ApiHelper.getRetrofit().create(ApiService.class);
            }

            Call<Void> call = apiService.sendBoardToServer(board);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    // 서버로의 요청이 성공적으로 처리됐을 때 실행할 코드를 여기에 작성하세요.
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // 서버로의 요청이 실패했을 때 실행할 코드를 여기에 작성하세요.
                }
            });
        }
    }
