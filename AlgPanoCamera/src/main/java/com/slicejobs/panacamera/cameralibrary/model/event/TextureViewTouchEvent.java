package com.slicejobs.panacamera.cameralibrary.model.event;

public class TextureViewTouchEvent {
    public TextureViewTouchEvent() {
    }

    public static class FocusState {
        private int focusState;

        public FocusState() {
        }

        public int getFocusState() {
            return this.focusState;
        }

        public void setFocusState(int focusState) {
            this.focusState = focusState;
        }
    }

    public static class TextureOneDrag {
        private float distance;

        public TextureOneDrag() {
        }

        public float getDistance() {
            return this.distance;
        }

        public void setDistance(float distance) {
            this.distance = distance;
        }
    }

    public static class TextureLongClick {
        private float x;
        private float y;

        public TextureLongClick() {
        }

        public float getX() {
            return this.x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return this.y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }

    public static class TextureClick {
        private float x;
        private float y;
        private float rawX;
        private float rawY;

        public TextureClick() {
        }

        public float getRawX() {
            return this.rawX;
        }

        public void setRawX(float rawX) {
            this.rawX = rawX;
        }

        public float getRawY() {
            return this.rawY;
        }

        public void setRawY(float rawY) {
            this.rawY = rawY;
        }

        public float getX() {
            return this.x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return this.y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}
