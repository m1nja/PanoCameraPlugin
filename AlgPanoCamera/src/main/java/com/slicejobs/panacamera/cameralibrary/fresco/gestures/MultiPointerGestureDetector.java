package com.slicejobs.panacamera.cameralibrary.fresco.gestures;

import android.view.MotionEvent;

public class MultiPointerGestureDetector {
    private static final int MAX_POINTERS = 2;
    private boolean mGestureInProgress;
    private int mPointerCount;
    private int mNewPointerCount;
    private final int[] mId = new int[2];
    private final float[] mStartX = new float[2];
    private final float[] mStartY = new float[2];
    private final float[] mCurrentX = new float[2];
    private final float[] mCurrentY = new float[2];
    private MultiPointerGestureDetector.Listener mListener = null;

    public MultiPointerGestureDetector() {
        this.reset();
    }

    public static MultiPointerGestureDetector newInstance() {
        return new MultiPointerGestureDetector();
    }

    public void setListener(MultiPointerGestureDetector.Listener listener) {
        this.mListener = listener;
    }

    public void reset() {
        this.mGestureInProgress = false;
        this.mPointerCount = 0;

        for(int i = 0; i < 2; ++i) {
            this.mId[i] = -1;
        }

    }

    protected boolean shouldStartGesture() {
        return true;
    }

    private void startGesture() {
        if (!this.mGestureInProgress) {
            if (this.mListener != null) {
                this.mListener.onGestureBegin(this);
            }

            this.mGestureInProgress = true;
        }

    }

    private void stopGesture() {
        if (this.mGestureInProgress) {
            this.mGestureInProgress = false;
            if (this.mListener != null) {
                this.mListener.onGestureEnd(this);
            }
        }

    }

    private int getPressedPointerIndex(MotionEvent event, int i) {
        int count = event.getPointerCount();
        int action = event.getActionMasked();
        int index = event.getActionIndex();
        if ((action == 1 || action == 6) && i >= index) {
            ++i;
        }

        return i < count ? i : -1;
    }

    private static int getPressedPointerCount(MotionEvent event) {
        int count = event.getPointerCount();
        int action = event.getActionMasked();
        if (action == 1 || action == 6) {
            --count;
        }

        return count;
    }

    private void updatePointersOnTap(MotionEvent event) {
        this.mPointerCount = 0;

        for(int i = 0; i < 2; ++i) {
            int index = this.getPressedPointerIndex(event, i);
            if (index == -1) {
                this.mId[i] = -1;
            } else {
                this.mId[i] = event.getPointerId(index);
                this.mCurrentX[i] = this.mStartX[i] = event.getX(index);
                this.mCurrentY[i] = this.mStartY[i] = event.getY(index);
                ++this.mPointerCount;
            }
        }

    }

    private void updatePointersOnMove(MotionEvent event) {
        for(int i = 0; i < 2; ++i) {
            int index = event.findPointerIndex(this.mId[i]);
            if (index != -1) {
                this.mCurrentX[i] = event.getX(index);
                this.mCurrentY[i] = event.getY(index);
            }
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getActionMasked()) {
            case 0:
            case 5:
                this.mNewPointerCount = getPressedPointerCount(event);
                this.updatePointersOnTap(event);
                if (this.mPointerCount > 0 && this.shouldStartGesture()) {
                    this.startGesture();
                }
                break;
            case 1:
            case 6:
                this.stopGesture();
                this.mNewPointerCount = getPressedPointerCount(event);
                this.updatePointersOnTap(event);
                break;
            case 2:
                this.updatePointersOnMove(event);
                if (!this.mGestureInProgress && this.mPointerCount > 0 && this.shouldStartGesture()) {
                    this.startGesture();
                }

                if (this.mGestureInProgress && this.mListener != null) {
                    this.mListener.onGestureUpdate(this);
                }
                break;
            case 3:
                this.mNewPointerCount = 0;
                this.stopGesture();
                this.reset();
            case 4:
        }

        return true;
    }

    public void restartGesture() {
        if (this.mGestureInProgress) {
            this.stopGesture();

            for(int i = 0; i < 2; ++i) {
                this.mStartX[i] = this.mCurrentX[i];
                this.mStartY[i] = this.mCurrentY[i];
            }

            this.startGesture();
        }
    }

    public boolean isGestureInProgress() {
        return this.mGestureInProgress;
    }

    public int getNewPointerCount() {
        return this.mNewPointerCount;
    }

    public int getPointerCount() {
        return this.mPointerCount;
    }

    public float[] getStartX() {
        return this.mStartX;
    }

    public float[] getStartY() {
        return this.mStartY;
    }

    public float[] getCurrentX() {
        return this.mCurrentX;
    }

    public float[] getCurrentY() {
        return this.mCurrentY;
    }

    public interface Listener {
        void onGestureBegin(MultiPointerGestureDetector var1);

        void onGestureUpdate(MultiPointerGestureDetector var1);

        void onGestureEnd(MultiPointerGestureDetector var1);
    }
}