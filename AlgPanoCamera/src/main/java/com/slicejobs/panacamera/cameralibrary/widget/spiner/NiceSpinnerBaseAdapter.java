package com.slicejobs.panacamera.cameralibrary.widget.spiner;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.slicejobs.panacamera.R;

public abstract class NiceSpinnerBaseAdapter<T> extends BaseAdapter {
    private final SpinnerTextFormatter spinnerTextFormatter;
    private int textColor;
    private int backgroundSelector;
    int selectedIndex;

    NiceSpinnerBaseAdapter(Context context, int textColor, int backgroundSelector, SpinnerTextFormatter spinnerTextFormatter) {
        this.spinnerTextFormatter = spinnerTextFormatter;
        this.backgroundSelector = backgroundSelector;
        this.textColor = textColor;
    }

    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        TextView textView;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.spinner_list_item, (ViewGroup)null);
            textView = (TextView)convertView.findViewById(R.id.text_view_spinner);
            convertView.setTag(new NiceSpinnerBaseAdapter.ViewHolder(textView));
        } else {
            textView = ((NiceSpinnerBaseAdapter.ViewHolder)convertView.getTag()).textView;
        }

        textView.setText(this.spinnerTextFormatter.format(this.getItem(position).toString()));
        textView.setTextColor(this.textColor);
        return convertView;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    void setSelectedIndex(int index) {
        this.selectedIndex = index;
    }

    public abstract T getItemInDataset(int var1);

    public long getItemId(int position) {
        return (long)position;
    }

    public abstract T getItem(int var1);

    public abstract int getCount();

    static class ViewHolder {
        TextView textView;

        ViewHolder(TextView textView) {
            this.textView = textView;
        }
    }
}
