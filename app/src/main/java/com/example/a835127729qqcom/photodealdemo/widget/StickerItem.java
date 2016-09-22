package com.example.a835127729qqcom.photodealdemo.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.example.a835127729qqcom.photodealdemo.dealaction.TextAction;

import java.util.ArrayList;
import java.util.List;


/**
 * @author panyi
 */
public class StickerItem {
    /**
     * 最小缩放比例
     */
    private static final float MIN_SCALE = 0.15f;
    //帮助工具间距
    private static final int HELP_BOX_PAD = 25;
    //帮助按钮大小
    private static final int BUTTON_WIDTH = 25;
    public static final int defaultStickerItemWidth = 350;
    public static final int defaultStickerItemHeight = 200;

    private Rect helpToolsRect; //编辑工具位置

    public RectF dstRect;// 绘制目标坐标
    public RectF deleteRect;// 删除按钮位置
    public RectF rotateRect;// 旋转按钮位置
    public RectF helpBox;

    public RectF detectRotateRect;
    public RectF detectDeleteRect;

    private Paint dstPaint = new Paint();
    private Paint helpBoxPaint = new Paint();
    private Paint toolPaint = new Paint();
    private Paint textPaint = new Paint();

    private static Bitmap deleteBit;
    private static Bitmap rotateBit;

    // 加入屏幕时原始宽度
    private float initWidth;
    // 变化矩阵
    public Matrix matrix;
    private float roatetAngle = 0;
    //是否显示编辑工具
    boolean isDrawHelpTool = false;
    Path path = new Path();
    //文字大小
    private float textSize = 40;
    //文字间距大小
    private float lineMargin = 5;
    /**
     * 文字编辑抽象
     */
    private TextAction mTextAction;
    private ArrayList<String> textContents  = new ArrayList<>();
    private ArrayList<String> tipContents  = new ArrayList<>();

    public StickerItem(Context context,TextAction textAction) {
        mTextAction = textAction;
        helpBoxPaint.setColor(Color.BLACK);
        helpBoxPaint.setStyle(Style.STROKE);
        helpBoxPaint.setAntiAlias(true);
        helpBoxPaint.setStrokeWidth(4);

        dstPaint = new Paint();
        dstPaint.setColor(Color.RED);
        dstPaint.setAlpha(120);

        toolPaint = new Paint();
        toolPaint.setColor(Color.GREEN);
        toolPaint.setAlpha(120);

        textPaint = new Paint();
        textPaint.setStyle(Style.FILL_AND_STROKE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.WHITE);

        // 导入工具按钮位图
        if (deleteBit == null) {
            deleteBit = BitmapFactory.decodeResource(context.getResources(),
                    com.xinlan.imageeditlibrary.R.drawable.sticker_delete);
        }// end if
        if (rotateBit == null) {
            rotateBit = BitmapFactory.decodeResource(context.getResources(),
                    com.xinlan.imageeditlibrary.R.drawable.sticker_rotate);
        }// end if
        tipContents.add("请输入文字");
    }

    public void init(@Nullable Rect addBit, View parentView) {
        if(addBit==null) {
            addBit = new Rect(0,0,defaultStickerItemWidth,defaultStickerItemHeight);
            int bitWidth = Math.min(addBit.width(), parentView.getWidth() >> 1);
            int bitHeight = (int) bitWidth * addBit.height() / addBit.width();
            int left = (parentView.getWidth() >> 1) - (bitWidth >> 1);
            int top = (parentView.getHeight() >> 1) - (bitHeight >> 1);
            this.dstRect = new RectF(left, top, left + bitWidth, top + bitHeight);
        }else{
            dstRect = new RectF(addBit.left,addBit.top,addBit.right,addBit.bottom);
        }
        this.matrix = new Matrix();
        this.matrix.postTranslate(this.dstRect.left, this.dstRect.top);

        initWidth = this.dstRect.width();// 记录原始宽度

        this.isDrawHelpTool = true;
        this.helpBox = new RectF(this.dstRect);
        updateHelpBoxRect();

        helpToolsRect = new Rect(0, 0, deleteBit.getWidth(),
                deleteBit.getHeight());

        deleteRect = new RectF(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH, helpBox.left + BUTTON_WIDTH, helpBox.top
                + BUTTON_WIDTH);
        rotateRect = new RectF(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH, helpBox.right + BUTTON_WIDTH, helpBox.bottom
                + BUTTON_WIDTH);

        detectRotateRect = new RectF(rotateRect);
        detectDeleteRect = new RectF(deleteRect);
        calculateTextAction();
    }

    private void updateHelpBoxRect() {
        this.helpBox.left -= HELP_BOX_PAD;
        this.helpBox.right += HELP_BOX_PAD;
        this.helpBox.top -= HELP_BOX_PAD;
        this.helpBox.bottom += HELP_BOX_PAD;
    }

    /**
     * 位置更新
     *
     * @param dx
     * @param dy
     */
    public void updatePos(final float dx, final float dy) {
        this.matrix.postTranslate(dx, dy);// 记录到矩阵中

        dstRect.offset(dx, dy);

        // 工具按钮随之移动
        helpBox.offset(dx, dy);
        deleteRect.offset(dx, dy);
        rotateRect.offset(dx, dy);

        this.detectRotateRect.offset(dx, dy);
        this.detectDeleteRect.offset(dx, dy);
        calculateTextAction();
    }

    /**
     * 旋转 缩放 更新
     *
     * @param dx
     * @param dy
     */
    public void updateRotateAndScale(final float oldx, final float oldy,
                                     final float dx, final float dy) {
        float c_x = dstRect.centerX();
        float c_y = dstRect.centerY();

        float x = this.detectRotateRect.centerX();
        float y = this.detectRotateRect.centerY();

        // float x = oldx;
        // float y = oldy;

        float n_x = x + dx;
        float n_y = y + dy;

        float xa = x - c_x;
        float ya = y - c_y;

        float xb = n_x - c_x;
        float yb = n_y - c_y;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);

        // System.out.println("srcLen--->" + srcLen + "   curLen---->" +
        // curLen);

        float scale = curLen / srcLen;// 计算缩放比

        float newWidth = dstRect.width() * scale;
        if (newWidth / initWidth < MIN_SCALE) {// 最小缩放值检测
            return;
        }

        textSize = textSize*scale;
        lineMargin = lineMargin*scale;

        this.matrix.postScale(scale, scale, this.dstRect.centerX(),
                this.dstRect.centerY());// 存入scale矩阵
        scaleRect(this.dstRect, scale);// 缩放目标矩形

        // 重新计算工具箱坐标
        helpBox.set(dstRect);
        updateHelpBoxRect();// 重新计算
        rotateRect.offsetTo(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH);
        deleteRect.offsetTo(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH);

        detectRotateRect.offsetTo(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH);
        detectDeleteRect.offsetTo(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH);

        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1)
            return;
        float angle = (float) Math.toDegrees(Math.acos(cos));
        // System.out.println("angle--->" + angle);

        // 拉普拉斯定理
        float calMatrix = xa * yb - xb * ya;// 行列式计算 确定转动方向

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        roatetAngle += angle;
        this.matrix.postRotate(angle, this.dstRect.centerX(),
                this.dstRect.centerY());

        rotateRect(this.detectRotateRect, this.dstRect.centerX(),
                this.dstRect.centerY(), roatetAngle);
        rotateRect(this.detectDeleteRect, this.dstRect.centerX(),
                this.dstRect.centerY(), roatetAngle);
        calculateTextAction();
    }

    public void draw(Canvas canvas) {
        //canvas.drawRect(this.dstRect, dstPaint);
        if (this.isDrawHelpTool) {// 绘制辅助工具线
            canvas.save();
            canvas.rotate(roatetAngle, helpBox.centerX(), helpBox.centerY());
            canvas.drawRoundRect(helpBox, 10, 10, helpBoxPaint);
            // 绘制工具按钮
            canvas.drawBitmap(deleteBit, helpToolsRect, deleteRect, null);
            canvas.drawBitmap(rotateBit, helpToolsRect, rotateRect, null);
            canvas.restore();

            // canvas.drawRect(deleteRect, dstPaint);
            // canvas.drawRect(rotateRect, dstPaint);
            //canvas.drawRect(detectRotateRect, this.toolPaint);
            //canvas.drawRect(detectDeleteRect, this.toolPaint);
        }// end if
        drawText(canvas);
    }

    /**
     * 绘制文字
     * @param canvas
     */
    private void drawText(Canvas canvas){
        realDrawText(canvas);
    }

    public void calculateTextAction(){
        ArrayList<String> texts = null;
        if(textContents.isEmpty()) {
            texts = tipContents;
        }else{
            texts = textContents;
        }
        mTextAction.getTextPaths().clear();
        mTextAction.getTexts().clear();
        int numOfTextLine = texts.size();
        if(numOfTextLine==0){
            return;
        }
        reCalculteTextSize(numOfTextLine,texts);
        //绘制文字
        float left = dstRect.centerX()-dstRect.width()/2;
        float right = dstRect.centerX()+dstRect.width()/2;
        float centerY = dstRect.centerY() + textSize/2;

        int topOfCenterLineNum;
        int bottomOfCenterLineNum;
        float topCenterY = centerY;
        float bottomCenterY = centerY;
        if(numOfTextLine%2==1){
            topCenterY = centerY;
            bottomCenterY = centerY + textSize + lineMargin;
            int centerLineNum = (numOfTextLine+1)/2;
            topOfCenterLineNum = centerLineNum-1;
            bottomOfCenterLineNum = centerLineNum;
        }else{
            topCenterY = centerY - textSize/2 - lineMargin;
            bottomCenterY = centerY + textSize/2 + lineMargin;
            topOfCenterLineNum = numOfTextLine/2-1;
            bottomOfCenterLineNum = numOfTextLine/2;
        }

        int topIndex = topOfCenterLineNum;
        while (topIndex >= 0){
            path.reset();
            path.moveTo(left,topCenterY + (textSize + lineMargin) * (topIndex-topOfCenterLineNum));
            path.lineTo(right,topCenterY + (textSize + lineMargin) * (topIndex-topOfCenterLineNum));
            mTextAction.getTextPaths().add(new Path(path));
            mTextAction.getTexts().add(texts.get(topIndex));
            topIndex--;
        }
        int bottomIndex = bottomOfCenterLineNum;
        while (bottomIndex<numOfTextLine){
            path.reset();
            path.moveTo(left,bottomCenterY + (textSize + lineMargin) * (bottomIndex-bottomOfCenterLineNum));
            path.lineTo(right,bottomCenterY + (textSize + lineMargin) * (bottomIndex-bottomOfCenterLineNum));
            mTextAction.getTextPaths().add(new Path(path));
            mTextAction.getTexts().add(texts.get(bottomIndex));
            bottomIndex++;
        }
        mTextAction.setTextSize(textSize);
        mTextAction.setRoatetAngle(roatetAngle);
        mTextAction.setRotateCenterX(helpBox.centerX());
        mTextAction.setRotateCenterY(helpBox.centerY());
    }

    public void realDrawText(Canvas canvas){
        textPaint.setTextSize(textSize);
        canvas.save();
        canvas.rotate(roatetAngle, helpBox.centerX(), helpBox.centerY());
        for(int i=0;i<mTextAction.getTextPaths().size();i++){
            canvas.drawTextOnPath(mTextAction.getTexts().get(i),mTextAction.getTextPaths().get(i),0,0,textPaint);
        }
        canvas.restore();
    }

    /**
     * 计算字体大小
     * @param numOfTextLine
     */
    private void reCalculteTextSize(int numOfTextLine, ArrayList<String> texts){
        if(numOfTextLine<=0) return;
        //根据高度,计算字体
        float percent = lineMargin / textSize;
        if(textSize * numOfTextLine + lineMargin*(numOfTextLine-1) > dstRect.height()){
            textSize = dstRect.height() / (numOfTextLine + percent*numOfTextLine - percent);
            lineMargin = textSize*percent;
        }
        //根据宽度计算字体
        int maxlen = 0;
        for(String text : texts){
            maxlen = Math.max(text.length(),maxlen);
        }
        if(textSize*maxlen>dstRect.width()){
            textSize = dstRect.width()/maxlen;
        }
    }

    /**
     * 缩放指定矩形
     *
     * @param rect
     * @param scale
     */
    private static void scaleRect(RectF rect, float scale) {
        float w = rect.width();
        float h = rect.height();

        float newW = scale * w;
        float newH = scale * h;

        float dx = (newW - w) / 2;
        float dy = (newH - h) / 2;

        rect.left -= dx;
        rect.top -= dy;
        rect.right += dx;
        rect.bottom += dy;
    }

    /**
     * 矩形绕指定点旋转
     *
     * @param rect
     * @param roatetAngle
     */
    private static void rotateRect(RectF rect, float center_x, float center_y,
                                   float roatetAngle) {
        float x = rect.centerX();
        float y = rect.centerY();
        float sinA = (float) Math.sin(Math.toRadians(roatetAngle));
        float cosA = (float) Math.cos(Math.toRadians(roatetAngle));
        float newX = center_x + (x - center_x) * cosA - (y - center_y) * sinA;
        float newY = center_y + (y - center_y) * cosA + (x - center_x) * sinA;

        float dx = newX - x;
        float dy = newY - y;

        rect.offset(dx, dy);
    }

    public TextAction getmTextAction() {
        return mTextAction;
    }

    /**
     * 改变文字内容
     * @param con
     */
    public void refreshTextContent(List<String> con){
        textContents.clear();
        textContents.addAll(con);
        calculateTextAction();
    }

    public String getContents(){
        StringBuilder sb = new StringBuilder();
        for(String con : textContents){
            sb.append(con).append("\n");
        }
        if(!TextUtils.isEmpty(sb.toString())) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
