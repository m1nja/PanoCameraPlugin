package com.slicejobs.panacamera.cameralibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Paint.Style;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.slicejobs.panacamera.cameralibrary.model.bean.InOutPutData;

public class RectImageView extends AppCompatImageView {
    private static final String TAG = "RectImageView";
    private float mAndroidPreviewImageHeight;
    private float mAndroidPreviewImageWidth;
    Paint mPaint;
    public InOutPutData.OutputData outputData;
    Object sync;
    private int mNumPoints;
    private PointF[] mPoints;
    private int mValidOverlap;
    private int mValidHint;

    public RectImageView(Context mContext) {
        this(mContext, (AttributeSet)null);
    }

    public RectImageView(Context mContext, AttributeSet attrs) {
        this(mContext, attrs, 0);
    }

    public RectImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.sync = new Object();
        this.mNumPoints = 0;
        this.mValidOverlap = 0;
        this.mValidHint = 0;
        this.mPaint = null;
        this.mPaint = new Paint();
        this.mPaint = new Paint();
        this.mPaint.setStyle(Style.FILL);
        this.mPaint.setStrokeWidth(15.0F);
        this.mPaint.setAntiAlias(true);
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        if (VERSION.SDK_INT >= 17) {
            display.getRealMetrics(dm);
        } else {
            display.getMetrics(dm);
        }

        this.mAndroidPreviewImageWidth = (float)Math.min(dm.widthPixels, dm.heightPixels);
        this.mAndroidPreviewImageHeight = (float)Math.max(dm.heightPixels, dm.heightPixels);
    }

    public void updateRect() {
        Log.d("DEADBEEF", "updateRect() +++++");
        this.setVisibility(VISIBLE);
        this.invalidate();
        Log.d("DEADBEEF", "updateRect() -----");
    }

    public void setWidthAndHight(float width, float height) {
        float tmpWidth = Math.min(width, height);
        float tmpHeight = Math.max(width, height);
        this.mAndroidPreviewImageWidth = Math.max(tmpWidth, this.mAndroidPreviewImageWidth);
        this.mAndroidPreviewImageHeight = Math.max(tmpHeight, this.mAndroidPreviewImageHeight);
    }

    public void setPoints(int validHint, int validOverlap, int numPoints, float[] points) {
        Log.d("DEADBEEF", "setPoints() +++++");
        Object var5 = this.sync;
        synchronized(this.sync) {
            Log.d("DEADBEEF", "setPoints() overlap " + validOverlap + " hint " + validHint + " num " + numPoints + "points");
            this.mValidHint = validHint;
            this.mValidOverlap = validOverlap;
            this.mNumPoints = numPoints;
            if (this.mNumPoints > 0) {
                this.mPoints = new PointF[this.mNumPoints];

                for(int i = 0; i < this.mNumPoints; ++i) {
                    this.mPoints[i] = new PointF(points[i * 2], points[i * 2 + 1]);
                }
            } else {
                this.mPoints = null;
            }
        }

        this.updateRect();
        Log.d("DEADBEEF", "setPoints() -----");
    }

    public void onDraw(Canvas canvas) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        Object var9 = this.sync;
        PointF[] points;
        int valid_overlap;
        int valid_hint;
        int i;
        int num_points;
        synchronized(this.sync) {
            num_points = this.mNumPoints;
            if (this.mNumPoints <= 0) {
                points = null;
                valid_overlap = 0;
                valid_hint = 0;
            } else {
                points = new PointF[this.mNumPoints];

                for(i = 0; i < this.mNumPoints; ++i) {
                    points[i] = new PointF(this.mPoints[i].x * (float)canvasWidth, this.mPoints[i].y * (float)canvasHeight);
                }

                valid_overlap = this.mValidOverlap;
                valid_hint = this.mValidHint;
            }
        }

        if (valid_hint == 0) {
            this.setVisibility(GONE);
        } else {
            if (points == null) {
                points = new PointF[4];

                for(int k = 0; k < 4; ++k) {
                    points[k].x = (float)((k + 1) / 2 % 2 * canvasWidth);
                    points[k].y = (float)(k / 2 * canvasHeight);
                }
            }

            Path path = new Path();
            path.moveTo(points[0].x, points[0].y);

            for(i = 1; i < num_points; ++i) {
                path.lineTo(points[i].x, points[i].y);
            }

            path.lineTo(points[0].x, points[0].y);
            if (valid_overlap > 0) {
                this.mPaint.setColor(Color.argb(128, 237, 210, 1));
            } else {
                this.mPaint.setColor(Color.argb(128, 178, 178, 178));
            }

            canvas.drawPath(path, this.mPaint);
        }

    }
}
