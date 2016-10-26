package com.example.a835127729qqcom.photodealdemo.util;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by 835127729qq.com on 16/10/26.
 */
public class RectUtil {
    public static Rect changeRectF2Rect(RectF rectF){
        return new Rect((int)rectF.left,(int)rectF.top,(int)rectF.right,(int)rectF.bottom);
    }

    public static RectF changeRect2RectF(Rect rect){
        return new RectF(rect);
    }
}
