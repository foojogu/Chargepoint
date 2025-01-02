package com.westyorks.chargepoint;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import android.util.SparseArray;
import androidx.annotation.NonNull;

public class DashboardPagerAdapter extends FragmentStateAdapter {
    private final SparseArray<Fragment> fragments = new SparseArray<>();

    public DashboardPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new ChargepointListFragment();
                break;
            case 1:
                fragment = new ChargepointMapFragment();
                break;
            default:
                fragment = new ChargepointListFragment();
                break;
        }
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public Fragment getFragment(int position) {
        return fragments.get(position);
    }
}