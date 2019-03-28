package com.slicejobs.panacamera.cameralibrary.model.event;

public class UploadProgressEvent {
    private long id;
    private String imageUrl;
    private int progress;

    public UploadProgressEvent() {
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
