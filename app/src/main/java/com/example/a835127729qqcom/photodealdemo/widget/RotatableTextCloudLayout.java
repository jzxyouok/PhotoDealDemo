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

import com.example.a835127729qqcom.photodealdemo.dealaction.TextAction;
import com.example.a835127729qqcom.photodealdemo.util.RotateUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by 835127729qq.com on 16/8/24.
 */
public class RotatableTextCloudLayout extends RelativeLayout implements View.OnTouchListener,RotatableEditText.DeleteBtnClickListener {
    private Context mContext;
    ArrayList<RotatableEditText> rotatableEditTexts = new ArrayList<RotatableEditText>();
    ArrayList<TextAction> rotatbleTextActions = new ArrayList<TextAction>();
    private float lastDownX,lastDownY;
    private Rect globalRect = null;
    private FinshAddTextListener mFinshAddTextListener;

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
        //super.onDraw(canvas);
        //use to test,you must change class TouchRotateBtnHandler into public
        Paint p = new Paint();
        p.setColor(Color.GREEN);
        p.setStyle(Paint.Style.FILL);

        for(int i=rotatableEditTexts.size()-1;i>=0;i--){
            if(rotatableEditTexts.get(i).mTouchRotateBtnHandler.rect!=null) {
                Rect r = new Rect(rotatableEditTexts.get(i).mTouchRotateBtnHandler.rect);
                r.offset(0,-globalRect.top);
                canvas.drawRect(r, p);
                rotatableEditTexts.get(i).refreshTextAction();
                rotatableEditTexts.get(i).mTextAction.execute(canvas);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        Log.i("cky","0");

        boolean isChildBeSelected = false;
        //是否点击了子控件
        for(RotatableEditText r:rotatableEditTexts){
            isChildBeSelected = isChildBeSelected || r.isSelected();
            r.hideControlBtn();
        }

        if(isChildBeSelected) return true;
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

    /**
     * 获取在屏幕的位置
     */
    private void getGlobalTop(){
        if(globalRect!=null) return;
        globalRect = new Rect();
        getGlobalVisibleRect(globalRect);
    }

    /**
     * 创建子控件
     * @param x
     * @param y
     */
    private void createRotatableEditText(float x, float y) {
        getGlobalTop();
        //创建控件
        TextAction textAction = new TextAction(globalRect.left,globalRect.top);
        rotatbleTextActions.add(textAction);
        RotatableEditText currentRotatableEditText = new RotatableEditText(mContext,globalRect,textAction,rotatableEditTexts);
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        currentRotatableEditText.setLayoutParams(params);
        params.leftMargin = (int)x;
        params.topMargin = (int)y;
        //删除通知
        currentRotatableEditText.setDeleteBtnClickListener(this);
        //添加控件
        rotatableEditTexts.add(currentRotatableEditText);
        addView(currentRotatableEditText);
    }

    @Override
    public void onClick(RotatableEditText rotatableEditText) {
        rotatableEditTexts.remove(rotatableEditText);
        removeView(rotatableEditText);
    }

    /**
     * 开始
     */
    public void startAddText(){
        setVisibility(VISIBLE);
    }

    /**
     * 结束
     */
    public void finishAddtext(){
        setVisibility(GONE);
        mFinshAddTextListener.onFinish(rotatbleTextActions);
    }

    public interface FinshAddTextListener{
        void onFinish(ArrayList<TextAction> rotatbleTextActions);
    }

    public void setFinshAddTextListener(FinshAddTextListener finshAddTextListener){
        mFinshAddTextListener = finshAddTextListener;
    }
}