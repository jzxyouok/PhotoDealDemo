package com.example.a835127729qqcom.photodealdemo.util;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by 835127729qq.com on 16/8/26.
 */
public class RotateUtil {
    /**
     * 某点1围绕点2旋转degree
     * @param target
     * @param source
     * @param degree
     * @return
     */
    public static PointF roationPoint(PointF target, PointF source, double degree) {
        source.x = source.x - target.x;
        source.y = source.y - target.y;
        double alpha = 0;
        double beta = 0;
        PointF result = new PointF();
        double dis = Math.sqrt(source.x*source.x+source.y*source.y);
        if(source.x==0&&source.y==0) {
            return target;
            //第一象限
        } else if(source.x>=0&&source.y>=0) {
            //计算与x正方向的夹角
            alpha = Math.asin(source.y/dis);
            //第二象限
        } else if(source.x<0&&source.y>=0){
            //计算与x正方向的夹角
            alpha = Math.asin(Math.abs(source.x)/dis);
            alpha = alpha+Math.PI/2;
            //第三象限
        } else if(source.x<0&&source.y<0) {
            //计算与x正方向的夹角
            alpha = Math.asin(Math.abs(source.y)/dis);
            alpha = alpha+Math.PI;
        } else if(source.x>=0&&source.y<0) {
            //计算与x正方向的夹角
            alpha = Math.asin(source.x/dis);
            alpha = alpha+Math.PI*3/2;

        }
        //弧度换算成角度
        alpha = radianToDegree(alpha);
        beta = alpha+degree;
        //角度转弧度
        beta = degreeToRadian(beta);
        result.x = (int) Math.round(dis*Math.cos(beta));
        result.y = (int) Math.round(dis*Math.sin(beta));
        result.x += target.x;
        result.y += target.y;

        return result;
    }

    public static double radianToDegree(double radian) {
        return radian*180/Math.PI;
    }

    public static double degreeToRadian(double degree) {
        return degree*Math.PI/180;
    }

    /**
     * 矩形绕指定点旋转
     *
     * @param rect
     * @param roatetAngle
     */
    public static void rotateRect(RectF rect, float center_x, float center_y,
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

    /**
     * 矩形绕指定点旋转
     *
     * @param rect
     * @param roatetAngle
     */
    public static void rotateRect(Rect rect, float center_x, float center_y,
                                  float roatetAngle) {
        float x = rect.centerX();
        float y = rect.centerY();
        float sinA = (float) Math.sin(Math.toRadians(roatetAngle));
        float cosA = (float) Math.cos(Math.toRadians(roatetAngle));
        float newX = center_x + (x - center_x) * cosA - (y - center_y) * sinA;
        float newY = center_y + (y - center_y) * cosA + (x - center_x) * sinA;

        float dx = newX - x;
        float dy = newY - y;

        rect.offset((int)dx, (int)dy);
    }
}
