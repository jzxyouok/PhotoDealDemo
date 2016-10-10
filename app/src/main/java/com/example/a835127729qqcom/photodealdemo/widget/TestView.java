package com.example.a835127729qqcom.photodealdemo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.a835127729qqcom.photodealdemo.R;
import com.example.a835127729qqcom.photodealdemo.util.DrawMode;

/**
 * Created by 835127729qq.com on 16/10/10.
 */
public class TestView extends View{

    private Canvas canvas2;
    private Bitmap bitmap;
    private RectF rectF;
    private boolean done;
    private Bitmap bitmap2;
    Paint mClearPaint = new Paint();
    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.testicon,options);
        bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.testicon,options);
        canvas2 = new Canvas(bitmap2);
        rectF = new RectF(0,0,bitmap2.getWidth(),bitmap2.getHeight());
        done = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(done) {
            mClearPaint.setXfermode(DrawMode.CLEAR);
            canvas2.drawPaint(mClearPaint);
            mClearPaint.setXfermode(DrawMode.SRC);
            canvas2.drawBitmap(bitmap,0,0,null);
            canvas2.save();
            canvas2.clipRect(new Rect(0, 0, 100, 100));
            canvas2.drawBitmap(bitmap2, null, rectF, null);
            canvas2.restore();
            canvas.drawBitmap(bitmap2, null, rectF, null);
        }
    }
}
