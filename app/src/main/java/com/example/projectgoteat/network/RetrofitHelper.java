package com.example.projectgoteat.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.projectgoteat.model.BoardDetailResponse;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    private static Retrofit retrofit;
    private static RetrofitService apiService;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://goteat-goteat-98eb531b.koyeb.app/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static void initApiService() {
        if (apiService == null) {
            apiService = getRetrofit().create(RetrofitService.class);
        }
    }

    public static void sendBoardToServer(Context context, String categoryId, String itemName, int headcnt, int remainHeadcnt, int totalPrice, int quantity,
                                         String meetingLocation, String meetingTime, boolean isUp, boolean isReusable, String scale,
                                         String imageUri1, String imageUri2, double latitude, double longitude, int userId) {

        initApiService();

        if (imageUri1 != null && !imageUri1.isEmpty() && imageUri2 != null && !imageUri2.isEmpty()) {
            try {
                RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), imageUri1);
                MultipartBody.Part body1 = MultipartBody.Part.createFormData("image1", imageUri1);

                RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), imageUri2);
                MultipartBody.Part body2 = MultipartBody.Part.createFormData("image2", imageUri2);

                RequestBody categoryIdBody = RequestBody.create(MediaType.parse("text/plain"), categoryId);
                RequestBody itemNameBody = RequestBody.create(MediaType.parse("text/plain"), itemName);
                RequestBody headcntBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(headcnt));
                RequestBody remainHeadcntBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(remainHeadcnt));
                RequestBody totalPriceBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(totalPrice));
                RequestBody quantityBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(quantity));
                RequestBody meetingLocationBody = RequestBody.create(MediaType.parse("text/plain"), meetingLocation);
                RequestBody meetingTimeBody = RequestBody.create(MediaType.parse("text/plain"), meetingTime);
                RequestBody isUpBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(isUp));
                RequestBody isReusableBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(isReusable));
                RequestBody scaleBody = RequestBody.create(MediaType.parse("text/plain"), scale);
                RequestBody latitudeBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latitude));
                RequestBody longitudeBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(longitude));

                Call<Void> call = apiService.sendBoardToServer(
                        body1, body2, categoryIdBody, itemNameBody, headcntBody, remainHeadcntBody, totalPriceBody,
                        quantityBody, meetingLocationBody, meetingTimeBody, isUpBody, isReusableBody, scaleBody, latitudeBody, longitudeBody, userId
                );

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "포스트가 성공적으로 등록되었습니다!", Toast.LENGTH_SHORT).show();
                            Log.d("sendBoardToServer", "포스트 등록 성공");
                        } else {
                            Toast.makeText(context, "포스트 등록에 실패했습니다. " + response.message(), Toast.LENGTH_SHORT).show();
                            Log.e("sendBoardToServer", "포스트 등록 실패: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "포스트 등록 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        Log.e("sendBoardToServer", "포스트 등록 중 오류 발생", t);
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "파일 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("sendBoardToServer", "파일 처리 중 오류 발생", e);
            }
        } else {
            Toast.makeText(context, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show();
        }
    }



    public static void getBoardDetail(Context context, int boardId, int userId, final ApiCallback<BoardDetailResponse> callback) {
        initApiService();

        Call<BoardDetailResponse> call = apiService.getBoardDetail(boardId, userId);
        call.enqueue(new Callback<BoardDetailResponse>() {
            @Override
            public void onResponse(Call<BoardDetailResponse> call, Response<BoardDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    Toast.makeText(context, "게시판을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BoardDetailResponse> call, Throwable t) {
                // Log the error
                Log.e("API_CALL", "API call failed", t);
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void requestBoard(int boardId, final ApiCallback<Void> callback) {
        initApiService();

        Call<Void> call = apiService.requestBoard(boardId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                    Log.i("RequestBoard", "요청 성공"); // 성공 로그 추가
                } else {
                    String errorMessage = "요청 실패: " + response.message();
                    Log.e("RequestBoard", errorMessage); // 오류 로그 추가
                    callback.onFailure(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String errorMessage = "요청 실패: " + t.getMessage();
                Log.e("RequestBoard", errorMessage); // 오류 로그 추가
                callback.onFailure(errorMessage);
            }
        });
    }


    }

