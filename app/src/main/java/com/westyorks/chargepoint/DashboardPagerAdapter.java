package com.westyorks.chargepoint;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import android.util.SparseArray;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

public class DashboardPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 2;
    private final SparseArray<Fragment> fragments;
    private boolean hasEditPermission = false;

    public DashboardPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fragments = new SparseArray<>();
    }

    public void setHasEditPermission(boolean hasEditPermission) {
        this.hasEditPermission = hasEditPermission;
        // Clear cached fragments when permission changes
        fragments.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Check if fragment is already created
        Fragment existingFragment = fragments.get(position);
        if (existingFragment != null) {
            return existingFragment;
        }

        // Create new fragment
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = hasEditPermission ? new ChargepointListFragment() : new ReadOnlyChargepointListFragment();
                break;
            case 1:
                fragment = new ChargepointMapFragment();
                break;
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }

        // Cache the fragment
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean containsItem(long itemId) {
        return itemId >= 0 && itemId < NUM_PAGES;
    }

    public Fragment getFragment(int position) {
        return fragments.get(position);
    }
}