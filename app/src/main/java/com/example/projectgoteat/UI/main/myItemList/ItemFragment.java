package com.example.projectgoteat.UI.main.myItemList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectgoteat.R;
import com.example.projectgoteat.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemFragment extends Fragment {
    private List<Item> itemList;
    private MyItemList myItemList;
    private boolean isCompletedItems;
    private int uid;
    private ItemAdapter itemAdapter;

    public static ItemFragment newInstance(List<Item> itemList, boolean isCompletedItems, int uid) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putSerializable("itemList", (java.io.Serializable) itemList);
        args.putBoolean("isCompletedItems", isCompletedItems);
        args.putInt("uid", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MyItemList) {
            myItemList = (MyItemList) context;
        } else {
            throw new RuntimeException(context.toString() + " must be an instance of MyItemList");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemList = (List<Item>) getArguments().getSerializable("itemList");
            isCompletedItems = getArguments().getBoolean("isCompletedItems");
            uid = getArguments().getInt("uid");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemAdapter = new ItemAdapter(itemList, myItemList, isCompletedItems, uid);
        recyclerView.setAdapter(itemAdapter);
        return view;
    }

    public void updateItems(List<Item> newItemList) {
        itemList.clear();
        itemList.addAll(newItemList);
        itemAdapter.notifyDataSetChanged();
    }
}
