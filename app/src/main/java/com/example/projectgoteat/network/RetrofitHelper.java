package com.example.projectgoteat.network;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.projectgoteat.model.Board;
import com.example.projectgoteat.model.BoardDetailResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    private static final String BASE_URL = "https://goteat-goteat-98eb531b.koyeb.app/";
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
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(RetrofitService.class);
    }

    public static void sendBoardToServer(Context context, Board board, File imageFile1, File imageFile2, int userId) {
        initApiService();

        try {
            // Convert board object to JSON string
            Gson gson = new GsonBuilder().create();
            String boardJson = gson.toJson(board);

            // Create RequestBody for board JSON data
            RequestBody boardRequestBody = RequestBody.create(MediaType.parse("application/json"), boardJson);

            // Create RequestBody for image files
            RequestBody requestFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile1);
            RequestBody requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile2);

            // Create MultipartBody.Part for image files
            MultipartBody.Part body1 = MultipartBody.Part.createFormData("item_image1", imageFile1.getName(), requestFile1);
            MultipartBody.Part body2 = MultipartBody.Part.createFormData("receipt_image", imageFile2.getName(), requestFile2);

            // Enqueue the call to send board data to server
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
        // 파일을 저장할 임시 파일 생성
        File tempFile = File.createTempFile("temp", null, context.getCacheDir());
        tempFile.deleteOnExit(); // 애플리케이션 종료 후 파일 자동 삭제

        // Uri에서 InputStream 가져오기
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new IOException("Cannot open input stream for URI: " + uri);
        }

        // InputStream에서 파일로 복사
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[4 * 1024]; // 4KB 버퍼 사용
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

