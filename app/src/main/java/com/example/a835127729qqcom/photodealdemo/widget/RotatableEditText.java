package com.example.a835127729qqcom.photodealdemo.widget;

import android.content.Context;
import android.graphics.Point;
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
        setUpRotateBtn();
        setUpRotateTextView();
        setUpRotateEditText();
    }

    private void initViews(){
        LayoutInflater.from(mContext).inflate(R.layout.rotatable_edittext, this,true);
        textLayout = (RelativeLayout) findViewById(R.id.text_layout);
        rotateTextView = (TextView) findViewById(R.id.rotate_textview);
        rotateEditText = (EditText) findViewById(R.id.rotate_edittext);
        deleteBtn = (ImageButton) findViewById(R.id.delete_btn);
        rotateBtn = (ImageButton) findViewById(R.id.rotate_btn);

    }

    private float lastX,lastY;
    private int lastLeftMargin,lastTopMargin;
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.i("cky","1");

        clearEditFocus();
        //其他范围拖动
        int action = motionEvent.getAction();
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        RelativeLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                lastX = rawX;
                lastY = rawY;
                lastLeftMargin = params.leftMargin;
                lastTopMargin = params.topMargin;
                //Log.i("cky","按下 x="+rawX+",y="+rawY);
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int newLeftMargin = (int) (lastLeftMargin + rawX - lastX);
                int newTopMargin = (int) (lastTopMargin + rawY - lastY);
                if(isOutOfParent(newLeftMargin,newTopMargin)) break;
                //Log.i("cky","移动 x="+rawX+",y="+rawY);
                params.setMargins(newLeftMargin,newTopMargin, params.rightMargin,params.bottomMargin);
                //Log.i("cky","位置 leftMargin="+params.leftMargin+",topMargin="+params.topMargin);
                setLayoutParams(params);
                view.postInvalidate();
                break;
        }
        return true;
    }

    private void setUpRotateTextView(){
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

    private void setUpDeleteBtn(){
        deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("cky","2");
                mDeleteBtnClickListener.onClick(RotatableEditText.this);
            }
        });
    }

    private void setUpRotateBtn(){
        rotateBtn.setOnTouchListener(new OnTouchListener() {
            float lastX,lastY;
            int mWidth,mHeight;
            int centerX,centerY;
            float pivotX,pivotY;
            Rect rect;
            double mCurrentAngle = 0;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Log.i("cky","3");
                int action = motionEvent.getAction();
                float rawX = motionEvent.getRawX();
                float rawY = motionEvent.getRawY();
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                Log.i("cky","x="+x+",y="+y+",rawx="+rawX+",rawY="+rawY);

                Rect rect2 = new Rect();
                Point p = new Point();
                RotatableEditText.this.getGlobalVisibleRect(rect2,p);
                //Log.i("cky","rect="+(rect2)+",point="+p);

                int[] location = new int[2];
                RotatableEditText.this.getLocationOnScreen(location);
                //Log.i("cky","location="+ Arrays.toString(location));
                //Log.i("cky","w="+ RotatableEditText.this.getMeasuredWidth()+",h="+RotatableEditText.this.getMeasuredHeight());

                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        lastX = rawX;
                        lastY = rawY;
                        mWidth = rotateTextView.getMeasuredWidth();
                        mHeight = rotateTextView.getMeasuredHeight();
                        rect = new Rect();
                        RotatableEditText.this.getGlobalVisibleRect(rect);
                        centerX = rect.centerX();
                        centerY = rect.centerY();
                        pivotX = RotatableEditText.this.getPivotX();
                        pivotY = RotatableEditText.this.getPivotY();
                        //Log.i("cky","centerX="+centerX+",PivotX="+R);
                        break;
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                        double angle = 0;
                        float dx = rawX - centerX;
                        float dy = rawY -centerY;
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
                        //Log.i("cky","angle="+angle);
                        double startAngle = Math.toDegrees(Math.atan(1.0f*(centerY-rect.bottom)/(rect.right-centerX)));
                       // Log.i("cky","startangle="+(startAngle));

                        Log.i("cky","deangle="+(angle-startAngle));
                        RotatableEditText.this.setPivotX(pivotX);
                        //RotatableEditText.this.setScaleX();
                        RotatableEditText.this.setPivotY(pivotY);
                        RotatableEditText.this.setRotation(-(float) (angle-startAngle));

                        /*
                        Rect rect = new Rect();
                        RotatableEditText.this.getGlobalVisibleRect(rect);
                        int centerX = (rect.left+rect.right)/2;
                        int centerY = (rect.top+rect.bottom)/2;
                        double angle = Math.toDegrees(Math.atan((centerY-y)/(x-centerX)));
                        RotatableEditText.this.setRotation(-(float) angle);
                        Log.i("cky","angle="+angle);
                        */
                        //int centerX = (left+right)/2;
                        //Log.i("cky","rect="+rect);
                        /*
                        Log.i("cky","lastY="+lastY+",y="+y+",lastX="+lastX+",x="+x);
                        Log.i("cky","tan="+((lastY-y)/(x-lastX)));
                        Log.i("cky","atan="+Math.atan((lastY-y)/(x-lastX)));
                        double angle = Math.toDegrees(Math.atan((lastY-y)/(x-lastX)));
                        Log.i("cky","angle="+angle);
                        double angleCompareBefore = angle-mCurrentAngle;
                        if((angleCompareBefore>0 && angleCompareBefore<90) || (angleCompareBefore>-180 && angleCompareBefore<-90)){//第一和第三象限,旋转
                            mCurrentAngle = angle;
                            RotatableEditText.this.setRotation(-(float) angle);
                            invalidate();
                            break;
                        }
                        */

                        //scale
                        /*
                        int xa = (int) (lastX-rect.centerX());
                        int ya = (int) (lastY-rect.centerY());
                        int xb = (int) (rawX-rect.centerX());
                        int yb = (int) (rawY-rect.centerY());
                        float srcLen = (float) Math.sqrt(xa*xa+ya*ya);
                        float curLen = (float) Math.sqrt(xb*xb+yb*yb);
                        float scale = curLen/srcLen;
                        Log.i("cky","scale="+scale);
                        int newWidth = (int) (mWidth*(scale));
                        int newHeight = (int) (mHeight*(scale));
                        rotateTextView.getMatrix().setScale(scale,scale);
                        */
                        /*
                        rotateTextView.setWidth(newWidth);
                        rotateEditText.setWidth(newWidth);
                        rotateTextView.setHeight(newHeight);
                        rotateEditText.setHeight(newHeight);
                        */
                        invalidate();

                        break;
                }
                return true;
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

    public interface DeleteBtnClickListener{
        public void onClick(RotatableEditText rotatableEditText);
    }

    public void setDeleteBtnClickListener(DeleteBtnClickListener deleteBtnClickListener){
        mDeleteBtnClickListener = deleteBtnClickListener;
    }
}
