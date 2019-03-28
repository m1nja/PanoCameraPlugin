package com.slicejobs.panacamera.cameralibrary.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import com.socks.library.KLog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class CameraUtil {
    private static final String TAG = "CameraUtil";
    public static float scale;
    public static int densityDpi;
    public static float fontScale;
    public static int screenWidth;
    public static int screenHeight;
    private CameraUtil.CameraDropSizeComparator dropSizeComparator = new CameraUtil.CameraDropSizeComparator();
    private CameraUtil.CameraAscendSizeComparator ascendSizeComparator = new CameraUtil.CameraAscendSizeComparator();
    private static CameraUtil myCamPara = null;
    private static final int MIN_PREVIEW_PIXELS = 777600;
    private static final double MAX_ASPECT_DISTORTION = 0.15D;

    public static void init(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        scale = dm.density;
        densityDpi = dm.densityDpi;
        fontScale = dm.scaledDensity;
        if (dm.widthPixels < dm.heightPixels) {
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
        } else {
            screenWidth = dm.heightPixels;
            screenHeight = dm.widthPixels;
        }

        Log.e("screen", "屏幕宽度是:" + screenWidth + " 高度是:" + screenHeight + " dp:" + scale + " fontScale:" + fontScale);
    }

    private CameraUtil() {
    }

    public static CameraUtil getInstance() {
        if (myCamPara == null) {
            myCamPara = new CameraUtil();
            return myCamPara;
        } else {
            return myCamPara;
        }
    }

    public void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch(rotation) {
            case 0:
                degrees = 0;
                break;
            case 1:
                degrees = 90;
                break;
            case 2:
                degrees = 180;
                break;
            case 3:
                degrees = 270;
        }

        int result;
        if (info.facing == 1) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);
    }

    public Bitmap setTakePicktrueOrientation(int id, Bitmap bitmap) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(id, info);
        bitmap = this.rotaingImageView(id, info.orientation, bitmap);
        return bitmap;
    }

    public Bitmap rotaingImageView(int id, int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float)angle);
        if (id == 1) {
            matrix.postScale(-1.0F, 1.0F);
        }

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    public Size getPropPreviewSize(List<Size> list, int minWidth) {
        Collections.sort(list, this.ascendSizeComparator);
        int i = 0;

        for(Iterator var4 = list.iterator(); var4.hasNext(); ++i) {
            Size s = (Size)var4.next();
            if (s.width >= minWidth) {
                break;
            }
        }

        if (i == list.size()) {
            i = 0;
        }

        return (Size)list.get(i);
    }

    public Size getPropPictureSize(List<Size> list, int minWidth) {
        Collections.sort(list, this.ascendSizeComparator);
        int i = 0;

        for(Iterator var4 = list.iterator(); var4.hasNext(); ++i) {
            Size s = (Size)var4.next();
            KLog.d("CameraUtil", new Object[]{s.width, s.height});
            if (s.width >= minWidth && this.equalRate(s, 1.33F)) {
                KLog.d("CameraUtil", new Object[]{"最终设置图片尺寸", s.width, s.height});
                break;
            }
        }

        if (i == list.size()) {
            i = 0;
        }

        return (Size)list.get(i);
    }

    public boolean equalRate(Size s, float rate) {
        float r = (float)s.width / (float)s.height;
        return (double)Math.abs(r - rate) <= 0.2D;
    }

    public void printSupportPreviewSize(Parameters params) {
        List<Size> previewSizes = params.getSupportedPreviewSizes();

        for(int i = 0; i < previewSizes.size(); ++i) {
            Size var4 = (Size)previewSizes.get(i);
        }

    }

    public void printSupportPictureSize(Parameters params) {
        List<Size> pictureSizes = params.getSupportedPictureSizes();

        for(int i = 0; i < pictureSizes.size(); ++i) {
            Size var4 = (Size)pictureSizes.get(i);
        }

    }

    public void printSupportFocusMode(Parameters params) {
        List<String> focusModes = params.getSupportedFocusModes();

        String var4;
        for(Iterator var3 = focusModes.iterator(); var3.hasNext(); var4 = (String)var3.next()) {
            ;
        }

    }

    public void turnLightOn(Camera mCamera) {
        if (mCamera != null) {
            Parameters parameters = mCamera.getParameters();
            if (parameters != null) {
                List<String> flashModes = parameters.getSupportedFlashModes();
                if (flashModes != null) {
                    String flashMode = parameters.getFlashMode();
                    if (!"on".equals(flashMode) && flashModes.contains("torch")) {
                        parameters.setFlashMode("torch");
                        mCamera.setParameters(parameters);
                    }

                }
            }
        }
    }

    public void turnLightAuto(Camera mCamera) {
        if (mCamera != null) {
            Parameters parameters = mCamera.getParameters();
            if (parameters != null) {
                List<String> flashModes = parameters.getSupportedFlashModes();
                if (flashModes != null) {
                    String flashMode = parameters.getFlashMode();
                    if (!"auto".equals(flashMode) && flashModes.contains("torch")) {
                        parameters.setFlashMode("torch");
                        mCamera.setParameters(parameters);
                    }

                }
            }
        }
    }

    public void turnLightOff(Camera mCamera) {
        if (mCamera != null) {
            Parameters parameters = mCamera.getParameters();
            if (parameters != null) {
                List<String> flashModes = parameters.getSupportedFlashModes();
                String flashMode = parameters.getFlashMode();
                if (flashModes != null) {
                    if (!"off".equals(flashMode) && flashModes.contains("torch")) {
                        parameters.setFlashMode("torch");
                        mCamera.setParameters(parameters);
                    }

                }
            }
        }
    }

    public static Size findBestPreviewResolution(Camera mCamera) {
        Parameters cameraParameters = mCamera.getParameters();
        Size defaultPreviewResolution = cameraParameters.getPreviewSize();
        List<Size> rawSupportedSizes = cameraParameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            return defaultPreviewResolution;
        } else {
            List<Size> supportedPreviewResolutions = new ArrayList(rawSupportedSizes);
            Collections.sort(supportedPreviewResolutions, new Comparator<Size>() {
                public int compare(Size a, Size b) {
                    int aPixels = a.height * a.width;
                    int bPixels = b.height * b.width;
                    if (bPixels < aPixels) {
                        return -1;
                    } else {
                        return bPixels > aPixels ? 1 : 0;
                    }
                }
            });
            StringBuilder previewResolutionSb = new StringBuilder();
            Iterator var6 = supportedPreviewResolutions.iterator();

            while(var6.hasNext()) {
                Size supportedPreviewResolution = (Size)var6.next();
                previewResolutionSb.append(supportedPreviewResolution.width).append('x').append(supportedPreviewResolution.height).append(' ');
            }

            double screenAspectRatio = (double)(screenWidth / screenHeight);
            Iterator it = supportedPreviewResolutions.iterator();

            while(true) {
                Size supportedPreviewResolution;
                while(it.hasNext()) {
                    supportedPreviewResolution = (Size)it.next();
                    int width = supportedPreviewResolution.width;
                    int height = supportedPreviewResolution.height;
                    if (width * height >= 777600 && !equalRate(width, height, 1.33F)) {
                        KLog.d("CameraUtil", new Object[]{"预览分辨率:", "W=" + supportedPreviewResolution.width, "H=" + supportedPreviewResolution.height});
                        if (width == 1920) {
                            return supportedPreviewResolution;
                        }

                        boolean isCandidatePortrait = width > height;
                        int maybeFlippedWidth = isCandidatePortrait ? height : width;
                        int maybeFlippedHeight = isCandidatePortrait ? width : height;
                        double aspectRatio = (double)maybeFlippedWidth / (double)maybeFlippedHeight;
                        double distortion = Math.abs(aspectRatio - screenAspectRatio);
                        if (maybeFlippedWidth == screenWidth && maybeFlippedHeight == screenHeight) {
                            return supportedPreviewResolution;
                        }

                        if (distortion > 0.15D) {
                            it.remove();
                        }
                    } else {
                        KLog.d("CameraUtil", new Object[]{"移除的分辨率:", "W=" + supportedPreviewResolution.width, "H=" + supportedPreviewResolution.height, "比例16:9=" + equalRate(width, height, 1.33F)});
                        it.remove();
                    }
                }

                if (!supportedPreviewResolutions.isEmpty()) {
                    supportedPreviewResolution = (Size)supportedPreviewResolutions.get(0);
                    return supportedPreviewResolution;
                }

                return defaultPreviewResolution;
            }
        }
    }

    public static boolean equalRate(int width, int height, float rate) {
        float r = (float)width / (float)height;
        return (double)Math.abs(r - rate) <= 0.2D;
    }

    public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;

        int x;
        int y;
        for(x = 0; x < imageWidth; ++x) {
            for(y = imageHeight - 1; y >= 0; --y) {
                yuv[i] = data[y * imageWidth + x];
                ++i;
            }
        }

        i = imageWidth * imageHeight * 3 / 2 - 1;

        for(x = imageWidth - 1; x > 0; x -= 2) {
            for(y = 0; y < imageHeight / 2; ++y) {
                yuv[i] = data[imageWidth * imageHeight + y * imageWidth + x];
                --i;
                yuv[i] = data[imageWidth * imageHeight + y * imageWidth + (x - 1)];
                --i;
            }
        }

        return yuv;
    }

    public static byte[] rotateYUV420Degree180(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int count = 0;

        int i;
        for(i = imageWidth * imageHeight - 1; i >= 0; --i) {
            yuv[count] = data[i];
            ++count;
        }

        i = imageWidth * imageHeight * 3 / 2 - 1;
        int count2 = count;

        for(i = imageWidth * imageHeight * 3 / 2 - 1; i >= imageWidth * imageHeight; i -= 2) {
            count = count2 + 1;
            yuv[count2] = data[i - 1];
            count2 = count + 1;
            yuv[count] = data[i];
        }

        return yuv;
    }

    public static byte[] rotateYUV420Degree270(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int wh = 0;
        int uvHeight = 0;
        if (imageWidth != 0 || imageHeight != 0) {
            wh = imageWidth * imageHeight;
            uvHeight = imageHeight >> 1;
        }

        int k = 0;

        int i;
        int j;
        int nPos;
        for(i = 0; i < imageWidth; ++i) {
            nPos = 0;

            for(j = 0; j < imageHeight; ++j) {
                yuv[k] = data[nPos + i];
                ++k;
                nPos += imageWidth;
            }
        }

        for(i = 0; i < imageWidth; i += 2) {
            nPos = wh;

            for(j = 0; j < uvHeight; ++j) {
                yuv[k] = data[nPos + i];
                yuv[k + 1] = data[nPos + i + 1];
                k += 2;
                nPos += imageWidth;
            }
        }

        return rotateYUV420Degree180(yuv, imageWidth, imageHeight);
    }

    public static Bitmap RGB2Bitmap(byte[] data, int width, int height) {
        int[] colors = convertByteToColor(data);
        if (colors == null) {
            return null;
        } else {
            try {
                return Bitmap.createBitmap(colors, width, height, Config.ARGB_8888);
            } catch (Exception var5) {
                return null;
            }
        }
    }

    private static int[] convertByteToColor(byte[] data) {
        int size = data.length;
        if (size == 0) {
            return null;
        } else {
            int arg = 0;
            if (size % 3 != 0) {
                arg = 1;
            }

            int[] color = new int[size / 3 + arg];
            int colorLen = color.length;
            int i;
            if (arg == 0) {
                Log.e("AAAA", "AAAAAA *3");

                for(i = 0; i < color.length; ++i) {
                    color[i] = Color.rgb(data[i * 3] & 255, data[i * 3 + 1] & 255, data[i * 3 + 2] & 255);
                }

                return color;
            } else {
                Log.e("AAAA", "AAAAAA  not 3");

                for(i = 0; i < color.length - 1; ++i) {
                    color[i] = Color.rgb(data[i * 3] & 255, data[i * 3 + 1] & 255, data[i * 3 + 2] & 255);
                }

                color[color.length - 1] = -16777216;
                return color;
            }
        }
    }

    public static String saveBytesToSD(Context context, byte[] bytes, int previewFormat, int width, int height) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "miaoshi");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String fileName1 = System.currentTimeMillis() + ".jpg";
        File pictureFile = new File(appDir, fileName1);
        if (pictureFile.exists()) {
            pictureFile.delete();
        }

        try {
            pictureFile.createNewFile();
            YuvImage image = new YuvImage(bytes, previewFormat, width, height, (int[])null);
            image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 90, new FileOutputStream(pictureFile));
        } catch (IOException var9) {
            var9.printStackTrace();
        }

        return pictureFile.toString();
    }

    public static Bitmap yuvToBitmap(byte[] data, int width, int height) {
        int frameSize = width * height;
        int[] rgba = new int[frameSize];

        for(int i = 0; i < height; ++i) {
            for(int j = 0; j < width; ++j) {
                int y = 255 & data[i * width + j];
                int u = 255 & data[frameSize + (i >> 1) * width + (j & -2) + 0];
                int v = 255 & data[frameSize + (i >> 1) * width + (j & -2) + 1];
                y = y < 16 ? 16 : y;
                int r = Math.round(1.164F * (float)(y - 16) + 1.596F * (float)(v - 128));
                int g = Math.round(1.164F * (float)(y - 16) - 0.813F * (float)(v - 128) - 0.391F * (float)(u - 128));
                int b = Math.round(1.164F * (float)(y - 16) + 2.018F * (float)(u - 128));
                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);
                rgba[i * width + j] = -16777216 + (b << 16) + (g << 8) + r;
            }
        }

        Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bmp.setPixels(rgba, 0, width, 0, 0, width, height);
        KLog.d("liujiandong", new Object[]{"1====" + bmp.toString()});
        return bmp;
    }

    public class CameraAscendSizeComparator implements Comparator<Size> {
        public CameraAscendSizeComparator() {
        }

        public int compare(Size lhs, Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else {
                return lhs.width > rhs.width ? 1 : -1;
            }
        }
    }

    public class CameraDropSizeComparator implements Comparator<Size> {
        public CameraDropSizeComparator() {
        }

        public int compare(Size lhs, Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else {
                return lhs.width < rhs.width ? 1 : -1;
            }
        }
    }
}
