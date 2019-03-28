package com.slicejobs.panacamera.cameralibrary.model.event;

public class MultiImageState {
    public int type = -1;
    public String path;
    public int position;

    public MultiImageState(int type, int posi, String path) {
        this.type = type;
        this.path = path;
        this.position = posi;
    }
}
