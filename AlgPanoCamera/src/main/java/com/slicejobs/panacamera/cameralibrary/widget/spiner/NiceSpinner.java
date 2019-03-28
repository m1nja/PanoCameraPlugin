package com.slicejobs.panacamera.cameralibrary.widget.spiner;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Build.VERSION;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.PopupWindow.OnDismissListener;

import com.slicejobs.panacamera.R;

import java.util.List;

public class NiceSpinner extends AppCompatTextView {
    private static final int MAX_LEVEL = 10000;
    private static final int DEFAULT_ELEVATION = 16;
    private static final String INSTANCE_STATE = "instance_state";
    private static final String SELECTED_INDEX = "selected_index";
    private static final String IS_POPUP_SHOWING = "is_popup_showing";
    private static final String IS_ARROW_HIDDEN = "is_arrow_hidden";
    private static final String ARROW_DRAWABLE_RES_ID = "arrow_drawable_res_id";
    public static final int VERTICAL_OFFSET = 1;
    private int selectedIndex;
    private Drawable arrowDrawable;
    private PopupWindow popupWindow;
    private ListView listView;
    private NiceSpinnerBaseAdapter adapter;
    private OnItemClickListener onItemClickListener;
    private OnItemSelectedListener onItemSelectedListener;
    private boolean isArrowHidden;
    private int textColor;
    private int backgroundSelector;
    private int arrowDrawableTint;
    private int displayHeight;
    private int parentVerticalOffset;
    private int dropDownListPaddingBottom;
    @DrawableRes
    private int arrowDrawableResId;
    private SpinnerTextFormatter spinnerTextFormatter = new SimpleSpinnerTextFormatter();
    private SpinnerTextFormatter selectedTextFormatter = new SimpleSpinnerTextFormatter();
    private NiceSpinner.OnChangeListener mChangeListener;

    public NiceSpinner(Context context) {
        super(context);
        this.init(context, (AttributeSet)null);
    }

    public NiceSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public NiceSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instance_state", super.onSaveInstanceState());
        bundle.putInt("selected_index", this.selectedIndex);
        bundle.putBoolean("is_arrow_hidden", this.isArrowHidden);
        bundle.putInt("arrow_drawable_res_id", this.arrowDrawableResId);
        if (this.popupWindow != null) {
            bundle.putBoolean("is_popup_showing", this.popupWindow.isShowing());
        }

        return bundle;
    }

    public void onRestoreInstanceState(Parcelable savedState) {
        if (savedState instanceof Bundle) {
            Bundle bundle = (Bundle)savedState;
            this.selectedIndex = bundle.getInt("selected_index");
            if (this.adapter != null) {
                this.setTextInternal(this.adapter.getItemInDataset(this.selectedIndex).toString());
                this.adapter.setSelectedIndex(this.selectedIndex);
            }

            if (bundle.getBoolean("is_popup_showing") && this.popupWindow != null) {
                this.post(new Runnable() {
                    public void run() {
                        NiceSpinner.this.showDropDown();
                    }
                });
            }

            this.isArrowHidden = bundle.getBoolean("is_arrow_hidden", false);
            this.arrowDrawableResId = bundle.getInt("arrow_drawable_res_id");
            savedState = bundle.getParcelable("instance_state");
        }

        super.onRestoreInstanceState(savedState);
    }

    private void init(Context context, AttributeSet attrs) {
        Resources resources = this.getResources();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NiceSpinner);
        int defaultPadding = resources.getDimensionPixelSize(R.dimen.one_and_a_half_grid_unit);
        this.setGravity(8388627);
        this.setClickable(true);
        this.textColor = typedArray.getColor(R.styleable.NiceSpinner_textTint, this.getDefaultTextColor(context));
        this.setTextColor(this.textColor);
        this.listView = new ListView(context);
        this.listView.setId(this.getId());
        this.listView.setDivider((Drawable)null);
        this.listView.setItemsCanFocus(true);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= NiceSpinner.this.selectedIndex && position < NiceSpinner.this.adapter.getCount()) {
                    ++position;
                }

                NiceSpinner.this.selectedIndex = position;
                if (NiceSpinner.this.onItemClickListener != null) {
                    NiceSpinner.this.onItemClickListener.onItemClick(parent, view, position, id);
                }

                if (NiceSpinner.this.onItemSelectedListener != null) {
                    NiceSpinner.this.onItemSelectedListener.onItemSelected(parent, view, position, id);
                }

                NiceSpinner.this.adapter.setSelectedIndex(position);
                NiceSpinner.this.setTextInternal(NiceSpinner.this.adapter.getItemInDataset(position).toString());
                NiceSpinner.this.dismissDropDown();
                if (NiceSpinner.this.mChangeListener != null) {
                    NiceSpinner.this.mChangeListener.change(position);
                }

            }
        });
        this.popupWindow = new PopupWindow(context);
        this.popupWindow.setContentView(this.listView);
        this.popupWindow.setOutsideTouchable(true);
        this.popupWindow.setFocusable(true);
        if (VERSION.SDK_INT >= 21) {
            this.popupWindow.setElevation(16.0F);
            this.popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.lingmou_spinner_drawable));
        } else {
            this.popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.lingmou_drop_down_shadow));
        }

        this.popupWindow.setOnDismissListener(new OnDismissListener() {
            public void onDismiss() {
                if (!NiceSpinner.this.isArrowHidden) {
                    NiceSpinner.this.animateArrow(false);
                }

            }
        });
        this.isArrowHidden = typedArray.getBoolean(R.styleable.NiceSpinner_hideArrow, false);
        this.arrowDrawableTint = typedArray.getColor(R.styleable.NiceSpinner_arrowTint, 2147483647);
        this.arrowDrawableResId = typedArray.getResourceId(R.styleable.NiceSpinner_arrowDrawable, R.drawable.lingmou_arrow);
        this.dropDownListPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.NiceSpinner_dropDownListPaddingBottom, 0);
        typedArray.recycle();
        this.measureDisplayHeight();
    }

    private void measureDisplayHeight() {
        this.displayHeight = this.getContext().getResources().getDisplayMetrics().heightPixels;
    }

    private int getParentVerticalOffset() {
        if (this.parentVerticalOffset > 0) {
            return this.parentVerticalOffset;
        } else {
            int[] locationOnScreen = new int[2];
            this.getLocationOnScreen(locationOnScreen);
            return this.parentVerticalOffset = locationOnScreen[1];
        }
    }

    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        this.arrowDrawable = this.initArrowDrawable(this.arrowDrawableTint);
        this.setArrowDrawableOrHide(this.arrowDrawable);
    }

    private Drawable initArrowDrawable(int drawableTint) {
        Drawable drawable = ContextCompat.getDrawable(this.getContext(), this.arrowDrawableResId);
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            if (drawableTint != 2147483647 && drawableTint != 0) {
                DrawableCompat.setTint(drawable, drawableTint);
            }
        }

        return drawable;
    }

    private void setArrowDrawableOrHide(Drawable drawable) {
        if (!this.isArrowHidden && drawable != null) {
            this.setCompoundDrawablesWithIntrinsicBounds((Drawable)null, (Drawable)null, drawable, (Drawable)null);
        } else {
            this.setCompoundDrawablesWithIntrinsicBounds((Drawable)null, (Drawable)null, (Drawable)null, (Drawable)null);
        }

    }

    private int getDefaultTextColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16842806, typedValue, true);
        TypedArray typedArray = context.obtainStyledAttributes(typedValue.data, new int[]{16842806});
        int defaultTextColor = typedArray.getColor(0, -16777216);
        typedArray.recycle();
        return defaultTextColor;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public void setArrowDrawable(@DrawableRes @ColorRes int drawableId) {
        this.arrowDrawableResId = drawableId;
        this.arrowDrawable = this.initArrowDrawable(R.drawable.lingmou_arrow);
        this.setArrowDrawableOrHide(this.arrowDrawable);
    }

    public void setArrowDrawable(Drawable drawable) {
        this.arrowDrawable = drawable;
        this.setArrowDrawableOrHide(this.arrowDrawable);
    }

    public void setTextInternal(String text) {
        if (this.selectedTextFormatter != null) {
            this.setText(this.selectedTextFormatter.format(text));
        } else {
            this.setText(text);
        }

    }

    public void setSelectedIndex(int position) {
        if (this.adapter != null) {
            if (position < 0 || position > this.adapter.getCount()) {
                throw new IllegalArgumentException("Position must be lower than adapter count!");
            }

            this.adapter.setSelectedIndex(position);
            this.selectedIndex = position;
            this.setTextInternal(this.adapter.getItemInDataset(position).toString());
        }

    }

    public void addOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public <T> void attachDataSource(List<T> list) {
        this.adapter = new NiceSpinnerAdapter(this.getContext(), list, this.textColor, this.backgroundSelector, this.spinnerTextFormatter);
        this.setAdapterInternal(this.adapter);
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = new NiceSpinnerAdapterWrapper(this.getContext(), adapter, this.textColor, this.backgroundSelector, this.spinnerTextFormatter);
        this.setAdapterInternal(this.adapter);
    }

    private void setAdapterInternal(NiceSpinnerBaseAdapter adapter) {
        this.selectedIndex = 0;
        this.listView.setAdapter(adapter);
        this.setTextInternal(adapter.getItemInDataset(this.selectedIndex).toString());
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.isEnabled() && event.getAction() == 1) {
            if (!this.popupWindow.isShowing()) {
                this.showDropDown();
            } else {
                this.dismissDropDown();
            }
        }

        return super.onTouchEvent(event);
    }

    private void animateArrow(boolean shouldRotateUp) {
        int start = shouldRotateUp ? 0 : 10000;
        int end = shouldRotateUp ? 10000 : 0;
        ObjectAnimator animator = ObjectAnimator.ofInt(this.arrowDrawable, "level", new int[]{start, end});
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.start();
    }

    public void dismissDropDown() {
        if (!this.isArrowHidden) {
            this.animateArrow(false);
        }

        this.popupWindow.dismiss();
    }

    public void showDropDown() {
        if (!this.isArrowHidden) {
            this.animateArrow(true);
        }

        this.measurePopUpDimension();
        this.popupWindow.showAsDropDown(this);
    }

    private void measurePopUpDimension() {
        int widthSpec = MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(this.displayHeight - this.getParentVerticalOffset() - this.getMeasuredHeight(), View.MeasureSpec.AT_MOST);
        this.listView.measure(widthSpec, heightSpec);
        this.popupWindow.setWidth(this.listView.getMeasuredWidth());
        this.popupWindow.setHeight(this.listView.getMeasuredHeight() - this.dropDownListPaddingBottom);
    }

    public void setTintColor(@ColorRes int resId) {
        if (this.arrowDrawable != null && !this.isArrowHidden) {
            DrawableCompat.setTint(this.arrowDrawable, ContextCompat.getColor(this.getContext(), resId));
        }

    }

    public void hideArrow() {
        this.isArrowHidden = true;
        this.setArrowDrawableOrHide(this.arrowDrawable);
    }

    public void showArrow() {
        this.isArrowHidden = false;
        this.setArrowDrawableOrHide(this.arrowDrawable);
    }

    public boolean isArrowHidden() {
        return this.isArrowHidden;
    }

    public void setDropDownListPaddingBottom(int paddingBottom) {
        this.dropDownListPaddingBottom = paddingBottom;
    }

    public int getDropDownListPaddingBottom() {
        return this.dropDownListPaddingBottom;
    }

    public void setSpinnerTextFormatter(SpinnerTextFormatter spinnerTextFormatter) {
        this.spinnerTextFormatter = spinnerTextFormatter;
    }

    public void setSelectedTextFormatter(SpinnerTextFormatter textFormatter) {
        this.selectedTextFormatter = textFormatter;
    }

    public void setOnChangeListener(NiceSpinner.OnChangeListener listener) {
        this.mChangeListener = listener;
    }

    public interface OnChangeListener {
        void change(int var1);
    }
}
