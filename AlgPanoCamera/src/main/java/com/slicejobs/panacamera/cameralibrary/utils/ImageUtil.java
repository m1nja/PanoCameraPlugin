package com.slicejobs.panacamera.cameralibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by martian on 13-9-15.
 */
public class ImageUtil {

    /**
     * Get the size of a bitmap
     *
     * @param bitmap
     * @return
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }

        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * Resize bitmap
     *
     * @param bitmap
     * @param height
     * @param width
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, final int height, final int width) {
        int oldHeight = bitmap.getHeight();
        int oldWidth = bitmap.getWidth();

        int newHeight = height < oldHeight ? height : oldHeight;
        int newWidth = width < oldWidth ? width : oldWidth;

        float scaleHeight = ((float) newHeight) / oldHeight;
        float scaleWidth = ((float) newWidth) / oldWidth;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, newWidth, newHeight, matrix, true);
    }

    /**
     * 截取正方形bitmap
     *
     * @param bitmap
     * @return
     */
    public static Bitmap cropCenterSquare(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        //截取正方形
        int wh = w > h ? h : w;
        int retX = w > h ? (w - h) / 2 : 0;
        int retY = w > h ? 0 : (h - w) / 2;
        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
    }

    public static byte[] cropCenterSquareToSize(Bitmap bitmap) {
        return cropCenterSquareToSize(bitmap, 32 * 1024);
    }

    /**
     * 截取缩略图到指定大小
     *
     * @param source
     * @param maxSize
     * @return
     */
    public static byte[] cropCenterSquareToSize(Bitmap source, int maxSize) {
        Bitmap bm = cropCenterSquare(source);
        int w = bm.getWidth();
        int h = bm.getHeight();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        // 额外多减去1K，防止超出范围
        if (b.length > (maxSize)) {
            double i = b.length / (maxSize - 1024);

            double newWidth = w / Math.sqrt(i);
            double newHeight = h / Math.sqrt(i);
            Matrix matrix = new Matrix();
            float scaleWidth = ((float) newWidth) / w;
            float scaleHeight = ((float) newHeight) / h;
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newBm = Bitmap.createBitmap(bm, 0, 0, w,
                    h, matrix, true);
            baos.reset();
            newBm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        }

        return baos.toByteArray();
    }

    /**
     * 调整Bitmap到指定大小
     *
     * @param source
     * @param size
     * @return
     */
    public static Bitmap resizeImageTo(Bitmap source, int size) {
        int w = source.getWidth();
        int h = source.getHeight();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        source.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        // 额外多减去1K，防止超出范围
        Bitmap newBm = source;
        if (b.length > (size)) {
            double i = b.length / (size - 1024);
            double newWidth = w / Math.sqrt(i);
            double newHeight = h / Math.sqrt(i);
            Matrix matrix = new Matrix();
            float scaleWidth = ((float) newWidth) / w;
            float scaleHeight = ((float) newHeight) / h;
            matrix.postScale(scaleWidth, scaleHeight);
            newBm = Bitmap.createBitmap(source, 0, 0, w,
                    h, matrix, true);
        }
        return newBm;
    }

    public static void saveBitmapToPath(Context context,String path, Bitmap source) throws IOException {
        File file = new File(path);
        FileOutputStream fos = new FileOutputStream(file);
        source.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();
        Uri uri = Uri.fromFile(file);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }

    /**
     * 旋转目标图片并保存
     *
     * @param uri      图片uri
     * @param rotation 旋转角度
     */
    public static void rotateImage(Uri uri, float rotation) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeFile(uri.getPath(), options);
        options.inSampleSize = calculateInSampleSize(options, 540, 540);//之前540
        options.inJustDecodeBounds = false;
        Bitmap source = BitmapFactory.decodeFile(uri.getPath(), options);
        Bitmap target = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(target);
        Matrix matrix = new Matrix();
        matrix.setRotate(rotation, source.getWidth() / 2, source.getHeight() / 2);
        canvas.drawBitmap(source, matrix, new Paint());
        FileOutputStream fos = new FileOutputStream(new File(uri.getPath()));
        target.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();
        if(!source.isRecycled() ){
            source.recycle();   //回收图片所占的内存
        }
        if(!target.isRecycled() ){
            target.recycle();   //回收图片所占的内存
            System.gc();
        }

    }




    public static void readPhotoExifOrientation(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);

            String orientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            String dateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            String make = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
            String model = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
            String flash = exifInterface.getAttribute(ExifInterface.TAG_FLASH);
            String imageLength = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            String imageWidth = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String latitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String longitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            String exposureTime = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            String aperture = exifInterface.getAttribute(ExifInterface.TAG_APERTURE);
            String isoSpeedRatings = exifInterface.getAttribute(ExifInterface.TAG_ISO);
            String dateTimeDigitized = exifInterface.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED);
            String subSecTime = exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME);
            String subSecTimeOrig = exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME_ORIG);
            String subSecTimeDig = exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME_DIG);
            String altitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
            String altitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF);
            String gpsTimeStamp = exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
            String gpsDateStamp = exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
            String whiteBalance = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
            String focalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            String processingMethod = exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);

            Log.e("---------------", "## orientation=" + orientation);
            Log.e("---------------", "## dateTime=" + dateTime);
            Log.e("---------------", "## make=" + make);
            Log.e("---------------", "## model=" + model);
            Log.e("---------------", "## flash=" + flash);
            Log.e("---------------", "## imageLength=" + imageLength);
            Log.e("---------------", "## imageWidth=" + imageWidth);
            Log.e("---------------", "## latitude=" + latitude);
            Log.e("---------------", "## longitude=" + longitude);
            Log.e("---------------", "## latitudeRef=" + latitudeRef);
            Log.e("---------------", "## longitudeRef=" + longitudeRef);
            Log.e("---------------", "## exposureTime=" + exposureTime);
            Log.e("---------------", "## aperture=" + aperture);
            Log.e("---------------", "## isoSpeedRatings=" + isoSpeedRatings);
            Log.e("---------------", "## dateTimeDigitized=" + dateTimeDigitized);
            Log.e("---------------", "## subSecTime=" + subSecTime);
            Log.e("---------------", "## subSecTimeOrig=" + subSecTimeOrig);
            Log.e("---------------", "## subSecTimeDig=" + subSecTimeDig);
            Log.e("---------------", "## altitude=" + altitude);
            Log.e("---------------", "## altitudeRef=" + altitudeRef);
            Log.e("---------------", "## gpsTimeStamp=" + gpsTimeStamp);
            Log.e("---------------", "## gpsDateStamp=" + gpsDateStamp);
            Log.e("---------------", "## whiteBalance=" + whiteBalance);
            Log.e("---------------", "## focalLength=" + focalLength);
            Log.e("---------------", "## processingMethod=" + processingMethod);
        } catch (Exception e) {

        }
    }



    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {//2400,2400

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 图片变灰
     *
     * @param bmpOriginal
     * @return
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static Bitmap compressImage(Bitmap bitmap, float scaleFactor) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Matrix matrix = new Matrix();
        matrix.setScale(scaleFactor, scaleFactor);
        Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        out.reset();
        result.compress(Bitmap.CompressFormat.JPEG, 70, out);
        return result;
    }

    public static void saveImageToSD(Context context, File origin, boolean insertToGallery) throws IOException {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Today");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true; //确保图片不加载到内存
        BitmapFactory.decodeFile(origin.getAbsolutePath(), opts);
        String fileName = System.currentTimeMillis() + "." +
                opts.outMimeType.substring(6, opts.outMimeType.length());
        File file = new File(appDir, fileName);
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(origin);
            output = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }

        // 其次把文件插入到系统图库
        if (insertToGallery) {
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + file.getAbsolutePath())));
    }

    public static Bitmap createRoundedBitmap(Bitmap bitmap) {
        bitmap = cropCenterSquare(bitmap);
        Bitmap bmp;
        bmp = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap,
                BitmapShader.TileMode.CLAMP,
                BitmapShader.TileMode.CLAMP);

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        RectF oval = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawOval(oval, paint);

        return bmp;
    }

    public static Bitmap drawableToBitmap (Drawable drawable, int defaultWidth, int defaultHeight) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : defaultWidth;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : defaultHeight;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * bitmaP旋转
     * @param bm
     * @param orientationDegree
     * @return
     */
    public static Bitmap adjustPhotoRotation(Bitmap bm, int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);

        return bm1;
    }



    public static Bitmap adjustPhotoRotationMatrix(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);

        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;
        } catch (OutOfMemoryError ex) {
            Log.d("slicejobs", "adjustPhotoRotationMatrix outofmemoryerror");
        }
        return null;
    }
}
