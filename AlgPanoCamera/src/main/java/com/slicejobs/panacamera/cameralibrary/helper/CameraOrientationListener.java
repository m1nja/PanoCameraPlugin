package com.slicejobs.panacamera.cameralibrary.helper;

import android.content.Context;
import android.view.OrientationEventListener;

public class CameraOrientationListener extends OrientationEventListener {
    private static final String TAG = "CameraOrientationListen";
    private int mCurrentNormalizedOrientation;
    private int mRememberedNormalOrientation;
    private int mCurrentOrignalOrientation;
    private int mRememberOrignalOrientation;
    private static final int ANGLE_DEX = 10;
    public static int mAngle = -1;

    public CameraOrientationListener(Context context) {
        super(context, 3);
    }

    public void onOrientationChanged(int orientation) {
        if (orientation != -1) {
            this.mCurrentOrignalOrientation = orientation;
            this.mCurrentNormalizedOrientation = this.normalize(orientation);
        }

    }

    private int normalize(int degrees) {
        if (degrees <= 315 && degrees > 45) {
            if (degrees > 45 && degrees <= 135) {
                return 90;
            } else if (degrees > 135 && degrees <= 225) {
                return 180;
            } else if (degrees > 225 && degrees <= 315) {
                return 270;
            } else {
                throw new RuntimeException("The physics as we know them are no more. Watch out for anomalies.");
            }
        } else {
            return 0;
        }
    }

    public void rememberOrientation() {
        this.mRememberedNormalOrientation = this.mCurrentNormalizedOrientation;
    }

    public void remeberOriginalOrientation() {
        this.mRememberOrignalOrientation = this.mCurrentOrignalOrientation;
    }

    public int getRememberedNormalOrientation() {
        return this.mRememberedNormalOrientation;
    }

    public int getRememberOriginalOrientation() {
        return mAngle;
    }

    public boolean isTakePhoto(int state) {
        if (state == 0) {
            if (this.mCurrentOrignalOrientation < 350 && this.mCurrentOrignalOrientation > 10) {
                ToastUtil.shortShow("请保持竖直拍摄");
                return true;
            } else {
                mAngle = 0;
                return false;
            }
        } else if (state == 1) {
            mAngle = 1;
            if (this.mCurrentOrignalOrientation >= 80 && this.mCurrentOrignalOrientation <= 100) {
                return false;
            } else if (this.mCurrentOrignalOrientation >= 260 && this.mCurrentOrignalOrientation <= 280) {
                return false;
            } else {
                ToastUtil.shortShow("请保持水平拍摄");
                return true;
            }
        } else if (this.mCurrentOrignalOrientation < 350 && this.mCurrentOrignalOrientation > 10) {
            if (this.mCurrentOrignalOrientation >= 280 && this.mCurrentOrignalOrientation <= 350) {
                ToastUtil.shortShow("请保持竖直拍摄");
                return true;
            } else if (this.mCurrentOrignalOrientation >= 10 && this.mCurrentOrignalOrientation <= 80) {
                ToastUtil.shortShow("请保持竖直拍摄");
                return true;
            } else if (this.mCurrentOrignalOrientation >= 80 && this.mCurrentOrignalOrientation <= 100) {
                mAngle = 1;
                return false;
            } else if (this.mCurrentOrignalOrientation >= 260 && this.mCurrentOrignalOrientation <= 280) {
                mAngle = 1;
                return false;
            } else if (this.mCurrentOrignalOrientation >= 100 && this.mCurrentOrignalOrientation <= 280) {
                ToastUtil.shortShow("请保持水平拍摄");
                return true;
            } else {
                return false;
            }
        } else {
            mAngle = 0;
            return false;
        }
    }
}
