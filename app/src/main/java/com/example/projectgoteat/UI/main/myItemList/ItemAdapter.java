package com.example.projectgoteat.UI.main.myItemList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectgoteat.R;
import com.example.projectgoteat.UI.main.myItemList.chat.ChatActivity;
import com.example.projectgoteat.model.Item;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> itemList;
    private MyItemListFragment myItemListFragment;
    private boolean isCompletedItems;
    private int uid;
    private Context context;

    public ItemAdapter(List<Item> itemList, MyItemListFragment myItemListFragment, boolean isCompletedItems, int uid) {
        this.itemList = itemList;
        this.context = myItemListFragment.getContext();
        this.myItemListFragment = myItemListFragment;
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
        String meetingTime = item.getMeetingTime().substring(0, 4) + "년 "
                + item.getMeetingTime().substring(5, 7) + "월 "
                + item.getMeetingTime().substring(8, 10) + "일 "
                + item.getMeetingTime().substring(11, 13) + ":"
                + item.getMeetingTime().substring(14, 16);
        holder.itemDate.setText("약속 일시:  " + meetingTime);
        String message = "최근 대화:  ";
        if (item.getMessage() != null) {
            message += item.getMessage();
        }
        holder.itemRecentMessage.setText(message);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("item", item);  // 변경된 부분: Parcelable로 Item 객체를 전달
            context.startActivity(intent);
        });

        if (isCompletedItems) {
            if ("1".equals(item.getHasReport())) {
                holder.btnFail.setText("신고완료");
                holder.btnFail.setEnabled(false);
                holder.btnFail.setBackgroundResource(R.drawable.button_disabled);
            } else {
                holder.btnFail.setText("신고하기");
                holder.btnFail.setEnabled(true);
                holder.btnFail.setBackgroundResource(R.drawable.button_red);
            }
            if ("1".equals(item.getHasReview())) {
                holder.btnSuccess.setText("리뷰완료");
                holder.btnSuccess.setEnabled(false);
                holder.btnSuccess.setBackgroundResource(R.drawable.button_disabled);
            } else {
                holder.btnSuccess.setText("리뷰하기");
                holder.btnSuccess.setEnabled(true);
                holder.btnSuccess.setBackgroundResource(R.drawable.button);
            }
            holder.btnSuccess.setOnClickListener(v -> myItemListFragment.showReviewDialog(item.getParticipantId(), item.getUserId() != uid ? item.getUserId() : item.getOrganizerId()));
            holder.btnFail.setOnClickListener(v -> myItemListFragment.showReportDialog(item.getParticipantId(), item.getUserId() != uid ? item.getUserId() : item.getOrganizerId()));
        } else {
            holder.btnSuccess.setOnClickListener(v -> myItemListFragment.showSuccessDialog(item));
            holder.btnFail.setOnClickListener(v -> myItemListFragment.showFailDialog(item));
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
