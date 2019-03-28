package com.slicejobs.panacamera.cameralibrary.widget.spiner;

import android.content.Context;
import java.util.List;

public class NiceSpinnerAdapter<T> extends NiceSpinnerBaseAdapter {
    private final List<T> items;

    NiceSpinnerAdapter(Context context, List<T> items, int textColor, int backgroundSelector, SpinnerTextFormatter spinnerTextFormatter) {
        super(context, textColor, backgroundSelector, spinnerTextFormatter);
        this.items = items;
    }

    public int getCount() {
        return this.items.size() - 1;
    }

    public T getItem(int position) {
        return position >= this.selectedIndex ? this.items.get(position + 1) : this.items.get(position);
    }

    public T getItemInDataset(int position) {
        return this.items.get(position);
    }
}
