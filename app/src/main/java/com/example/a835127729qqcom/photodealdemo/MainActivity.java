package com.example.a835127729qqcom.photodealdemo;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.a835127729qqcom.photodealdemo.widget.ColorPickBox;
import com.example.a835127729qqcom.photodealdemo.widget.CropImageView;
import com.example.a835127729qqcom.photodealdemo.widget.EditTextActionLayout;
import com.example.a835127729qqcom.photodealdemo.widget.MasicSizePickBox;
import com.example.a835127729qqcom.photodealdemo.widget.StickerView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    CropImageView cropImageView;
    ActionImageView actionImageView;
    //长图w=2000,h=600
    //String testurl = "http://oss.xlyprint.cn/79e6b1fc-9c28-4d03-b53e-3191f9b96060.jpg";
    //长图w=600,h=2000
    //String testurl = "http://www.qqkubao.com/uploadfile/2016/07/2/20160726094926525.jpg";
    //小图w=50,h=50
    //String testurl = "http://www.xuanbird.com/wp-content/uploads/avatars/1/0d845f23b2a61342e7f9b79e97c5ba3c-bpthumb.jpg";
    //普通图片
    //String testurl = "http://img05.tooopen.com/images/20141101/sy_73835537934.jpg";
    StickerView stickerView;
    EditTextActionLayout editView;
    ColorPickBox mColorPickBox;
    MasicSizePickBox mMasicSizePickBox;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initImageLoader();
        path = getIntent().getStringExtra("path");;
        if(TextUtils.isEmpty(path)){
            finish();
        }
        allFindViewById();
        initImage();
        setupColorPickBox();
        setupMasicSizePickBox();
        addAllListener();

    }

    private void setupMasicSizePickBox() {
        mMasicSizePickBox.init(15,25,35,45,55);
    }

    private void initImage() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.NONE) // default 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default 设置图片的解码类型
                .build();
        ImageLoader.getInstance().displayImage("file://"+path, actionImageView,new ImageLoadingListener(){

            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Log.i("cky",failReason.toString());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                new Thread(){
                    @Override
                    public void run() {
                        actionImageView.setComplete(true);
                        actionImageView.init(path);
                        actionImageView.postInvalidate();
                    }
                }.start();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    private void addAllListener() {
        actionImageView.setmBackTextActionListener(stickerView);
        stickerView.setmTextsControlListener(actionImageView);
        stickerView.setmBeginAddTextListener(editView);
        editView.setmStopAddTextListener(stickerView);
        actionImageView.setmCropActionListener(stickerView);
        actionImageView.setmTextActionCacheQuery(stickerView);
        stickerView.setmCurrentRotateRectQuery(actionImageView);
        mColorPickBox.addColorPickListener(actionImageView);
        mColorPickBox.addColorPickListener(stickerView);
        mColorPickBox.addColorPickListener(editView);
        mMasicSizePickBox.addMasicSizePickListener(actionImageView);
    }

    private void allFindViewById() {
        cropImageView = (CropImageView) findViewById(R.id.crop);
        actionImageView = (ActionImageView) findViewById(R.id.main_image);
        editView = (EditTextActionLayout) findViewById(R.id.edit);
        stickerView = (StickerView) findViewById(R.id.stick);
        mColorPickBox = (ColorPickBox) findViewById(R.id.color_pick_box);
        mMasicSizePickBox = (MasicSizePickBox) findViewById(R.id.masic_size_pick_box);
    }

    private void setupColorPickBox() {
        ArrayList<Integer> arr = new ArrayList<Integer>();
        arr.add(Color.rgb(255,255,255));
        arr.add(Color.rgb(0,0,0));
        arr.add(Color.rgb(196,200,25));
        arr.add(Color.rgb(219,134,0));
        arr.add(Color.rgb(219,0,120));
        arr.add(Color.rgb(152,0,198));
        arr.add(Color.rgb(68,12,203));
        arr.add(Color.rgb(0,173,202));
        arr.add(Color.rgb(0,212,67));
        mColorPickBox.initByInteger(arr);
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

    private void preHide(){
        stickerView.setVisibility(View.GONE);
        cropImageView.setVisibility(View.GONE);
        actionImageView.setEnabled(true);
        actionImageView.setMode(ActionImageView.MODE_IDLE);
        mColorPickBox.setVisibility(View.GONE);
        mMasicSizePickBox.setVisibility(View.GONE);
        stickerView.clearState();
    }

    public void mark(View view){
        Log.i("tag","mark");
        if(!actionImageView.isComplete()) return;
        preHide();
        mColorPickBox.setVisibility(View.VISIBLE);
        actionImageView.setMode(ActionImageView.MODE_MARK);
    }

    public void masic(View view){
        Log.i("tag","masic");
        if(!actionImageView.isComplete()) return;
        preHide();
        mMasicSizePickBox.setVisibility(View.VISIBLE);
        actionImageView.setMode(ActionImageView.MODE_MASIC);
    }

    public void text(View view){
        Log.i("tag","text");
        if(!actionImageView.isComplete()) return;
        preHide();
        actionImageView.setMode(ActionImageView.MODE_TEXT);
        stickerView.setVisibility(View.VISIBLE);
        mColorPickBox.setVisibility(View.VISIBLE);
        actionImageView.setEnabled(false);
    }

    public void crop(View view){
        Log.i("tag","crop");
        if(!actionImageView.isComplete()) return;
        preHide();
        actionImageView.setMode(ActionImageView.MODE_CROP);
        cropImageView.setRatioCropRect(actionImageView.getCurrentRotateRectF(),1);
        cropImageView.setVisibility(View.VISIBLE);
    }

    public void rotate(View view){
        Log.i("tag","rotate");
        if(!actionImageView.isComplete()) return;
        preHide();
        actionImageView.setMode(ActionImageView.MODE_ROTATE);
        actionImageView.rotate(actionImageView.mCurrentAngle+90,cropImageView,stickerView);
    }

    public void back(View view){
        preHide();
        Log.i("tag","back");
        if(!actionImageView.isComplete()) return;
        actionImageView.back();
    }

    public void finish(View view){
        //preHide();
        Log.i("tag","finish");
        if(!actionImageView.isComplete()) return;
        if(actionImageView.getMode()!=ActionImageView.MODE_CROP) return;
        actionImageView.crop(cropImageView.getCropRect());
        actionImageView.setMode(ActionImageView.MODE_IDLE);
        cropImageView.setVisibility(View.GONE);
    }

    public void output(View view){
        Log.i("tag","output");
        if(!actionImageView.isComplete()) return;
        actionImageView.output();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        actionImageView.setComplete(true);
//        actionImageView.init(path);
//        actionImageView.invalidate();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager activityMgr= (ActivityManager) this.getSystemService(ACTIVITY_SERVICE );
        activityMgr.killBackgroundProcesses(getPackageName());
    }
}
