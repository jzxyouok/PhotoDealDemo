package com.example.a835127729qqcom.photodealdemo.dealaction;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by 835127729qq.com on 16/8/22.
 */
public class TextAction implements Action{
    private String mContent;
    private float mAngle;
    private Color color;
    private RectF mRectF;

    @Override
    public void execute(Canvas canvas) {
        canvas.drawText(mContent,0,0,null);
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
