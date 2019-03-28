package com.slicejobs.panacamera.cameralibrary.helper.upload;

import android.content.Context;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PhotoUploadTaskController {
    private final Context mContext;
    private final List<PhotoUploadTask> mSelectedPhotoList;
    private final List<PhotoUploadTask> mUploadingList;

    public PhotoUploadTaskController(Context context) {
        this.mContext = context;
        this.mSelectedPhotoList = new ArrayList();
        this.mUploadingList = new ArrayList();
    }

    public boolean addUpload(PhotoUploadTask selection) {
        if (null != selection) {
            synchronized(this) {
                if (!this.mUploadingList.contains(selection)) {
                    selection.setUploadState(2);
                    this.mUploadingList.add(selection);
                    this.mSelectedPhotoList.remove(selection);
                    return true;
                }
            }
        }

        return false;
    }

    public synchronized int getActiveUploadsCount() {
        int count = 0;
        Iterator var2 = this.mUploadingList.iterator();

        while(var2.hasNext()) {
            PhotoUploadTask upload = (PhotoUploadTask)var2.next();
            if (upload.getUploadState() != 5) {
                ++count;
            }
        }

        return count;
    }

    public synchronized PhotoUploadTask getNextUpload() {
        Iterator var1 = this.mUploadingList.iterator();

        PhotoUploadTask selection;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            selection = (PhotoUploadTask)var1.next();
        } while(selection.getUploadState() != 2);

        return selection;
    }

    public synchronized List<PhotoUploadTask> getUploadingUploads() {
        return new ArrayList(this.mUploadingList);
    }

    public synchronized int getUploadsCount() {
        return this.mUploadingList.size();
    }
}
