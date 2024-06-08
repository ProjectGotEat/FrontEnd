package com.example.projectgoteat.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import android.net.Uri;

import com.example.projectgoteat.model.Board;
import com.example.projectgoteat.model.BoardDetailResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MultipartBody;
import okhttp3.MediaType;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    private static final String BASE_URL = "http://goteat-project-goteat-fbd23032.koyeb.app/";
    private static Retrofit retrofit;
    private static RetrofitService apiService;
    private static Context context;

    // Retrofit 인스턴스 초기화
    public static Retrofit getRetrofitInstance(Context ctx) {
        context = ctx;
        if (retrofit == null) {
            // 로그 인터셉터 추가
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // uid 인터셉터 추가
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("uid", getUidFromPreferences())
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    private static String getUidFromPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        int uid = sharedPreferences.getInt("uid", -1);
        return String.valueOf(uid);
    }

    // API 서비스 초기화
    public static RetrofitService getApiService() {
        if (apiService == null) {
            apiService = getRetrofitInstance(context).create(RetrofitService.class);
        }
        return apiService;
    }

    // 서버에 게시물 전송
    public static void sendBoardToServer(Context context, Board board, File imageFile1, File imageFile2, int userId) {
        try {
            RetrofitService apiService = getApiService();

            Gson gson = new GsonBuilder().create();
            String boardJson = gson.toJson(board);

            RequestBody boardRequestBody = RequestBody.create(MediaType.parse("application/json"), boardJson);
            RequestBody requestFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile1);
            RequestBody requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile2);

            MultipartBody.Part body1 = MultipartBody.Part.createFormData("item_image1", imageFile1.getName(), requestFile1);
            MultipartBody.Part body2 = MultipartBody.Part.createFormData("receipt_image", imageFile2.getName(), requestFile2);

            Call<Void> call = apiService.sendBoardToServer(userId, body1, body2, boardRequestBody);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "포스트가 성공적으로 등록되었습니다!", Toast.LENGTH_SHORT).show();
                        Log.d("RetrofitHelper", "포스트 등록 성공");
                    } else {
                        try {
                            String errorMessage = response.errorBody().string();
                            Toast.makeText(context, "포스트 등록에 실패했습니다. " + errorMessage, Toast.LENGTH_SHORT).show();
                            Log.e("RetrofitHelper", "포스트 등록 실패: " + errorMessage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "포스트 등록 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    Log.e("RetrofitHelper", "포스트 등록 중 오류 발생", t);
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "파일 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            Log.e("RetrofitHelper", "파일 처리 중 오류 발생", e);
        }
    }

    public static File getFileFromUri(Context context, Uri uri) throws IOException {
        File tempFile = File.createTempFile("temp", null, context.getCacheDir());
        tempFile.deleteOnExit();

        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new IOException("URI에서 InputStream을 열 수 없습니다: " + uri);
        }

        FileOutputStream outputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[4 * 1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();

        return tempFile;
    }

    public static void getBoardDetail(Context context, int boardId, int userId, final ApiCallback<BoardDetailResponse> callback) {
        RetrofitService apiService = getApiService();

        Call<BoardDetailResponse> call = apiService.getBoardDetail(boardId, userId);
        call.enqueue(new Callback<BoardDetailResponse>() {
            @Override
            public void onResponse(Call<BoardDetailResponse> call, Response<BoardDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int organizerId = response.body().getOrganizerId();
                    response.body().setOrganizerId(organizerId);
                    callback.onSuccess(response.body());
                } else {
                    Toast.makeText(context, "게시판을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BoardDetailResponse> call, Throwable t) {
                Log.e("API_CALL", "API 호출 실패", t);
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void requestBoard(Context context, int boardId, int userId, int organizerId, final ApiCallback<Void> callback) {
        RetrofitService apiService = getApiService();

        Map<String, Integer> body = new HashMap<>();
        body.put("organizerId", organizerId);

        Call<Void> call = apiService.requestBoard(boardId, userId, body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                    Log.i("RequestBoard", "요청 성공");
                } else {
                    try {
                        String errorMessage = response.errorBody().string();
                        Log.e("RequestBoard", "요청 실패: " + errorMessage);
                        callback.onFailure("요청 실패: " + errorMessage);
                    } catch (IOException e) {
                        Log.e("RequestBoard", "오류 발생: " + e.getMessage());
                        callback.onFailure("오류 발생: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("RequestBoard", "요청 실패: " + t.getMessage());
                callback.onFailure("요청 실패: " + t.getMessage());
            }
        });
    }
}
