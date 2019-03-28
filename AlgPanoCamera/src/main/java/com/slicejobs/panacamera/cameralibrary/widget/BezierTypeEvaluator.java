package com.slicejobs.panacamera.cameralibrary.widget;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

public class BezierTypeEvaluator implements TypeEvaluator<PointF> {
    private PointF mControllPoint;

    public BezierTypeEvaluator(PointF mControllPoint) {
        this.mControllPoint = mControllPoint;
    }

    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        PointF pointCur = new PointF();
        pointCur.x = (1.0F - fraction) * (1.0F - fraction) * startValue.x + 2.0F * fraction * (1.0F - fraction) * this.mControllPoint.x + fraction * fraction * endValue.x;
        pointCur.y = (1.0F - fraction) * (1.0F - fraction) * startValue.y + 2.0F * fraction * (1.0F - fraction) * this.mControllPoint.y + fraction * fraction * endValue.y;
        return pointCur;
    }
}
