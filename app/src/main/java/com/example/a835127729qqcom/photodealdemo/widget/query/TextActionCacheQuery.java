package com.example.a835127729qqcom.photodealdemo.widget.query;

import com.example.a835127729qqcom.photodealdemo.dealaction.TextAction;

/**
 * Created by 835127729qq.com on 16/9/21.
 * ActionImageView向StickerView查询,某个textAction是否在可以控制的状态
 */
public interface TextActionCacheQuery {
    boolean query(TextAction textAction);
}
