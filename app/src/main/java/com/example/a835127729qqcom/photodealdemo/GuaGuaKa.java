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
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;


public class GuaGuaKa extends View
{

	/**
	 * 绘制线条的Paint,即用户手指绘制Path
	 */
	private Paint mOutterPaint = new Paint();
	/**
	 * 记录用户绘制的Path
	 */
	private Path mPath = new Path();
	/**
	 * 内存中创建的Canvas
	 */
	private Canvas mCanvas;
	/**
	 * mCanvas绘制内容在其上
	 */
	private Bitmap mBitmap;

	/**
	 * ------------------------以下是奖区的一些变量
	 */
	// private Bitmap mBackBitmap;
	private boolean isComplete;

	private Paint mBackPint = new Paint();

	private int mLastX;
	private int mLastY;

	public GuaGuaKa(Context context)
	{
		this(context, null);
	}

	public GuaGuaKa(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public GuaGuaKa(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	Bitmap result;
	Bitmap backBitmap;
	private void init()
	{
		mPath = new Path();
		// mBackBitmap = BitmapFactory.decodeResource(getResources(),
		// R.drawable.t2);
		setUpOutPaint();
		setUpBackPaint();
		backBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.testicon)).getBitmap();
		Bitmap srcBitmap = Bitmap.createBitmap(backBitmap.copy(
				Bitmap.Config.RGB_565, true));
		result = PhotoProcessing.filterPhoto(srcBitmap,12);

	}

	/**
	 * 初始化canvas的绘制用的画笔
	 */
	private void setUpBackPaint()
	{
		mBackPint.setStyle(Style.FILL);
		mBackPint.setColor(Color.DKGRAY);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// canvas.drawBitmap(mBackBitmap, 0, 0, null);
		// 绘制奖项
		/*
		canvas.drawText(mText, getWidth() / 2 - mTextBound.width() / 2,
				getHeight() / 2 + mTextBound.height() / 2, mBackPint);
				*/
		canvas.drawBitmap(result,0,0,null);
		if (!isComplete)
		{
			drawPath();
			canvas.drawBitmap(mBitmap, 0, 0, null);
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		// 初始化bitmap
		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);

		// 绘制遮盖层
		// mCanvas.drawColor(Color.parseColor("#c0c0c0"));
		mOutterPaint.setStyle(Style.FILL);
		mCanvas.drawRoundRect(new RectF(0, 0, width, height), 30, 30,
				mOutterPaint);
		mCanvas.drawBitmap(backBitmap,0,0,null);
		/*
		mCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.s_title), null, new RectF(0, 0, width, height), null);
				*/
	}

	/**
	 * 设置画笔的一些参数
	 */
	private void setUpOutPaint()
	{
		// 设置画笔
		// mOutterPaint.setAlpha(0);
		mOutterPaint.setColor(Color.parseColor("#c0c0c0"));
		mOutterPaint.setAntiAlias(true);
		mOutterPaint.setDither(true);
		mOutterPaint.setStyle(Style.STROKE);
		mOutterPaint.setStrokeJoin(Paint.Join.ROUND); // 圆角
		mOutterPaint.setStrokeCap(Paint.Cap.ROUND); // 圆角
		// 设置画笔宽度
		mOutterPaint.setStrokeWidth(40);
	}

	/**
	 * 绘制线条
	 */
	private void drawPath()
	{
		mOutterPaint.setStyle(Style.STROKE);
		mOutterPaint
				.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		mCanvas.drawPath(mPath, mOutterPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			mLastX = x;
			mLastY = y;
			mPath.moveTo(mLastX, mLastY);
			break;
		case MotionEvent.ACTION_MOVE:

			int dx = Math.abs(x - mLastX);
			int dy = Math.abs(y - mLastY);

			if (dx > 3 || dy > 3)
				mPath.lineTo(x, y);

			mLastX = x;
			mLastY = y;
			break;
		case MotionEvent.ACTION_UP:
			break;
		}

		invalidate();
		return true;
	}

}
