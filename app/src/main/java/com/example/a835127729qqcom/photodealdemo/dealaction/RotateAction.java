package com.example.a835127729qqcom.photodealdemo.dealaction;

import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by 835127729qq.com on 16/8/22.
 */
public class RotateAction implements Action{
    private float mAngle;
    private RotateActionBackListener mRotateActionBackListener;

    public RotateAction(float angle,RotateActionBackListener cropActionBackListener){
        mAngle = angle;
        mRotateActionBackListener = cropActionBackListener;
    }

    @Override
    public void execute(Canvas canvas) {
    }

    @Override
    public void start(Object... params) {

    }

    @Override
    public void next(Object... params) {

    }

    @Override
    public void stop(Object... params) {
        if(mRotateActionBackListener!=null){
            mRotateActionBackListener.onCropActionBack((RectF) params[0]);
        }
    }

    public float getmAngle() {
        return mAngle;
    }

    public interface RotateActionBackListener {
        void onCropActionBack(RectF destRect);
    }
}
