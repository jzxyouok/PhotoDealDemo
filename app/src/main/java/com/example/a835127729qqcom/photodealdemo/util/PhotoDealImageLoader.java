package com.example.a835127729qqcom.photodealdemo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 835127729qq.com on 16/10/10.
 * 只能加载本地图片,建议不要在其他地方使用这个loader
 */
public class PhotoDealImageLoader {
    private static final int GL_MAX_TEXTURE_SIZE = 2048;
    private static final int DEFAULT_MAX_SIZE = 720;
    private static final int BUFFER_SIZE = 32 * 1024; // 32 Kb
    private final Bitmap.Config mDecodeConfig = Bitmap.Config.ARGB_8888;
    private int mMaxWidth;
    private int mMaxHeight;
    private int eachCacheSize;
    private static BitmapWorkerTask mBitmapWorkerTask;

    public synchronized static PhotoDealImageLoader getInstance(){
        return new PhotoDealImageLoader();
    }

    private PhotoDealImageLoader(){
    }

    public synchronized void loadBitmap(String url, ImageView imageView,  LoadListener mLoadListener){
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long restMemory = (maxMemory-totalMemory+freeMemory)/ 1024;
        // 使用剩余可用内存值的1/12作为每片缓存最大值,一次编辑需要6片缓存。
        eachCacheSize = (int)(restMemory / 12);
        if (mBitmapWorkerTask != null) {
            mBitmapWorkerTask.cancel(true);
        }
        mBitmapWorkerTask = new BitmapWorkerTask(url, imageView, mLoadListener);
        mBitmapWorkerTask.execute();
    }

    public interface LoadListener{
        public void onStart();
        public void onEnd();
    }

    public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private String filePath;
        private ImageView imageview;
        private LoadListener mLoadListener;
        public BitmapWorkerTask(String url,ImageView imageview,LoadListener mLoadListener){
            filePath = url;
            this.imageview = imageview;
            this.mLoadListener = mLoadListener;
        }

        @Override
        protected void onPreExecute() {
            mLoadListener.onStart();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap res = null;
            try {
                res = InputStream2Bitmap(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageview.setImageBitmap(bitmap);
            mLoadListener.onEnd();
        }
    }

    /**
     * 计算能够使用的最大尺寸
     */
    private void calculateMaxSizeDependOnMemory(int actualWidth,int actualHeight,String imageUri){
        //根据内存大小进行限制
        int bitmapSize = sizeOf(imageUri);
        if(bitmapSize>eachCacheSize){
            float scale = 1.0f*eachCacheSize/bitmapSize;
            mMaxWidth = (int) (scale*actualWidth);
            mMaxHeight = (int) (scale*actualHeight);
        }else{
            mMaxWidth = actualWidth;
            mMaxHeight = actualHeight;
        }
        //根据OPENGL能绘制的最大尺寸,进行限制
        if(mMaxWidth>=GL_MAX_TEXTURE_SIZE){
            mMaxWidth = GL_MAX_TEXTURE_SIZE;
        }
        if(mMaxHeight>=GL_MAX_TEXTURE_SIZE){
            mMaxHeight = GL_MAX_TEXTURE_SIZE;
        }
        //根据自己设定的最大尺寸进行限制
        if(mMaxWidth>=DEFAULT_MAX_SIZE){
            mMaxWidth = DEFAULT_MAX_SIZE;
        }
        if(mMaxHeight>=DEFAULT_MAX_SIZE){
            mMaxHeight = DEFAULT_MAX_SIZE;
        }
        //根据屏幕大小进行限制
        //因为这里需要传入context对象,所有不实现,需要的可以自己实现
    }

    private int sizeOf(String imageUri) {
        return (int) (new File(imageUri).length()/1024);
    }

    private Bitmap InputStream2Bitmap(String filePath) throws IOException {
        InputStream is = getStreamFromFile(filePath);
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inPreferredConfig = mDecodeConfig;
        Bitmap bitmap = null;
        // If we have to resize this image, first get the natural bounds.
        decodeOptions.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeStream(is,null,decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;
        calculateMaxSizeDependOnMemory(actualWidth,actualHeight,filePath);

        // Then compute the dimensions we would ideally like to decode to.
        int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
                actualWidth, actualHeight);
        int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
                actualHeight, actualWidth);

        // Decode to the nearest power of two scaling factor.
        decodeOptions.inJustDecodeBounds = false;
        is.close();
        is = getStreamFromFile(filePath);
        // TODO(ficus): Do we need this or is it okay since API 8 doesn't support it?
        // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
        decodeOptions.inSampleSize =
                findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
        Bitmap tempBitmap = BitmapFactory.decodeStream(is,null,decodeOptions);

        // If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                tempBitmap.getHeight() > desiredHeight)) {
            bitmap = Bitmap.createScaledBitmap(tempBitmap,
                    desiredWidth, desiredHeight, true);
            tempBitmap.recycle();
        } else {
            bitmap = tempBitmap;
        }
        return bitmap;
    }

    // Visible for testing.
    private int findBestSampleSize(
            int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
    }

    private int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                    int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    private InputStream getStreamFromFile(String imageUri) throws IOException {
        BufferedInputStream imageStream = new BufferedInputStream(new FileInputStream(imageUri), BUFFER_SIZE);
        return imageStream;
    }
}
