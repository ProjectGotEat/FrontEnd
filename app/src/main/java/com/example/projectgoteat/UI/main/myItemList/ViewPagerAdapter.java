package com.example.projectgoteat.UI.main.myItemList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectgoteat.model.Item;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private List<List<Item>> itemLists;
    private int uid;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<List<Item>> itemLists, int uid) {
        super(fragmentActivity);
        this.itemLists = itemLists;
        this.uid = uid;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        boolean isCompletedItems = (position == 2);
        return ItemFragment.newInstance(itemLists.get(position), isCompletedItems, uid);
    }

    @Override
    public int getItemCount() {
        return itemLists.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean containsItem(long itemId) {
        return itemId < getItemCount();
    }
}
