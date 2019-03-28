package com.slicejobs.panacamera.cameralibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

public class RateTextCircularProgressBar extends FrameLayout implements CircularProgressBar.OnProgressChangeListener {
    private CircularProgressBar mCircularProgressBar;
    private TextView mRateText;

    public RateTextCircularProgressBar(Context context) {
        super(context);
        this.init();
    }

    public RateTextCircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        this.mCircularProgressBar = new CircularProgressBar(this.getContext());
        this.addView(this.mCircularProgressBar);
        LayoutParams lp = new LayoutParams(-1, -1);
        lp.gravity = 17;
        this.mCircularProgressBar.setLayoutParams(lp);
        this.mRateText = new TextView(this.getContext());
        this.addView(this.mRateText);
        this.mRateText.setLayoutParams(lp);
        this.mRateText.setGravity(17);
        this.mRateText.setTextColor(-16777216);
        this.mRateText.setTextSize(20.0F);
        this.mCircularProgressBar.setOnProgressChangeListener(this);
    }

    public void setMax(int max) {
        this.mCircularProgressBar.setMax(max);
    }

    public void setProgress(int progress) {
        this.mCircularProgressBar.setProgress(progress);
    }

    public CircularProgressBar getCircularProgressBar() {
        return this.mCircularProgressBar;
    }

    public void setTextSize(float size) {
        this.mRateText.setTextSize(size);
    }

    public void setTextColor(int color) {
        this.mRateText.setTextColor(color);
    }

    public void onChange(int duration, int progress, float rate) {
        this.mRateText.setText(String.valueOf((int)(rate * 100.0F) + "%"));
    }
}
