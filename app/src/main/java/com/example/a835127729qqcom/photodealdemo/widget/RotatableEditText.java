package com.example.a835127729qqcom.photodealdemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
import com.example.a835127729qqcom.photodealdemo.util.RotateUtil;
import com.xinlan.imageeditlibrary.editimage.utils.Matrix3;

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
    private int mParentWidth,mParentHeight;
    TouchRotateBtnHandler mTouchRotateBtnHandler = new TouchRotateBtnHandler();

    public RotatableEditText(Context context,int parentWidth,int parentHeight) {
        super(context);
        mParentHeight = parentHeight;
        mParentWidth = parentWidth;
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
        //setUpRotateBtn();
        //setUpRotateTextView();
        //setUpRotateTextView();
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
    private int stage = 0;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.i("cky","1");

        clearEditFocus();
        int action = motionEvent.getAction();
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        float x= motionEvent.getX();
        float y = motionEvent.getY();
        /*
        Log.i("cky","x="+x+",y="+y+",rawx="+rawX+",rawY="+rawY);
        //处理rotatebtn
        Log.i("cky","x="+x+",y="+y+",rawx="+rawX+",rawY="+rawY);

        Rect rect2 = new Rect();
        Point p = new Point();
        RotatableEditText.this.getGlobalVisibleRect(rect2,p);
        Log.i("cky","rect="+(rect2)+",point="+p);

        int[] location = new int[2];
        RotatableEditText.this.getLocationOnScreen(location);
        Log.i("cky","location="+ Arrays.toString(location));
        Rect r2 = new Rect();
        RotatableEditText.this.getLocalVisibleRect(r2);
        Log.i("cku","LocalVisibleRect="+ r2.toString());
        */
        //RotatableEditText.this.get
        //Log.i("cky","w="+ RotatableEditText.this.getWidth()+",h="+RotatableEditText.this.getHeight());
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

        //其他范围拖动
        RelativeLayout.LayoutParams params = (LayoutParams) getLayoutParams();
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
                int newLeftMargin = (int) (lastLeftMargin + rawX - lastX);
                int newTopMargin = (int) (lastTopMargin + rawY - lastY);
                if(isOutOfParent(newLeftMargin,newTopMargin)) break;
                //Log.i("cky","移动 x="+rawX+",y="+rawY);
                params.setMargins(newLeftMargin,newTopMargin, params.rightMargin,params.bottomMargin);
                mTouchRotateBtnHandler.changeRect((int)(rawX - lastX),(int)(rawY - lastY));
                //Log.i("cky","位置 leftMargin="+params.leftMargin+",topMargin="+params.topMargin);
                setLayoutParams(params);
                view.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                /*
                if(lastX==rawX&&lastY==rawY&&isTouchTextLayout((int)rawX,(int)rawY)) {
                    rotateTextView.setVisibility(GONE);
                    rotateEditText.setVisibility(VISIBLE);
                    rotateEditText.requestFocus();
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(rotateEditText, 0);
                    return false;
                }
                */
                break;
        }
        return true;
    }

    /**
     * 处理旋和转拉
     */
    private class TouchRotateBtnHandler{
        //是否初始化
        boolean isInit = false;
        int mWidth,mHeight;
        int centerX,centerY;
        //缩放前的范围矩形
        Rect lastRect;
        //当前的范围矩形
        Rect rect;
        float textSize = 0;
        //当前旋转到角度
        double currentAngle = 0;
        //起始角度,因为旋转按钮和中心本来就存在一定角度
        double startAngle = 0;

        /**
         * 只初始化一次
         */
        public void init(){
            isInit = true;
            mWidth = rotateTextView.getMeasuredWidth();
            mHeight = rotateTextView.getMeasuredHeight();
            rect = new Rect();
            lastRect = rect;
            //(RotatableEditText.this).getLocalVisibleRect(rect);
            //Log.i("cky","rect1="+rect);
            //((RotatableTextCloudLayout)RotatableEditText.this.getParent()).getChildVisibleRect(RotatableEditText.this,rect,null);
            //Log.i("cky","rect2="+rect);
            (RotatableEditText.this).getGlobalVisibleRect(rect);
            //Log.i("cky","rect3="+rect);
            centerX = rect.centerX();
            centerY = rect.centerY();
            startAngle = Math.toDegrees(Math.atan(1.0d*(centerY-rect.bottom)/(rect.right-centerX)));
            textSize = rotateTextView.getTextSize();
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
            Log.i("cky","deangle="+currentAngle);
            RotatableEditText.this.setRotation(-(float) (currentAngle));
        }

        private void scale(float x,float y){
            if(Math.abs(x-lastX)<touchSlop||Math.abs(y-lastY)<touchSlop){
                return;
            }
            float mw = lastRect.width()/2 - rotateBtn.getWidth()/2;
            float mh = lastRect.width()/2 - rotateBtn.getHeight()/2;

            double halfSrcLen = Math.sqrt(mw*mw+mh*mh);

            double srcLen = 2 * halfSrcLen;
            float xa = x - lastRect.centerX();
            float ya = y - lastRect.centerY();
            double destLen = Math.sqrt(xa*xa + ya*ya)+halfSrcLen;
            float scaleX = (float) (destLen/srcLen);
            float scaleY = scaleX;
            if(scaleX < 0.5) return;
            rotateTextView.setWidth((int) (mWidth*scaleX));
            rotateTextView.setHeight((int) (mHeight*scaleX));
            //rotateTextView.setTextSize(textSize*scaleX);
            rotateEditText.setWidth((int) (mWidth*scaleX));
            rotateEditText.setHeight((int) (mHeight*scaleX));

            //旋转
            PointF lt = RotateUtil.roationPoint(new PointF(lastRect.centerX(),lastRect.centerY()),new PointF(lastRect.left,lastRect.top),currentAngle);
            PointF lb = RotateUtil.roationPoint(new PointF(lastRect.centerX(),lastRect.centerY()),new PointF(lastRect.left,lastRect.bottom),currentAngle);
            PointF rt = RotateUtil.roationPoint(new PointF(lastRect.centerX(),lastRect.centerY()),new PointF(lastRect.right,lastRect.top),currentAngle);
            PointF rb = RotateUtil.roationPoint(new PointF(lastRect.centerX(),lastRect.centerY()),new PointF(lastRect.right,lastRect.bottom),currentAngle);

            PointF rt2 = new PointF(scaleX * (rt.x - lt.x) + lt.x,scaleY * (rt.y - lt.y) + lt.y);
            PointF lb2 = new PointF(scaleX * (lb.x - rb.x) + rb.x,scaleY * (lb.y - rb.y) + rb.y);
            PointF nc = new PointF((rt2.x+lb2.x)/2,(rt2.y+lb2.y)/2);

            //平移
            RelativeLayout.LayoutParams params = (LayoutParams) getLayoutParams();
            params.setMargins((int)(lastLeftMargin-(nc.x-lastRect.centerX())),(int)(lastTopMargin-(nc.y-lastRect.centerY())),
                    params.rightMargin,params.bottomMargin);
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
                    break;
                case MotionEvent.ACTION_MOVE:
                    //rotate
                    rotate(x,y);
                    //scale
                    scale(x,y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    rotate(x,y);
                    scale(x,y);
                    changeLastRect();
                    stage = 0;
                    break;
            }
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

    private void setUpRotateTextView(){
        rotateTextView.setClickable(true);
        rotateTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("cky","点击");
                rotateTextView.setVisibility(GONE);
                rotateEditText.setVisibility(VISIBLE);
                rotateEditText.requestFocus();
                ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(rotateEditText, 0);
            }
        });
        /*
        rotateTextView.setOnTouchListener(new OnTouchListener() {
            private float lastDownX,lastDownY;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                float x = motionEvent.getRawX();
                float y = motionEvent.getRawY();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        lastDownX = x;
                        lastDownY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("cky","lastDownX="+lastDownX+",x="+x+"lastDownY="+lastDownY+",y="+y);
                        if(lastDownX == x && lastDownY==y){//点击
                            Log.i("cky","点击");
                            rotateTextView.setVisibility(GONE);
                            rotateEditText.setVisibility(VISIBLE);
                            rotateEditText.requestFocus();
                            ((InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(rotateEditText, 0);
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
        */
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
    public boolean isOutOfParent(int leftMargin,int topMargin){
        if(leftMargin<0 || topMargin<0 || getWidth()+leftMargin > mParentWidth || getHeight()+topMargin>mParentHeight){
            return true;
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
     * 通知父控件删除自己
     */
    public interface DeleteBtnClickListener{
        void onClick(RotatableEditText rotatableEditText);
    }

    public void setDeleteBtnClickListener(DeleteBtnClickListener deleteBtnClickListener){
        mDeleteBtnClickListener = deleteBtnClickListener;
    }
}
