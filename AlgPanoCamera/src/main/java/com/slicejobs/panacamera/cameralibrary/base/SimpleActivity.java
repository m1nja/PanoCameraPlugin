package com.slicejobs.panacamera.cameralibrary.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

public abstract class SimpleActivity extends Activity {
    protected Activity mContext;

    public SimpleActivity() {
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getLayout());
        this.mContext = this;
        this.initEventAndData();
    }

    protected void setToolBar(Toolbar toolBar) {
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    protected abstract int getLayout();

    protected abstract void initEventAndData();
}
