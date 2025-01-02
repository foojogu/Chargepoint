package com.westyorks.chargepoint.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

public class MapTouchInterceptorLayout extends FrameLayout {
    private ViewPager2 viewPager;
    private View mapView;

    public MapTouchInterceptorLayout(@NonNull Context context) {
        super(context);
    }

    public MapTouchInterceptorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MapTouchInterceptorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        viewPager = findViewById(com.westyorks.chargepoint.R.id.viewPager);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mapView == null) {
            // Find the map view
            findMapView(this);
        }
        
        if (mapView != null && viewPager != null) {
            // Get the location of the touch event relative to the map view
            int[] location = new int[2];
            mapView.getLocationOnScreen(location);
            float x = ev.getRawX() - location[0];
            float y = ev.getRawY() - location[1];

            // If the touch is within the bounds of the map view, disable ViewPager2 scrolling
            if (x >= 0 && x < mapView.getWidth() && y >= 0 && y < mapView.getHeight()) {
                viewPager.setUserInputEnabled(false);
                return false;
            } else {
                viewPager.setUserInputEnabled(true);
            }
        }
        
        return super.onInterceptTouchEvent(ev);
    }

    private void findMapView(View view) {
        if (view.getClass().getName().contains("FragmentContainerView")) {
            // This is likely the container for the map fragment
            mapView = view;
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                findMapView(group.getChildAt(i));
            }
        }
    }
}
