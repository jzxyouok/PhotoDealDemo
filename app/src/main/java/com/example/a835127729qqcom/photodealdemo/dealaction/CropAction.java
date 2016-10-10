package com.example.a835127729qqcom.photodealdemo.dealaction;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.example.a835127729qqcom.photodealdemo.ActionImageView;
import com.example.a835127729qqcom.photodealdemo.util.DrawMode;
import com.example.a835127729qqcom.photodealdemo.util.RotateUtil;
import com.example.a835127729qqcom.photodealdemo.util.SaveBitmap2File;

import java.io.IOException;

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
    private Rect rect;
    private RectF rotateRectf;
    private Rect normalRect;
    private static Paint paint = new Paint();
    private float currentAngle = 0;

    private RectF lastNormalRectf;
    private RectF lastScaleRectf;
    private RectF lastRotateRectf;


    static {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.TRANSPARENT);
    }

    public CropAction(RectF cropRect, float centerX,float centerY,Bitmap cropBitmap, Bitmap foreBitmap, Canvas croprCanvas,
                      Bitmap cropMasicBitmap, Bitmap behindBitmap, Canvas cropMasicCanvas, float angle){
        mCropRect = cropRect;
        mCropBitmap = cropBitmap;
        mforeBitmap = foreBitmap;
        mCropCanvas = croprCanvas;
        mCropMasicBitmap = cropMasicBitmap;
        mBehindBitmap = behindBitmap;
        mCropMasicCanvas = cropMasicCanvas;
        RectF rf = new RectF(Math.round(mCropRect.left),Math.round(mCropRect.top),
                Math.round(mCropRect.right),Math.round(mCropRect.bottom));
        mCropRect = rf;
        //RotateUtil.rotateRect(rf,centerX,centerY,-angle);
//        Matrix m = new Matrix();
//        m.postRotate(-angle,centerX,centerY);
//        m.mapRect(rf);
//        rect = new Rect((int) rf.left,(int) rf.top,(int) rf.right,(int) rf.bottom);
    }

    @Override
    public void execute(Canvas canvas) {
        //清屏,清除mCropBitmap之前上的绘制,因为新的绘制,有当前forebitmap决定
        paint.setXfermode(DrawMode.CLEAR);
        mCropCanvas.drawPaint(paint);
        paint.setXfermode(DrawMode.SRC);
        //将foreBitmap内容绘制到mCropBitmap上
//        mCropCanvas.drawBitmap(mforeBitmap,rect,mDestRect,null);
//        try {
//            SaveBitmap2File.saveFile(mforeBitmap,"/storage/emulated/0/ActionImage/","ssss.png");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        new Thread(){
//            @Override
//            public void run() {
//                try {
//                    SaveBitmap2File.saveFile(mCropBitmap,"/storage/emulated/0/ActionImage/","ttt.png");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
        mCropCanvas.save();
        mCropCanvas.rotate(currentAngle,lastNormalRectf.centerX(),lastNormalRectf.centerY());
        Rect lastNormalRect = new Rect((int)lastNormalRectf.left,(int)lastNormalRectf.top,
                (int)lastNormalRectf.right,(int)lastNormalRectf.bottom);
        mCropCanvas.drawBitmap(mforeBitmap,lastNormalRect,lastScaleRectf,null);
        mCropCanvas.restore();
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        mCropCanvas.drawRect(mCropRect,paint);
//        try {
//            SaveBitmap2File.saveFile(mCropBitmap,"/storage/emulated/0/ActionImage/","ttt.png");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        paint.setXfermode(DrawMode.CLEAR);
        canvas.drawPaint(paint);
        paint.setXfermode(DrawMode.SRC);

        canvas.save();
        canvas.rotate(-currentAngle,lastNormalRectf.centerX(),lastNormalRectf.centerY());
        //canvas.clipRect(mCropRect);
        Rect cropRect = new Rect((int)mCropRect.left,(int)mCropRect.top,(int)mCropRect.right,(int)mCropRect.bottom);
        canvas.drawBitmap(mCropBitmap,cropRect,rotateRectf,null);
        Log.i("cky","width="+rotateRectf.width()+",h="+rotateRectf.height());
        canvas.restore();
//        mCropCanvas.save();
//        mCropCanvas.clipRect(mCropRect);
//        //Rect cropRect = new Rect((int)mCropRect.left,(int)mCropRect.top,(int)mCropRect.right,(int)mCropRect.bottom);
//        mCropCanvas.drawBitmap(mCropBitmap,null,new RectF(0,0,300,300),null);
//        mCropCanvas.restore();

        //drawCropBitmapDirectly(canvas);
    }

    public void drawCropBitmapDirectly(Canvas canvas) {
        //清屏,清除foreBitmap之前上的绘制,因为已经将这些,绘制到mCropBitmap
        paint.setXfermode(DrawMode.CLEAR);
        canvas.drawPaint(paint);
        paint.setXfermode(DrawMode.SRC);
        //绘制裁剪图片
        Rect rect2 = new Rect((int)mDestRect.left,(int)mDestRect.top,(int)mDestRect.right,(int)mDestRect.bottom);
        //Paint paint = new Paint();
        //paint.setColor(Color.RED);
        //canvas.drawRect(rect2,paint);
        canvas.drawBitmap(mCropBitmap, rect2, mDestRect, null);
//        if(currentAngle/90%2==0){
//            Rect rect2 = new Rect((int)rotateRectf.left,(int)rotateRectf.top,(int)rotateRectf.right,(int)rotateRectf.bottom);
//            canvas.drawBitmap(mCropBitmap,rect2,rotateRectf,null);
//        }else {
//            Rect rect2 = new Rect((int)mDestRect.left,(int)mDestRect.top,(int)mDestRect.right,(int)mDestRect.bottom);
//            canvas.drawBitmap(mCropBitmap, rect2, mDestRect, null);
//        }
    }


    @Override
    public void start(Object... params) {
        rotateRectf = (RectF) params[0];
        currentAngle = (float) params[1];
        mDestRect = (RectF) params[2];
        normalRect = (Rect) params[3];
        lastNormalRectf = (RectF) params[4];
        lastScaleRectf = (RectF) params[5];
        lastRotateRectf = (RectF) params[6];
    }

    public void drawCropMasicBitmapDirectly(Canvas canvas) {
        //清屏,清除mBehindBitmap之前上的绘制,因为已经将这些,绘制到mCropBitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        //绘制裁剪图片
        if(currentAngle/90%2==0){
            Rect rect2 = new Rect((int)rotateRectf.left,(int)rotateRectf.top,(int)rotateRectf.right,(int)rotateRectf.bottom);
            canvas.drawBitmap(mCropMasicBitmap,rect2,rotateRectf,null);
        }else {
            Rect rect2 = new Rect((int)mDestRect.left,(int)mDestRect.top,(int)mDestRect.right,(int)mDestRect.bottom);
            canvas.drawBitmap(mCropMasicBitmap, rect2, mDestRect, null);
        }
    }

    @Override
    public void next(Object... params) {
        Canvas canvas = (Canvas) params[0];
        //清屏,清除mCropMasicBitmap之前上的绘制,因为新的绘制,有当前mBehindBitmap决定
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCropMasicCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        //将mBehindBitmap内容绘制到mCropMasicBitmap上
        mCropMasicCanvas.drawBitmap(mBehindBitmap,rect,mDestRect,null);
        drawCropMasicBitmapDirectly(canvas);
    }

    @Override
    public void stop(Object... params) {
        ((ActionImageView.CropSnapshot)params[0]).setCropAction(this);
    }
}
