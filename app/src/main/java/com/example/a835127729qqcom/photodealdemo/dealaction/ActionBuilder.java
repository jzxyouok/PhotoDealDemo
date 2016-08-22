package com.example.a835127729qqcom.photodealdemo.dealaction;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by 835127729qq.com on 16/8/22.
 */
public class ActionBuilder {
    public enum ActionType{
        MARK, MASIC, CROP, ROTATE, TEXT;
    }
    public static Action produceAction(ActionType actionType, Paint paint){
        Action action = null;
        switch (actionType){
            case MARK:
                action = new MarkAction(new Path(),paint);
                break;
            case MASIC:
                action = new MasicAction(new Path(),paint);
                break;
        }
        return action;
    }
}
