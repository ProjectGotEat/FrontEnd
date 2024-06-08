package com.example.projectgoteat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private List<List<Item>> itemLists;
    private MyItemList myItemList;
    private int uid;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<List<Item>> itemLists, int uid) {
        super(fragmentActivity);
        this.itemLists = itemLists;
        this.uid = uid;
        if (fragmentActivity instanceof MyItemList) {
            this.myItemList = (MyItemList) fragmentActivity;
        } else {
            throw new IllegalArgumentException("fragmentActivity must be an instance of MyItemList");
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        boolean isCompletedItems = position == 2;
        return ItemFragment.newInstance(itemLists.get(position), myItemList, isCompletedItems, uid);
    }

    @Override
    public int getItemCount() {
        return itemLists.size();
    }
}
