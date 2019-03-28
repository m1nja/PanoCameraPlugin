package com.slicejobs.panacamera.cameralibrary.fresco.zoomable;

import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

public class GestureListenerWrapper extends SimpleOnGestureListener {
    private SimpleOnGestureListener mDelegate = new SimpleOnGestureListener();

    public GestureListenerWrapper() {
    }

    public void setListener(SimpleOnGestureListener listener) {
        this.mDelegate = listener;
    }

    public void onLongPress(MotionEvent e) {
        this.mDelegate.onLongPress(e);
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return this.mDelegate.onScroll(e1, e2, distanceX, distanceY);
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return this.mDelegate.onFling(e1, e2, velocityX, velocityY);
    }

    public void onShowPress(MotionEvent e) {
        this.mDelegate.onShowPress(e);
    }

    public boolean onDown(MotionEvent e) {
        return this.mDelegate.onDown(e);
    }

    public boolean onDoubleTap(MotionEvent e) {
        return this.mDelegate.onDoubleTap(e);
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        return this.mDelegate.onDoubleTapEvent(e);
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        return this.mDelegate.onSingleTapConfirmed(e);
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return this.mDelegate.onSingleTapUp(e);
    }
}