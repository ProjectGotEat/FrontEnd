package com.example.projectgotit;

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
import com.example.projectgotit.network.RetrofitHelper;
import com.example.projectgotit.network.RetrofitService;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

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
        holder.btnBookmark.setChecked(item.isBookmarked());

        holder.btnBookmark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setBookmarked(isChecked);

            SharedPreferences sharedPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
            String uid = String.valueOf(sharedPreferences.getInt("uid", -1));

            // API를 호출할 객체 생성
            Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
            RetrofitService retrofitService = retrofit.create(RetrofitService.class);
            Call<Void> call = retrofitService.postScrap(uid, Integer.parseInt(item.getBid().split("\\.")[0]));

            // API 호출
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) { // 200(정상적인 응답)이 왔을 때는 response.isSuccessful() 가 true
                        Toast.makeText(context, "스크랩 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("BoardAdapter", "Network Error: " + t.getMessage());
                }
            });
            /*if (isChecked) {
                addScrap(item);
            } else {
                removeScrap(item);
            }*/
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    String bidString = item.getBid();
                    int bid;
                    try {
                        // 소수점을 기준으로 문자열을 잘라서 정수로 변환
                        bid = Integer.parseInt(bidString.split("\\.")[0]);
                        listener.onItemClick(bid);
                    } catch (NumberFormatException e) {
                        Log.e("BoardAdapter", "NumberFormatException: " + e.getMessage());
                        // 적절한 오류 처리를 여기에 추가 (예: 사용자에게 오류 메시지를 표시)
                    }
                }
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

    public class ViewHolder extends RecyclerView.ViewHolder {
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
