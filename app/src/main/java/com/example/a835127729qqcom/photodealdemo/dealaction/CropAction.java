package com.example.a835127729qqcom.photodealdemo.dealaction;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.a835127729qqcom.photodealdemo.R;
import com.example.a835127729qqcom.photodealdemo.util.RotateUtil;

/**
 * Created by 835127729qq.com on 16/8/23.
 */
public class CropAction implements Action{
    public RectF mCropRect;
    public RectF mDestRect;
    private Bitmap mCropBitmap;
    private Bitmap mforeBitmap;
    private Canvas mCropCanvas;
    private Bitmap mCropMasicBitmap;
    private Bitmap mBehindBitmap;
    private Canvas mCropMasicCanvas;
    private Canvas mBehindCanvas;
    private float angle;
    private Rect rect;
    private Rect rect2;
    private static Paint paint = new Paint();
    static {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.TRANSPARENT);
    }

    public CropAction(RectF cropRect,RectF destRect,Bitmap cropBitmap,Bitmap foreBitmap,Canvas croprCanvas,
                      Bitmap cropMasicBitmap,Bitmap behindBitmap,Canvas cropMasicCanvas,float angle){
        mCropRect = cropRect;
        mDestRect = destRect;
        mCropBitmap = cropBitmap;
        mforeBitmap = foreBitmap;
        mCropCanvas = croprCanvas;
        mCropMasicBitmap = cropMasicBitmap;
        mBehindBitmap = behindBitmap;
        mCropMasicCanvas = cropMasicCanvas;
        //mBehindCanvas = behindCanvas;
        this.angle = angle;
        RectF rf = new RectF(mCropRect);
        Matrix m = new Matrix();
        m.postRotate(-angle,mDestRect.centerX(),mDestRect.centerY());
        m.mapRect(rf);
        rect = new Rect((int) rf.left,(int) rf.top,(int) rf.right,(int) rf.bottom);

        rect2 = new Rect((int)mDestRect.left,(int)mDestRect.top,(int)mDestRect.right,(int)mDestRect.bottom);
    }

    @Override
    public void execute(Canvas canvas) {
        //清屏,清除mCropBitmap之前上的绘制,因为新的绘制,有当前forebitmap决定
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCropCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        //将foreBitmap内容绘制到mCropBitmap上
        mCropCanvas.drawBitmap(mforeBitmap,rect,mDestRect,null);

        //清屏,清除foreBitmap之前上的绘制,因为已经将这些,绘制到mCropBitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        //绘制裁剪图片
        canvas.drawBitmap(mCropBitmap,rect2,mDestRect,null);
    }

    @Override
    public void start(Object... params) {
        Canvas canvas = (Canvas) params[0];
        //清屏,清除mCropMasicBitmap之前上的绘制,因为新的绘制,有当前mBehindBitmap决定
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCropMasicCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        //将mBehindBitmap内容绘制到mCropMasicBitmap上
        mCropMasicCanvas.drawBitmap(mBehindBitmap,rect,mDestRect,null);
        //清屏,清除mBehindBitmap之前上的绘制,因为已经将这些,绘制到mCropBitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        //绘制裁剪图片
        canvas.drawBitmap(mCropMasicBitmap,rect2,mDestRect,null);
    }

    @Override
    public void next(Object... params) {

    }

    @Override
    public void stop(Object... params) {
    }

}
