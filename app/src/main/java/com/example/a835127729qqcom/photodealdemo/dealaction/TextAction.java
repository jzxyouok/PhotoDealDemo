package com.example.a835127729qqcom.photodealdemo.dealaction;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by 835127729qq.com on 16/8/22.
 */
public class TextAction implements Action{
    private String mContent = "";
    private int color = Color.WHITE;
    private Path mPath = new Path();
    private float textSize = 10;
    private float offsetTop = 0;
    private float offsetLeft = 0;
    private static Paint paint = new Paint();

    public TextAction(int left, int top) {
        offsetLeft = left;
        offsetTop = top;
    }


    @Override
    public void execute(Canvas canvas) {
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(color);
        canvas.drawTextOnPath(mContent,mPath, 0,0,paint);
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

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public void setmPath(float sx,float sy,float ex,float ey) {
        mPath.reset();
        mPath.moveTo(sx-offsetLeft,sy-offsetTop);
        mPath.lineTo(ex-offsetLeft,ey-offsetTop);
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getTextSize() {
        return textSize;
    }

}
