package com.example.a835127729qqcom.photodealdemo.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/**
 * Created by 835127729qq.com on 16/9/29.
 */
public final class MasicUtil {
    /**
     * 生成马赛克图片,如果你不想使用native包
     * @param bmp
     * @param precent
     * @return
     */
    public static Bitmap getMosaicsBitmap(Bitmap bmp, double precent) {
        long start = System.currentTimeMillis();
        int bmpW = bmp.getWidth();
        int bmpH = bmp.getHeight();
        Bitmap resultBmp = Bitmap.createBitmap(bmpW, bmpH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBmp);
        Paint paint = new Paint();
        double unit;
        if (precent == 0) {
            unit = bmpW;
        } else {
            unit = 1 / precent;
        }
        double resultBmpW = bmpW / unit;
        double resultBmpH = bmpH / unit;
        for (int i = 0; i < resultBmpH; i++) {
            for (int j = 0; j < resultBmpW; j++) {
                int pickPointX = (int) (unit * (j + 0.5));
                int pickPointY = (int) (unit * (i + 0.5));
                int color;
                if (pickPointX >= bmpW || pickPointY >= bmpH) {
                    color = bmp.getPixel(bmpW / 2, bmpH / 2);
                } else {
                    color = bmp.getPixel(pickPointX, pickPointY);
                }
                paint.setColor(color);
                canvas.drawRect((int) (unit * j), (int) (unit * i), (int) (unit * (j + 1)), (int) (unit * (i + 1)), paint);
            }
        }
        canvas.setBitmap(null);
        long end = System.currentTimeMillis();
        Log.i("MasicUtil","DrawTime:" + (end - start)+"ms");
        return resultBmp;
    }

    /**
     * 和上面的函数同样的功能，效率远高于上面
     *
     * @param bmp
     * @param precent
     * @return
     */
    public static Bitmap getMosaicsBitmaps(Bitmap bmp, double precent) {
        long start = System.currentTimeMillis();
        int bmpW = bmp.getWidth();
        int bmpH = bmp.getHeight();
        int[] pixels = new int[bmpH * bmpW];
        bmp.getPixels(pixels, 0, bmpW, 0, 0, bmpW, bmpH);
        int raw = (int) (bmpW * precent);
        int unit;
        if (raw == 0) {
            unit = bmpW;
        } else {
            unit = bmpW / raw; //原来的unit*unit像素点合成一个，使用原左上角的值
        }
        if (unit >= bmpW || unit >= bmpH) {
            return getMosaicsBitmap(bmp, precent);
        }
        for (int i = 0; i < bmpH; ) {
            for (int j = 0; j < bmpW; ) {
                int leftTopPoint = i * bmpW + j;
                for (int k = 0; k < unit; k++) {
                    for (int m = 0; m < unit; m++) {
                        int point = (i + k) * bmpW + (j + m);
                        if (point < pixels.length) {
                            pixels[point] = pixels[leftTopPoint];
                        }
                    }
                }
                j += unit;
            }
            i += unit;
        }
        long end = System.currentTimeMillis();
        Log.i("MasicUtil","DrawTime:" + (end - start)+"ms");
        return Bitmap.createBitmap(pixels, bmpW, bmpH, Bitmap.Config.ARGB_8888);
    }
}
