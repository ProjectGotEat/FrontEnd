package com.example.projectgoteat.api;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.projectgoteat.model.Board;
import com.example.projectgoteat.model.BoardDetailResponse;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHelper {
    private static Retrofit retrofit;
    private static ApiService apiService;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://172.20.39.247:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static void initApiService() {
        if (apiService == null) {
            apiService = getRetrofit().create(ApiService.class);
        }
    }

    public static void sendBoardToServer(Context context, Board board) {
        initApiService();

        if (board.getItem_image1() != null && board.getReceipt_image() != null) {
            try {
                File file1 = new File(getRealPathFromURI(context, board.getItem_image1()));
                RequestBody requestFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
                MultipartBody.Part body1 = MultipartBody.Part.createFormData("image1", file1.getName(), requestFile1);

                File file2 = new File(getRealPathFromURI(context, board.getReceipt_image()));
                RequestBody requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), file2);
                MultipartBody.Part body2 = MultipartBody.Part.createFormData("image2", file2.getName(), requestFile2);

                RequestBody category_id = RequestBody.create(MultipartBody.FORM, board.getCategory_id());
                RequestBody item_name = RequestBody.create(MultipartBody.FORM, board.getItem_name());
                RequestBody headcnt = RequestBody.create(MultipartBody.FORM, String.valueOf(board.getHeadcnt()));
                RequestBody remain_headcnt = RequestBody.create(MultipartBody.FORM, String.valueOf(board.getRemain_headcnt()));
                RequestBody total_price = RequestBody.create(MultipartBody.FORM, String.valueOf(board.getTotal_price()));
                RequestBody meeting_location = RequestBody.create(MultipartBody.FORM, board.getMeeting_location());
                RequestBody meeting_time = RequestBody.create(MultipartBody.FORM, board.getMeeting_time());
                RequestBody is_up = RequestBody.create(MultipartBody.FORM, String.valueOf(board.isIs_up()));
                RequestBody is_reusable = RequestBody.create(MultipartBody.FORM, String.valueOf(board.isIs_reusable()));
                RequestBody scale = RequestBody.create(MultipartBody.FORM, board.getScale());

                // 위도와 경도를 추가합니다.
                RequestBody latitude = RequestBody.create(MultipartBody.FORM, String.valueOf(board.getLatitude()));
                RequestBody longitude = RequestBody.create(MultipartBody.FORM, String.valueOf(board.getLongitude()));

                Call<Void> call = apiService.sendBoardToServer(body1, body2, category_id, item_name, headcnt, remain_headcnt, total_price, meeting_location, meeting_time, is_up, is_reusable, scale, latitude, longitude);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // 서버 응답 본문을 특정 성공 지표에 대해 확인할 수 있습니다.
                            Toast.makeText(context, "포스트가 성공적으로 등록되었습니다!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "포스트 등록에 실패했습니다. " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "포스트 등록 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "파일 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private static String getRealPathFromURI(Context context, Uri uri) {
        String result;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static void getBoardDetail(Context context, int boardId, String userId, final ApiCallback<BoardDetailResponse> callback) {
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
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void sendRequestToServer(Context context, int boardId, String uid) {
        initApiService();

        // RequestBody에 필요한 데이터를 추가
        int organizerId = 123;
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(organizerId));

        // 서버로 POST 요청을 보냄
        Call<Void> call = apiService.sendRequest(uid, boardId, requestBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 서버로의 요청이 성공했을 때 처리할 코드
                    Toast.makeText(context, "신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 서버로의 요청이 실패했을 때 처리할 코드
                    Toast.makeText(context, "신청에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 통신 실패 시 처리할 코드
                Toast.makeText(context, "네트워크 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
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
                    callback.onSuccess(null);
                } else {
                    callback.onFailure("요청 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure("요청 실패: " + t.getMessage());
            }
        });
    }
    public static void scrapBoard(int boardId, String uid, final ApiCallback<Void> callback) {
        initApiService();

        Call<Void> call = apiService.scrapBoard(boardId, uid);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onFailure("게시물 스크랩에 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure("게시물 스크랩에 실패했습니다.");
            }
        });
    }
}

