package com.slicejobs.panacamera.cameralibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.slicejobs.panacamera.cameralibrary.helper.FindHomography;

public class OverlapView extends View {
    private int tl_x;
    private int tl_y;
    private int br_x;
    private int br_y;
    private int valid;
    private PointF[] points;
    private final Paint bgPaint;
    private final Paint fgPaint = new Paint();
    private FindHomography mFindHomography = null;
    private float wfactor = 1.0F;
    private float hfactor = 1.0F;

    public void setmFindHomography(FindHomography findHomography) {
        this.mFindHomography = findHomography;
    }

    public OverlapView(Context context, AttributeSet set) {
        super(context, set);
        this.fgPaint.setAlpha(180);
        this.fgPaint.setColor(-65536);
        this.bgPaint = new Paint();
        this.bgPaint.setAlpha(180);
        this.bgPaint.setColor(-16711936);
        this.tl_x = -1;
        this.tl_y = -1;
        this.br_x = -1;
        this.br_y = -1;
    }

    public void setResults(int tl_x, int tl_y, int br_x, int br_y, int valid) {
        this.tl_x = tl_x;
        this.tl_y = tl_y;
        this.br_x = br_x;
        this.br_y = br_y;
        this.valid = valid;
        this.postInvalidate();
    }

    public void setResults(PointF[] points, int valid) {
        this.postInvalidate();
    }

    public void draw(float wfactor, float hfactor) {
        this.wfactor = wfactor;
        this.hfactor = hfactor;
        this.postInvalidate();
    }

    public void onDraw(Canvas canvas) {
        FindHomography var10000 = this.mFindHomography;
        float[] result = FindHomography.getoverlaprect();
        int size = result.length;
        int num_points = size / 2;
        this.points = new PointF[num_points];

        for(int i = 0; i < num_points; ++i) {
            this.points[i] = new PointF(result[i * 2 + 1] * this.wfactor, result[i * 2] * this.hfactor);
            Log.d("DEADBEEF", "get point" + this.points[i]);
        }

        this.valid = result[size - 1] > 0.0F ? 1 : 0;
        if (this.points != null && this.points.length > 0) {
            Path path = new Path();
            path.moveTo(this.points[0].x, this.points[0].y);

            for(int i = 1; i < num_points; ++i) {
                path.lineTo(this.points[i].x, this.points[i].y);
            }

            path.lineTo(this.points[0].x, this.points[0].y);
            if (this.valid > 0) {
                canvas.drawPath(path, this.bgPaint);
            } else {
                canvas.drawPath(path, this.fgPaint);
            }
        } else if (this.tl_x != -1) {
            if (this.valid > 0) {
                canvas.drawRect((float)this.tl_x, (float)this.tl_y, (float)this.br_x, (float)this.br_y, this.bgPaint);
            } else {
                canvas.drawRect((float)this.tl_x, (float)this.tl_y, (float)this.br_x, (float)this.br_y, this.fgPaint);
            }
        }

    }
}
