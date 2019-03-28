package com.slicejobs.panacamera.cameralibrary.fresco.zoomable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.support.annotation.Nullable;
import android.support.v4.view.ScrollingView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageView;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchyInflater;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;

public class ZoomableDraweeView extends DraweeView<GenericDraweeHierarchy> implements ScrollingView {
    private static final Class<?> TAG = ZoomableDraweeView.class;
    private static final float HUGE_IMAGE_SCALE_FACTOR_THRESHOLD = 1.1F;
    private static final boolean DEFAULT_ALLOW_TOUCH_INTERCEPTION_WHILE_ZOOMED = true;
    private boolean mUseSimpleTouchHandling = false;
    private final RectF mImageBounds = new RectF();
    private final RectF mViewBounds = new RectF();
    private DraweeController mHugeImageController;
    private ZoomableController mZoomableController;
    private GestureDetector mTapGestureDetector;
    private boolean mAllowTouchInterceptionWhileZoomed = true;
    private final ControllerListener mControllerListener = new BaseControllerListener<Object>() {
        public void onFinalImageSet(String id, @Nullable Object imageInfo, @Nullable Animatable animatable) {
            ZoomableDraweeView.this.onFinalImageSet();
        }

        public void onRelease(String id) {
            ZoomableDraweeView.this.onRelease();
        }
    };
    private final ZoomableController.Listener mZoomableListener = new ZoomableController.Listener() {
        public void onTransformChanged(Matrix transform) {
            ZoomableDraweeView.this.onTransformChanged(transform);
        }
    };
    private final GestureListenerWrapper mTapListenerWrapper = new GestureListenerWrapper();

    public ZoomableDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context);
        this.setHierarchy(hierarchy);
        this.init();
    }

    public ZoomableDraweeView(Context context) {
        super(context);
        this.inflateHierarchy(context, (AttributeSet)null);
        this.init();
    }

    public ZoomableDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.inflateHierarchy(context, attrs);
        this.init();
    }

    public ZoomableDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.inflateHierarchy(context, attrs);
        this.init();
    }

    public void setSwipeDownListener(ZoomableController.OnSwipeDownListener listener) {
        this.mZoomableController.setSwipeDownListener(listener);
    }

    protected void inflateHierarchy(Context context, @Nullable AttributeSet attrs) {
        Resources resources = context.getResources();
        GenericDraweeHierarchyBuilder builder = (new GenericDraweeHierarchyBuilder(resources)).setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        GenericDraweeHierarchyInflater.updateBuilder(builder, context, attrs);
        this.setAspectRatio(builder.getDesiredAspectRatio());
        this.setHierarchy(builder.build());
    }

    private void init() {
        this.mZoomableController = this.createZoomableController();
        this.mZoomableController.setListener(this.mZoomableListener);
        this.mTapGestureDetector = new GestureDetector(this.getContext(), this.mTapListenerWrapper);
    }

    protected void getImageBounds(RectF outBounds) {
        ((GenericDraweeHierarchy)this.getHierarchy()).getActualImageBounds(outBounds);
    }

    protected void getLimitBounds(RectF outBounds) {
        outBounds.set(0.0F, 0.0F, (float)this.getWidth(), (float)this.getHeight());
    }

    public void setZoomableController(ZoomableController zoomableController) {
        Preconditions.checkNotNull(zoomableController);
        this.mZoomableController.setListener((ZoomableController.Listener)null);
        this.mZoomableController = zoomableController;
        this.mZoomableController.setListener(this.mZoomableListener);
    }

    public ZoomableController getZoomableController() {
        return this.mZoomableController;
    }

    public boolean allowsTouchInterceptionWhileZoomed() {
        return this.mAllowTouchInterceptionWhileZoomed;
    }

    public void setAllowTouchInterceptionWhileZoomed(boolean allowTouchInterceptionWhileZoomed) {
        this.mAllowTouchInterceptionWhileZoomed = allowTouchInterceptionWhileZoomed;
    }

    public void setTapListener(SimpleOnGestureListener tapListener) {
        this.mTapListenerWrapper.setListener(tapListener);
    }

    public void setEnableGestureDiscard(boolean discard) {
        this.mZoomableController.setEnableGestureDiscard(discard);
    }

    public void setIsLongpressEnabled(boolean enabled) {
        this.mTapGestureDetector.setIsLongpressEnabled(enabled);
    }

    public void setController(@Nullable DraweeController controller) {
        this.setControllers(controller, (DraweeController)null);
    }

    public void setControllers(@Nullable DraweeController controller, @Nullable DraweeController hugeImageController) {
        this.setControllersInternal((DraweeController)null, (DraweeController)null);
        this.mZoomableController.setEnabled(false);
        this.setControllersInternal(controller, hugeImageController);
    }

    private void setControllersInternal(@Nullable DraweeController controller, @Nullable DraweeController hugeImageController) {
        this.removeControllerListener(this.getController());
        this.addControllerListener(controller);
        this.mHugeImageController = hugeImageController;
        super.setController(controller);
    }

    private void maybeSetHugeImageController() {
        if (this.mHugeImageController != null && this.mZoomableController.getScaleFactor() > 1.1F) {
            this.setControllersInternal(this.mHugeImageController, (DraweeController)null);
        }

    }

    private void removeControllerListener(DraweeController controller) {
        if (controller instanceof AbstractDraweeController) {
            ((AbstractDraweeController)controller).removeControllerListener(this.mControllerListener);
        }

    }

    private void addControllerListener(DraweeController controller) {
        if (controller instanceof AbstractDraweeController) {
            ((AbstractDraweeController)controller).addControllerListener(this.mControllerListener);
        }

    }

    protected void onDraw(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.concat(this.mZoomableController.getTransform());
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int a = event.getActionMasked();
        FLog.v(this.getLogTag(), "onTouchEvent: %d, view %x, received", a, this.hashCode());
        if (this.mTapGestureDetector.onTouchEvent(event)) {
            FLog.v(this.getLogTag(), "onTouchEvent: %d, view %x, handled by tap gesture detector", a, this.hashCode());
            return true;
        } else {
            if (this.mUseSimpleTouchHandling) {
                if (this.mZoomableController.onTouchEvent(event)) {
                    return true;
                }
            } else if (this.mZoomableController.onTouchEvent(event)) {
                if (!this.mAllowTouchInterceptionWhileZoomed && !this.mZoomableController.isIdentity() || this.mAllowTouchInterceptionWhileZoomed && !this.mZoomableController.wasTransformCorrected()) {
                    this.getParent().requestDisallowInterceptTouchEvent(true);
                }

                FLog.v(this.getLogTag(), "onTouchEvent: %d, view %x, handled by zoomable controller", a, this.hashCode());
                return true;
            }

            if (super.onTouchEvent(event)) {
                FLog.v(this.getLogTag(), "onTouchEvent: %d, view %x, handled by the super", a, this.hashCode());
                return true;
            } else {
                MotionEvent cancelEvent = MotionEvent.obtain(event);
                cancelEvent.setAction(3);
                this.mTapGestureDetector.onTouchEvent(cancelEvent);
                this.mZoomableController.onTouchEvent(cancelEvent);
                cancelEvent.recycle();
                return false;
            }
        }
    }

    public int computeHorizontalScrollRange() {
        return this.mZoomableController.computeHorizontalScrollRange();
    }

    public int computeHorizontalScrollOffset() {
        return this.mZoomableController.computeHorizontalScrollOffset();
    }

    public int computeHorizontalScrollExtent() {
        return this.mZoomableController.computeHorizontalScrollExtent();
    }

    public int computeVerticalScrollRange() {
        return this.mZoomableController.computeVerticalScrollRange();
    }

    public int computeVerticalScrollOffset() {
        return this.mZoomableController.computeVerticalScrollOffset();
    }

    public int computeVerticalScrollExtent() {
        return this.mZoomableController.computeVerticalScrollExtent();
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        FLog.v(this.getLogTag(), "onLayout: view %x", this.hashCode());
        super.onLayout(changed, left, top, right, bottom);
        this.updateZoomableControllerBounds();
    }

    private void onFinalImageSet() {
        FLog.v(this.getLogTag(), "onFinalImageSet: view %x", this.hashCode());
        if (!this.mZoomableController.isEnabled()) {
            this.updateZoomableControllerBounds();
            this.mZoomableController.setEnabled(true);
        }

    }

    private void onRelease() {
        FLog.v(this.getLogTag(), "onRelease: view %x", this.hashCode());
        this.mZoomableController.setEnabled(false);
    }

    protected void onTransformChanged(Matrix transform) {
        FLog.v(this.getLogTag(), "onTransformChanged: view %x, transform: %s", this.hashCode(), transform);
        this.maybeSetHugeImageController();
        this.invalidate();
    }

    protected void updateZoomableControllerBounds() {
        this.getImageBounds(this.mImageBounds);
        this.getLimitBounds(this.mViewBounds);
        this.mZoomableController.setImageBounds(this.mImageBounds);
        this.mZoomableController.setViewBounds(this.mViewBounds);
        this.mZoomableController.initDefaultScale(this.mViewBounds, this.mImageBounds);
        FLog.v(this.getLogTag(), "updateZoomableControllerBounds: view %x, view bounds: %s, image bounds: %s", this.hashCode(), this.mViewBounds, this.mImageBounds);
    }

    protected Class<?> getLogTag() {
        return TAG;
    }

    protected ZoomableController createZoomableController() {
        return AnimatedZoomableController.newInstance();
    }

    public void setExperimentalSimpleTouchHandlingEnabled(boolean enabled) {
        this.mUseSimpleTouchHandling = enabled;
    }
}
