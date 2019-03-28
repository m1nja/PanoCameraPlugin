package com.slicejobs.panacamera.cameralibrary.fresco.gestures;

import android.view.MotionEvent;

public class TransformGestureDetector implements MultiPointerGestureDetector.Listener {
    private final MultiPointerGestureDetector mDetector;
    private TransformGestureDetector.Listener mListener = null;

    public TransformGestureDetector(MultiPointerGestureDetector multiPointerGestureDetector) {
        this.mDetector = multiPointerGestureDetector;
        this.mDetector.setListener(this);
    }

    public static TransformGestureDetector newInstance() {
        return new TransformGestureDetector(MultiPointerGestureDetector.newInstance());
    }

    public void setListener(TransformGestureDetector.Listener listener) {
        this.mListener = listener;
    }

    public void reset() {
        this.mDetector.reset();
    }

    public boolean onTouchEvent(MotionEvent event) {
        return this.mDetector.onTouchEvent(event);
    }

    public void onGestureBegin(MultiPointerGestureDetector detector) {
        if (this.mListener != null) {
            this.mListener.onGestureBegin(this);
        }

    }

    public void onGestureUpdate(MultiPointerGestureDetector detector) {
        if (this.mListener != null) {
            this.mListener.onGestureUpdate(this);
        }

    }

    public void onGestureEnd(MultiPointerGestureDetector detector) {
        if (this.mListener != null) {
            this.mListener.onGestureEnd(this);
        }

    }

    private float calcAverage(float[] arr, int len) {
        float sum = 0.0F;

        for(int i = 0; i < len; ++i) {
            sum += arr[i];
        }

        return len > 0 ? sum / (float)len : 0.0F;
    }

    public void restartGesture() {
        this.mDetector.restartGesture();
    }

    public boolean isGestureInProgress() {
        return this.mDetector.isGestureInProgress();
    }

    public int getNewPointerCount() {
        return this.mDetector.getNewPointerCount();
    }

    public int getPointerCount() {
        return this.mDetector.getPointerCount();
    }

    public float getPivotX() {
        return this.calcAverage(this.mDetector.getStartX(), this.mDetector.getPointerCount());
    }

    public float getPivotY() {
        return this.calcAverage(this.mDetector.getStartY(), this.mDetector.getPointerCount());
    }

    public float getCurrentX() {
        return this.calcAverage(this.mDetector.getCurrentX(), this.mDetector.getPointerCount());
    }

    public float getCurrentY() {
        return this.calcAverage(this.mDetector.getCurrentY(), this.mDetector.getPointerCount());
    }

    public float getTranslationX() {
        return this.calcAverage(this.mDetector.getCurrentX(), this.mDetector.getPointerCount()) - this.calcAverage(this.mDetector.getStartX(), this.mDetector.getPointerCount());
    }

    public float getTranslationY() {
        return this.calcAverage(this.mDetector.getCurrentY(), this.mDetector.getPointerCount()) - this.calcAverage(this.mDetector.getStartY(), this.mDetector.getPointerCount());
    }

    public float getScale() {
        if (this.mDetector.getPointerCount() < 2) {
            return 1.0F;
        } else {
            float startDeltaX = this.mDetector.getStartX()[1] - this.mDetector.getStartX()[0];
            float startDeltaY = this.mDetector.getStartY()[1] - this.mDetector.getStartY()[0];
            float currentDeltaX = this.mDetector.getCurrentX()[1] - this.mDetector.getCurrentX()[0];
            float currentDeltaY = this.mDetector.getCurrentY()[1] - this.mDetector.getCurrentY()[0];
            float startDist = (float)Math.hypot((double)startDeltaX, (double)startDeltaY);
            float currentDist = (float)Math.hypot((double)currentDeltaX, (double)currentDeltaY);
            return currentDist / startDist;
        }
    }

    public float getRotation() {
        if (this.mDetector.getPointerCount() < 2) {
            return 0.0F;
        } else {
            float startDeltaX = this.mDetector.getStartX()[1] - this.mDetector.getStartX()[0];
            float startDeltaY = this.mDetector.getStartY()[1] - this.mDetector.getStartY()[0];
            float currentDeltaX = this.mDetector.getCurrentX()[1] - this.mDetector.getCurrentX()[0];
            float currentDeltaY = this.mDetector.getCurrentY()[1] - this.mDetector.getCurrentY()[0];
            float startAngle = (float)Math.atan2((double)startDeltaY, (double)startDeltaX);
            float currentAngle = (float)Math.atan2((double)currentDeltaY, (double)currentDeltaX);
            return currentAngle - startAngle;
        }
    }

    public interface Listener {
        void onGestureBegin(TransformGestureDetector var1);

        void onGestureUpdate(TransformGestureDetector var1);

        void onGestureEnd(TransformGestureDetector var1);
    }
}
