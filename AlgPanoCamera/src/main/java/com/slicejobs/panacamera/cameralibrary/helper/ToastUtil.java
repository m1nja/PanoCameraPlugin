package com.slicejobs.panacamera.cameralibrary.helper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.slicejobs.panacamera.R;
import com.slicejobs.panacamera.cameralibrary.CameraSnap;

public class ToastUtil {
    static ToastUtil td;
    Context context;
    Toast toast;
    String msg;

    public static void show(int resId) {
        show(CameraSnap.mApp.getString(resId));
    }

    public static void show(String msg) {
        if (td == null) {
            td = new ToastUtil(CameraSnap.mApp);
        }

        td.setText(msg);
        td.create().show();
    }

    public static void shortShow(String msg) {
        if (td == null) {
            td = new ToastUtil(CameraSnap.mApp);
        }

        td.setText(msg);
        td.createShort().show();
    }

    public ToastUtil(Context context) {
        this.context = context;
    }

    public Toast create() {
        View contentView = View.inflate(this.context, R.layout.dialog_toast, (ViewGroup)null);
        TextView tvMsg = (TextView)contentView.findViewById(R.id.tv_toast_msg);
        this.toast = new Toast(this.context);
        this.toast.setView(contentView);
        this.toast.setGravity(17, 0, 0);
        this.toast.setDuration(Toast.LENGTH_LONG);
        tvMsg.setText(this.msg);
        return this.toast;
    }

    public Toast createShort() {
        View contentView = View.inflate(this.context, R.layout.dialog_toast, (ViewGroup)null);
        TextView tvMsg = (TextView)contentView.findViewById(R.id.tv_toast_msg);
        this.toast = new Toast(this.context);
        this.toast.setView(contentView);
        this.toast.setGravity(17, 0, 0);
        this.toast.setDuration(Toast.LENGTH_SHORT);
        tvMsg.setText(this.msg);
        return this.toast;
    }

    public void show() {
        if (this.toast != null) {
            this.toast.show();
        }

    }

    public void setText(String text) {
        this.msg = text;
    }
}
