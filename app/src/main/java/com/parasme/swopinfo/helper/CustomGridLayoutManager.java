package com.parasme.swopinfo.helper;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by SoNu on 6/3/2017.
 */

public class CustomGridLayoutManager extends StaggeredGridLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }


    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return false;
    }
}