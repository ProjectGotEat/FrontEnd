package com.example.projectgoteat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private List<List<Item>> itemLists;
    private MyItemList myItemList;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<List<Item>> itemLists) {
        super(fragmentActivity);
        this.itemLists = itemLists;
        if (fragmentActivity instanceof MyItemList) {
            this.myItemList = (MyItemList) fragmentActivity;
        } else {
            throw new IllegalArgumentException("fragmentActivity must be an instance of MainActivity");
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ItemFragment.newInstance(itemLists.get(position), myItemList, position == 2);
    }

    @Override
    public int getItemCount() {
        return itemLists.size();
    }
}