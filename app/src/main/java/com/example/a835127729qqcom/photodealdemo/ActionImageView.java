package com.example.a835127729qqcom.photodealdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.a835127729qqcom.photodealdemo.dealaction.Action;
import com.example.a835127729qqcom.photodealdemo.dealaction.CropAction;
import com.example.a835127729qqcom.photodealdemo.dealaction.MarkAction;
import com.example.a835127729qqcom.photodealdemo.dealaction.MasicAction;
import com.example.a835127729qqcom.photodealdemo.util.SaveBitmap2File;
import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.easing.Bounce;

import java.io.IOException;
import java.util.LinkedList;


public class ActionImageView extends ImageView {
	/**
	 * 当前操作
	 */
	Action mCurrentAction;
	/**
	 * 当前模式
	 */
	private int mode = 1;

	/**
	 * 内存中创建的Canvas
	 */
	private Canvas mForeCanvas;
	/**
	 * Actions操作
	 */
	private LinkedList<Action> actions = new LinkedList<Action>();
	/**
	 * 是否初始化完成,图片是否加载完成
	 */
	private boolean isComplete;
	/**
	 * 是否裁剪状态
	 */
	private boolean isCrop;
	//Mark画笔
	private Paint mMarkPaint = new Paint();
	//Masic画笔
	private Paint mMasicPaint = new Paint();
	/**
	 * 马赛克图
	 */
	Bitmap masicBitmap;
	/**
	 * 原背景图
	 */
	Bitmap originBitmap;
	/**
	 * mCanvas绘制内容在其上
	 */
	public Bitmap mForeBackground;
	/**
	 * 控件长宽
	 */
	private int mWidth,mHeight;

	public ActionImageView(Context context) {
		this(context, null);
	}

	public ActionImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ActionImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setUpMarkPaint();
		setUpMasicPaint();
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

	public void produceMasicPhoto(){
		originBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		//originBitmap = Bitmap.createBitmap(((BitmapDrawable) getDrawable()).getBitmap(),0,0,mWidth,mHeight);
		// 初始化bitmap
		mForeBackground = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
		mForeCanvas = new Canvas(mForeBackground);

		// 绘制遮盖层
		mForeCanvas.drawBitmap(originBitmap, null,getmRect(),null);

		Bitmap srcBitmap = Bitmap.createBitmap(originBitmap.copy(
				Bitmap.Config.RGB_565, true));
		masicBitmap = PhotoProcessing.filterPhoto(srcBitmap, 12);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//绘制masic背景
		if(masicBitmap!=null) {
			canvas.rotate(mAngle,mWidth/2,mHeight/2);
			if(isCrop){//裁剪
				canvas.drawBitmap(((CropAction)actions.getLast()).getmCropBitmap(),0,0,null);
				return;
			}
			canvas.drawBitmap(masicBitmap,null,getmRect(),null);
			//canvas.drawBitmap(masicBitmap, 0, 0, null);
			if (!isComplete) {
				//mForeCanvas.rotate(mAngle,mWidth/2,mHeight/2);
				drawActions(mForeCanvas);
				canvas.drawBitmap(mForeBackground,null,getmRect(),null);
			}
		}else{
			super.onDraw(canvas);
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

		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
		Log.i("tag","w="+mWidth+",h="+mHeight);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!isEnabled()) return false;
		int action = MotionEventCompat.getActionMasked(event);
		switch (action){
			case MotionEvent.ACTION_DOWN:
				Log.i("tag","down");
				mCurrentAction = produceAction();
				mCurrentAction.start(event.getX(),event.getY());
				actions.add(mCurrentAction);
				return true;
			case MotionEvent.ACTION_MOVE:
				Log.i("tag","move");
				mCurrentAction.next(event.getX(),event.getY());
				invalidate();
				return true;
			case MotionEvent.ACTION_UP:
				Log.i("tag","up");
				mCurrentAction.stop(event.getX(),event.getY());
				invalidate();
				return true;
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

	public void back(){
		if(actions.size()==0) return;
		actions.removeLast();
		post(new Runnable() {
			@Override
			public void run() {
				mForeCanvas.drawBitmap(masicBitmap,null,getmRect(),null);
				postInvalidate();
			}
		});
	}

	public void crop(RectF rectf){
		//Log.i("cky","masic w="+masicBitmap.getWidth()+",h="+masicBitmap.getHeight());
		Bitmap newbmp = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		Canvas cv = new Canvas(newbmp);
		cv.rotate(mAngle);
		//draw bg into
		cv.drawBitmap(masicBitmap, null, getmRect(), null);//在 0，0坐标开始画入bg
		//draw fg into
		cv.drawBitmap(mForeBackground, null, getmRect(), null);//在 0，0坐标开始画入fg ，可以从任意位置画入
		//save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);//保存
		//store
		cv.restore();//存储
		Bitmap resultBit = Bitmap.createBitmap(newbmp,
				(int) rectf.left, (int) rectf.top,
				(int) rectf.width(), (int) rectf.height());
		mCurrentAction = new CropAction(rectf,getmRect(),resultBit);
		try {
			SaveBitmap2File.saveFile(resultBit, "aa.jpg", Environment.getExternalStorageDirectory().getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		actions.add(mCurrentAction);
	}

	float mAngle;
	public void rotate(float angle){
		mAngle = mAngle+angle;
		postInvalidate();
	}

	/**
	 * 返回矩阵
	 * @return
     */
	public RectF getmRect(){
		return new RectF(getLeft(),getTop(),getRight(),getBottom());
	}

	public void setMode(int mode){
		this.mode = mode;
	}

	public int getMode(){
		return mode;
	}
}
