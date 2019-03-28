package com.slicejobs.panacamera.cameralibrary.widget.spiner;

import android.content.Context;
import android.widget.ListAdapter;

public class NiceSpinnerAdapterWrapper extends NiceSpinnerBaseAdapter {
    private final ListAdapter baseAdapter;

    NiceSpinnerAdapterWrapper(Context context, ListAdapter toWrap, int textColor, int backgroundSelector, SpinnerTextFormatter spinnerTextFormatter) {
        super(context, textColor, backgroundSelector, spinnerTextFormatter);
        this.baseAdapter = toWrap;
    }

    public int getCount() {
        return this.baseAdapter.getCount() - 1;
    }

    public Object getItem(int position) {
        return this.baseAdapter.getItem(position >= this.selectedIndex ? position + 1 : position);
    }

    public Object getItemInDataset(int position) {
        return this.baseAdapter.getItem(position);
    }
}
