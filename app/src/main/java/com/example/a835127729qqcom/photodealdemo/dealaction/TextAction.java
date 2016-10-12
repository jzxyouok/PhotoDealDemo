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
    private Paint paint;
    private float rectCenterX,rectCenterY;
    //旋转角度
    private float roatetAngle = 0;
    //旋转中心
    private float rotateCenterX,rotateCenterY;
    //字体大小
    public float textSize = 40;
    //画笔颜色
    public int color = Color.WHITE;
    private float[] res = new float[2];
    private static Matrix matrix = new Matrix();
    private float currentNormalRectF2scaleRectF;

    @Override
    public void execute(Canvas canvas) {
        matrix.reset();
        matrix.postRotate(-delAngle,rectCenterX,rectCenterY);
        matrix.mapPoints(res,new float[]{rotateCenterX,rotateCenterY});

        canvas.save();
        canvas.translate(res[0]-rotateCenterX,res[1]-rotateCenterY);
        canvas.rotate(roatetAngle-delAngle,rotateCenterX,rotateCenterY);

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
        rectCenterX = (float) params[1];
        rectCenterY = (float) params[2];
        paint = (Paint) params[3];
        //currentNormalRectF2scaleRectF = (float) params[4];
    }

    @Override
    public void next(Object... params) {

    }

    @Override
    public void stop(Object... params) {

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

