package com.example.a835127729qqcom.photodealdemo.util;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * Created by 835127729qq.com on 16/9/27.
 */
public final class DrawMode {
    public final static PorterDuffXfermode CLEAR = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    public final static PorterDuffXfermode SRC = new PorterDuffXfermode(PorterDuff.Mode.SRC);
    public final static PorterDuffXfermode DST_OUT = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
}
