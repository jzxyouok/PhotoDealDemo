package com.example.a835127729qqcom.photodealdemo.util;

import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by 835127729qq.com on 16/8/26.
 */
public class RotateUtil {
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
}
