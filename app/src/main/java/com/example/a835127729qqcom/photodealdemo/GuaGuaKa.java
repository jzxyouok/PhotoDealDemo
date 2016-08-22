package com.example.a835127729qqcom.photodealdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.a835127729qqcom.photodealdemo.dealaction.Action;
import com.example.a835127729qqcom.photodealdemo.dealaction.MarkAction;
import com.example.a835127729qqcom.photodealdemo.dealaction.MasicAction;
import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;

import java.util.LinkedList;


public class GuaGuaKa extends ImageView {
	Action mCurrentAction;
	/**
	 * 当前模式
	 */
	public int mode = 1;

	/**
	 * 内存中创建的Canvas
	 */
	private Canvas mCanvas;
	/**
	 * mCanvas绘制内容在其上
	 */
	private Bitmap mBitmap;
	/**
	 * Actions
	 */
	private LinkedList<Action> actions = new LinkedList<Action>();

	/**
	 * ------------------------以下是奖区的一些变量
	 */
	private boolean isComplete;

	private Paint mBackPint = new Paint();

	//Mark画笔
	private Paint mMarkPaint = new Paint();
	//Masic画笔
	private Paint mMasicPaint = new Paint();

	public GuaGuaKa(Context context) {
		this(context, null);
	}

	public GuaGuaKa(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GuaGuaKa(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	Bitmap masicBitmap;
	Bitmap backBitmap;

	private void init() {
		setUpMarkPaint();
		setUpMasicPaint();

		produceMasicPhoto();
	}

	private void setUpMarkPaint(){
		mMarkPaint.setStyle(Style.STROKE);
		mMarkPaint.setColor(Color.RED);
		mMarkPaint.setStrokeWidth(20);
	}

	private void setUpMasicPaint(){
		mMasicPaint.setColor(Color.parseColor("#c0c0c0"));
		mMasicPaint.setAntiAlias(true);
		mMasicPaint.setDither(true);
		mMasicPaint.setStyle(Style.STROKE);
		mMasicPaint.setStrokeJoin(Paint.Join.ROUND); // 圆角
		mMasicPaint.setStrokeCap(Paint.Cap.ROUND); // 圆角
		// 设置画笔宽度
		mMasicPaint.setStrokeWidth(60);
	}

	private void produceMasicPhoto(){
		backBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.testicon)).getBitmap();
		Bitmap srcBitmap = Bitmap.createBitmap(backBitmap.copy(
				Bitmap.Config.RGB_565, true));
		masicBitmap = PhotoProcessing.filterPhoto(srcBitmap,12);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//绘制masic背景
		canvas.drawBitmap(masicBitmap,0,0,null);

		if (!isComplete) {
			drawActions(mCanvas);
			canvas.drawBitmap(mBitmap,0,0,null);
		}
	}

	private void drawActions(Canvas canvas){
		for(Action action:actions){
			action.execute(canvas);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		// 初始化bitmap
		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);

		// 绘制遮盖层
		mCanvas.drawBitmap(backBitmap,0,0,null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = MotionEventCompat.getActionMasked(event);
		switch (action){
			case MotionEvent.ACTION_DOWN:
				Log.i("tag","down");
				mCurrentAction = produceAction();
				mCurrentAction.start(event.getX(),event.getY());
				actions.add(mCurrentAction);
				break;
			case MotionEvent.ACTION_MOVE:
				Log.i("tag","move");
				mCurrentAction.next(event.getX(),event.getY());
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				Log.i("tag","up");
				mCurrentAction.stop(event.getX(),event.getY());
				invalidate();
				break;
		}
		return true;
	}

	private Action produceAction(){
		Action action = null;
		switch (mode){
			case 1:
				action = new MarkAction(new Path(),mMarkPaint);
				break;
			case 2:
				action = new MasicAction(new Path(),mMasicPaint);
				break;
		}
		return action;
	}

	public void setMode(int mode){
		this.mode = mode;
	}

	public void back(){
		if(actions.size()==0) return;
		actions.removeLast();
		post(new Runnable() {
			@Override
			public void run() {
				mCanvas.drawBitmap(backBitmap,0,0,null);
				postInvalidate();
			}
		});
	}
}
