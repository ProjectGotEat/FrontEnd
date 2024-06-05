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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PointHistoryAdapter extends RecyclerView.Adapter<PointHistoryAdapter.ViewHolder> {
    private Context context;
    private List<PointHistoryItem> itemList;

    public PointHistoryAdapter(Context context, List<PointHistoryItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.point_history_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PointHistoryItem item = itemList.get(position);

        holder.textViewReason.setText(item.getChangeReason());
        holder.textViewCreated.setText(item.getCreatedAt());
        holder.textViewPoint.setText(item.getChangePoint());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewReason;
        private TextView textViewCreated;
        private TextView textViewPoint;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewReason = itemView.findViewById(R.id.textViewReason);
            textViewCreated = itemView.findViewById(R.id.textViewCreated);
            textViewPoint = itemView.findViewById(R.id.textViewPoint);
        }
        public TextView getTextViewReason() { return textViewReason; }
        public TextView getTextViewCreated() {
            return textViewCreated;
        }
        public TextView getTextViewPoint() {
            return textViewPoint;
        }
    }
}
