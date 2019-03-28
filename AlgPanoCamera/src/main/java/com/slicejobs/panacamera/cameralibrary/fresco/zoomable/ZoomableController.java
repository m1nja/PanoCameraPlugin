package com.slicejobs.panacamera.cameralibrary.fresco.zoomable;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.MotionEvent;

public interface ZoomableController {
    void setSwipeDownListener(ZoomableController.OnSwipeDownListener var1);

    void setEnabled(boolean var1);

    boolean isEnabled();

    void setEnableGestureDiscard(boolean var1);

    void setListener(ZoomableController.Listener var1);

    float getScaleFactor();

    float getOriginScaleFactor();

    float getTranslateY();

    boolean isIdentity();

    boolean wasTransformCorrected();

    int computeHorizontalScrollRange();

    int computeHorizontalScrollOffset();

    int computeHorizontalScrollExtent();

    int computeVerticalScrollRange();

    int computeVerticalScrollOffset();

    int computeVerticalScrollExtent();

    Matrix getTransform();

    RectF getImageBounds();

    void setImageBounds(RectF var1);

    void setViewBounds(RectF var1);

    void initDefaultScale(RectF var1, RectF var2);

    boolean onTouchEvent(MotionEvent var1);

    public interface OnSwipeDownListener {
        void onSwipeDown(float var1);

        void onSwipeRelease(float var1);
    }

    public interface Listener {
        void onTransformChanged(Matrix var1);
    }
}

