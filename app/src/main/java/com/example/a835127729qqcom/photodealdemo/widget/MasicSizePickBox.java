package com.example.a835127729qqcom.photodealdemo.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 835127729qq.com on 16/10/8.
 */
public class MasicSizePickBox extends View{
    private int mWidth,mHeight;
    private int mWidthWithoutPadding,mHeightWithoutPadding;
    private ArrayList<Integer> sizes = new ArrayList<>();
    private ArrayList<Circle> circles = new ArrayList<>();
    private Paint circlePaint = new Paint();
    //圆形直径和圆形之间距离的比值
    private float paddingScale = 1;
    //最小圆直径和最大圆直径的比值
    private float minSizeCircle2MaxSizeCircle = 0.4f;
    private ArrayList<MasicSizePickListener> listeners = new ArrayList<>();
    private Circle lastCircle = null;
    private Circle currentCircle = null;
    private boolean isChange = false;
    private int color = Color.WHITE;
    private float circleWidth = 1;

    public MasicSizePickBox(Context context) {
        super(context);
    }

    public MasicSizePickBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MasicSizePickBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(ArrayList<Integer> sizes){
        this.sizes.addAll(sizes);
        initPaint();
        intCircles(this.sizes);
    }

    public void init(Integer... sizes){
        this.sizes.addAll(Arrays.asList(sizes));
        initPaint();
        intCircles(this.sizes);
    }

    private void initPaint() {
        circlePaint.setColor(color);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(circleWidth);
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mWidthWithoutPadding = mWidth-getPaddingLeft()-getPaddingRight();
        mHeightWithoutPadding = mHeight-getPaddingTop()-getPaddingBottom();
        if(mHeight<=0||mWidth<=0) return;
        intCircles(this.sizes);
    }

    private void intCircles(List<Integer> sizes) {
        int count = sizes.size();
        if(mHeight<=0||mWidth<=0||count==0) return;
        float scalePower = count==1?1:(1-minSizeCircle2MaxSizeCircle)/(count-1);
        //计算最大圆直径
        float totalScale = 0;
        for(int i=0;i<count;i++){
            totalScale += minSizeCircle2MaxSizeCircle+scalePower*i;
        }
        totalScale += (count-1)*paddingScale;
        float maxRadius = 0.9f*mWidthWithoutPadding/totalScale/2;
        maxRadius = maxRadius>0.9f*mHeightWithoutPadding/2?0.9f*mHeightWithoutPadding/2:maxRadius;
        //计算圆的宽度
        circleWidth = maxRadius*0.05f;
        circlePaint.setStrokeWidth(circleWidth);
        //圆边缘的间距
        float padding = 2*maxRadius*paddingScale;
        //计算最左边的圆的圆心位置
        float start = mWidthWithoutPadding/2.0f;
        float totalLength = 0;
        for(int i=0;i<count;i++){
            totalLength = totalLength + (maxRadius*2)*(minSizeCircle2MaxSizeCircle+scalePower*i);
        }
        totalLength = totalLength + padding*(count-1);
        start = start - totalLength/2 + maxRadius*minSizeCircle2MaxSizeCircle;
        //计算每个圆的坐标和大小
        lastCircle = null;
        circles.clear();
        for(int i=0;i<count;i++){
            Circle circle = new Circle();
            circle.isEnable = false;
            circle.size = sizes.get(i);
            circle.radius = maxRadius*(minSizeCircle2MaxSizeCircle+scalePower*i);
            if(lastCircle==null){
                circle.x = start;
            }else{
                circle.x = lastCircle.x+maxRadius*(2*minSizeCircle2MaxSizeCircle+scalePower*(2*i-1))+padding;
            }
            circle.y = mHeight/2.0f;
            circle.rectF = new RectF(circle.x-circle.radius,circle.y-circle.radius,circle.x+circle.radius,circle.y+circle.radius);
            lastCircle = circle;
            circles.add(circle);
        }
        circles.get(0).isEnable = true;
        currentCircle = circles.get(0);
        notifyAllListener(currentCircle.size);
        lastCircle = null;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(Circle circle:circles){
            canvas.drawCircle(circle.x,circle.y,circle.radius,circlePaint);
            if(circle.isEnable) {
                circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawCircle(circle.x, circle.y, circle.radius, circlePaint);
                circlePaint.setStyle(Paint.Style.STROKE);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastCircle = currentCircle;
                for (Circle circle : circles) {
                    if (circle.rectF.contains(x, y)) {
                        isChange = true;
                        currentCircle = circle;
                        circle.isEnable = true;
                        notifyAllListener(circle.size);
                    } else {
                        circle.isEnable = false;
                    }
                }
                if (!isChange && lastCircle != null) {
                    lastCircle.isEnable = true;
                }
                isChange = false;
                postInvalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    private class Circle{
        float x,y;
        float radius;
        boolean isEnable;
        RectF rectF;
        float size;
    }

    public void setColor(int color) {
        this.color = color;
        circlePaint.setColor(color);
    }

    private void notifyAllListener(float size) {
        for(MasicSizePickListener masicSizePickListener:listeners){
            masicSizePickListener.notify(size);
        }
    }

    public interface MasicSizePickListener{
        void notify(float size);
    }

    public void addMasicSizePickListener(MasicSizePickListener mMasicSizePickListener){
        listeners.add(mMasicSizePickListener);
    }
}
