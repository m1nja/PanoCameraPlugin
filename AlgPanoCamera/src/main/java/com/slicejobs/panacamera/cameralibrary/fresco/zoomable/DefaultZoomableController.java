package com.slicejobs.panacamera.cameralibrary.fresco.zoomable;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Matrix.ScaleToFit;
import android.view.MotionEvent;
import com.facebook.common.logging.FLog;
import com.slicejobs.panacamera.cameralibrary.fresco.gestures.TransformGestureDetector.Listener;
import com.slicejobs.panacamera.cameralibrary.fresco.gestures.TransformGestureDetector;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DefaultZoomableController implements ZoomableController, Listener {
    public static final int LIMIT_NONE = 0;
    public static final int LIMIT_TRANSLATION_X = 1;
    public static final int LIMIT_TRANSLATION_Y = 2;
    public static final int LIMIT_SCALE = 4;
    public static final int LIMIT_ALL = 7;
    private static final float EPS = 0.001F;
    private static final Class<?> TAG = DefaultZoomableController.class;
    private static final RectF IDENTITY_RECT = new RectF(0.0F, 0.0F, 1.0F, 1.0F);
    private static final float MAX_SCALE_FACTOR = 3.0F;
    private static final float MIN_SCALE_FACTOR = 0.7F;
    private TransformGestureDetector mGestureDetector;
    private ZoomableController.Listener mListener = null;
    private boolean mIsEnabled = false;
    private boolean mEnableGestureDiscard = true;
    private boolean mIsRotationEnabled = false;
    private boolean mIsScaleEnabled = true;
    private boolean mIsTranslationEnabled = true;
    private float mMinScaleFactor = 0.7F;
    private float mMaxScaleFactor = 3.0F;
    private float mOriginScaleFactor = 1.0F;
    private final RectF mViewBounds = new RectF();
    private final RectF mImageBounds = new RectF();
    private final RectF mTransformedImageBounds = new RectF();
    private final Matrix mPreviousTransform = new Matrix();
    private final Matrix mActiveTransform = new Matrix();
    private final Matrix mActiveTransformInverse = new Matrix();
    private final float[] mTempValues = new float[9];
    private final RectF mTempRect = new RectF();
    private boolean mWasTransformCorrected;
    private boolean mCanScrollUpThisGesture;
    private boolean mIsInSwipeDown;
    protected OnSwipeDownListener mSwipeDownListener;

    public static DefaultZoomableController newInstance() {
        return new DefaultZoomableController(TransformGestureDetector.newInstance());
    }

    public DefaultZoomableController(TransformGestureDetector gestureDetector) {
        this.mGestureDetector = gestureDetector;
        this.mGestureDetector.setListener(this);
    }

    public void setSwipeDownListener(OnSwipeDownListener listener) {
        this.mSwipeDownListener = listener;
    }

    public void reset() {
        FLog.v(TAG, "reset");
        this.mGestureDetector.reset();
        this.mPreviousTransform.reset();
        this.mActiveTransform.reset();
        this.onTransformChanged();
    }

    public void setListener(ZoomableController.Listener listener) {
        this.mListener = listener;
    }

    public void setEnabled(boolean enabled) {
        this.mIsEnabled = enabled;
        if (!enabled) {
            this.reset();
        }

    }

    public void setEnableGestureDiscard(boolean enable) {
        this.mEnableGestureDiscard = enable;
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    public void setRotationEnabled(boolean enabled) {
        this.mIsRotationEnabled = enabled;
    }

    public boolean isRotationEnabled() {
        return this.mIsRotationEnabled;
    }

    public void setScaleEnabled(boolean enabled) {
        this.mIsScaleEnabled = enabled;
    }

    public boolean isScaleEnabled() {
        return this.mIsScaleEnabled;
    }

    public void setTranslationEnabled(boolean enabled) {
        this.mIsTranslationEnabled = enabled;
    }

    public boolean isTranslationEnabled() {
        return this.mIsTranslationEnabled;
    }

    public void setMinScaleFactor(float minScaleFactor) {
        this.mMinScaleFactor = minScaleFactor;
    }

    public float getMinScaleFactor() {
        return this.mMinScaleFactor * this.mOriginScaleFactor;
    }

    public void setMaxScaleFactor(float maxScaleFactor) {
        this.mMaxScaleFactor = maxScaleFactor;
    }

    public float getMaxScaleFactor() {
        return this.mMaxScaleFactor * this.mOriginScaleFactor;
    }

    public void setOriginScaleFactor(float originScaleFactor) {
        this.mOriginScaleFactor = originScaleFactor;
    }

    public float getOriginScaleFactor() {
        return this.mOriginScaleFactor;
    }

    public float getScaleFactor() {
        return this.getMatrixScaleFactor(this.mActiveTransform);
    }

    public float getTranslateY() {
        return this.getMatrixTranslateY(this.mActiveTransform);
    }

    public void setImageBounds(RectF imageBounds) {
        if (!imageBounds.equals(this.mImageBounds)) {
            this.mImageBounds.set(imageBounds);
            this.onTransformChanged();
        }

    }

    public RectF getImageBounds() {
        return this.mImageBounds;
    }

    private RectF getTransformedImageBounds() {
        return this.mTransformedImageBounds;
    }

    public void setViewBounds(RectF viewBounds) {
        this.mViewBounds.set(viewBounds);
    }

    public RectF getViewBounds() {
        return this.mViewBounds;
    }

    public void initDefaultScale(RectF viewBounds, RectF imageBounds) {
        if (imageBounds.left > viewBounds.left) {
            float scale = (viewBounds.right - viewBounds.left) / (imageBounds.right - imageBounds.left);
            this.setOriginScaleFactor(scale);
            this.zoomToPoint(scale, new PointF(0.0F, 0.0F), new PointF(0.0F, 0.0F));
        }

    }

    public boolean isIdentity() {
        return this.isMatrixIdentity(this.mActiveTransform, 0.001F);
    }

    public boolean wasTransformCorrected() {
        return this.mWasTransformCorrected;
    }

    public Matrix getTransform() {
        return this.mActiveTransform;
    }

    public void getImageRelativeToViewAbsoluteTransform(Matrix outMatrix) {
        outMatrix.setRectToRect(IDENTITY_RECT, this.mTransformedImageBounds, ScaleToFit.FILL);
    }

    public PointF mapViewToImage(PointF viewPoint) {
        float[] points = this.mTempValues;
        points[0] = viewPoint.x;
        points[1] = viewPoint.y;
        this.mActiveTransform.invert(this.mActiveTransformInverse);
        this.mActiveTransformInverse.mapPoints(points, 0, points, 0, 1);
        this.mapAbsoluteToRelative(points, points, 1);
        return new PointF(points[0], points[1]);
    }

    public PointF mapImageToView(PointF imagePoint) {
        float[] points = this.mTempValues;
        points[0] = imagePoint.x;
        points[1] = imagePoint.y;
        this.mapRelativeToAbsolute(points, points, 1);
        this.mActiveTransform.mapPoints(points, 0, points, 0, 1);
        return new PointF(points[0], points[1]);
    }

    private void mapAbsoluteToRelative(float[] destPoints, float[] srcPoints, int numPoints) {
        for(int i = 0; i < numPoints; ++i) {
            destPoints[i * 2 + 0] = (srcPoints[i * 2 + 0] - this.mImageBounds.left) / this.mImageBounds.width();
            destPoints[i * 2 + 1] = (srcPoints[i * 2 + 1] - this.mImageBounds.top) / this.mImageBounds.height();
        }

    }

    private void mapRelativeToAbsolute(float[] destPoints, float[] srcPoints, int numPoints) {
        for(int i = 0; i < numPoints; ++i) {
            destPoints[i * 2 + 0] = srcPoints[i * 2 + 0] * this.mImageBounds.width() + this.mImageBounds.left;
            destPoints[i * 2 + 1] = srcPoints[i * 2 + 1] * this.mImageBounds.height() + this.mImageBounds.top;
        }

    }

    public void zoomToPoint(float scale, PointF imagePoint, PointF viewPoint) {
        FLog.v(TAG, "zoomToPoint");
        this.calculateZoomToPointTransform(this.mActiveTransform, scale, imagePoint, viewPoint, 7);
        this.onTransformChanged();
    }

    protected boolean calculateZoomToPointTransform(Matrix outTransform, float scale, PointF imagePoint, PointF viewPoint, int limitFlags) {
        float[] viewAbsolute = this.mTempValues;
        viewAbsolute[0] = imagePoint.x;
        viewAbsolute[1] = imagePoint.y;
        this.mapRelativeToAbsolute(viewAbsolute, viewAbsolute, 1);
        float distanceX = viewPoint.x - viewAbsolute[0];
        float distanceY = viewPoint.y - viewAbsolute[1];
        boolean transformCorrected = false;
        outTransform.setScale(scale, scale, viewAbsolute[0], viewAbsolute[1]);
        transformCorrected |= this.limitScale(outTransform, viewAbsolute[0], viewAbsolute[1], limitFlags);
        outTransform.postTranslate(distanceX, distanceY);
        transformCorrected |= this.limitTranslation(outTransform, limitFlags);
        return transformCorrected;
    }

    public void translateTo(float distanceX, float distanceY) {
        FLog.d(TAG, "Before translateTo: " + this.mActiveTransform.toShortString());
        this.calculateTranslateTransform(this.mActiveTransform, distanceX, distanceY);
        this.onTransformChanged();
    }

    protected void calculateTranslateTransform(Matrix outTransform, float distanceX, float distanceY) {
        outTransform.postTranslate(distanceX, distanceY);
        float[] viewAbsolute = this.mTempValues;
        viewAbsolute[0] = 0.5F;
        viewAbsolute[1] = 0.5F;
        this.mapRelativeToAbsolute(viewAbsolute, viewAbsolute, 1);
        float scale = (this.getViewBounds().height() - distanceY) / this.getViewBounds().height();
        outTransform.postScale(scale, scale, viewAbsolute[0], viewAbsolute[1]);
        this.limitScale(outTransform, viewAbsolute[0], viewAbsolute[1], 7);
    }

    public void setTransform(Matrix newTransform) {
        FLog.v(TAG, "setTransform");
        this.mActiveTransform.set(newTransform);
        this.onTransformChanged();
    }

    protected TransformGestureDetector getDetector() {
        return this.mGestureDetector;
    }

    public boolean onTouchEvent(MotionEvent event) {
        FLog.v(TAG, "onTouchEvent: action: ", event.getAction());
        return this.mIsEnabled ? this.mGestureDetector.onTouchEvent(event) : false;
    }

    public void onGestureBegin(TransformGestureDetector detector) {
        FLog.v(TAG, "onGestureBegin");
        this.mPreviousTransform.set(this.mActiveTransform);
        this.mWasTransformCorrected = !this.canScrollInAllDirection();
        if (!this.canScrollUp()) {
            this.mCanScrollUpThisGesture = false;
        } else {
            this.mCanScrollUpThisGesture = true;
        }

    }

    public void onGestureUpdate(TransformGestureDetector detector) {
        FLog.v(TAG, "onGestureUpdate");
        boolean transformCorrected = this.calculateGestureTransform(this.mActiveTransform, 7);
        float translateX = detector.getTranslationX();
        float translateY = detector.getTranslationY();
        if (this.getScaleFactor() == this.getOriginScaleFactor() && !this.mCanScrollUpThisGesture && translateY > 0.0F) {
            FLog.d(TAG, "onGestureUpdate: start X: " + detector.getPivotX() + " start Y: " + detector.getPivotY());
            FLog.d(TAG, "onGestureUpdate: current X: " + detector.getCurrentX() + " current Y: " + detector.getCurrentY());
            this.translateTo(translateX, translateY);
            this.mIsInSwipeDown = true;
            if (this.mSwipeDownListener != null) {
                this.mSwipeDownListener.onSwipeDown(translateY);
            }
        }

        this.onTransformChanged();
        this.mWasTransformCorrected = transformCorrected;
    }

    public void onGestureEnd(TransformGestureDetector detector) {
        FLog.v(TAG, "onSwipeDownGestureEnd");
        this.dispatchSwipeRelease(detector.getTranslationY());
        if (this.mEnableGestureDiscard && this.isGestureNeedDiscard()) {
            this.restoreImage(detector.getCurrentX(), detector.getCurrentY());
        }

    }

    protected boolean isGestureNeedDiscard() {
        return this.getScaleFactor() < this.getOriginScaleFactor() || this.getScaleFactor() == this.getOriginScaleFactor() && this.getTranslateY() != 0.0F;
    }

    protected void restoreImage(float fromX, float fromY) {
        PointF viewPoint = new PointF(fromX, fromY);
        this.zoomToPoint(this.getOriginScaleFactor(), this.mapViewToImage(viewPoint), viewPoint);
    }

    protected void dispatchSwipeRelease(float translateY) {
        if (this.mIsInSwipeDown) {
            this.mIsInSwipeDown = false;
            if (this.mSwipeDownListener != null) {
                this.mSwipeDownListener.onSwipeRelease(translateY);
            }
        }

    }

    protected boolean calculateGestureTransform(Matrix outTransform, int limitTypes) {
        TransformGestureDetector detector = this.mGestureDetector;
        boolean transformCorrected = false;
        outTransform.set(this.mPreviousTransform);
        float scale;
        if (this.mIsRotationEnabled) {
            scale = detector.getRotation() * 57.29578F;
            outTransform.postRotate(scale, detector.getPivotX(), detector.getPivotY());
        }

        if (this.mIsScaleEnabled) {
            scale = detector.getScale();
            outTransform.postScale(scale, scale, detector.getPivotX(), detector.getPivotY());
        }

        transformCorrected |= this.limitScale(outTransform, detector.getPivotX(), detector.getPivotY(), limitTypes);
        if (this.mIsTranslationEnabled) {
            outTransform.postTranslate(detector.getTranslationX(), detector.getTranslationY());
        }

        transformCorrected |= this.limitTranslation(outTransform, limitTypes);
        return transformCorrected;
    }

    private void onTransformChanged() {
        this.mActiveTransform.mapRect(this.mTransformedImageBounds, this.mImageBounds);
        if (this.mListener != null && this.isEnabled()) {
            this.mListener.onTransformChanged(this.mActiveTransform);
        }

    }

    private boolean limitScale(Matrix transform, float pivotX, float pivotY, int limitTypes) {
        if (!shouldLimit(limitTypes, 4)) {
            return false;
        } else {
            float currentScale = this.getMatrixScaleFactor(transform);
            float targetScale = this.limit(currentScale, this.mMinScaleFactor * this.mOriginScaleFactor, this.mMaxScaleFactor * this.mOriginScaleFactor);
            if (targetScale != currentScale) {
                float scale = targetScale / currentScale;
                transform.postScale(scale, scale, pivotX, pivotY);
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean limitTranslation(Matrix transform, int limitTypes) {
        if (!shouldLimit(limitTypes, 3)) {
            return false;
        } else {
            RectF b = this.mTempRect;
            b.set(this.mImageBounds);
            transform.mapRect(b);
            float offsetLeft = shouldLimit(limitTypes, 1) ? this.getOffset(b.left, b.right, this.mViewBounds.left, this.mViewBounds.right, this.mImageBounds.centerX()) : 0.0F;
            float offsetTop = shouldLimit(limitTypes, 2) ? this.getOffset(b.top, b.bottom, this.mViewBounds.top, this.mViewBounds.bottom, this.mImageBounds.centerY()) : 0.0F;
            if (offsetLeft == 0.0F && offsetTop == 0.0F) {
                return false;
            } else {
                transform.postTranslate(offsetLeft, offsetTop);
                return true;
            }
        }
    }

    private static boolean shouldLimit(int limits, int flag) {
        return (limits & flag) != 0;
    }

    private float getOffset(float imageStart, float imageEnd, float limitStart, float limitEnd, float limitCenter) {
        float imageWidth = imageEnd - imageStart;
        float limitWidth = limitEnd - limitStart;
        float limitInnerWidth = Math.min(limitCenter - limitStart, limitEnd - limitCenter) * 2.0F;
        if (imageWidth < limitInnerWidth) {
            return limitCenter - (imageEnd + imageStart) / 2.0F;
        } else if (imageWidth < limitWidth) {
            return limitCenter < (limitStart + limitEnd) / 2.0F ? limitStart - imageStart : limitEnd - imageEnd;
        } else if (imageStart > limitStart) {
            return limitStart - imageStart;
        } else {
            return imageEnd < limitEnd ? limitEnd - imageEnd : 0.0F;
        }
    }

    private float limit(float value, float min, float max) {
        return Math.min(Math.max(min, value), max);
    }

    private float getMatrixScaleFactor(Matrix transform) {
        transform.getValues(this.mTempValues);
        return this.mTempValues[0];
    }

    private float getMatrixTranslateY(Matrix transform) {
        transform.getValues(this.mTempValues);
        return this.mTempValues[5];
    }

    private boolean isMatrixIdentity(Matrix transform, float eps) {
        transform.getValues(this.mTempValues);
        --this.mTempValues[0];
        --this.mTempValues[4];
        --this.mTempValues[8];

        for(int i = 0; i < 9; ++i) {
            if (Math.abs(this.mTempValues[i]) > eps) {
                return false;
            }
        }

        return true;
    }

    private boolean canScrollInAllDirection() {
        return this.mTransformedImageBounds.left < this.mViewBounds.left - 0.001F && this.mTransformedImageBounds.top < this.mViewBounds.top - 0.001F && this.mTransformedImageBounds.right > this.mViewBounds.right + 0.001F && this.mTransformedImageBounds.bottom > this.mViewBounds.bottom + 0.001F;
    }

    private boolean canScrollUp() {
        return this.mTransformedImageBounds.top < this.mViewBounds.top - 0.001F;
    }

    private boolean canScrollDown() {
        return this.mTransformedImageBounds.bottom > this.mViewBounds.bottom + 0.001F;
    }

    public int computeHorizontalScrollRange() {
        return (int)this.mTransformedImageBounds.width();
    }

    public int computeHorizontalScrollOffset() {
        return (int)(this.mViewBounds.left - this.mTransformedImageBounds.left);
    }

    public int computeHorizontalScrollExtent() {
        return (int)this.mViewBounds.width();
    }

    public int computeVerticalScrollRange() {
        return (int)this.mTransformedImageBounds.height();
    }

    public int computeVerticalScrollOffset() {
        return (int)(this.mViewBounds.top - this.mTransformedImageBounds.top);
    }

    public int computeVerticalScrollExtent() {
        return (int)this.mViewBounds.height();
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface LimitFlag {
    }
}