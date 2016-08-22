package com.example.a835127729qqcom.photodealdemo.dealaction;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by 835127729qq.com on 16/8/22.
 */
public interface Action {
    public void execute(Canvas canvas);

    public void start(Object... params);

    public void next(Object... params);

    public void stop(Object... params);
}
