package com.slicejobs.panacamera.cameralibrary.widget;

import android.graphics.Bitmap;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.request.BasePostprocessor;

public class CutProcess extends BasePostprocessor {
    private float mBeginXPercent;
    private float mBeginYPercent;
    private float mCutWidthPercent;
    private float mCutHeightPercent;

    public CutProcess(float beginXPercent, float beginYPercent, float cutWidthPercent, float cutHeightPercent) {
        this.mBeginXPercent = beginXPercent;
        this.mBeginYPercent = beginYPercent;
        this.mCutWidthPercent = cutWidthPercent;
        this.mCutHeightPercent = cutHeightPercent;
    }

    public CloseableReference<Bitmap> process(Bitmap sourceBitmap, PlatformBitmapFactory bitmapFactory) {
        int viewWidth = sourceBitmap.getWidth();
        int viewHeight = sourceBitmap.getHeight();
        int beginx = (int)(this.mBeginXPercent * (float)viewWidth);
        int beginy = (int)(this.mBeginYPercent * (float)viewHeight);
        int width = (int)(this.mCutWidthPercent * (float)viewWidth);
        int height = (int)(this.mCutHeightPercent * (float)viewHeight);
        CloseableReference<Bitmap> bitmapRef = bitmapFactory.createBitmap(sourceBitmap, beginx, beginy, width, height);
        return CloseableReference.cloneOrNull(bitmapRef);
    }
}
