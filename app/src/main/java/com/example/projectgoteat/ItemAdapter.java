package com.example.projectgoteat;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> itemList;
    private MyItemList myItemList;
    private boolean isCompletedItems;
    private int uid;

    public ItemAdapter(List<Item> itemList, MyItemList myItemList, boolean isCompletedItems, int uid) {
        this.itemList = itemList;
        this.myItemList = myItemList;
        this.isCompletedItems = isCompletedItems;
        this.uid = uid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemName.setText(item.getTitle());
        holder.itemDate.setText(item.getMeetingTime());
        holder.itemRecentMessage.setText(item.getMessage());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(myItemList, ChatActivity.class);
            intent.putExtra("participantId", item.getParticipantId());
            intent.putExtra("receiverId", item.getUserId() != uid ? item.getUserId() : item.getOrganizerId());
            intent.putExtra("chatRoomTitle", item.getTitle());
            myItemList.startActivity(intent);
        });

        if (isCompletedItems) {
            holder.btnSuccess.setText("리뷰하기");
            holder.btnFail.setText("신고하기");
            holder.btnSuccess.setOnClickListener(v -> myItemList.showReviewDialog(item.getParticipantId(), item.getUserId() != uid ? item.getUserId() : item.getOrganizerId()));
            holder.btnFail.setOnClickListener(v -> myItemList.showReportDialog(item.getParticipantId()));
        } else {
            holder.btnSuccess.setOnClickListener(v -> myItemList.showSuccessDialog(item));
            holder.btnFail.setOnClickListener(v -> myItemList.showFailDialog(item));
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public TextView itemDate;
        public TextView itemRecentMessage;
        public Button btnSuccess;
        public Button btnFail;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemDate = itemView.findViewById(R.id.itemDate);
            itemRecentMessage = itemView.findViewById(R.id.itemRecentMessage);
            btnSuccess = itemView.findViewById(R.id.btnSuccess);
            btnFail = itemView.findViewById(R.id.btnFail);
        }
    }
}
