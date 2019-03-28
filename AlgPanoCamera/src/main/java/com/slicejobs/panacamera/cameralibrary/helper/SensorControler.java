package com.slicejobs.panacamera.cameralibrary.helper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.slicejobs.panacamera.cameralibrary.CameraSnap;
import com.slicejobs.panacamera.cameralibrary.ui.helper.IActivityLifiCycle;
import com.socks.library.KLog;
import java.util.Calendar;

public class SensorControler implements IActivityLifiCycle, SensorEventListener {
    public static final String TAG = "SensorControler";
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int mX;
    private int mY;
    private int mZ;
    private long lastStaticStamp = 0L;
    Calendar mCalendar;
    boolean isFocusing = false;
    boolean canFocusIn = false;
    boolean canFocus = false;
    public static final int DELEY_DURATION = 200;
    public static final int STATUS_NONE = 0;
    public static final int STATUS_STATIC = 1;
    public static final int STATUS_MOVE = 2;
    private int STATUE = 0;
    private SensorControler.CameraFocusListener mCameraFocusListener;
    private static SensorControler mInstance;
    private int foucsing = 1;

    private SensorControler() {
        this.mSensorManager = (SensorManager) CameraSnap.mApp.getSystemService(Context.SENSOR_SERVICE);
        this.mSensor = this.mSensorManager.getDefaultSensor(1);
    }

    public static SensorControler getInstance() {
        if (mInstance == null) {
            mInstance = new SensorControler();
        }

        return mInstance;
    }

    public void setCameraFocusListener(SensorControler.CameraFocusListener mCameraFocusListener) {
        this.mCameraFocusListener = mCameraFocusListener;
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor != null) {
            if (this.isFocusing) {
                this.restParams();
            } else {
                if (event.sensor.getType() == 1) {
                    int x = (int)event.values[0];
                    int y = (int)event.values[1];
                    int z = (int)event.values[2];
                    this.mCalendar = Calendar.getInstance();
                    long stamp = this.mCalendar.getTimeInMillis();
                    int second = this.mCalendar.get(Calendar.SECOND);
                    if (this.STATUE != 0) {
                        int px = Math.abs(this.mX - x);
                        int py = Math.abs(this.mY - y);
                        int pz = Math.abs(this.mZ - z);
                        double value = Math.sqrt((double)(px * px + py * py + pz * pz));
                        if (value >= 1.4D) {
                            this.STATUE = 2;
                        } else {
                            if (this.STATUE == 2) {
                                this.lastStaticStamp = stamp;
                                this.canFocusIn = true;
                            }

                            if (this.canFocusIn && stamp - this.lastStaticStamp > 200L && !this.isFocusing) {
                                this.canFocusIn = false;
                                if (this.mCameraFocusListener != null) {
                                    this.mCameraFocusListener.onFocus();
                                }
                            }

                            this.STATUE = 1;
                        }
                    } else {
                        this.lastStaticStamp = stamp;
                        this.STATUE = 1;
                    }

                    this.mX = x;
                    this.mY = y;
                    this.mZ = z;
                }

            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onStart() {
        this.restParams();
        this.canFocus = true;
        this.mSensorManager.registerListener(this, this.mSensor, 3);
    }

    public void onStop() {
        this.mSensorManager.unregisterListener(this, this.mSensor);
        this.canFocus = false;
    }

    public boolean isFocusLocked() {
        if (this.canFocus) {
            return this.foucsing <= 0;
        } else {
            return false;
        }
    }

    public void lockFocus() {
        this.isFocusing = true;
        --this.foucsing;
        KLog.i("SensorControler", new Object[]{"lockFocus"});
    }

    public void unlockFocus() {
        this.isFocusing = false;
        ++this.foucsing;
        KLog.i("SensorControler", new Object[]{"unlockFocus"});
    }

    public void restFoucs() {
        this.foucsing = 1;
    }

    private void restParams() {
        this.STATUE = 0;
        this.canFocusIn = false;
        this.mX = 0;
        this.mY = 0;
        this.mZ = 0;
    }

    public interface CameraFocusListener {
        void onFocus();
    }
}
