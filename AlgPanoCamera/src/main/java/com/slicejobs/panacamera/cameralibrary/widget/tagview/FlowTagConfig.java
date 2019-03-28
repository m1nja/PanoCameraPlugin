package com.slicejobs.panacamera.cameralibrary.widget.tagview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.slicejobs.panacamera.R;

public class FlowTagConfig {
    private static final int DEFAULT_LINE_SPACING = 5;
    private static final int DEFAULT_TAG_SPACING = 10;
    private static final int DEFAULT_FIXED_COLUMN_SIZE = 3;
    private int lineSpacing;
    private int tagSpacing;
    private int columnSize;
    private boolean isFixed;

    public FlowTagConfig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowTagView);

        try {
            this.lineSpacing = a.getDimensionPixelSize(R.styleable.FlowTagView_lineSpacing, 5);
            this.tagSpacing = a.getDimensionPixelSize(R.styleable.FlowTagView_tagSpacing, 10);
            this.columnSize = a.getInteger(R.styleable.FlowTagView_columnSize, 3);
            this.isFixed = a.getBoolean(R.styleable.FlowTagView_isFixed, false);
        } finally {
            a.recycle();
        }

    }

    public int getLineSpacing() {
        return this.lineSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public int getTagSpacing() {
        return this.tagSpacing;
    }

    public void setTagSpacing(int tagSpacing) {
        this.tagSpacing = tagSpacing;
    }

    public int getColumnSize() {
        return this.columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public boolean isFixed() {
        return this.isFixed;
    }

    public void setIsFixed(boolean isFixed) {
        this.isFixed = isFixed;
    }
}
