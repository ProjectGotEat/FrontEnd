package com.example.projectgoteat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private List<List<Item>> itemLists;
    private boolean isCompletedItems;
    private int uid;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<List<Item>> itemLists, int uid) {
        super(fragmentActivity);
        this.itemLists = itemLists;
        this.uid = uid;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        isCompletedItems = (position == 2);
        return ItemFragment.newInstance(itemLists.get(position), isCompletedItems, uid);
    }

    @Override
    public int getItemCount() {
        return itemLists.size();
    }

    // 추가: getItem 메서드
    public Fragment getItem(int position) {
        isCompletedItems = (position == 2);
        return ItemFragment.newInstance(itemLists.get(position), isCompletedItems, uid);
    }
}