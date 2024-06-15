package com.example.projectgoteat.UI.main.board;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.projectgoteat.R;
import com.example.projectgoteat.model.BoardItem;
import com.example.projectgoteat.network.RetrofitHelper;
import com.example.projectgoteat.network.RetrofitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {
    private Context context;
    private List<BoardItem> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int boardId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public BoardAdapter(Context context, List<BoardItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BoardItem item = itemList.get(position);
        Glide.with(context).load(item.getImageUrl()).into(holder.imageView);

        if (item.isFinished()) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrix darkenMatrix = new ColorMatrix();
            darkenMatrix.set(new float[]{
                    0.5f, 0, 0, 0, 0,
                    0, 0.5f, 0, 0, 0,
                    0, 0, 0.5f, 0, 0,
                    0, 0, 0, 1, 0
            });
            matrix.postConcat(darkenMatrix);
            holder.imageView.setColorFilter(new ColorMatrixColorFilter(matrix));
            holder.textViewIsFinished.setVisibility(View.VISIBLE);
            holder.textViewIsFinished.setText("종료");
        } else {
            holder.imageView.clearColorFilter();
            holder.textViewIsFinished.setVisibility(View.GONE);
        }

        holder.textViewTitle.setText(item.getTitle());
        holder.textViewCategory.setText(item.getCategory());
        holder.textViewEachPrice.setText(item.getEachPrice());
        holder.textViewMittingLocationText.setText(item.getLocation());
        holder.textViewParticipants.setText(item.getParticipants());
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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                String bidString = item.getBid();
                int bid;
                try {
                    bid = Integer.parseInt(bidString.split("\\.")[0]);
                    listener.onItemClick(bid);
                } catch (NumberFormatException e) {
                    Log.e("BoardAdapter", "NumberFormatException: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
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
    }
}
