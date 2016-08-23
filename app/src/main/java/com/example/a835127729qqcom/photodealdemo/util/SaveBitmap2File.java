package com.example.a835127729qqcom.photodealdemo.util;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 835127729qq.com on 16/8/22.
 */
public class SaveBitmap2File {
    public static File saveFile(Bitmap bm, String fileName, String path) throws IOException {
        Log.i("tag",path+"/"+fileName);
        File myCaptureFile = null;
        try {
            String subForder = path;
            File foder = new File(subForder);
            if (!foder.exists()) {
                foder.mkdirs();
            }
            myCaptureFile = new File(subForder, fileName);
            if (!myCaptureFile.exists()) {
                myCaptureFile.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return myCaptureFile;
    }
}
