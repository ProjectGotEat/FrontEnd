package com.example.projectgoteat;

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
import java.util.List;

public class ItemFragment extends Fragment {
    private List<Item> itemList;
    private MyItemList myItemList;
    private boolean isCompletedItems;

    public static ItemFragment newInstance(List<Item> itemList, MyItemList myItemList, boolean isCompletedItems) {
        ItemFragment fragment = new ItemFragment();
        fragment.itemList = itemList;
        fragment.myItemList = myItemList;
        fragment.isCompletedItems = isCompletedItems;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemAdapter itemAdapter = new ItemAdapter(itemList, myItemList, isCompletedItems);
        recyclerView.setAdapter(itemAdapter);
        return view;
    }
}
