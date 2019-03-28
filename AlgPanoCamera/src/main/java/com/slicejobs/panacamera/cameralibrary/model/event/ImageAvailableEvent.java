package com.slicejobs.panacamera.cameralibrary.model.event;

import android.media.ImageReader;

public class ImageAvailableEvent {
    public ImageAvailableEvent() {
    }

    public static class ImagePathAvailable {
        private String imagePath;

        public ImagePathAvailable() {
        }

        public String getImagePath() {
            return this.imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }
    }

    public static class ImageReaderAvailable {
        private ImageReader imageReader;

        public ImageReaderAvailable() {
        }

        public ImageReader getImageReader() {
            return this.imageReader;
        }

        public void setImageReader(ImageReader imageReader) {
            this.imageReader = imageReader;
        }
    }
}
