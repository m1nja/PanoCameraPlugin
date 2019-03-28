package com.slicejobs.panacamera.cameralibrary.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {
    public BitmapUtils() {
    }

    public static int[] getImageWidthHeight(String path) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return new int[]{options.outWidth, options.outHeight};
    }

    public static void deleteImageWithPath(Context context, String path) {
        File file = new File(path.replace("file://", ""));
        if (file != null && file.isFile() && file.exists()) {
            file.delete();
            context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse("file://" + file)));
        }

    }

    public static byte[] InputStream2ByteArray(String filePath) throws IOException {
        InputStream in = new FileInputStream(filePath);
        byte[] data = toByteArray(in);
        in.close();
        return data;
    }

    private static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        boolean var3 = false;

        int n;
        while((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }

        return out.toByteArray();
    }
}