package com.example.a835127729qqcom.photodealdemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 835127729qq.com on 16/8/30.
 */
public class InnerTextView extends View {

    public InnerTextView(Context context) {
        super(context);
    }

    public InnerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InnerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRGB(100,100,100);
    }
}
