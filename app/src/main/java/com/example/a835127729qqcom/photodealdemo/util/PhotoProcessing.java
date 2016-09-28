package com.example.a835127729qqcom.photodealdemo.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

/**
 * Created by 835127729qq.com on 16/9/28.
 */
public class PhotoProcessing {
    private static final String TAG = "PhotoProcessing";

    public static final String[] FILTERS = {"origin", "soft", "black", "classic", "gorgeous",
            "retro", "grace", "film", "memory", "yogurt", "fleeting", "shine","masic","blur"};

    public static Bitmap filterPhoto(Bitmap bitmap, int position) {
        if (bitmap != null) {
            sendBitmapToNative(bitmap);
        }
        switch (position) {
            case 0: // Original
                break;
            case 1: // Instafix
                nativeApplyInstafix();
                break;
            case 2: // Ansel
                nativeApplyAnsel();
                break;
            case 3: // Testino
                nativeApplyTestino();
                break;
            case 4: // XPro
                nativeApplyXPro();
                break;
            case 5: // Retro
                nativeApplyRetro();
                break;
            case 6: // Black & White
                nativeApplyBW();
                break;
            case 7: // Sepia
                nativeApplySepia();
                break;
            case 8: // Cyano
                nativeApplyCyano();
                break;
            case 9: // Georgia
                nativeApplyGeorgia();
                break;
            case 10: // Sahara
                nativeApplySahara();
                break;
            case 11: // HDR
                nativeApplyHDR();
                break;
            case 12: // MASIC
                nativeApplyMasic();
                break;
            case 13: // BLUR
                nativeApplyBlur();
                break;
        }
        Bitmap filteredBitmap = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();
        return filteredBitmap;
    }

    // /////////////////////////////////////////////
    static {
        System.loadLibrary("photoprocessing");
    }

    public static native int nativeInitBitmap(int width, int height);

    public static native void nativeGetBitmapRow(int y, int[] pixels);

    public static native void nativeSetBitmapRow(int y, int[] pixels);

    public static native int nativeGetBitmapWidth();

    public static native int nativeGetBitmapHeight();

    public static native void nativeDeleteBitmap();

    public static native int nativeRotate90();

    public static native void nativeRotate180();

    public static native void nativeFlipHorizontally();

    public static native void nativeApplyInstafix();

    public static native void nativeApplyAnsel();

    public static native void nativeApplyTestino();

    public static native void nativeApplyXPro();

    public static native void nativeApplyRetro();

    public static native void nativeApplyBW();

    public static native void nativeApplySepia();

    public static native void nativeApplyCyano();

    public static native void nativeApplyGeorgia();

    public static native void nativeApplySahara();

    public static native void nativeApplyHDR();

    public static native void nativeApplyMasic();

    public static native void nativeApplyBlur();

    public static native void nativeLoadResizedJpegBitmap(byte[] jpegData,
                                                          int size, int maxPixels);

    public static native void nativeResizeBitmap(int newWidth, int newHeight);

    private static void sendBitmapToNative(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        nativeInitBitmap(width, height);
        int[] pixels = new int[width];
        for (int y = 0; y < height; y++) {
            bitmap.getPixels(pixels, 0, width, 0, y, width, 1);
            nativeSetBitmapRow(y, pixels);
        }
    }

    private static Bitmap getBitmapFromNative(Bitmap bitmap) {
        int width = nativeGetBitmapWidth();
        int height = nativeGetBitmapHeight();

        if (bitmap == null || width != bitmap.getWidth()
                || height != bitmap.getHeight() || !bitmap.isMutable()) { // in
            Config config = Config.ARGB_8888;
            if (bitmap != null) {
                config = bitmap.getConfig();
                bitmap.recycle();
            }
            bitmap = Bitmap.createBitmap(width, height, config);
        }

        int[] pixels = new int[width];
        for (int y = 0; y < height; y++) {
            nativeGetBitmapRow(y, pixels);
            bitmap.setPixels(pixels, 0, width, 0, y, width, 1);
        }

        return bitmap;
    }

    public static Bitmap makeBitmapMutable(Bitmap bitmap) {
        sendBitmapToNative(bitmap);
        return getBitmapFromNative(bitmap);
    }

    public static Bitmap rotate(Bitmap bitmap, int angle) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Config config = bitmap.getConfig();
        nativeInitBitmap(width, height);
        sendBitmapToNative(bitmap);

        if (angle == 90) {
            nativeRotate90();
            bitmap.recycle();
            bitmap = Bitmap.createBitmap(height, width, config);
            bitmap = getBitmapFromNative(bitmap);
            nativeDeleteBitmap();
        } else if (angle == 180) {
            nativeRotate180();
            bitmap.recycle();
            bitmap = Bitmap.createBitmap(width, height, config);
            bitmap = getBitmapFromNative(bitmap);
            nativeDeleteBitmap();
        } else if (angle == 270) {
            nativeRotate180();
            nativeRotate90();
            bitmap.recycle();
            bitmap = Bitmap.createBitmap(height, width, config);
            bitmap = getBitmapFromNative(bitmap);
            nativeDeleteBitmap();
        }
        return bitmap;
    }

    public static Bitmap flipHorizontally(Bitmap bitmap) {
        nativeInitBitmap(bitmap.getWidth(), bitmap.getHeight());
        sendBitmapToNative(bitmap);
        nativeFlipHorizontally();
        bitmap = getBitmapFromNative(bitmap);
        nativeDeleteBitmap();
        return bitmap;
    }
}// end class
