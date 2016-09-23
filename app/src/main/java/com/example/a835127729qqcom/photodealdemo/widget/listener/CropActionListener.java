package com.example.a835127729qqcom.photodealdemo.widget.listener;

import android.graphics.RectF;

/**
 * Created by 835127729qq.com on 16/9/21.
 * ActionImageView通知StickerView,发生裁剪操作
 */
public interface CropActionListener {
    void onCrop();
    void onCropBack(RectF rectF);
}
