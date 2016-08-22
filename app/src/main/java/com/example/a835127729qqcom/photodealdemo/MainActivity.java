package com.example.a835127729qqcom.photodealdemo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;

public class MainActivity extends AppCompatActivity {
    TestView testView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //testView = (TestView) findViewById(R.id.test);


    }

    public void back(View view){
        testView.back();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        /*
        BitmapDrawable bd = (BitmapDrawable) testView.getDrawable();
        Bitmap bitmap = bd.getBitmap();
        Bitmap srcBitmap = Bitmap.createBitmap(bitmap.copy(
                Bitmap.Config.RGB_565, true));
        Bitmap result = PhotoProcessing.filterPhoto(srcBitmap,12);
        testView.setImageBitmap(result);
        */
    }
}
