package com.slicejobs.panacamera.cameralibrary.fresco.zoomable;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

public class DoubleTapGestureListener extends SimpleOnGestureListener {
    private static final int DURATION_MS = 300;
    private static final int DOUBLE_TAP_SCROLL_THRESHOLD = 20;
    private final ZoomableDraweeView mDraweeView;
    private final PointF mDoubleTapViewPoint = new PointF();
    private final PointF mDoubleTapImagePoint = new PointF();
    private float mDoubleTapScale = 1.0F;
    private boolean mDoubleTapScroll = false;

    public DoubleTapGestureListener(ZoomableDraweeView zoomableDraweeView) {
        this.mDraweeView = zoomableDraweeView;
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        AbstractAnimatedZoomableController zc = (AbstractAnimatedZoomableController)this.mDraweeView.getZoomableController();
        PointF vp = new PointF(e.getX(), e.getY());
        PointF ip = zc.mapViewToImage(vp);
        float scale;
        switch(e.getActionMasked()) {
            case 0:
                this.mDoubleTapViewPoint.set(vp);
                this.mDoubleTapImagePoint.set(ip);
                this.mDoubleTapScale = zc.getScaleFactor();
                break;
            case 1:
                if (this.mDoubleTapScroll) {
                    scale = this.calcScale(vp);
                    if (scale < zc.getOriginScaleFactor()) {
                        zc.zoomToPoint(zc.getOriginScaleFactor(), this.mDoubleTapImagePoint, this.mDoubleTapViewPoint, 7, 300L, (Runnable)null);
                    } else {
                        zc.zoomToPoint(scale, this.mDoubleTapImagePoint, this.mDoubleTapViewPoint);
                    }
                } else {
                    scale = zc.getMaxScaleFactor();
                    float minScale = zc.getMinScaleFactor();
                    if (zc.getScaleFactor() < (scale + minScale) / 2.0F) {
                        zc.zoomToPoint(scale, ip, vp, 7, 300L, (Runnable)null);
                    } else {
                        zc.zoomToPoint(zc.getOriginScaleFactor(), ip, vp, 7, 300L, (Runnable)null);
                    }
                }

                this.mDoubleTapScroll = false;
                break;
            case 2:
                this.mDoubleTapScroll = this.mDoubleTapScroll || this.shouldStartDoubleTapScroll(vp);
                if (this.mDoubleTapScroll) {
                    scale = this.calcScale(vp);
                    zc.zoomToPoint(scale, this.mDoubleTapImagePoint, this.mDoubleTapViewPoint);
                }
        }

        return true;
    }

    private boolean shouldStartDoubleTapScroll(PointF viewPoint) {
        double dist = Math.hypot((double)(viewPoint.x - this.mDoubleTapViewPoint.x), (double)(viewPoint.y - this.mDoubleTapViewPoint.y));
        return dist > 20.0D;
    }

    private float calcScale(PointF currentViewPoint) {
        float dy = currentViewPoint.y - this.mDoubleTapViewPoint.y;
        float t = 1.0F + Math.abs(dy) * 0.001F;
        return dy < 0.0F ? this.mDoubleTapScale / t : this.mDoubleTapScale * t;
    }
}