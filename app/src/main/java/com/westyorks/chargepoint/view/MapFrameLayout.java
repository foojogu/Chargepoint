package com.westyorks.chargepoint.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import androidx.viewpager2.widget.ViewPager2;

public class MapFrameLayout extends FrameLayout {
    private ViewPager2 viewPager;
    private boolean isMapReady = false;

    public MapFrameLayout(Context context) {
        super(context);
    }

    public MapFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setViewPager(ViewPager2 viewPager) {
        this.viewPager = viewPager;
    }

    public void setMapReady(boolean isReady) {
        this.isMapReady = isReady;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isMapReady && viewPager != null && ev.getAction() == MotionEvent.ACTION_DOWN) {
            // Disable ViewPager2 swiping when touching the map
            viewPager.setUserInputEnabled(false);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isMapReady && viewPager != null && ev.getAction() == MotionEvent.ACTION_UP) {
            // Re-enable ViewPager2 swiping when touch is released
            viewPager.setUserInputEnabled(true);
        }
        return super.onTouchEvent(ev);
    }
}
