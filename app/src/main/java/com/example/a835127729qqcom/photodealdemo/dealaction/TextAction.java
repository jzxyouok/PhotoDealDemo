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

import java.util.ArrayList;

/**
 * Created by 835127729qq.com on 16/8/22.
 */
public class TextAction implements Action{
    private float delAngle;
    private ArrayList<Path> textPaths = new ArrayList<Path>();
    private ArrayList<String> texts = new ArrayList<String>();
    private static Paint paint = new Paint();
    private Bitmap textBitmap,forBitmap;
    private RectF rectF;
    static {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    //旋转角度
    private float roatetAngle = 0;
    //旋转中心
    private float rotateCenterX,rotateCenterY;
    //字体大小
    float textSize;
    //画笔颜色
    private int color = Color.WHITE;

    @Override
    public void execute(Canvas canvas) {
//        Canvas c = new Canvas(textBitmap);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        c.drawPaint(paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
//        c.save();
//        c.rotate(delAngle,rectF.centerX(),rectF.centerY());
//        Rect r = new Rect((int)rectF.left,(int)rectF.top,(int)rectF.right,(int)rectF.bottom);
//        c.drawBitmap(forBitmap,r,rectF,null);
//        c.restore();
//
//        c.save();
//        c.rotate(roatetAngle,rotateCenterX,rotateCenterY);
//        for(int i=0;i<textPaths.size();i++){
//            paint.setTextSize(textSize);
//            paint.setColor(color);
//            c.drawTextOnPath(texts.get(i),textPaths.get(i),0,0,paint);
//        }
//        c.restore();
//
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        canvas.drawPaint(paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
//        canvas.save();
//        canvas.rotate(-delAngle,rectF.centerX(),rectF.centerY());
//        canvas.drawBitmap(textBitmap,r,rectF,null);
//        canvas.restore();

        Matrix m = new Matrix();
        m.postRotate(-delAngle,rectF.centerX(),rectF.centerY());
        float[] res = new float[2];
        m.mapPoints(res,new float[]{rotateCenterX,rotateCenterY});

        canvas.save();
        canvas.translate(res[0]-rotateCenterX,res[1]-rotateCenterY);
        //canvas.translate(res[0],res[1]);
        canvas.rotate(roatetAngle,res[0],res[1]);
        for(int i=0;i<textPaths.size();i++){
            paint.setTextSize(textSize);
            paint.setColor(color);
            canvas.drawTextOnPath(texts.get(i),textPaths.get(i),0,0,paint);
        }
        canvas.restore();
    }

    @Override
    public void start(Object... params) {
        delAngle = (float) params[0];
        textBitmap = (Bitmap) params[1];
        forBitmap = (Bitmap) params[2];
        rectF = (RectF) params[3];
    }

    @Override
    public void next(Object... params) {

    }

    @Override
    public void stop(Object... params) {

    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setColor(int color) {
        this.color = color;
    }


    public ArrayList<Path> getTextPaths() {
        return textPaths;
    }

    public ArrayList<String> getTexts() {
        return texts;
    }

    public void setRoatetAngle(float roatetAngle) {
        this.roatetAngle = roatetAngle;
    }

    public void setRotateCenterX(float rotateCenterX) {
        this.rotateCenterX = rotateCenterX;
    }

    public void setRotateCenterY(float rotateCenterY) {
        this.rotateCenterY = rotateCenterY;
    }
}
