package com.slicejobs.panacamera.cameralibrary.helper.upload;


import com.slicejobs.panacamera.cameralibrary.helper.ToastUtil;

public class PhotoUploadTask {
    public static final int STATE_UPLOAD_COMPLETED = 5;
    public static final int STATE_UPLOAD_ERROR = 4;
    public static final int STATE_UPLOAD_IN_PROGRESS = 3;
    public static final int STATE_UPLOAD_WAITING = 2;
    public static final int STATE_SELECTED = 1;
    public static final int STATE_NONE = 0;
    private int mState;
    private String name;

    public PhotoUploadTask() {
    }

    public int getUploadState() {
        return this.mState;
    }

    public void setUploadState(int state) {
        if (this.mState != state) {
            this.mState = state;
            switch(state) {
                case 1:
                case 2:
                case 3:
                default:
                    break;
                case 4:
                    ToastUtil.shortShow("上传出错");
                    break;
                case 5:
                    ToastUtil.shortShow("上传完成");
            }

            this.notifyUploadStateListener();
        }

    }

    private void notifyUploadStateListener() {
    }
}
