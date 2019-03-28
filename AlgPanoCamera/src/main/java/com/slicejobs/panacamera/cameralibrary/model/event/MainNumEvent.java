package com.slicejobs.panacamera.cameralibrary.model.event;

public class MainNumEvent {
    public String todayNum;
    public String saleNum;
    public String lineNum;

    public MainNumEvent(String todayNum, String saleNum, String lineNum) {
        this.todayNum = todayNum;
        this.saleNum = saleNum;
        this.lineNum = lineNum;
    }
}
