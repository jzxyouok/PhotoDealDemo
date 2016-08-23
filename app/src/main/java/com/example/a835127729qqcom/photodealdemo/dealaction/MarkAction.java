package com.example.a835127729qqcom.photodealdemo.dealaction;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by 835127729qq.com on 16/8/22.
 */
public class MarkAction implements Action{
    Path mPath;
    Color color;
    Paint mPaint;

    public MarkAction(Path path,Paint paint){
        mPaint = paint;
        mPath = path;
    }

    @Override
    public void execute(Canvas canvas) {
        canvas.drawPath(mPath,mPaint);
        //canvas.drawPath(mPath,mPaint);
    }

    @Override
    public void start(Object... params) {
        mPath.moveTo((float)params[0],(float)params[1]);
    }

    @Override
    public void next(Object... params) {
        mPath.lineTo((float)params[0],(float)params[1]);
    }

    @Override
    public void stop(Object... params) {

    }


}
