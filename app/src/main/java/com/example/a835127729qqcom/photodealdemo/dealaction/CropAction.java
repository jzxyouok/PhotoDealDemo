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
import com.example.a835127729qqcom.photodealdemo.util.RectUtil;
import com.example.a835127729qqcom.photodealdemo.util.SaveBitmap2File;

import java.io.IOException;

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
    public RectF mCropRectF;
    //裁剪后的矩阵
    private RectF rotateRectf;
    public RectF scaleRect;
    public RectF normalRectF;
    //裁剪前的矩阵
    private Rect lastNormalRect;
    private RectF lastScaleRectf;

    public float angle;
    private static Paint paint = new Paint();
    static {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.TRANSPARENT);
    }

    public CropAction(float centerx,float centery,RectF cropRect, Bitmap cropBitmap, Bitmap foreBitmap, Canvas croprCanvas,
                      Bitmap cropMasicBitmap, Bitmap behindBitmap, Canvas cropMasicCanvas,float angle){
        this.angle = angle;
        centerX = centerx;
        centerY = centery;
        mCropBitmap = cropBitmap;
        mforeBitmap = foreBitmap;
        mCropCanvas = croprCanvas;
        mCropMasicBitmap = cropMasicBitmap;
        mBehindBitmap = behindBitmap;
        mCropMasicCanvas = cropMasicCanvas;
        mCropRectF = new RectF(cropRect);
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
        mCropCanvas.rotate(angle,centerX,centerY);
        mCropCanvas.drawBitmap(mforeBitmap,lastNormalRect,lastScaleRectf,null);
        mCropCanvas.restore();

//        paint.setColor(Color.GREEN);
//        mCropCanvas.drawRect(normalRectF,paint);
//        paint.setColor(Color.RED);
//        mCropCanvas.drawRect(scaleRect,paint);
        Log.i("cky","lastnormal width="+lastNormalRect.width()+",height="+lastNormalRect.height());
        Log.i("cky","lastscale width="+lastScaleRectf.width()+",height="+lastScaleRectf.height());
//        try {
//            SaveBitmap2File.saveFile(mCropBitmap,"/storage/emulated/0/ActionImage","ttt.png");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        drawCropBitmapDirectly(canvas);
    }

    private void drawCropBitmapDirectly(Canvas canvas) {
        //清屏,清除foreBitmap之前上的绘制,因为已经将这些,绘制到mCropBitmap
        paint.setXfermode(DrawMode.CLEAR);
        canvas.drawPaint(paint);
        paint.setXfermode(DrawMode.SRC);
        //绘制裁剪图片
//        canvas.drawRGB(255,255,0);
        if(angle/90%2==0){
            canvas.drawBitmap(mCropBitmap,mCropRect,normalRectF,null);
        }else{
            canvas.save();
            canvas.rotate(-angle,centerX,centerY);
            canvas.drawBitmap(mCropBitmap,mCropRect,scaleRect,null);
            canvas.restore();

//            canvas.drawBitmap(mCropBitmap,mCropRect,normalRectF,null);
//
//            Rect rect = RectUtil.changeRectF2Rect(normalRectF);
//            paint.setXfermode(DrawMode.CLEAR);
//            mCropCanvas.drawPaint(paint);
//            paint.setXfermode(DrawMode.SRC);
//            mCropCanvas.drawBitmap(mforeBitmap,rect,normalRectF,null);
//
////            try {
////                SaveBitmap2File.saveFile(mCropBitmap,"/storage/emulated/0/ActionImage","vvv.png");
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//
//            paint.setXfermode(DrawMode.CLEAR);
//            canvas.drawPaint(paint);
//            paint.setXfermode(DrawMode.SRC);
//            canvas.save();
//            canvas.rotate(-angle,centerX,centerY);
//            canvas.drawBitmap(mCropBitmap,rect,scaleRect,null);
//            canvas.restore();
            //到这来,仍然是完整的图片
//            try {
//                SaveBitmap2File.saveFile(mforeBitmap,"/storage/emulated/0/ActionImage","qqq.png");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        Log.i("ccc","width="+rotateRectf.width()+",height="+rotateRectf.height());
//        try {
//            SaveBitmap2File.saveFile(mforeBitmap,"/storage/emulated/0/ActionImage","kkk.png");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void drawCropBitmapFromCache(Canvas canvas) {
//        new Thread(){
//            @Override
//            public void run() {
//                try {
//                    Log.i("cky","cache");
//                    SaveBitmap2File.saveFile(mCropBitmap,"/storage/emulated/0/ActionImage","kkk.png");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
        //清屏,清除foreBitmap之前上的绘制,因为已经将这些,绘制到mCropBitmap
        paint.setXfermode(DrawMode.CLEAR);
        canvas.drawPaint(paint);
        paint.setXfermode(DrawMode.SRC);
        //绘制裁剪图片
//        canvas.drawRGB(255,255,0);
        if(angle/90%2==0){
            canvas.drawBitmap(mCropBitmap,mCropRect,normalRectF,null);
        }else {
            canvas.save();
            canvas.rotate(-angle, centerX, centerY);
            canvas.drawBitmap(mCropBitmap, mCropRect, scaleRect, null);
            canvas.restore();
        }
    }

    @Override
    public void start(Object... params) {
        currentAngle = (float) params[0];
        rotateRectf = (RectF) params[1];
        scaleRect = (RectF) params[2];
        normalRectF = (RectF) params[5];
        //裁剪前的矩阵
        lastNormalRect = (Rect) params[3];
        lastScaleRectf = (RectF) params[4];
        Log.i("ccc","normalRectF "+normalRectF.toString());
        Log.i("ccc","rotateRectf "+rotateRectf.toString());
        Log.i("ccc","scaleRect "+scaleRect.toString());
        Log.i("ccc","normalRectF width="+normalRectF.width()+",height="+normalRectF.height());
        Log.i("ccc","rotateRectf width="+rotateRectf.width()+",height="+rotateRectf.height());
        Log.i("ccc","scaleRect width="+scaleRect.width()+",height="+scaleRect.height());
    }

    @Override
    public void next(Object... params) {
        Canvas canvas = (Canvas) params[0];
        //清屏,清除mCropMasicBitmap之前上的绘制,因为新的绘制,有当前mBehindBitmap决定
        paint.setXfermode(DrawMode.CLEAR);
        mCropMasicCanvas.drawPaint(paint);
        paint.setXfermode(DrawMode.SRC);
        //将mBehindBitmap内容绘制到mCropMasicBitmap上
        mCropMasicCanvas.save();
        mCropMasicCanvas.rotate(angle,centerX,centerY);
        mCropMasicCanvas.drawBitmap(mBehindBitmap,lastNormalRect,lastScaleRectf,null);
        mCropMasicCanvas.restore();
        drawCropMasicBitmapDirectly(canvas);
    }


    private void drawCropMasicBitmapDirectly(Canvas canvas) {
        //清屏,清除mBehindBitmap之前上的绘制,因为已经将这些,绘制到mCropBitmap
        paint.setXfermode(DrawMode.CLEAR);
        canvas.drawPaint(paint);
        paint.setXfermode(DrawMode.SRC);
        //绘制裁剪图片
        if(angle/90%2==0){
            canvas.drawBitmap(mCropMasicBitmap,mCropRect,normalRectF,null);
        }else {
            canvas.save();
            canvas.rotate(-angle, centerX, centerY);
            canvas.drawBitmap(mCropMasicBitmap,mCropRect,scaleRect, null);
            canvas.restore();
        }
//        canvas.save();
//        canvas.rotate(-angle,centerX,centerY);
//        canvas.drawBitmap(mCropMasicBitmap,mCropRect,rotateRectf,null);
//        //Log.i("cky","width="+rotateRectf.width()+",h="+rotateRectf.height());
//        canvas.restore();
    }

    public void drawCropMasicBitmapFromCache(Canvas canvas) {
        //清屏,清除mBehindBitmap之前上的绘制,因为已经将这些,绘制到mCropBitmap
        paint.setXfermode(DrawMode.CLEAR);
        canvas.drawPaint(paint);
        paint.setXfermode(DrawMode.SRC);
        //绘制裁剪图片
        if(angle/90%2==0){
            canvas.drawBitmap(mCropMasicBitmap,mCropRect,normalRectF,null);
        }else {
            canvas.save();
            canvas.rotate(-angle, centerX, centerY);
            canvas.drawBitmap(mCropMasicBitmap,mCropRect,scaleRect, null);
            canvas.restore();
        }
    }

    @Override
    public void stop(Object... params) {
        ((ActionImageView.CropSnapshot)params[0]).setCropAction(this);
    }
}
