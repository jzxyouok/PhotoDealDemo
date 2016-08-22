package com.example.a835127729qqcom.photodealdemo.dealaction;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * Created by 835127729qq.com on 16/8/22.
 */
public class MasicAction implements Action{
    Path mPath;
    Paint mPaint;

    public MasicAction(Path path,Paint paint){
        mPaint = paint;
        mPath = path;
    }

    @Override
    public void execute(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawPath(mPath, mPaint);
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
