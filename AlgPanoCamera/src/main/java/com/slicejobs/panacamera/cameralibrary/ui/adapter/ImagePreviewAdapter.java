package com.slicejobs.panacamera.cameralibrary.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.slicejobs.panacamera.R;
import com.slicejobs.panacamera.cameralibrary.fresco.zoomable.ZoomableDraweeView;

import java.util.ArrayList;

public class ImagePreviewAdapter extends PagerAdapter {
    private ArrayList<String> mImagesList;
    private Context mContext;
    private View mCurrentView;
    private ScaleType mType;
    private ImagePreviewAdapter.SincleClick mClick;

    public ImagePreviewAdapter(@NonNull ArrayList<String> imagesList, Context mContext, int type) {
        this.mImagesList = imagesList;
        this.mContext = mContext;
        this.mType = ScaleType.FIT_CENTER;
    }

    public void setData(@NonNull ArrayList<String> imagesList) {
        this.mImagesList = imagesList;
    }

    public int getCount() {
        return this.mImagesList.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        this.mCurrentView = (View)object;
    }

    public View getPrimaryItem() {
        return this.mCurrentView;
    }

    public ZoomableDraweeView getPrimaryImageView() {
        return (ZoomableDraweeView)this.mCurrentView.findViewById(R.id.simpleDraweeView);
    }

    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.item_pre_photo_view, container, false);
        ZoomableDraweeView simpleDraweeView = (ZoomableDraweeView)view.findViewById(R.id.simpleDraweeView);
        DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(Uri.parse((String)this.mImagesList.get(position))).build();
        simpleDraweeView.setController(controller);
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(container.getResources());
        GenericDraweeHierarchy hierarchy = builder.setFadeDuration(300).setActualImageScaleType(this.mType).build();
        simpleDraweeView.setHierarchy(hierarchy);
        simpleDraweeView.setTapListener(new SimpleOnGestureListener() {
            public boolean onSingleTapUp(MotionEvent e) {
                if (ImagePreviewAdapter.this.mClick != null) {
                    ImagePreviewAdapter.this.mClick.click();
                }

                return super.onSingleTapUp(e);
            }
        });
        container.addView(view);
        return view;
    }

    public int getItemPosition(Object object) {
        return -2;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    public void setSingleClick(ImagePreviewAdapter.SincleClick click) {
        this.mClick = click;
    }

    public interface SincleClick {
        void click();
    }
}
