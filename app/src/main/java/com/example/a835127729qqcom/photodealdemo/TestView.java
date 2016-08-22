package com.example.a835127729qqcom.photodealdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 835127729qq.com on 16/8/22.
 */
public class TestView extends ImageView{
    Path path = new Path();
    List<Path> paths = new ArrayList<Path>();
    private Paint mBitPaint;
    Bitmap result;
    private Bitmap mBitmap;
    private Bitmap backBitmap;
    private int mBitWidth, mBitHeight;
    private Rect mSrcRect, mDestRect;
    private PorterDuffXfermode mXfermode;

    private Canvas mCanvas;
    private Paint mOutterPaint = new Paint();

    public TestView(Context context) {
        super(context);
        init();
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        initBitmap();
        initPaint();
        initXfermode();
    }

    private void initXfermode() {
        // 叠加处绘制源图
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    private void initPaint() {
        // 初始化paint
        mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);
        mBitPaint.setColor(Color.RED);
    }

    private void initBitmap() {
        // 初始化bitmap
        backBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        mBitWidth = backBitmap.getWidth();
        mBitHeight = backBitmap.getHeight();

        Bitmap srcBitmap = Bitmap.createBitmap(backBitmap.copy(
                Bitmap.Config.RGB_565, true));
        result = PhotoProcessing.filterPhoto(srcBitmap,12);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(backBitmap, mSrcRect, mDestRect, mBitPaint);
        drawPath();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        // 初始化bitmap
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        // 绘制遮盖层
        // mCanvas.drawColor(Color.parseColor("#c0c0c0"));
        mOutterPaint.setStyle(Paint.Style.FILL);
        mCanvas.drawBitmap(result,0,0,mOutterPaint);
        //mCanvas.drawRoundRect(new RectF(0, 0, width, height), 30, 30, mOutterPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                Log.i("tag","down");
                path = new Path();
                paths.add(path);
                path.moveTo(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("tag","move");
                path.lineTo(event.getX(),event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                Log.i("tag","up");
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mSrcRect = new Rect(0,0,mBitWidth,mBitHeight);
        Log.i("tag","w="+w+",h="+h);
        mDestRect = new Rect(0,0,w,h);
        // 初始化bitmap
    }

    public void back(){
        if(paths.size()<=0) return;

        paths.remove(paths.size()-1);
        invalidate();
    }

    private void drawPath() {
        //canvas.drawBitmap(result,mSrcRect, mDestRect,mOutterPaint);
       // mCanvas.drawBitmap(result,mSrcRect, mDestRect,mOutterPaint);
        mOutterPaint.setStyle(Paint.Style.STROKE);
        mOutterPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        for(Path path : paths) {
            mCanvas.drawPath(path, mOutterPaint);
            //canvas.drawPath(path, mBitPaint);
        }
        mCanvas.drawBitmap(result,0,0,null);
        //mCanvas.drawPath(mPath, mOutterPaint);
    }
}
