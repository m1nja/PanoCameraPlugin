package com.slicejobs.panacamera.cameralibrary.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.slicejobs.panacamera.R;
import com.slicejobs.panacamera.cameralibrary.base.SimpleActivity;
import com.slicejobs.panacamera.cameralibrary.helper.RxBus;
import com.slicejobs.panacamera.cameralibrary.model.event.MultiImageState;
import com.slicejobs.panacamera.cameralibrary.ui.adapter.ImagePreviewAdapter;
import com.slicejobs.panacamera.cameralibrary.widget.HackyViewPager;
import com.socks.library.KLog;
import java.util.ArrayList;

public class ImagePreviewActivity extends SimpleActivity implements ImagePreviewAdapter.SincleClick {
    private HackyViewPager viewPager;
    private RelativeLayout rootView;
    private RelativeLayout rlTitleBar;
    private ImageView imageBack;
    private TextView tvTitle;
    private static final int REQUEST_CAMERA = 1002;
    private ArrayList<String> mImagesList;
    private int mIndex = 0;
    private ImagePreviewAdapter mImagePreviewAdapter;
    private boolean isShowBar = true;
    private int mOrinentation = -1;
    private int type = -1;
    private AnimationSet hideAnimationSet;
    private AnimationSet showAnimationSet;
    private int mosaicType = 1;

    public ImagePreviewActivity() {
    }

    public static void start(Activity context, ArrayList<String> images, int index, int requestCode, int type, int mOrinentation, int mosaicType) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        bundle.putStringArrayList("image_preview_lists", images);
        bundle.putInt("type", type);
        bundle.putInt("orientation", mOrinentation);
        bundle.putInt("mosaicType", mosaicType);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
    }

    protected int getLayout() {
        return R.layout.activity_image_preview;
    }

    protected void initEventAndData() {
        this.initView();
        Bundle bundle = this.getIntent().getExtras();
        this.mosaicType = bundle.getInt("mosaicType", 1);
        this.initAnim();
        this.mImagesList = bundle.getStringArrayList("image_preview_lists");
        this.mIndex = bundle.getInt("index", 0);
        this.type = bundle.getInt("type", -1);
        this.mImagePreviewAdapter = new ImagePreviewAdapter(this.mImagesList, this.mContext, this.type);
        this.mImagePreviewAdapter.setSingleClick(this);
        this.viewPager.setAdapter(this.mImagePreviewAdapter);
        this.viewPager.setCurrentItem(this.mIndex);
        this.viewPager.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ImagePreviewActivity.this.mIndex = position;
                ImagePreviewActivity.this.updateTitle();
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                KLog.d("liujiandong", new Object[]{"当前是第几页"});
            }
        });
        this.updateTitle();
        this.imageBack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ImagePreviewActivity.this.finishActivity();
            }
        });
    }

    private void initView() {
        this.viewPager = (HackyViewPager)this.findViewById(R.id.viewPager);
        this.rootView = (RelativeLayout)this.findViewById(R.id.rootView);
        this.rlTitleBar = (RelativeLayout)this.findViewById(R.id.rlTitleBar);
        this.imageBack = (ImageView)this.findViewById(R.id.imageBack);
        this.tvTitle = (TextView)this.findViewById(R.id.tvTitle);
    }

    private void updateTitle() {
        this.tvTitle.setText(String.format(this.getString(R.string.select), this.mIndex + 1, this.mImagesList.size()));
    }

    public void click() {
        this.isShowBar = !this.isShowBar;
        this.showTitleBar(this.isShowBar);
    }

    private void initAnim() {
        this.hideAnimationSet = (AnimationSet)AnimationUtils.loadAnimation(this.mContext, R.anim.slicejobs_slide_top_out);
        this.showAnimationSet = (AnimationSet)AnimationUtils.loadAnimation(this.mContext, R.anim.slicejobs_slide_top_in);
    }

    private void showTitleBar(boolean isShow) {
        if (isShow) {
            this.rlTitleBar.startAnimation(this.showAnimationSet);
        } else {
            this.rlTitleBar.startAnimation(this.hideAnimationSet);
        }

        this.rlTitleBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch(requestCode) {
                case 1002:
                    String path = data.getStringExtra("image_picture");
                    this.mImagesList.set(this.viewPager.getCurrentItem(), path);
                    this.mImagePreviewAdapter.setData(this.mImagesList);
                    this.mImagePreviewAdapter.notifyDataSetChanged();
                    RxBus.getInstance().post(new MultiImageState(1, this.viewPager.getCurrentItem(), path));
                    this.finish();
            }
        }

    }

    public void onBackPressed() {
        this.finishActivity();
    }

    private void finishActivity() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("image_preview_lists", this.mImagesList);
        this.setResult(-1, intent);
        this.finish();
    }
}
