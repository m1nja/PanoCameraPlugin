package com.slicejobs.panacamera.cameralibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import java.util.ArrayList;
import com.slicejobs.panacamera.R;

public class MultipleStatusView extends RelativeLayout {
    private static final String TAG = "MultipleStatusView";
    private static final LayoutParams DEFAULT_LAYOUT_PARAMS = new LayoutParams(-1, -1);
    public static final int STATUS_CONTENT = 0;
    public static final int STATUS_LOADING = 1;
    public static final int STATUS_EMPTY = 2;
    public static final int STATUS_ERROR = 3;
    public static final int STATUS_NO_NETWORK = 4;
    private static final int NULL_RESOURCE_ID = -1;
    private View mEmptyView;
    private View mErrorView;
    private View mLoadingView;
    private View mNoNetworkView;
    private View mContentView;
    private int mEmptyViewResId;
    private int mErrorViewResId;
    private int mLoadingViewResId;
    private int mNoNetworkViewResId;
    private int mContentViewResId;
    private int mViewStatus;
    private LayoutInflater mInflater;
    private OnClickListener mOnRetryClickListener;
    private ArrayList<Integer> mOtherIds;

    public MultipleStatusView(Context context) {
        this(context, (AttributeSet)null);
    }

    public MultipleStatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultipleStatusView, defStyleAttr, 0);
        this.mEmptyViewResId = a.getResourceId(R.styleable.MultipleStatusView_emptyView, R.layout.multi_empty_view);
        this.mErrorViewResId = a.getResourceId(R.styleable.MultipleStatusView_errorView, R.layout.multi_error_view);
        this.mLoadingViewResId = a.getResourceId(R.styleable.MultipleStatusView_loadingView, R.layout.multi_loading_view);
        this.mNoNetworkViewResId = a.getResourceId(R.styleable.MultipleStatusView_noNetworkView, R.layout.multi_no_net_view);
        this.mContentViewResId = a.getResourceId(R.styleable.MultipleStatusView_contentView, -1);
        a.recycle();
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mInflater = LayoutInflater.from(this.getContext());
        this.mOtherIds = new ArrayList();
        this.showContent();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.clear(this.mEmptyView, this.mLoadingView, this.mErrorView, this.mNoNetworkView);
        if (null != this.mOtherIds) {
            this.mOtherIds.clear();
        }

        if (null != this.mOnRetryClickListener) {
            this.mOnRetryClickListener = null;
        }

        this.mInflater = null;
    }

    public int getViewStatus() {
        return this.mViewStatus;
    }

    public void setOnRetryClickListener(OnClickListener onRetryClickListener) {
        this.mOnRetryClickListener = onRetryClickListener;
    }

    public final void showEmpty() {
        this.showEmpty(this.mEmptyViewResId, DEFAULT_LAYOUT_PARAMS);
    }

    public final void showEmpty(int layoutId, android.view.ViewGroup.LayoutParams layoutParams) {
        this.showEmpty(this.inflateView(layoutId), layoutParams);
    }

    public final void showEmpty(View view, android.view.ViewGroup.LayoutParams layoutParams) {
        this.checkNull(view, "Empty view is null!");
        this.mViewStatus = 2;
        if (null == this.mEmptyView) {
            this.mEmptyView = view;
            View emptyRetryView = this.mEmptyView.findViewById(R.id.empty_retry_view);
            if (null != this.mOnRetryClickListener && null != emptyRetryView) {
                emptyRetryView.setOnClickListener(this.mOnRetryClickListener);
            }

            this.mOtherIds.add(this.mEmptyView.getId());
            this.addView(this.mEmptyView, 0, layoutParams);
        }

        this.showViewById(this.mEmptyView.getId());
    }

    public final void showError() {
        this.showError(this.mErrorViewResId, DEFAULT_LAYOUT_PARAMS);
    }

    public final void showError(int layoutId, android.view.ViewGroup.LayoutParams layoutParams) {
        this.showError(this.inflateView(layoutId), layoutParams);
    }

    public final void showError(View view, android.view.ViewGroup.LayoutParams layoutParams) {
        this.checkNull(view, "Error view is null!");
        this.mViewStatus = 3;
        if (null == this.mErrorView) {
            this.mErrorView = view;
            View errorRetryView = this.mErrorView.findViewById(R.id.error_retry_view);
            if (null != this.mOnRetryClickListener && null != errorRetryView) {
                errorRetryView.setOnClickListener(this.mOnRetryClickListener);
            }

            this.mOtherIds.add(this.mErrorView.getId());
            this.addView(this.mErrorView, 0, layoutParams);
        }

        this.showViewById(this.mErrorView.getId());
    }

    public final void showLoading() {
        this.showLoading(this.mLoadingViewResId, DEFAULT_LAYOUT_PARAMS);
    }

    public final void showLoading(int layoutId, android.view.ViewGroup.LayoutParams layoutParams) {
        this.showLoading(this.inflateView(layoutId), layoutParams);
    }

    public final void showLoading(View view, android.view.ViewGroup.LayoutParams layoutParams) {
        this.checkNull(view, "Loading view is null!");
        this.mViewStatus = 1;
        if (null == this.mLoadingView) {
            this.mLoadingView = view;
            this.mOtherIds.add(this.mLoadingView.getId());
            this.addView(this.mLoadingView, 0, layoutParams);
        }

        this.showViewById(this.mLoadingView.getId());
    }

    public void setLoadingTips(String tips) {
        TextView tvTips = (TextView)this.mLoadingView.findViewById(R.id.tvTips);
        tvTips.setText(tips);
    }

    public final void showNoNetwork() {
        this.showNoNetwork(this.mNoNetworkViewResId, DEFAULT_LAYOUT_PARAMS);
    }

    public final void showNoNetwork(int layoutId, android.view.ViewGroup.LayoutParams layoutParams) {
        this.showNoNetwork(this.inflateView(layoutId), layoutParams);
    }

    public final void showNoNetwork(View view, android.view.ViewGroup.LayoutParams layoutParams) {
        this.checkNull(view, "No network view is null!");
        this.mViewStatus = 4;
        if (null == this.mNoNetworkView) {
            this.mNoNetworkView = view;
            View noNetworkRetryView = this.mNoNetworkView.findViewById(R.id.no_network_retry_view);
            if (null != this.mOnRetryClickListener && null != noNetworkRetryView) {
                noNetworkRetryView.setOnClickListener(this.mOnRetryClickListener);
            }

            this.mOtherIds.add(this.mNoNetworkView.getId());
            this.addView(this.mNoNetworkView, 0, layoutParams);
        }

        this.showViewById(this.mNoNetworkView.getId());
    }

    public final void showContent() {
        this.mViewStatus = 0;
        if (null == this.mContentView && this.mContentViewResId != -1) {
            this.mContentView = this.mInflater.inflate(this.mContentViewResId, (ViewGroup)null);
            this.addView(this.mContentView, 0, DEFAULT_LAYOUT_PARAMS);
        }

        this.showContentView();
    }

    private void showContentView() {
        int childCount = this.getChildCount();

        for(int i = 0; i < childCount; ++i) {
            View view = this.getChildAt(i);
            view.setVisibility(this.mOtherIds.contains(view.getId()) ? GONE : VISIBLE);
        }

    }

    private View inflateView(int layoutId) {
        return this.mInflater.inflate(layoutId, (ViewGroup)null);
    }

    private void showViewById(int viewId) {
        int childCount = this.getChildCount();

        for(int i = 0; i < childCount; ++i) {
            View view = this.getChildAt(i);
            view.setVisibility(view.getId() == viewId ? VISIBLE : GONE);
        }

    }

    private void checkNull(Object object, String hint) {
        if (null == object) {
            throw new NullPointerException(hint);
        }
    }

    private void clear(View... views) {
        if (null != views) {
            try {
                View[] var2 = views;
                int var3 = views.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    View view = var2[var4];
                    if (null != view) {
                        this.removeView(view);
                    }
                }
            } catch (Exception var6) {
                ;
            }

        }
    }
}
