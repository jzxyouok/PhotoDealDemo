package com.example.a835127729qqcom.photodealdemo.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.example.a835127729qqcom.photodealdemo.ActionImageView.BackTextActionListener;
import com.example.a835127729qqcom.photodealdemo.dealaction.TextAction;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 贴图操作控件
 *
 * @author panyi
 */
public class StickerView extends View implements BackTextActionListener{
    private static int STATUS_IDLE = 0;
    private static int STATUS_MOVE = 1;// 移动状态
    private static int STATUS_DELETE = 2;// 删除状态
    private static int STATUS_ROTATE = 3;// 图片旋转状态

    private int itemCount;// 已加入照片的数量
    private Context mContext;
    private int currentStatus;// 当前状态
    private StickerItem currentItem;// 当前操作的贴图数据
    private float oldx, oldy;

    private LinkedHashMap<Integer, StickerItem> bank = new LinkedHashMap<Integer, StickerItem>();// 存贮每层贴图数据
    private LinkedHashMap<TextAction, StickerItem> textActionStickItemMap = new LinkedHashMap<TextAction, StickerItem>();// 存贮每层贴图数据

    private float lastDownX, lastDownY;
    private boolean isDown = false;
    /**
     * 图片编辑完成
     */
    private TextsControlListener mTextsControlListener;

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
        for (Integer id : bank.keySet()) {
            StickerItem item = bank.get(id);
            item.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(bank.size()==0){
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
                for (Integer id : bank.keySet()) {
                    StickerItem item = bank.get(id);
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

                if(currentItem==null){
                    isDown = true;
                }

                if (!ret && currentItem != null && currentStatus == STATUS_IDLE) {// 没有贴图被选择
                    currentItem.isDrawHelpTool = false;
                    ret = true;
                    currentItem = null;
                    invalidate();
                }

                if (deleteId > 0 && currentStatus == STATUS_DELETE) {// 删除选定贴图
                    StickerItem item = bank.remove(deleteId);
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
                if(isDown && lastDownX ==x && lastDownY ==y){//判断是点击事件
                    addTextRect(new Rect((int) (x-StickerItem.defaultStickerItemWidth/2), (int) (y-StickerItem.defaultStickerItemHeight/2),
                            (int) (StickerItem.defaultStickerItemWidth/2+x), (int) (StickerItem.defaultStickerItemHeight/2+y)));
                    isDown = false;
                }
                ret = true;
                currentStatus = STATUS_IDLE;
                break;
        }
        return true;
    }

    /**
     * 添加文字矩形
     * @param textRect
     */
    public void addTextRect(@Nullable final Rect textRect) {
        TextAction textAction = new TextAction();
        StickerItem item = new StickerItem(this.getContext(),textAction);
        textActionStickItemMap.put(textAction,item);
        item.init(textRect, this);
        if (currentItem != null) {
            currentItem.isDrawHelpTool = false;
        }
        currentItem = item;
        bank.put(++itemCount, item);
        onFinishAddText(textAction);
        this.invalidate();// 重绘视图
    }

    @Override
    public void onBackTextAction(TextAction action) {
        StickerItem item = textActionStickItemMap.remove(action);
        bank.values().remove(item);
        postInvalidate();
    }

    public void onFinishAddText(TextAction textAction){
        mTextsControlListener.onFinishAddText(textAction);
    }

    public void setmTextsControlListener(TextsControlListener mTextsControlListener) {
        this.mTextsControlListener = mTextsControlListener;
    }

}// end class
