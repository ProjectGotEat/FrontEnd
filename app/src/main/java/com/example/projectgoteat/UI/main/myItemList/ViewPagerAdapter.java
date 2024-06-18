package com.example.projectgoteat.UI.main.myItemList;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectgoteat.model.Item;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private List<List<Item>> itemLists;
    private int uid;
    private SparseArray<ItemFragment> registeredFragments = new SparseArray<>();

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<List<Item>> itemLists, int uid) {
        super(fragmentActivity);
        this.itemLists = itemLists;
        this.uid = uid;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        boolean isCompletedItems = (position == 2);
        ItemFragment fragment = ItemFragment.newInstance(itemLists.get(position), isCompletedItems, uid);
        registeredFragments.put(position, fragment);
        return fragment;
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

    public void updateFragment(int position, List<Item> items) {
        ItemFragment fragment = registeredFragments.get(position);
        if (fragment != null) {
            fragment.updateItems(items);
        }
    }
}
