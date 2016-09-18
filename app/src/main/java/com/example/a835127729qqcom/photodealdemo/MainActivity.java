package com.example.a835127729qqcom.photodealdemo;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.a835127729qqcom.photodealdemo.widget.StickerView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.xinlan.imageeditlibrary.editimage.view.CropImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    CropImageView cropImageView;
    ActionImageView guaKa;
    String testurl = "http://pic.sc.chinaz.com/files/pic/pic9/201508/apic14052.jpg";
    //String testurl = "http://www.iteye.com/upload/logo/user/254048/1468917d-4784-3baa-a365-68315ed82ebb.jpg?1274705681";
    StickerView stickerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initImageLoader();
        cropImageView = (CropImageView) findViewById(R.id.crop);
        guaKa = (ActionImageView) findViewById(R.id.guagua);

        ImageLoader.getInstance().displayImage(testurl,guaKa,new ImageLoadingListener(){

            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Log.i("cky",failReason.toString());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                guaKa.init();
                guaKa.invalidate();
                //cropImageView.setRatioCropRect(guaKa.getRotatedmRectF(),1);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        stickerView = (StickerView) findViewById(R.id.stick);

        guaKa.setmBackTextActionListener(stickerView);
        stickerView.setmTextsControlListener(guaKa);
    }

    /**
     * 初始化图片载入框架
     */
    private void initImageLoader() {
        File cacheDir = StorageUtils.getCacheDirectory(this);
        int MAXMEMONRY = (int) (Runtime.getRuntime().maxMemory());

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).memoryCacheExtraOptions(480, 800).defaultDisplayImageOptions(defaultOptions)
                .diskCacheExtraOptions(480, 800, null).threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(MAXMEMONRY / 5))
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
                .imageDecoder(new BaseImageDecoder(false)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()).build();

        ImageLoader.getInstance().init(config);
    }

    private void perHide(){
        stickerView.setVisibility(View.GONE);
        cropImageView.setVisibility(View.GONE);
        guaKa.setEnabled(true);
        guaKa.setMode(ActionImageView.MODE_IDLE);
    }

    public void mark(View view){
        Log.i("tag","mark");
        perHide();
        guaKa.setMode(ActionImageView.MODE_MARK);
    }

    public void masic(View view){
        Log.i("tag","masic");
        perHide();
        guaKa.setMode(ActionImageView.MODE_MASIC);
    }

    public void text(View view){
        Log.i("tag","text");
        perHide();
        guaKa.setMode(ActionImageView.MODE_TEXT);
        stickerView.setVisibility(View.VISIBLE);
        guaKa.setEnabled(false);
    }

    public void crop(View view){
        Log.i("tag","crop");
        perHide();
        guaKa.setMode(ActionImageView.MODE_CROP);
        cropImageView.setCropRect(guaKa.getRotatedmRectF());
        cropImageView.setVisibility(View.VISIBLE);
    }

    public void rotate(View view){
        Log.i("tag","rotate");
        perHide();
        guaKa.setMode(ActionImageView.MODE_ROTATE);
        guaKa.rotate(guaKa.mCurrentAngle+90);
    }

    public void back(View view){
        Log.i("tag","back");
        guaKa.back();
    }

    public void finish(View view){
        Log.i("tag","finish");
        if(guaKa.getMode()!=ActionImageView.MODE_CROP) return;
        guaKa.crop(cropImageView.getCropRect());
        guaKa.setMode(ActionImageView.MODE_IDLE);
        cropImageView.setVisibility(View.GONE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

}
