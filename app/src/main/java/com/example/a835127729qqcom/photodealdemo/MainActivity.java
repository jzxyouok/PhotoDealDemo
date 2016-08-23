package com.example.a835127729qqcom.photodealdemo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.a835127729qqcom.photodealdemo.util.SaveBitmap2File;
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
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    CropImageView cropImageView;
    ActionImageView guaKa;
    String testurl = "http://img3.duitang.com/uploads/item/201406/18/20140618204206_J3yFZ.jpeg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initImageLoader();
        cropImageView = (CropImageView) findViewById(R.id.crop);

        //testView = (TestView) findViewById(R.id.test);
        guaKa = (ActionImageView) findViewById(R.id.guagua);
        //guaKa.setEnabled(false);
        ImageLoader.getInstance().displayImage(testurl,guaKa,new ImageLoadingListener(){

            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Log.i("cky","com");
                guaKa.produceMasicPhoto();
                guaKa.invalidate();
                cropImageView.setCropRect(guaKa.getmRect());
                //cropImageView.setRatioCropRect(guaKa.getmRect(),0.2f);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    /**
     * 初始化图片载入框架
     */
    private void initImageLoader() {
        File cacheDir = StorageUtils.getCacheDirectory(this);
        int MAXMEMONRY = (int) (Runtime.getRuntime().maxMemory());
        // System.out.println("dsa-->"+MAXMEMONRY+"   "+(MAXMEMONRY/5));//.memoryCache(new
        // LruMemoryCache(50 * 1024 * 1024))
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


    public void back(View view){
        Log.i("tag","back");
        guaKa.back();
    }

    public void crop(View view){
        Log.i("cky","crop="+cropImageView.getCropRect().toString());
        guaKa.crop(cropImageView.getCropRect());
        /*
        try {
            Bitmap resultBit = Bitmap.createBitmap(guaKa.masicBitmap,
                    (int) cropImageView.getCropRect().left, (int) cropImageView.getCropRect().top,
                   (int) cropImageView.getCropRect().width(), (int) cropImageView.getCropRect().height());




            SaveBitmap2File.saveFile(resultBit, "aa.jpg", Environment.getExternalStorageDirectory().getAbsolutePath());
            //SaveBitmap2File.saveFile(((BitmapDrawable) guaKa.getDrawable()).getBitmap(),"aa.jpg", Environment.getExternalStorageDirectory().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    public void change(View view){
        guaKa.setEnabled(true);
        cropImageView.setVisibility(View.GONE);
        if(guaKa.getMode()==3){
            guaKa.setMode(1);
        }else{
            guaKa.setMode(guaKa.getMode()+1);
            if(guaKa.getMode()==3){
                cropImageView.setVisibility(View.VISIBLE);
                guaKa.setEnabled(false);
            }
        }
        Toast.makeText(this,guaKa.getMode()+"",Toast.LENGTH_SHORT).show();
    }

    public void rotate(View view){
        Log.i("tag","rotate");
        guaKa.rotate(90);
    }
}
