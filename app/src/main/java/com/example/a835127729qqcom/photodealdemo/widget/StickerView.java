package com.example.a835127729qqcom.photodealdemo.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.example.a835127729qqcom.photodealdemo.dealaction.TextAction;
import com.example.a835127729qqcom.photodealdemo.widget.listener.BackTextActionListener;
import com.example.a835127729qqcom.photodealdemo.widget.listener.BeginAddTextListener;
import com.example.a835127729qqcom.photodealdemo.widget.listener.CropActionListener;
import com.example.a835127729qqcom.photodealdemo.widget.listener.RotateActionListener;
import com.example.a835127729qqcom.photodealdemo.widget.listener.StopAddTextListener;
import com.example.a835127729qqcom.photodealdemo.widget.listener.TextsControlListener;
import com.example.a835127729qqcom.photodealdemo.widget.query.CurrentRotateRectQuery;
import com.example.a835127729qqcom.photodealdemo.widget.query.TextActionCacheQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 贴图操作控件
 */
public class StickerView extends View implements BackTextActionListener,StopAddTextListener,RotateActionListener,
        CropActionListener,TextActionCacheQuery,ColorPickBox.ColorPickListener {
    private static int STATUS_IDLE = 0;
    private static int STATUS_MOVE = 1;// 移动状态
    private static int STATUS_DELETE = 2;// 删除状态
    private static int STATUS_ROTATE = 3;// 图片旋转状态

    private int itemCount;// 已加入text的数量
    private Context mContext;
    private int currentStatus;// 当前状态
    private StickerItem currentItem;// 当前操作的贴图数据
    private float oldx, oldy;
    private int currentColor = Color.WHITE;

    private LinkedHashMap<Integer, StickerItem> stickerItemMap = new LinkedHashMap<Integer, StickerItem>();// 存贮每层贴图数据
    private LinkedHashMap<TextAction, StickerItem> textActionStickItemMap = new LinkedHashMap<TextAction, StickerItem>();// 存贮每层贴图数据

    private float lastDownX, lastDownY;
    private boolean isDown = false;
    /**
     * 图片编辑完成
     */
    private TextsControlListener mTextsControlListener;
    /**
     * 修改文字内容
     * @param context
     */
    private BeginAddTextListener mBeginAddTextListener;
    private CurrentRotateRectQuery mCurrentRotateRectQuery;
    private Matrix rotateMatrix = new Matrix();

    public StickerView(Context context) {
        super(context);
        init(context);
    }

    public StickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        currentStatus = STATUS_IDLE;
    }

    /**
     * 绘制客户页面
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Integer id : stickerItemMap.keySet()) {
            StickerItem item = stickerItemMap.get(id);
            item.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(stickerItemMap.size()==0){
            addTextRect(null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);// 是否向下传递事件标志 true为消耗

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                lastDownX = x;
                lastDownY = y;
                int deleteId = -1;
                for (Integer id : stickerItemMap.keySet()) {
                    StickerItem item = stickerItemMap.get(id);
                    if (item.detectDeleteRect.contains(x, y)) {// 删除模式
                        deleteId = id;
                        currentStatus = STATUS_DELETE;
                    } else if (item.detectRotateRect.contains(x, y)) {// 点击了旋转按钮
                        ret = true;
                        if (currentItem != null) {
                            currentItem.isDrawHelpTool = false;
                        }
                        currentItem = item;
                        currentItem.isDrawHelpTool = true;
                        currentStatus = STATUS_ROTATE;
                        oldx = x;
                        oldy = y;
                    } else if (item.dstRect.contains(x, y)) {// 移动模式
                        // 被选中一张贴图
                        ret = true;
                        if (currentItem != null) {
                            currentItem.isDrawHelpTool = false;
                        }
                        currentItem = item;
                        currentItem.isDrawHelpTool = true;
                        currentStatus = STATUS_MOVE;
                        oldx = x;
                        oldy = y;
                    }
                }// end for each

                if(currentItem==null){//上一次没有贴图被选中
                    isDown = true;
                }

                if (!ret && currentItem != null && currentStatus == STATUS_IDLE) {// 没有贴图被选择
                    currentItem.isDrawHelpTool = false;
                    ret = true;
                    currentItem = null;
                    invalidate();
                }

                if (deleteId > 0 && currentStatus == STATUS_DELETE) {// 删除选定贴图
                    StickerItem item = stickerItemMap.remove(deleteId);
                    textActionStickItemMap.values().remove(item);
                    mTextsControlListener.onDeleteText(item.getmTextAction());
                    currentStatus = STATUS_IDLE;// 返回空闲状态
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                //isDown = false;
                if (currentStatus == STATUS_MOVE) {// 移动贴图
                    float dx = x - oldx;
                    float dy = y - oldy;
                    if(currentItem != null && isOutOfView(dx, dy)) break;
                    if (currentItem != null) {
                        currentItem.updatePos(dx, dy);
                        invalidate();
                    }
                    oldx = x;
                    oldy = y;
                } else if (currentStatus == STATUS_ROTATE) {// 旋转 缩放图片操作
                    // System.out.println("旋转");
                    float dx = x - oldx;
                    float dy = y - oldy;
                    if (currentItem != null) {
                        currentItem.updateRotateAndScale(oldx, oldy, dx, dy);// 旋转
                        invalidate();
                    }
                    oldx = x;
                    oldy = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(lastDownX ==x && lastDownY ==y){
                    if(isDown){//判断是点击事件,点击空白
                        addTextRect(new Rect((int) (x-StickerItem.defaultStickerItemWidth/2), (int) (y-StickerItem.defaultStickerItemHeight/2),
                                (int) (StickerItem.defaultStickerItemWidth/2+x), (int) (StickerItem.defaultStickerItemHeight/2+y)));
                        isDown = false;
                    }else if(currentItem!=null && currentItem.dstRect.contains(x,y)){
                        setVisibility(GONE);
                        mBeginAddTextListener.onStartEditText(currentItem.getContents());
                    }
                }
                ret = true;
                currentStatus = STATUS_IDLE;
                break;
        }
        return true;
    }

    /**
     * 中点是否被拉出看范围
     * @param dx
     * @param dy
     * @return
     */
    private boolean isOutOfView(float dx, float dy) {
        return !mCurrentRotateRectQuery.query().contains(currentItem.dstRect.centerX()+dx,currentItem.dstRect.centerY()+dy);
    }

    /**
     * 添加文字矩形
     * @param textRect
     */
    public void addTextRect(@Nullable final Rect textRect) {
        TextAction textAction = new TextAction();
        textAction.color = currentColor;
        StickerItem item = new StickerItem(this.getContext(),textAction);
        textActionStickItemMap.put(textAction,item);
        item.init(textRect, this);
        if (currentItem != null) {
            currentItem.isDrawHelpTool = false;
        }
        currentItem = item;
        stickerItemMap.put(++itemCount, item);
        onFinishAddText(textAction);
        this.invalidate();// 重绘视图
    }

    @Override
    public void onBackTextAction(TextAction action) {
        StickerItem item = textActionStickItemMap.remove(action);
        stickerItemMap.values().remove(item);
        postInvalidate();
    }

    public void onFinishAddText(TextAction textAction){
        mTextsControlListener.onAddText(textAction);
    }

    public void setmTextsControlListener(TextsControlListener mTextsControlListener) {
        this.mTextsControlListener = mTextsControlListener;
    }

    @Override
    public void onStopEditText(String text,int color) {
        setVisibility(VISIBLE);
        ArrayList<String> arr = new ArrayList<String>();
        if(!TextUtils.isEmpty(text.trim())){
            arr.addAll(Arrays.asList(text.split("\n")));
        }
        if(currentItem!=null) {
            currentItem.refreshTextContent(arr);
            currentItem.getmTextAction().color = color;
            postInvalidate();
        }
    }

    @Override
    public void onRotate(float angle,float nextNormalRectF2scaleRectF) {
        for(StickerItem item : stickerItemMap.values()){
            calculateRotateInfluences(angle, item,nextNormalRectF2scaleRectF);
        }
        for(ArrayList<TextData> datas:textActionCache){
            for(TextData data:datas){
                calculateRotateInfluences(angle, data.item,nextNormalRectF2scaleRectF);
            }
        }
        invalidate();
    }

    @Override
    public void onRotateBack(float angle,float nextNormalRectF2scaleRectF) {
        //// TODO: 16/10/12
        for(StickerItem item : stickerItemMap.values()){
            calculateRotateInfluences(angle, item,nextNormalRectF2scaleRectF);
        }
        for(ArrayList<TextData> datas:textActionCache){
            for(TextData data:datas){
                calculateRotateInfluences(angle, data.item,nextNormalRectF2scaleRectF);
            }
        }
        invalidate();
    }

    private void calculateRotateInfluences(float angle, StickerItem item,float nextNormalRectF2scaleRectF) {
        rotateMatrix.reset();
        //计算新的中心点
        float newCenter[] = new float[2];
        rotateMatrix.postRotate(angle,getMeasuredWidth()/2,getMeasuredHeight()/2);
        rotateMatrix.postScale(nextNormalRectF2scaleRectF,nextNormalRectF2scaleRectF,getMeasuredWidth()/2,getMeasuredHeight()/2);
        rotateMatrix.mapPoints(newCenter,new float[]{item.dstRect.centerX(),item.dstRect.centerY()});
        //平移
        item.updatePos(newCenter[0]-item.dstRect.centerX(),newCenter[1]-item.dstRect.centerY());
        //旋转
        rotateMatrix.reset();
        rotateMatrix.postRotate(angle,newCenter[0],newCenter[1]);
        float[] res = new float[2];
        rotateMatrix.mapPoints(res,new float[]{item.detectRotateRect.centerX(),item.detectRotateRect.centerY()});

        //缩放
        rotateMatrix.reset();
        rotateMatrix.postScale(nextNormalRectF2scaleRectF,nextNormalRectF2scaleRectF,newCenter[0],newCenter[1]);
        rotateMatrix.mapPoints(res);

        item.updateRotateAndScale(item.detectRotateRect.centerX(),item.detectRotateRect.centerY(),
                res[0]-item.detectRotateRect.centerX(),res[1]-item.detectRotateRect.centerY());
        item.calculateTextAction();
    }

    public void setmBeginAddTextListener(BeginAddTextListener mBeginAddTextListener) {
        this.mBeginAddTextListener = mBeginAddTextListener;
    }

    private ArrayList<ArrayList<TextData>> textActionCache = new ArrayList<ArrayList<TextData>>();
    @Override
    public void onCrop() {
        ArrayList<TextData> datas = new ArrayList<TextData>();
        for(Map.Entry<Integer,StickerItem> entry:stickerItemMap.entrySet()){
            datas.add(new TextData(entry.getKey(),entry.getValue().getmTextAction(),entry.getValue()));
        }
        textActionCache.add(datas);
        stickerItemMap.clear();
        textActionStickItemMap.clear();
        postInvalidate();
    }

    @Override
    public void onCropBack(RectF rectF) {
        if(textActionCache.isEmpty()) return;
        ArrayList<TextData> datas = textActionCache.remove(textActionCache.size()-1);
        if(datas.isEmpty()) return;
        LinkedHashMap<Integer,StickerItem> temp = new LinkedHashMap<Integer,StickerItem>();
        temp.putAll(stickerItemMap);//保证原理的插入顺序
        stickerItemMap.clear();
        for(TextData data:datas){
            stickerItemMap.put(data.integer,data.item);
            textActionStickItemMap.put(data.textAction,data.item);
        }
        stickerItemMap.putAll(temp);
        temp.clear();
        postInvalidate();
    }

    @Override
    public boolean query(TextAction textAction) {
        return textActionStickItemMap.keySet().contains(textAction);
    }

    @Override
    public void notifyColorChange(int color) {
        currentColor = color;
        if(currentItem!=null){
            currentItem.getmTextAction().color = color;
            postInvalidate();
        }
    }

    private class TextData{
        public TextData(Integer integer, TextAction textAction, StickerItem item) {
            this.integer = integer;
            this.textAction = textAction;
            this.item = item;
        }

        Integer integer;
        TextAction textAction;
        StickerItem item;
    }

    public void setmCurrentRotateRectQuery(CurrentRotateRectQuery mCurrentRotateRectQuery) {
        this.mCurrentRotateRectQuery = mCurrentRotateRectQuery;
    }

    public void clearState(){
        currentItem = null;
    }
}
