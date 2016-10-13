package com.example.a835127729qqcom.photodealdemo.dealaction;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by 835127729qq.com on 16/8/22.
 */
public class TextAction implements Action{
    //裁剪前状态
    public float saveNormalRectF2scaleRectF = 1;
    public float saveAngle = 0;

    private float delAngle;
    private ArrayList<Path> textPaths = new ArrayList<Path>();
    private ArrayList<String> texts = new ArrayList<String>();
    private ArrayList<Line> lines = new ArrayList<Line>();
    private Paint paint;
    private float rectCenterX,rectCenterY;
    //旋转角度
    private float roatetAngle = 0;
    //旋转中心
    private float rotateCenterX,rotateCenterY;
    //字体大小
    public float textSize = 40;
    //画笔颜色
    public int color = Color.WHITE;
    private float[] res = new float[2];
    private static Matrix matrix = new Matrix();
    public float currentNormalRectF2scaleRectF = 1.0f;
//    public float saveCurrentNormalRectF2scaleRectF = 1.0f;

    @Override
    public void execute(Canvas canvas) {
        paint.setTextSize(textSize*currentNormalRectF2scaleRectF);
        paint.setColor(color);
        matrix.reset();
        for(int i=0;i<texts.size();i++){
            Line line = lines.get(i);
            matrix.reset();
            matrix.postScale(currentNormalRectF2scaleRectF,currentNormalRectF2scaleRectF,rectCenterX,rectCenterY);
            matrix.postRotate(delAngle,rectCenterX,rectCenterY);
            float[] startpoint = new float[]{line.startX,line.startY};
            float[] endpoint = new float[]{line.endX,line.endY};
            float[] centerpoint = new float[]{rotateCenterX,rotateCenterY};
            matrix.mapPoints(startpoint);
            matrix.mapPoints(endpoint);
            matrix.mapPoints(centerpoint);

            matrix.reset();
            matrix.postRotate(roatetAngle,centerpoint[0],centerpoint[1]);
            matrix.mapPoints(startpoint);
            matrix.mapPoints(endpoint);

            Path dest = new Path();
            dest.moveTo(startpoint[0],startpoint[1]);
            dest.lineTo(endpoint[0],endpoint[1]);
            canvas.drawTextOnPath(texts.get(i),dest,0,0,paint);
        }

//        matrix.reset();
//        matrix.postRotate(-delAngle,rectCenterX,rectCenterY);
//        matrix.mapPoints(res,new float[]{rotateCenterX,rotateCenterY});
//
//        canvas.save();
//        canvas.translate(res[0]-rotateCenterX,res[1]-rotateCenterY);
//        canvas.rotate(roatetAngle-delAngle,rotateCenterX,rotateCenterY);
//        for(int i=0;i<texts.size();i++){
//            Line line = lines.get(i);
//            paint.setTextSize(textSize);
//            paint.setColor(color);
//            matrix.reset();
//            matrix.postScale(currentNormalRectF2scaleRectF,currentNormalRectF2scaleRectF,rectCenterX,rectCenterY);
//            float[] startpoint = new float[]{line.startX,line.startY};
//            float[] endpoint = new float[]{line.endX,line.endY};
//            matrix.mapPoints(startpoint);
//            matrix.mapPoints(endpoint);
//            Path dest = new Path();
//            dest.moveTo(startpoint[0],startpoint[1]);
//            dest.lineTo(endpoint[0],endpoint[1]);
//            canvas.drawTextOnPath(texts.get(i),dest,0,0,paint);
//        }
//        canvas.restore();

//        matrix.reset();
//        matrix.postRotate(-delAngle,rectCenterX,rectCenterY);
//        matrix.mapPoints(res,new float[]{rotateCenterX,rotateCenterY});
//
//        canvas.save();
//        canvas.translate(res[0]-rotateCenterX,res[1]-rotateCenterY);
//        canvas.rotate(roatetAngle-delAngle,rotateCenterX,rotateCenterY);
//
//        for(int i=0;i<textPaths.size();i++){
//            paint.setTextSize(textSize);
//            paint.setColor(color);
//            canvas.drawTextOnPath(texts.get(i),textPaths.get(i),0,0,paint);
//        }
//        canvas.restore();

//        matrix.reset();
//        matrix.postRotate(-delAngle,rectCenterX,rectCenterY);
//        matrix.mapPoints(res,new float[]{rotateCenterX,rotateCenterY});
//
//        canvas.save();
//        canvas.translate(res[0]-rotateCenterX,res[1]-rotateCenterY);
//        canvas.rotate(roatetAngle-delAngle,rotateCenterX,rotateCenterY);
//        for(int i=0;i<textPaths.size();i++){
//            paint.setTextSize(textSize*currentNormalRectF2scaleRectF);
//            paint.setColor(color);
//            matrix.reset();
//            matrix.postScale(currentNormalRectF2scaleRectF,currentNormalRectF2scaleRectF,rectCenterX,rectCenterY);
//            Path dest = new Path();
//            textPaths.get(i).transform(matrix,dest);
//            canvas.drawTextOnPath(texts.get(i),dest,0,0,paint);
//        }
//        canvas.restore();
    }

    @Override
    public void start(Object... params) {
        delAngle = (float) params[0];
        rectCenterX = (float) params[1];
        rectCenterY = (float) params[2];
        paint = (Paint) params[3];
        currentNormalRectF2scaleRectF = (float) params[4];
        Log.i("cky","delAngle="+delAngle+",currentNormalRectF2scaleRectF="+currentNormalRectF2scaleRectF);
    }

    @Override
    public void next(Object... params) {

    }

    @Override
    public void stop(Object... params) {

    }

    private class Line{
        float startX,startY,endX,endY;

        public Line(float startX, float startY, float endX, float endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public ArrayList<Path> getTextPaths() {
        return textPaths;
    }

    public ArrayList<String> getTexts() {
        return texts;
    }

    public void addLine(float startX, float startY, float endX, float endY){
        lines.add(new Line(startX,startY,endX,endY));
    }

    public void setRoatetAngle(float roatetAngle) {
        this.roatetAngle = roatetAngle;
    }

    public void setRotateCenterX(float rotateCenterX) {
        this.rotateCenterX = rotateCenterX;
    }

    public void setRotateCenterY(float rotateCenterY) {
        this.rotateCenterY = rotateCenterY;
    }
}

