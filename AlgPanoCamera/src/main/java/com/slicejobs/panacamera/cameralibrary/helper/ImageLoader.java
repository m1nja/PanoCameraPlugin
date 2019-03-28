package com.slicejobs.panacamera.cameralibrary.helper;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.slicejobs.panacamera.cameralibrary.widget.CutProcess;
import com.socks.library.KLog;

public class ImageLoader {
    private static final String TAG = "ImageLoader";

    public ImageLoader() {
    }

    public static void onDisplayImage(Context context, SimpleDraweeView draweeView, String url) {
        if (!TextUtils.isEmpty(url)) {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).setProgressiveRenderingEnabled(true).setAutoRotateEnabled(true).build();
            PipelineDraweeController controller = (PipelineDraweeController)((PipelineDraweeControllerBuilder)((PipelineDraweeControllerBuilder)((PipelineDraweeControllerBuilder)Fresco.newDraweeControllerBuilder().setImageRequest(request)).setOldController(draweeView.getController())).setAutoPlayAnimations(true)).build();
            draweeView.setController(controller);
        }
    }

    public static void onDisplayImage(Context context, SimpleDraweeView draweeView, String url, int width, int height) {
        if (!TextUtils.isEmpty(url)) {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).setResizeOptions(new ResizeOptions(width, height)).setProgressiveRenderingEnabled(true).setAutoRotateEnabled(true).build();
            PipelineDraweeController controller = (PipelineDraweeController)((PipelineDraweeControllerBuilder)((PipelineDraweeControllerBuilder)((PipelineDraweeControllerBuilder)Fresco.newDraweeControllerBuilder().setImageRequest(request)).setOldController(draweeView.getController())).setAutoPlayAnimations(true)).build();
            draweeView.setController(controller);
        }
    }

    public static void getCutedPic(SimpleDraweeView simpleDraweeView, String picUrl, float widthPer) {
        KLog.d("liujiandong", new Object[]{widthPer + "..............."});
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(picUrl)).setPostprocessor(new CutProcess(1.0F - widthPer, 0.0F, widthPer, 1.0F)).build();
        PipelineDraweeController pipelineDraweeController = (PipelineDraweeController)((PipelineDraweeControllerBuilder)((PipelineDraweeControllerBuilder)((PipelineDraweeControllerBuilder)Fresco.newDraweeControllerBuilder().setImageRequest(imageRequest)).setOldController(simpleDraweeView.getController())).setTapToRetryEnabled(false)).build();
        simpleDraweeView.setController(pipelineDraweeController);
    }
}
