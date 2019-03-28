package com.slicejobs.panacamera.cameralibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgressBar extends View {
    private int mDuration = 100;
    private int mProgress = 30;
    private Paint mPaint = new Paint();
    private RectF mRectF = new RectF();
    private int mBackgroundColor = -3355444;
    private int mPrimaryColor = Color.parseColor("#6DCAEC");
    private float mStrokeWidth = 10.0F;
    private CircularProgressBar.OnProgressChangeListener mOnChangeListener;

    public void setOnProgressChangeListener(CircularProgressBar.OnProgressChangeListener l) {
        this.mOnChangeListener = l;
    }

    public CircularProgressBar(Context context) {
        super(context);
    }

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMax(int max) {
        if (max < 0) {
            max = 0;
        }

        this.mDuration = max;
    }

    public int getMax() {
        return this.mDuration;
    }

    public void setProgress(int progress) {
        if (progress > this.mDuration) {
            progress = this.mDuration;
        }

        this.mProgress = progress;
        if (this.mOnChangeListener != null) {
            this.mOnChangeListener.onChange(this.mDuration, progress, this.getRateOfProgress());
        }

        this.invalidate();
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setBackgroundColor(int color) {
        this.mBackgroundColor = color;
    }

    public void setPrimaryColor(int color) {
        this.mPrimaryColor = color;
    }

    public void setCircleWidth(float width) {
        this.mStrokeWidth = width;
    }

    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int halfWidth = this.getWidth() / 2;
        int halfHeight = this.getHeight() / 2;
        int radius = halfWidth < halfHeight ? halfWidth : halfHeight;
        float halfStrokeWidth = this.mStrokeWidth / 2.0F;
        this.mPaint.setColor(this.mBackgroundColor);
        this.mPaint.setDither(true);
        this.mPaint.setFlags(1);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStrokeWidth(this.mStrokeWidth);
        this.mPaint.setStyle(Style.STROKE);
        canvas.drawCircle((float)halfWidth, (float)halfHeight, (float)radius - halfStrokeWidth, this.mPaint);
        this.mPaint.setColor(this.mPrimaryColor);
        this.mRectF.top = (float)(halfHeight - radius) + halfStrokeWidth;
        this.mRectF.bottom = (float)(halfHeight + radius) - halfStrokeWidth;
        this.mRectF.left = (float)(halfWidth - radius) + halfStrokeWidth;
        this.mRectF.right = (float)(halfWidth + radius) - halfStrokeWidth;
        canvas.drawArc(this.mRectF, -90.0F, this.getRateOfProgress() * 360.0F, false, this.mPaint);
        canvas.save();
    }

    private float getRateOfProgress() {
        return (float)this.mProgress / (float)this.mDuration;
    }

    public interface OnProgressChangeListener {
        void onChange(int var1, int var2, float var3);
    }
}
