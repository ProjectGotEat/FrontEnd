package com.example.projectgotit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private Context context;
    private List<ReviewItem> itemList;

    public ReviewAdapter(Context context, List<ReviewItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewItem item = itemList.get(position);

        holder.tvItemName.setText(item.getItemName());
        holder.tvCreatedAt.setText(item.getCreatedAt());
        holder.tvRate.setText(item.getRate());
        holder.tvReviewer.setText(item.getReviewerNickname());
        holder.tvContent.setText(item.getContent());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName;
        private TextView tvCreatedAt;
        private TextView tvRate;
        private TextView tvReviewer;
        private TextView tvContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvRate = itemView.findViewById(R.id.tvRate);
            tvReviewer = itemView.findViewById(R.id.tvReviewer);
            tvContent = itemView.findViewById(R.id.tvContent);
        }

        public TextView getTvItemName() { return tvItemName; }
        public TextView getTvCreatedAt() { return tvCreatedAt; }
        public TextView getTvRate() { return tvRate; }
        public TextView getTvReviewer() { return tvReviewer; }
        public TextView getTvContent() { return tvContent; }
    }
}
