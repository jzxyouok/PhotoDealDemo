package com.example.a835127729qqcom.photodealdemo.dealaction;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by 835127729qq.com on 16/8/23.
 */
public class CropAction implements Action{
    public RectF mRect;
    public RectF mDestRect;
    private Bitmap mCropBitmap;

    public CropAction(RectF rect,RectF destRect,Bitmap bitmap){
        mRect = rect;
        mDestRect = destRect;
        mCropBitmap = bitmap;
    }

    @Override
    public void execute(Canvas canvas) {
        canvas.drawBitmap(mCropBitmap,null,mDestRect,null);
    }

    @Override
    public void start(Object... params) {

    }

    @Override
    public void next(Object... params) {

    }

    @Override
    public void stop(Object... params) {
        mCropBitmap.recycle();
        mCropBitmap = null;
    }

    public Bitmap getmCropBitmap() {
        return mCropBitmap;
    }
}
