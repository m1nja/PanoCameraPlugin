package com.slicejobs.panacamera.cameralibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {
    private CustomScrollView.OnScrollChangeListener mOnScrollChangeListener;

    public void setOnScrollChangeListener(CustomScrollView.OnScrollChangeListener onScrollChangeListener) {
        this.mOnScrollChangeListener = onScrollChangeListener;
    }

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.mOnScrollChangeListener != null) {
            this.mOnScrollChangeListener.onScrollChanged(this, l, t, oldl, oldt);
        }

    }

    public interface OnScrollChangeListener {
        void onScrollChanged(CustomScrollView var1, int var2, int var3, int var4, int var5);
    }
}