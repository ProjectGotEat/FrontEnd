package com.example.addpost;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

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
                    .baseUrl("https://172.20.39.225:8080/") // API 서버 URL로 변경
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

        if (board.getImageUri() != null) {
            File file = new File(getRealPathFromURI(context, board.getImageUri()));
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            RequestBody category = RequestBody.create(MultipartBody.FORM, board.getCategory());
            RequestBody productName = RequestBody.create(MultipartBody.FORM, board.getProductName());
            RequestBody totalAmount = RequestBody.create(MultipartBody.FORM, String.valueOf(board.getTotalAmount()));
            RequestBody numberOfPeople = RequestBody.create(MultipartBody.FORM, String.valueOf(board.getNumberOfPeople()));
            RequestBody costPerPerson = RequestBody.create(MultipartBody.FORM, String.valueOf(board.getCostPerPerson()));
            RequestBody meetingPlace = RequestBody.create(MultipartBody.FORM, board.getMeetingPlace());
            RequestBody meetingTime = RequestBody.create(MultipartBody.FORM, board.getMeetingTime());
            RequestBody isFeatured = RequestBody.create(MultipartBody.FORM, String.valueOf(board.isFeatured()));
            RequestBody isContainer = RequestBody.create(MultipartBody.FORM, String.valueOf(board.isContainer()));
            RequestBody unit = RequestBody.create(MultipartBody.FORM, board.getUnit());

            Call<Void> call = apiService.sendBoardToServer(body, category, productName, totalAmount, numberOfPeople, costPerPerson, meetingPlace, meetingTime, isFeatured, isContainer, unit);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "포스트가 성공적으로 등록되었습니다!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "포스트 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "포스트 등록 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
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

    public static void getBoardDetail(Context context, int id, final TextView textView) {
        initApiService();

        Call<Board> call = apiService.getBoardDetail(id);
        call.enqueue(new Callback<Board>() {
            @Override
            public void onResponse(Call<Board> call, Response<Board> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Board board = response.body();

                } else {
                    Toast.makeText(context, "게시판 상세 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Board> call, Throwable t) {
                Toast.makeText(context, "게시판 상세 정보를 가져오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
