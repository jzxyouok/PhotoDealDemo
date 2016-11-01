package com.example.a835127729qqcom.photodealdemo.util;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

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

    /**
     * 判断p是否在abcd组成的四边形内
     * @param a
     * @param b
     * @param c
     * @param d
     * @param p
     * @return 如果p在四边形内返回true,否则返回false.
     */
    public static boolean pInQuadrangle(PointF a, PointF b, PointF c, PointF d,
                                        PointF p) {
        double dTriangle = triangleArea(a, b, p) + triangleArea(a, c, p)
                + triangleArea(c, d, p) + triangleArea(d, b, p);
        double dQuadrangle = triangleArea(a, b, c) + triangleArea(c, d, b);
        return dTriangle == dQuadrangle;
    }

    // 返回三个点组成三角形的面积
    private static double triangleArea(PointF a, PointF b, PointF c) {
        double result = Math.abs((a.x * b.y + b.x * c.y + c.x * a.y - b.x * a.y
                - c.x * b.y - a.x * c.y) / 2.0D);
        return result;
    }

    public static boolean pInQuadrangle(float ax,float ay,float bx,float by,float cx,float cy,float dx,float dy,
                                        float px,float py) {
        double dTriangle = triangleArea(ax,ay,bx,by,px,py) + triangleArea(ax,ay,cx,cy,px,py)
                + triangleArea(cx,cy,dx,dy,px,py) + triangleArea(dx,dy,bx,by,px,py);
        double dQuadrangle = triangleArea(ax,ay,bx,by,cx,cy) + triangleArea(cx,cy,dx,dy,bx,by);
//        Log.i("cky","dTriangle="+dTriangle+",dQuadrangle="+dQuadrangle);
        return Math.floor(dTriangle) == Math.floor(dQuadrangle);
    }

    // 返回三个点组成三角形的面积
    private static double triangleArea(float ax,float ay,float bx,float by,float cx,float cy) {
        double result = Math.abs((ax * by + bx * cy + cx * ay - bx * ay
                - cx * by - ax * cy) / 2.0D);
        //(ax*by+by*cy+cx*ay-ax*cy-bx*ay-cx*by)/2
        return result;
    }
}
