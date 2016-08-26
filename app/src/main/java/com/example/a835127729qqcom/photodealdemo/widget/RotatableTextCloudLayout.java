package com.example.a835127729qqcom.photodealdemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.a835127729qqcom.photodealdemo.util.RotateUtil;

import java.util.ArrayList;

/**
 * Created by 835127729qq.com on 16/8/24.
 */
public class RotatableTextCloudLayout extends RelativeLayout implements View.OnTouchListener,RotatableEditText.DeleteBtnClickListener {
    private Context mContext;
    ArrayList<RotatableEditText> rotatableEditTexts = new ArrayList<RotatableEditText>();
    private float lastDownX,lastDownY;

    public RotatableTextCloudLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RotatableTextCloudLayout(Context context) {
        super(context);
        init(context);
    }

    public RotatableTextCloudLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        setOnTouchListener(this);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*
        use to test,you must change class TouchRotateBtnHandler into public
        Paint p = new Paint();
        p.setColor(Color.GREEN);
        p.setStyle(Paint.Style.FILL);

        for(int i=rotatableEditTexts.size()-1;i>=0;i--){
            if(rotatableEditTexts.get(i).mTouchRotateBtnHandler.rect!=null) {
                canvas.drawRect(rotatableEditTexts.get(i).mTouchRotateBtnHandler.rect, p);
                p.setColor(Color.BLUE);
                canvas.drawRect(rotatableEditTexts.get(i).mTouchRotateBtnHandler.lastRect, p);
            }
        }
        */
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Log.i("ontouch","left="+getLeft()+",top="+getTop());
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        Log.i("cky","0");

        //是否点击了子控件
        RotatableEditText currentRotatableEditText = null;
        for(int i=rotatableEditTexts.size()-1;i>=0;i--){
            if(rotatableEditTexts.get(i).containXY(x,y)){
                currentRotatableEditText = rotatableEditTexts.get(i);
                break;
            }
        }
        if(currentRotatableEditText==null){//如果不是,新建一个
            boolean isAble2Clear = false;
            //清除edittext状态
            for(RotatableEditText rotatableEditText:rotatableEditTexts){
                isAble2Clear = isAble2Clear || rotatableEditText.clearEditFocus();
            }
            if(isAble2Clear) return true;
            //判断是否点击事件
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    lastDownX = x;
                    lastDownY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    if(lastDownX == x && lastDownY == y){//点击
                        createRotatableEditText(x,y);
                    }
                    break;
            }

            return true;
        }

        //如果不是,新建一个{
            //设置移动边界
        //}
        //如果是,交给RotatableEditText自己处理
        //currentRotatableEditText.handleOnTouch(this,motionEvent);
        //return true;
        return true;
    }

    private void createRotatableEditText(float x, float y) {
        RotatableEditText currentRotatableEditText = new RotatableEditText(mContext,getWidth(),getHeight());
        currentRotatableEditText.setWillNotDraw(false);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        currentRotatableEditText.setLayoutParams(params);
        params.leftMargin = (int)x;
        params.topMargin = (int)y;

        currentRotatableEditText.setDeleteBtnClickListener(this);

        rotatableEditTexts.add(currentRotatableEditText);
        addView(currentRotatableEditText);
    }

    @Override
    public void onClick(RotatableEditText rotatableEditText) {
        rotatableEditTexts.remove(rotatableEditText);
        removeView(rotatableEditText);
    }
}