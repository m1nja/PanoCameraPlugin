package com.slicejobs.panacamera.cameralibrary.model.event;

public class UploadImageState {
    public boolean isSuccess;
    public long imageId;
    public String taskId;

    public UploadImageState(boolean isSuccess, long imageId, String taskId) {
        this.isSuccess = isSuccess;
        this.imageId = imageId;
        this.taskId = taskId;
    }
}