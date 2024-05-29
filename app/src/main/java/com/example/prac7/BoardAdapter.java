package com.example.prac7;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {
    private Context context;
    private List<BoardItem> itemList;

    public BoardAdapter(Context context, List<BoardItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        BoardAdapter.ViewHolder viewHolder = new BoardAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BoardAdapter.ViewHolder holder, int position) {
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
        holder.textViewParticipants.setText(item.getParticipants());
        holder.btnBookmark.setChecked(item.isBookmarked());
        holder.btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2.5 게시물 스크랩 하기 API 호출. (백엔드 담당자: 하늘이)
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
