package com.example.projectgoteat.UI.main.myPage.scrap;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectgoteat.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectgoteat.model.BoardItem;
import com.example.projectgoteat.network.RetrofitHelper;
import com.example.projectgoteat.network.RetrofitService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScrapAdapter extends RecyclerView.Adapter<ScrapAdapter.ViewHolder> {
    private Context context;
    private List<BoardItem> itemList;

    public ScrapAdapter(Context context, List<BoardItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BoardItem item = itemList.get(position);
//        holder.imageView.setImageResource(item.getImageUrl());
        Glide.with(context).load(item.getImageUrl()).into(holder.imageView);

        if (item.isFinished()) { // 종료된 소분이면
            // 이미지 뷰를 흑백으로 만들기
//            holder.imageView.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray), android.graphics.PorterDuff.Mode.MULTIPLY);
            // 이미지를 어둡게 설정
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0); // 흑백으로 변환
            ColorMatrix darkenMatrix = new ColorMatrix();
            darkenMatrix.set(new float[]{
                    0.5f, 0, 0, 0, 0,
                    0, 0.5f, 0, 0, 0,
                    0, 0, 0.5f, 0, 0,
                    0, 0, 0, 1, 0
            });
            matrix.postConcat(darkenMatrix);
            holder.imageView.setColorFilter(new ColorMatrixColorFilter(matrix));

            // "종료" 텍스트 표시
            holder.textViewIsFinished.setVisibility(View.VISIBLE);
            holder.textViewIsFinished.setText("종료");
        } else {
            // 조건에 맞지 않는 경우 이미지 뷰를 다시 원래 색상으로 변경
            holder.imageView.clearColorFilter();

            // "종료" 텍스트 숨기기
            holder.textViewIsFinished.setVisibility(View.GONE);
        }

        holder.textViewTitle.setText(item.getTitle());
        holder.textViewCategory.setText(item.getCategory());
        holder.textViewEachPrice.setText(item.getEachPrice());
        holder.textViewMittingLocationText.setText(item.getLocation());
        holder.textViewParticipants.setText(item.getParticipantsStatus());
        holder.btnBookmark.setOnCheckedChangeListener(null); // 초기화 과정에서 리스너가 실행되는 것을 방지하기 위해 리스너를 null로 설정
        holder.btnBookmark.setChecked(item.isBookmarked());

        holder.btnBookmark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setBookmarked(isChecked);

            SharedPreferences sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
            int uid = sharedPreferences.getInt("uid", -1);
            if (uid == -1) {
                Toast.makeText(context, "로그인 정보가 없습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            Retrofit retrofit = RetrofitHelper.getRetrofitInstance(context);
            RetrofitService retrofitService = retrofit.create(RetrofitService.class);

            if (isChecked) { // 스크랩하는 경우
                Call<Void> postScrap = retrofitService.postScrap(String.valueOf(uid), Integer.parseInt(item.getBid().split("\\.")[0]));
                postScrap.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "스크랩 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("BoardAdapter", "Network Error: " + t.getMessage());
                    }
                });
            } else { // 스크랩 취소하는 경우
                Call<Void> deleteScrap = retrofitService.deleteScrap(String.valueOf(uid), Integer.parseInt(item.getBid().split("\\.")[0]));
                deleteScrap.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "스크랩이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("BoardAdapter", "Network Error: " + t.getMessage());
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void addScrap(BoardItem item) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("scrap", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> scraps = sharedPreferences.getStringSet("scrap_set", new HashSet<>());
        scraps.add(item.getTitle()); // unique identifier of item, assuming title is unique
        editor.putStringSet("scrap_set", scraps);
        editor.apply();
    }

    private void removeScrap(BoardItem item) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("scrap", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> scraps = sharedPreferences.getStringSet("scrap_set", new HashSet<>());
        scraps.remove(item.getTitle());
        editor.putStringSet("scrap_set", scraps);
        editor.apply();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textViewTitle;
        private TextView textViewEachPrice;
        private TextView textViewCategory;
        private TextView textViewParticipants;
        private TextView textViewMittingLocationText;
        private CheckBox btnBookmark;
        private TextView textViewIsFinished;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewEachPrice = itemView.findViewById(R.id.textViewEachPrice);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewParticipants = itemView.findViewById(R.id.textViewParticipants);
            textViewMittingLocationText = itemView.findViewById(R.id.textViewMittingLocationText);
            btnBookmark = itemView.findViewById(R.id.btnScrap);
            textViewIsFinished = itemView.findViewById(R.id.textViewIsFinished);
        }
        public ImageView getImageView() { return imageView; }
        public TextView getTextViewTitle() {
            return textViewTitle;
        }
        public TextView getTextViewEachPrice() {
            return textViewEachPrice;
        }
        public TextView getTextViewCategory() {
            return textViewCategory;
        }
        public TextView getTextViewParticipants() {
            return textViewParticipants;
        }
        public TextView getTextViewMittingLocationText() {
            return textViewMittingLocationText;
        }
        public CheckBox getBtnBookmark() {
            return btnBookmark;
        }
    }
}
