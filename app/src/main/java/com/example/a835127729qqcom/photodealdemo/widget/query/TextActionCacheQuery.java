package com.example.a835127729qqcom.photodealdemo.widget.query;

import com.example.a835127729qqcom.photodealdemo.dealaction.TextAction;

/**
 * Created by 835127729qq.com on 16/9/21.
 * ActionImageView向StickerView查询,某个textAction是否在可以控制的状态
 */
public interface TextActionCacheQuery {
    /**
     * textAction是否保证在当前操作队列中(也就是不在缓存中)
     * @param textAction
     * @return
     */
    boolean query(TextAction textAction);

    /**
     * 是否所有操作都在缓存中
     * @return
     */
    boolean isCurrentEmpty();
}
