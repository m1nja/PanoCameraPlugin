package com.slicejobs.panacamera.cameralibrary.model.event;

public class TakePictureResult {
    public String mPath;
    public boolean isAdd;

    public TakePictureResult(String path, boolean isAdd) {
        this.mPath = path;
        this.isAdd = isAdd;
    }
}
