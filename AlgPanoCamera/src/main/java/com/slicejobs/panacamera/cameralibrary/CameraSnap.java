package com.slicejobs.panacamera.cameralibrary;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.slicejobs.panacamera.cameralibrary.ui.activity.CameraActivity;
import com.socks.library.KLog;

public final class CameraSnap {
    private static final String TAG = "CameraSnap";
    public static Application mApp;
    private static int MAX_MEM = 15728640;
    public static CameraSnap mCameraSnap = new CameraSnap();

    private CameraSnap() {
    }

    public static void initApplication(Application application) {
        mApp = application;
        KLog.init(false);
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(MAX_MEM, 2147483647, MAX_MEM, 2147483647, 2147483647);
        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(application).setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams).build();
        Fresco.initialize(application, config);
    }

    public static CameraSnap getInstance() {
        if (mApp == null) {
            throw new IllegalArgumentException("先调用 initApplication");
        } else {
            return mCameraSnap;
        }
    }

    public void startCamera(Activity context, int requestCode, Bundle bundle) {
        CameraActivity.start(context, requestCode, bundle);
    }
}
