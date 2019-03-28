package com.slicejobs.panacamera.cameralibrary.widget.tagview;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;

public class FlowTagView extends ViewGroup {
    private int mLineSpacing;
    private int mTagSpacing;
    private BaseAdapter mAdapter;
    private FlowTagView.TagItemClickListener mListener;
    private FlowTagView.DataChangeObserver mObserver;

    public FlowTagView(Context context) {
        super(context);
        this.init(context, (AttributeSet)null, 0);
    }

    public FlowTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs, 0);
    }

    public FlowTagView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        FlowTagConfig config = new FlowTagConfig(context, attrs);
        this.mLineSpacing = config.getLineSpacing();
        this.mTagSpacing = config.getTagSpacing();
    }

    private void drawLayout() {
        if (this.mAdapter != null && this.mAdapter.getCount() != 0) {
            this.removeAllViews();

            for(int i = 0; i < this.mAdapter.getCount(); ++i) {
                View view = this.mAdapter.getView(i, (View)null, (ViewGroup)null);
                final int finalI = i;
                view.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if (FlowTagView.this.mListener != null) {
                            FlowTagView.this.mListener.itemClick(finalI);
                        }

                    }
                });
                this.addView(view);
            }

        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wantHeight = 0;
        int wantWidth = resolveSize(0, widthMeasureSpec);
        int paddingLeft = this.getPaddingLeft();
        int paddingRight = this.getPaddingRight();
        int paddingTop = this.getPaddingTop();
        int paddingBottom = this.getPaddingBottom();
        int childLeft = paddingLeft;
        int childTop = paddingTop;
        int lineHeight = 0;

        for(int i = 0; i < this.getChildCount(); ++i) {
            View childView = this.getChildAt(i);
            LayoutParams params = childView.getLayoutParams();
            childView.measure(getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, params.width), getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, params.height));
            int childHeight = childView.getMeasuredHeight();
            int childWidth = childView.getMeasuredWidth();
            lineHeight = Math.max(childHeight, lineHeight);
            if (childLeft + childWidth + paddingRight > wantWidth) {
                childLeft = paddingLeft;
                childTop += this.mLineSpacing + childHeight;
                lineHeight = childHeight;
            }

            childLeft += childWidth + this.mTagSpacing;
        }

        wantHeight = wantHeight + childTop + lineHeight + paddingBottom;
        this.setMeasuredDimension(wantWidth, resolveSize(wantHeight, heightMeasureSpec));
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int paddingLeft = this.getPaddingLeft();
        int paddingTop = this.getPaddingTop();
        int paddingRight = this.getPaddingRight();
        int childLeft = paddingLeft;
        int childTop = paddingTop;
        int lineHeight = 0;

        for(int i = 0; i < this.getChildCount(); ++i) {
            View childView = this.getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                int childWidth = childView.getMeasuredWidth();
                int childHeight = childView.getMeasuredHeight();
                lineHeight = Math.max(childHeight, lineHeight);
                if (childLeft + childWidth + paddingRight > width) {
                    childLeft = paddingLeft;
                    childTop += this.mLineSpacing + lineHeight;
                    lineHeight = childHeight;
                }

                childView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
                childLeft += childWidth + this.mTagSpacing;
            }
        }

    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(this.getContext(), attrs);
    }

    public void setAdapter(BaseAdapter adapter) {
        if (this.mAdapter == null) {
            this.mAdapter = adapter;
            if (this.mObserver == null) {
                this.mObserver = new FlowTagView.DataChangeObserver();
                this.mAdapter.registerDataSetObserver(this.mObserver);
            }

            this.drawLayout();
        }

    }

    public void setItemClickListener(FlowTagView.TagItemClickListener mListener) {
        this.mListener = mListener;
    }

    class DataChangeObserver extends DataSetObserver {
        DataChangeObserver() {
        }

        public void onChanged() {
            FlowTagView.this.drawLayout();
        }

        public void onInvalidated() {
            super.onInvalidated();
        }
    }

    public interface TagItemClickListener {
        void itemClick(int var1);
    }
}
