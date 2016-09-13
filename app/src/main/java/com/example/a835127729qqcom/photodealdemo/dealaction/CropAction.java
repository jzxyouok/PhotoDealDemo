package com.example.a835127729qqcom.photodealdemo.dealaction;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
    private Bitmap mforeBitmap;
    private Canvas mCanvas;

    public CropAction(RectF rect,RectF destRect,Bitmap bitmap,Bitmap foreBitmap,Canvas canvas){
        mRect = rect;
        mDestRect = destRect;
        mCropBitmap = bitmap;
        mforeBitmap = foreBitmap;
        mCanvas = canvas;
    }

    @Override
    public void execute(Canvas canvas) {
        //canvas.save();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        Rect r = new Rect((int) mRect.left,(int) mRect.top,(int) mRect.right,(int) mRect.bottom);
        mCanvas.drawBitmap(mforeBitmap,r,mDestRect,null);
        canvas.drawBitmap(mCropBitmap,null,mDestRect,null);
        //canvas.drawBitmap(mCropBitmap,r,r,null);
        //paint.setColor(Color.YELLOW);
        //canvas.drawRect(mDestRect,paint);
        //paint.setColor(Color.GREEN);
        //canvas.drawRect(mRect,paint);
        //canvas.restore();
        //canvas.clipRect();
    }

    @Override
    public void start(Object... params) {

    }

    @Override
    public void next(Object... params) {

    }

    @Override
    public void stop(Object... params) {
    }

}
