package com.example.a835127729qqcom.photodealdemo.widget.listener;

import com.example.a835127729qqcom.photodealdemo.dealaction.TextAction;

/**
 * Created by 835127729qq.com on 16/9/21.
 * ActionImageView通知StickerView,因为back操作,某个textAction被删除,
 * 对应的stickeritem也应该被删除
 */
public interface BackTextActionListener{
    void onBackTextAction(TextAction action);
}

