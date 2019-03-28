package com.slicejobs.panacamera.cameralibrary.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class FilkerProgressBar extends ProgressBar {
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FilkerProgressBar.this.setProgress(FilkerProgressBar.this.mProgress);
            FilkerProgressBar.this.mProgress++;
        }
    };
    private int mProgress = 0;
    private int max = 100;
    private static final int TIME = 1000;

    public FilkerProgressBar(Context context) {
        super(context);
    }

    public FilkerProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FilkerProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMyProgress(int progress) {
        if (progress > 0) {
            if (progress >= 100) {
                this.max = 100;
            } else {
                this.max = progress;
            }

            (new Thread(new Runnable() {
                public void run() {
                    while(FilkerProgressBar.this.mProgress < FilkerProgressBar.this.max) {
                        SystemClock.sleep(13L);
                        FilkerProgressBar.this.mHandler.sendEmptyMessage(0);
                    }

                }
            })).start();
        }
    }

    public void onDestory() {
        if (this.mHandler != null) {
            this.mHandler.removeCallbacksAndMessages((Object)null);
        }

    }
}
