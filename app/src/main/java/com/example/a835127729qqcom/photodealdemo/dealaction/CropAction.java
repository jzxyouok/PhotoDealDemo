package com.example.a835127729qqcom.photodealdemo.dealaction;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.a835127729qqcom.photodealdemo.ActionImageView;
import com.example.a835127729qqcom.photodealdemo.util.DrawMode;

/**
 * Created by 835127729qq.com on 16/8/23.
 */
public class CropAction implements Action{
    private Bitmap mCropBitmap;
    private Bitmap mforeBitmap;
    private Canvas mCropCanvas;
    private Bitmap mCropMasicBitmap;
    private Bitmap mBehindBitmap;
    private Canvas mCropMasicCanvas;
    private float currentAngle = 0;
    //旋转中心
    float centerX,centerY;
    //裁剪矩阵
    public Rect mCropRect;
    //裁剪后的矩阵
    private RectF rotateRectf;
    public RectF scaleRect;
    //裁剪前的矩阵
    private Rect lastNormalRect;
    private RectF lastScaleRectf;

    private static Paint paint = new Paint();
    static {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.TRANSPARENT);
    }

    public CropAction(float centerx,float centery,RectF cropRect, Bitmap cropBitmap, Bitmap foreBitmap, Canvas croprCanvas,
                      Bitmap cropMasicBitmap, Bitmap behindBitmap, Canvas cropMasicCanvas){
        centerX = centerx;
        centerY = centery;
        mCropBitmap = cropBitmap;
        mforeBitmap = foreBitmap;
        mCropCanvas = croprCanvas;
        mCropMasicBitmap = cropMasicBitmap;
        mBehindBitmap = behindBitmap;
        mCropMasicCanvas = cropMasicCanvas;
        mCropRect = new Rect(Math.round(cropRect.left),Math.round(cropRect.top),
                Math.round(cropRect.right),Math.round(cropRect.bottom));
    }

    @Override
    public void execute(Canvas canvas) {
        //清屏,清除mCropBitmap之前上的绘制,因为新的绘制,有当前forebitmap决定
        paint.setXfermode(DrawMode.CLEAR);
        mCropCanvas.drawPaint(paint);
        paint.setXfermode(DrawMode.SRC);
        //将foreBitmap内容绘制到mCropBitmap上
        mCropCanvas.save();
        mCropCanvas.rotate(currentAngle,centerX,centerY);
        mCropCanvas.drawBitmap(mforeBitmap,lastNormalRect,lastScaleRectf,null);
        mCropCanvas.restore();

        drawCropBitmapDirectly(canvas);
    }

    private void drawCropBitmapDirectly(Canvas canvas) {
        //清屏,清除foreBitmap之前上的绘制,因为已经将这些,绘制到mCropBitmap
        paint.setXfermode(DrawMode.CLEAR);
        canvas.drawPaint(paint);
        paint.setXfermode(DrawMode.SRC);
        //绘制裁剪图片
        canvas.save();
        canvas.rotate(-currentAngle,centerX,centerY);
        canvas.drawBitmap(mCropBitmap,mCropRect,rotateRectf,null);
        //Log.i("cky","width="+rotateRectf.width()+",h="+rotateRectf.height());
        canvas.restore();
    }

    public void drawCropBitmapFromCache(Canvas canvas) {
        //清屏,清除foreBitmap之前上的绘制,因为已经将这些,绘制到mCropBitmap
        paint.setXfermode(DrawMode.CLEAR);
        canvas.drawPaint(paint);
        paint.setXfermode(DrawMode.SRC);
        //绘制裁剪图片
        canvas.save();
        canvas.drawBitmap(mCropBitmap,mCropRect,rotateRectf,null);
        //Log.i("cky","width="+rotateRectf.width()+",h="+rotateRectf.height());
        canvas.restore();
    }

    @Override
    public void start(Object... params) {
        currentAngle = (float) params[0];
        rotateRectf = (RectF) params[1];
        scaleRect = (RectF) params[2];
        //裁剪前的矩阵
        lastNormalRect = (Rect) params[3];
        lastScaleRectf = (RectF) params[4];
    }

    @Override
    public void next(Object... params) {
        Canvas canvas = (Canvas) params[0];
        //清屏,清除mCropMasicBitmap之前上的绘制,因为新的绘制,有当前mBehindBitmap决定
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCropMasicCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        //将mBehindBitmap内容绘制到mCropMasicBitmap上
        mCropMasicCanvas.save();
        mCropMasicCanvas.rotate(currentAngle,centerX,centerY);
        mCropMasicCanvas.drawBitmap(mBehindBitmap,lastNormalRect,lastScaleRectf,null);
        mCropMasicCanvas.restore();
        drawCropMasicBitmapDirectly(canvas);
    }

    private void drawCropMasicBitmapDirectly(Canvas canvas) {
        //清屏,清除mBehindBitmap之前上的绘制,因为已经将这些,绘制到mCropBitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        //绘制裁剪图片
        canvas.save();
        canvas.rotate(-currentAngle,centerX,centerY);
        canvas.drawBitmap(mCropMasicBitmap,mCropRect,rotateRectf,null);
        //Log.i("cky","width="+rotateRectf.width()+",h="+rotateRectf.height());
        canvas.restore();
    }

    public void drawCropMasicBitmapFromCache(Canvas canvas) {
        //清屏,清除mBehindBitmap之前上的绘制,因为已经将这些,绘制到mCropBitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        //绘制裁剪图片
        canvas.save();
        canvas.rotate(-currentAngle,centerX,centerY);
        canvas.drawBitmap(mCropMasicBitmap,mCropRect,rotateRectf,null);
        //Log.i("cky","width="+rotateRectf.width()+",h="+rotateRectf.height());
        canvas.restore();
    }

    @Override
    public void stop(Object... params) {
        ((ActionImageView.CropSnapshot)params[0]).setCropAction(this);
    }
}
