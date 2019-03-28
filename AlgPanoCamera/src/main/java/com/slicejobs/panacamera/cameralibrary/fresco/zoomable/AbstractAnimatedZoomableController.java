package com.slicejobs.panacamera.cameralibrary.fresco.zoomable;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.slicejobs.panacamera.cameralibrary.fresco.gestures.TransformGestureDetector;

public abstract class AbstractAnimatedZoomableController extends DefaultZoomableController {
    private boolean mIsAnimating;
    private final float[] mStartValues = new float[9];
    private final float[] mStopValues = new float[9];
    private final float[] mCurrentValues = new float[9];
    private final Matrix mNewTransform = new Matrix();
    private final Matrix mWorkingTransform = new Matrix();

    public AbstractAnimatedZoomableController(TransformGestureDetector transformGestureDetector) {
        super(transformGestureDetector);
    }

    public void reset() {
        FLog.v(this.getLogTag(), "reset");
        this.stopAnimation();
        this.mWorkingTransform.reset();
        this.mNewTransform.reset();
        super.reset();
    }

    public boolean isIdentity() {
        return !this.isAnimating() && super.isIdentity();
    }

    public void zoomToPoint(float scale, PointF imagePoint, PointF viewPoint) {
        this.zoomToPoint(scale, imagePoint, viewPoint, 7, 0L, (Runnable)null);
    }

    public void zoomToPoint(float scale, PointF imagePoint, PointF viewPoint, int limitFlags, long durationMs, @Nullable Runnable onAnimationComplete) {
        FLog.v(this.getLogTag(), "zoomToPoint: duration %d ms", durationMs);
        this.calculateZoomToPointTransform(this.mNewTransform, scale, imagePoint, viewPoint, limitFlags);
        this.setTransform(this.mNewTransform, durationMs, onAnimationComplete);
    }

    public void setTransform(Matrix newTransform, long durationMs, @Nullable Runnable onAnimationComplete) {
        FLog.v(this.getLogTag(), "setTransform: duration %d ms", durationMs);
        if (durationMs <= 0L) {
            this.setTransformImmediate(newTransform);
        } else {
            this.setTransformAnimated(newTransform, durationMs, onAnimationComplete);
        }

    }

    private void setTransformImmediate(Matrix newTransform) {
        FLog.v(this.getLogTag(), "setTransformImmediate");
        this.stopAnimation();
        this.mWorkingTransform.set(newTransform);
        super.setTransform(newTransform);
        this.getDetector().restartGesture();
    }

    protected boolean isAnimating() {
        return this.mIsAnimating;
    }

    protected void setAnimating(boolean isAnimating) {
        this.mIsAnimating = isAnimating;
    }

    protected float[] getStartValues() {
        return this.mStartValues;
    }

    protected float[] getStopValues() {
        return this.mStopValues;
    }

    protected Matrix getWorkingTransform() {
        return this.mWorkingTransform;
    }

    public void onGestureBegin(TransformGestureDetector detector) {
        FLog.v(this.getLogTag(), "onGestureBegin");
        this.stopAnimation();
        super.onGestureBegin(detector);
    }

    public void onGestureUpdate(TransformGestureDetector detector) {
        FLog.v(this.getLogTag(), "onGestureUpdate %s", this.isAnimating() ? "(ignored)" : "");
        if (!this.isAnimating()) {
            super.onGestureUpdate(detector);
        }
    }

    protected void restoreImage(float fromX, float fromY) {
        PointF viewPoint = new PointF(fromX, fromY);
        this.zoomToPoint(this.getOriginScaleFactor(), this.mapViewToImage(viewPoint), viewPoint, 7, 300L, (Runnable)null);
    }

    protected void calculateInterpolation(Matrix outMatrix, float fraction) {
        for(int i = 0; i < 9; ++i) {
            this.mCurrentValues[i] = (1.0F - fraction) * this.mStartValues[i] + fraction * this.mStopValues[i];
        }

        outMatrix.setValues(this.mCurrentValues);
    }

    public abstract void setTransformAnimated(Matrix var1, long var2, @Nullable Runnable var4);

    protected abstract void stopAnimation();

    protected abstract Class<?> getLogTag();
}
