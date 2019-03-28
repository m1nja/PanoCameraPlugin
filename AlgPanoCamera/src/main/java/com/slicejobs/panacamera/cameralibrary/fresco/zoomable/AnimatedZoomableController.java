package com.slicejobs.panacamera.cameralibrary.fresco.zoomable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.view.animation.DecelerateInterpolator;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import com.slicejobs.panacamera.cameralibrary.fresco.gestures.TransformGestureDetector;

public class AnimatedZoomableController extends AbstractAnimatedZoomableController {
    private static final Class<?> TAG = AnimatedZoomableController.class;
    private final ValueAnimator mValueAnimator = ValueAnimator.ofFloat(new float[]{0.0F, 1.0F});

    public static AnimatedZoomableController newInstance() {
        return new AnimatedZoomableController(TransformGestureDetector.newInstance());
    }

    @SuppressLint({"NewApi"})
    public AnimatedZoomableController(TransformGestureDetector transformGestureDetector) {
        super(transformGestureDetector);
        this.mValueAnimator.setInterpolator(new DecelerateInterpolator());
    }

    @SuppressLint({"NewApi"})
    public void setTransformAnimated(Matrix newTransform, long durationMs, @Nullable final Runnable onAnimationComplete) {
        FLog.v(this.getLogTag(), "setTransformAnimated: duration %d ms", durationMs);
        this.stopAnimation();
        Preconditions.checkArgument(durationMs > 0L);
        Preconditions.checkState(!this.isAnimating());
        this.setAnimating(true);
        this.mValueAnimator.setDuration(durationMs);
        this.getTransform().getValues(this.getStartValues());
        newTransform.getValues(this.getStopValues());
        this.mValueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                AnimatedZoomableController.this.calculateInterpolation(AnimatedZoomableController.this.getWorkingTransform(), (Float)valueAnimator.getAnimatedValue());
                AnimatedZoomableController.super.setTransform(AnimatedZoomableController.this.getWorkingTransform());
            }
        });
        this.mValueAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationCancel(Animator animation) {
                FLog.v(AnimatedZoomableController.this.getLogTag(), "setTransformAnimated: animation cancelled");
                this.onAnimationStopped();
            }

            public void onAnimationEnd(Animator animation) {
                FLog.v(AnimatedZoomableController.this.getLogTag(), "setTransformAnimated: animation finished");
                this.onAnimationStopped();
            }

            private void onAnimationStopped() {
                if (onAnimationComplete != null) {
                    onAnimationComplete.run();
                }

                AnimatedZoomableController.this.setAnimating(false);
                AnimatedZoomableController.this.getDetector().restartGesture();
            }
        });
        this.mValueAnimator.start();
    }

    @SuppressLint({"NewApi"})
    public void stopAnimation() {
        if (this.isAnimating()) {
            FLog.v(this.getLogTag(), "stopAnimation");
            this.mValueAnimator.cancel();
            this.mValueAnimator.removeAllUpdateListeners();
            this.mValueAnimator.removeAllListeners();
        }
    }

    protected Class<?> getLogTag() {
        return TAG;
    }
}