package com.slicejobs.panacamera.cameralibrary.fresco.gestures;

import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

/** @deprecated */
@Deprecated
public class SwipeGestureDetector extends SimpleOnGestureListener {
    private static final String TAG = "SwipeGestureDetector";
    private SwipeGestureDetector.OnSwipeListener mSwipeListener;
    private boolean mInThisGesture;
    private float mStartX;
    private float mStartY;
    private float mCurrentX;
    private float mCurrentY;

    public SwipeGestureDetector(SwipeGestureDetector.OnSwipeListener swipeListener) {
        this.mSwipeListener = swipeListener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getActionMasked()) {
            case 0:
                this.mStartX = this.mCurrentX = event.getX();
                this.mStartY = this.mCurrentY = event.getY();
                if (this.mSwipeListener != null && !this.mInThisGesture) {
                    this.mInThisGesture = this.mSwipeListener.onOpenSwipe();
                }

                if (this.mInThisGesture && this.mSwipeListener != null) {
                    this.mSwipeListener.onSwipeBegin();
                }
                break;
            case 1:
                this.mInThisGesture = false;
                if (this.mSwipeListener != null) {
                    this.mSwipeListener.onSwipeReleased();
                }
                break;
            case 2:
                this.mCurrentX = event.getX();
                this.mCurrentY = event.getY();
                Log.d("SwipeGestureDetector", "onTouchEvent: start   X: " + this.mStartX + "  start Y: " + this.mStartY);
                Log.d("SwipeGestureDetector", "onTouchEvent: current X: " + this.mCurrentX + "  current Y: " + this.mCurrentY);
                if (this.mSwipeListener != null && this.mInThisGesture) {
                    this.mSwipeListener.onSwiping(this.mCurrentX - this.mStartX, this.mCurrentY - this.mStartY);
                }
                break;
            case 3:
                this.mInThisGesture = false;
                if (this.mSwipeListener != null) {
                    this.mSwipeListener.onSwipeReleased();
                }
        }

        return this.mInThisGesture;
    }

    public float getTranslateY() {
        return this.mCurrentY - this.mStartY;
    }

    public float getTranslateX() {
        return this.mCurrentX - this.mStartX;
    }

    public interface OnSwipeListener {
        boolean onOpenSwipe();

        void onSwipeBegin();

        void onSwipeReleased();

        void onSwiping(float var1, float var2);
    }
}
