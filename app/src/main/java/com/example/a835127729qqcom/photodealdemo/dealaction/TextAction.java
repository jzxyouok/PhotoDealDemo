package com.example.a835127729qqcom.photodealdemo.dealaction;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
        canvas.save();
        canvas.rotate(roatetAngle,rotateCenterX,rotateCenterY);
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
