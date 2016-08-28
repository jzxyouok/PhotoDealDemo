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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.a835127729qqcom.photodealdemo.R;
import com.example.a835127729qqcom.photodealdemo.dealaction.TextAction;
import com.example.a835127729qqcom.photodealdemo.util.RotateUtil;
import com.xinlan.imageeditlibrary.editimage.utils.Matrix3;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by 835127729qq.com on 16/8/24.
 */
public class RotatableEditText extends RelativeLayout implements View.OnTouchListener{
    private Context mContext;
    private ImageButton deleteBtn,rotateBtn;
    private TextView rotateTextView;
    private EditText rotateEditText;
    private RelativeLayout textLayout;
    private DeleteBtnClickListener mDeleteBtnClickListener;
    private ViewConfiguration mViewConfiguration;
    private int touchSlop;
    private Rect mParentRect;
    public TouchRotateBtnHandler mTouchRotateBtnHandler = new TouchRotateBtnHandler();
    public TextAction mTextAction;
    private ArrayList<RotatableEditText> mRotatableEditTexts;

    //是否被选中
    private boolean isSelected = true;

    //减少对象的创建,防止内存抖动
    PointF pf1 = new PointF();
    PointF pf2 = new PointF();

    public RotatableEditText(Context context,Rect parentRect,TextAction textAction,ArrayList<RotatableEditText> rotatableEditTexts) {
        super(context);
        mParentRect = parentRect;
        mTextAction = textAction;
        mRotatableEditTexts = rotatableEditTexts;
        init(context);
    }

    public RotatableEditText(Context context) {
        super(context);
        init(context);
    }

    public RotatableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RotatableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        mViewConfiguration = ViewConfiguration.get(mContext);
        touchSlop = mViewConfiguration.getScaledTouchSlop();
        setOnTouchListener(this);
        initViews();
        setUpDeleteBtn();
        setUpRotateEditText();
    }

    private void initViews(){
        LayoutInflater.from(mContext).inflate(R.layout.rotatable_edittext, this,true);
        textLayout = (RelativeLayout) findViewById(R.id.text_layout);
        rotateTextView = (TextView) findViewById(R.id.rotate_textview);
        rotateEditText = (EditText) findViewById(R.id.rotate_edittext);
        deleteBtn = (ImageButton) findViewById(R.id.delete_btn);
        rotateBtn = (ImageButton) findViewById(R.id.rotate_btn);
        rotateBtn.setEnabled(false);
        rotateBtn.setClickable(false);
    }

    private float lastX,lastY;
    private int lastLeftMargin,lastTopMargin;
    //0初始状态,1旋转或者拉伸
    private int stage = 0;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.i("cky","1");

        clearEditFocus();
        //清除其他子控件状态
        for(RotatableEditText r:mRotatableEditTexts){
            if(r==this){
                r.showControlBtn();
            } else {
                r.hideControlBtn();
            }
        }

        int action = motionEvent.getAction();
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        float x= motionEvent.getX();
        float y = motionEvent.getY();

        if(!mTouchRotateBtnHandler.isInit){
            mTouchRotateBtnHandler.init();
        }
        if(isTouchRotateBtn((int)x,(int) y)){
            mTouchRotateBtnHandler.handleTouchRotateBtn(view,motionEvent);
            return true;
        }
        if(stage==1){//手指离开旋转按钮,但是没有松手,说明还在旋转或拉伸状态,继续处理
            mTouchRotateBtnHandler.handleTouchRotateBtn(view,motionEvent);
            return true;
        }

        RelativeLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        //其他范围拖动
        switch (action){
            case MotionEvent.ACTION_DOWN:
                lastX = rawX;
                lastY = rawY;
                lastLeftMargin = params.leftMargin;
                lastTopMargin = params.topMargin;
                mTouchRotateBtnHandler.changeLastRect();
                //Log.i("cky","按下 x="+rawX+",y="+rawY);
                break;
            case MotionEvent.ACTION_MOVE:
                move(rawX,rawY);
                break;
            case MotionEvent.ACTION_UP:
                move(rawX,rawY);
                mTouchRotateBtnHandler.changeLastRect();
                edit(rawX,rawY,x,y);
                break;
        }

        return true;
    }

    /**
     * 移动控件
     * @param rawX
     * @param rawY
     */
    private void move(float rawX,float rawY){
        RelativeLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        int newLeftMargin = (int) (lastLeftMargin + rawX - lastX);
        int newTopMargin = (int) (lastTopMargin + rawY - lastY);

        if(isOutOfParent(rawX - lastX,rawY - lastY)) return;

        params.setMargins(newLeftMargin,newTopMargin, params.rightMargin,params.bottomMargin);
        mTouchRotateBtnHandler.changeRect((int)(rawX - lastX),(int)(rawY - lastY));
        setLayoutParams(params);
        postInvalidate();
    }

    /**
     * 编辑
     * @param rawX
     * @param rawY
     * @param x
     * @param y
     */
    private void edit(float rawX,float rawY,float x,float y){
        if(lastX==rawX&&lastY==rawY&&isTouchTextLayout((int)x,(int)y)) {
            rotateTextView.setVisibility(GONE);
            rotateEditText.setVisibility(VISIBLE);
            rotateEditText.requestFocus();
            ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(rotateEditText, 0);
        }
    }

    private boolean isTouchRotateBtn(int x,int y){
        Rect rect = new Rect(rotateBtn.getLeft(),rotateBtn.getTop(),rotateBtn.getRight(),rotateBtn.getBottom());
        return rect.contains(x,y);
    }

    private boolean isTouchTextLayout(int x,int y){
        Rect rect = new Rect(rotateTextView.getLeft(),rotateTextView.getTop(),rotateTextView.getRight(),rotateTextView.getBottom());
        return rect.contains(x,y);
    }

    private void setUpRotateEditText(){
        rotateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                rotateTextView.setText(editable.toString());
            }
        });
    }

    /**
     * 删除按钮
     */
    private void setUpDeleteBtn(){
        deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeleteBtnClickListener.onClick(RotatableEditText.this);
            }
        });
    }


    /**
     * 判断是否被拖出边界
     * @return
     */
    public boolean isOutOfParent(float offsetLeft,float offsetTop){
        PointF[] points = mTouchRotateBtnHandler.getRotateRect();
        for(PointF point:points){
            if(!mParentRect.contains((int)(point.x+offsetLeft),(int) (point.y+offsetTop))){
                return true;
            }
        }
        return false;
    }


    /**
     * 清除edittext选中状态,显示textview
     * @return
     */
    public boolean clearEditFocus(){
        boolean flag = rotateEditText.hasFocus();
        rotateTextView.setVisibility(VISIBLE);
        rotateEditText.setVisibility(GONE);
        rotateEditText.clearFocus();
        return flag;
    }

    public boolean containXY(float x, float y){
        RectF rectF = new RectF(getLeft(), getTop(), getRight(), getBottom());
        return rectF.contains(x,y);
    }

    /**
     * 处理旋和转拉
     */
    public class TouchRotateBtnHandler{
        //是否初始化
        boolean isInit = false;
        int mWidth,mHeight;
        int centerX,centerY;
        //缩放前的范围矩形
        Rect lastRect;
        //当前的范围矩形
        public Rect rect;
        float textSize = 0;
        //当前旋转到角度
        double currentAngle = 0;
        //起始角度,因为旋转按钮和中心本来就存在一定角度
        double startAngle = 0;
        //四个点,表示旋转和拉伸以后的矩形
        PointF[] points = new PointF[4];
        /**
         * 只初始化一次
         */
        public void init(){
            isInit = true;
            mWidth = rotateTextView.getMeasuredWidth();
            mHeight = rotateTextView.getMeasuredHeight();
            rect = new Rect();
            lastRect = rect;
            (RotatableEditText.this).getGlobalVisibleRect(rect);
            centerX = rect.centerX();
            centerY = rect.centerY();
            startAngle = Math.toDegrees(Math.atan(1.0d*(centerY-rect.bottom)/(rect.right-centerX)));
            textSize = rotateTextView.getTextSize();
            //初始化一次
            points[0] = new PointF(rect.left,rect.top);
            points[1] = new PointF(rect.left,rect.bottom);
            points[2] = new PointF(rect.right,rect.top);
            points[3] = new PointF(rect.right,rect.bottom);
        }

        /**
         * 旋转,缩放,移动开始或者完毕,更新矩阵信息
         */
        private void changeLastRect(){
            lastRect = rect;
        }

        /**
         * 更新范围矩阵相关信息
         * @param leftOffset
         * @param topOffset
         */
        public void changeRect(int leftOffset,int topOffset){
            rect = new Rect(lastRect.left+leftOffset,lastRect.top+topOffset,lastRect.right+leftOffset,lastRect.bottom+topOffset);
            centerX = rect.centerX();
            centerY = rect.centerY();
            textSize = rotateTextView.getTextSize();
        }

        private void rotate(float x,float y){
            double angle = 0;
            float dx = x - centerX;
            float dy = y -centerY;
            if(dx < 0){
                if(dy<0){
                    angle = 180-Math.toDegrees(Math.atan(dy/dx));
                }else if(dy==0){
                    angle = 180;
                }else{
                    angle = Math.toDegrees(Math.atan(dy/-dx))+180;
                }
            }else if(dx == 0){
                if(dy > 0) angle = 270;
                else angle = 90;
            }else{
                if(dy<0){
                    angle = Math.toDegrees(Math.atan(-dy/dx));
                }else if(dy==0){
                    angle = 0;
                }else{
                    angle = 360-Math.toDegrees(Math.atan(dy/dx));
                }
            }
            currentAngle = angle - startAngle;
            //判断旋转是否超出范围
            if(isOutOfParent(0,0)){
                return;
            }

            //og.i("cky","deangle="+currentAngle);
            RotatableEditText.this.setRotation(-(float) (currentAngle));
        }

        private void scale(float x,float y){
            if(Math.abs(x-lastX)<touchSlop||Math.abs(y-lastY)<touchSlop){
                return;
            }
            float mw = lastRect.width()/2 - rotateBtn.getWidth()/2;
            float mh = lastRect.height()/2 - rotateBtn.getHeight()/2;
            //Log.i("cky1","rotatebtn w="+mw+",h="+mh);
            double halfSrcLen = Math.sqrt(mw*mw+mh*mh);

            double srcLen = halfSrcLen;
            float xa = x - lastRect.centerX();
            float ya = y - lastRect.centerY();
            double destLen = Math.sqrt(xa*xa + ya*ya);//+halfSrcLen;
            float scaleX = (float) (destLen/srcLen);
            float scaleY = scaleX;

            if(scaleX < 0.5) return;

            int newWidth = (int) (mWidth*scaleX)/2*2;
            int newHeight = (int) (mHeight*scaleX)/2*2;
            int delWidth = (newWidth - mWidth)/2*2;
            int delHeight = (newHeight-mHeight)/2*2;

            newWidth = delWidth + mWidth;
            newHeight = delHeight + mHeight;

            //判断拉伸是否超出范围
            if(isOutOfParent(delWidth/2,delHeight/2)){
                return;
            }

            //缩放rect
            rect = new Rect(lastRect.left-delWidth/2,lastRect.top-delHeight/2,lastRect.right+delWidth/2,lastRect.bottom+delHeight/2);

            //平移
            rotateTextView.setWidth(newWidth);
            rotateTextView.setHeight(newHeight);
            rotateEditText.setWidth(newWidth);
            rotateEditText.setHeight(newHeight);
            //todo 字体大小变化
            //rotateTextView.setTextSize(textSize*scaleX);

            RelativeLayout.LayoutParams params = (LayoutParams) getLayoutParams();
            params.setMargins(lastLeftMargin-delWidth/2,lastTopMargin-delHeight/2,
                    params.rightMargin,params.bottomMargin);
            setLayoutParams(params);
        }

        public void handleTouchRotateBtn(View view, MotionEvent motionEvent){
            int action = motionEvent.getAction();
            float x = motionEvent.getRawX();
            float y = motionEvent.getRawY();

            switch (action){
                case MotionEvent.ACTION_DOWN:
                    stage = 1;
                    changeLastRect();

                    RelativeLayout.LayoutParams params = (LayoutParams) getLayoutParams();
                    lastLeftMargin = params.leftMargin;
                    lastTopMargin = params.topMargin;
                    mWidth = rotateTextView.getMeasuredWidth();
                    mHeight = rotateTextView.getMeasuredHeight();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //rotate
                    rotate(x,y);
                    //scale
                    scale(x,y);
                    break;
                case MotionEvent.ACTION_UP:
                    rotate(x,y);
                    scale(x,y);
                    changeLastRect();
                    stage = 0;
                    break;
            }
            postInvalidate();
        }

        /**
         * 获取旋转后的rect的四个顶点
         * @return
         */

        public PointF[] getRotateRect(){
            //if(stage==0) return points;
            pf1.set(rect.centerX(),rect.centerY());
            pf2.set(rect.left,rect.top);
            points[0] = RotateUtil.roationPoint(pf1,pf2,currentAngle);
            pf2.set(rect.left,rect.bottom);
            points[1] = RotateUtil.roationPoint(pf1,pf2,currentAngle);
            pf2.set(rect.right,rect.top);
            points[2] = RotateUtil.roationPoint(pf1,pf2,currentAngle);
            pf2.set(rect.right,rect.bottom);
            points[3] = RotateUtil.roationPoint(pf1,pf2,currentAngle);
            return points;
        }
    }

    /**
     * 获取最新textAction
     */
    public void refreshTextAction(){
        //更新字体和内容
        mTextAction.setmContent(rotateTextView.getText().toString());
        mTextAction.setTextSize(rotateTextView.getTextSize());
        //计算坐标
        int offsetLeft = textLayout.getLeft() + rotateTextView.getLeft();
        int offsetTop = textLayout.getTop() + rotateTextView.getTop();
        Rect textRect = new Rect(mTouchRotateBtnHandler.rect.left+offsetLeft,mTouchRotateBtnHandler.rect.top+offsetTop,
                mTouchRotateBtnHandler.rect.right-offsetLeft,mTouchRotateBtnHandler.rect.bottom-offsetTop);
        pf1 = new PointF(textRect.centerX(),textRect.centerY());
        pf2.set(textRect.left,textRect.top);
        PointF lt = RotateUtil.roationPoint(pf1,pf2,-mTouchRotateBtnHandler.currentAngle);
        //lt.offset(0,rotateTextView.getTextSize()/2);
        pf2.set(textRect.left,textRect.bottom);
        PointF lb = RotateUtil.roationPoint(pf1,pf2,-mTouchRotateBtnHandler.currentAngle);
//        lb.offset(0,rotateTextView.getTextSize()/2);
        pf2.set(textRect.right,textRect.top);
        PointF rt = RotateUtil.roationPoint(pf1,pf2,-mTouchRotateBtnHandler.currentAngle);
        //rt.offset(0,rotateTextView.getTextSize()/2);
        pf2.set(textRect.right,textRect.bottom);
        PointF rb = RotateUtil.roationPoint(pf1,pf2,-mTouchRotateBtnHandler.currentAngle);
        //rb.offset(0,rotateTextView.getTextSize()/2);
        //text起始坐标
        PointF leftP = new PointF((lt.x+lb.x)/2,(lt.y+lb.y)/2);
        PointF rightP = new PointF((rt.x+rb.x)/2,(rt.y+rb.y)/2);
        //计算数字大小引起的偏移
        float moveLeft = (float)(mTextAction.getTextSize()/2*Math.sin(Math.toRadians(mTouchRotateBtnHandler.currentAngle)));
        float moveTop = (float)(mTextAction.getTextSize()/2*Math.cos(Math.toRadians(mTouchRotateBtnHandler.currentAngle)));
        mTextAction.setmPath(leftP.x+moveLeft, leftP.y+moveTop,
                rightP.x+moveLeft,
                rightP.y+moveTop);

    }

    public void hideControlBtn(){
        isSelected = false;
        deleteBtn.setVisibility(INVISIBLE);
        rotateBtn.setVisibility(INVISIBLE);
        rotateTextView.setVisibility(VISIBLE);
        rotateEditText.setVisibility(GONE);
        rotateEditText.clearFocus();
    }

    public void showControlBtn(){
        isSelected = true;
        deleteBtn.setVisibility(VISIBLE);
        rotateBtn.setVisibility(VISIBLE);
    }

    /**
     * 通知父控件删除自己
     */
    public interface DeleteBtnClickListener{
        void onClick(RotatableEditText rotatableEditText);
    }

    public void setDeleteBtnClickListener(DeleteBtnClickListener deleteBtnClickListener){
        mDeleteBtnClickListener = deleteBtnClickListener;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    public TouchRotateBtnHandler getmTouchRotateBtnHandler() {
        return mTouchRotateBtnHandler;
    }
}
