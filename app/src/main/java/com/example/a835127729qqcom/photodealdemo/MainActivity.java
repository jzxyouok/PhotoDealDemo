package com.example.a835127729qqcom.photodealdemo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;

public class MainActivity extends AppCompatActivity {
    TestView testView;
    GuaGuaKa guaKa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //testView = (TestView) findViewById(R.id.test);
        guaKa = (GuaGuaKa) findViewById(R.id.guagua);

    }

    public void back(View view){
        Log.i("tag","back");
        guaKa.back();
    }

    public void change(View view){
        if(guaKa.mode == 1){
            guaKa.setMode(2);
        }else{
            guaKa.setMode(1);
        }
    }
}
