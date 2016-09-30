package com.example.a835127729qqcom.photodealdemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by 835127729qq.com on 16/9/29.
 */
public class ColorPickBox extends View{
    private int mWidth,mHeight;
    private int mWidthWithoutPadding,mHeightWithoutPadding;
    private ArrayList<Integer> colors = new ArrayList<>();
    private ArrayList<Circle> circles = new ArrayList<>();
    private Paint circlePaint = new Paint();
    //圆形大小和圆形之间距离的比值
    private float paddingScale = 1;
    private float innerScale = 0.7f;
    private float aroundWidthScale = 0.09f;
    private ArrayList<ColorPickListener> listeners = new ArrayList<>();
    public ColorPickBox(Context context) {
        super(context);
    }

    public ColorPickBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorPickBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initByInteger(ArrayList<Integer> colors){
        this.colors.addAll(colors);
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
        intCircles();
    }

    public void initByString(ArrayList<String> colors){
        for(String color:colors) {
            this.colors.add(Color.parseColor(color));
        }
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
        intCircles();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mWidthWithoutPadding = mWidth-getPaddingLeft()-getPaddingRight();
        mHeightWithoutPadding = mHeight-getPaddingTop()-getPaddingBottom();
        if(mHeight<=0||mWidth<=0) return;
        intCircles();
    }

    private void intCircles() {
        if(mHeight<=0||mWidth<=0||colors.size()==0) return;
        float radius = mWidthWithoutPadding/(colors.size()+(colors.size()-1)*paddingScale)/2;
        radius = radius>mHeightWithoutPadding/2?mHeightWithoutPadding/2:radius;
        float padding = radius*paddingScale;
        float diameter = 2*radius+padding;
        float start = radius;
        if(colors.size()%2==1){//奇数
            start = mWidthWithoutPadding/2.0f-(colors.size()-1)/2*diameter;
        }else{
            start = mWidthWithoutPadding/2.0f-(colors.size()/2-1)*diameter-diameter/2;
        }
        circles.clear();
        for(int i=0;i<colors.size();i++){
            Circle circle = new Circle();
            circle.color = colors.get(i);
            circle.isEnable = false;
            circle.radius = radius;
            circle.innerRadius = radius*innerScale;
            circle.x = start+diameter*i;
            circle.y = mHeight/2.0f;
            circle.aroundWidth = radius*aroundWidthScale;
            circle.rectF = new RectF(circle.x-radius,circle.y-radius,circle.x+radius,circle.y+radius);
            circles.add(circle);
        }
        circles.get(0).isEnable = true;
        notifyAllListener(circles.get(0).color);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(Circle circle:circles){
            circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            circlePaint.setColor(circle.color);
            canvas.drawCircle(circle.x,circle.y,circle.innerRadius,circlePaint);
            if(circle.isEnable) {
                circlePaint.setStrokeWidth(circle.aroundWidth);
                circlePaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(circle.x, circle.y, circle.radius-circle.aroundWidth/2, circlePaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        for(Circle circle:circles){
            if(circle.rectF.contains(x,y)){
                circle.isEnable = true;
                notifyAllListener(circle.color);
            }else{
                circle.isEnable = false;
            }
        }
        postInvalidate();
        return super.onTouchEvent(event);
    }

    private void notifyAllListener(int color) {
        for(ColorPickListener colorPickListener:listeners){
            colorPickListener.notify(color);
        }
    }

    private class Circle{
        RectF rectF;
        float x,y;
        int color;
        float radius;
        float innerRadius;
        float aroundWidth;
        boolean isEnable = false;
    }

    public interface ColorPickListener{
        void notify(int color);
    }

    public void addColorPickListener(ColorPickListener mColorPickListener){
        listeners.add(mColorPickListener);
    }
}
