package com.example.a835127729qqcom.photodealdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.Paint.Style;
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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.a835127729qqcom.photodealdemo.dealaction.Action;
import com.example.a835127729qqcom.photodealdemo.dealaction.CropAction;
import com.example.a835127729qqcom.photodealdemo.dealaction.MarkAction;
import com.example.a835127729qqcom.photodealdemo.dealaction.MasicAction;
import com.example.a835127729qqcom.photodealdemo.dealaction.RotateAction;
import com.example.a835127729qqcom.photodealdemo.dealaction.TextAction;
import com.example.a835127729qqcom.photodealdemo.widget.listener.BackTextActionListener;
import com.example.a835127729qqcom.photodealdemo.widget.listener.CropActionListener;
import com.example.a835127729qqcom.photodealdemo.widget.listener.RotateActionListener;
import com.example.a835127729qqcom.photodealdemo.widget.listener.TextsControlListener;
import com.example.a835127729qqcom.photodealdemo.widget.query.TextActionCacheQuery;
import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;

import java.util.LinkedList;


public class ActionImageView extends ImageView implements TextsControlListener {
	public static final int MODE_IDLE = 0;
	public static final int MODE_MARK = 1;
	public static final int MODE_MASIC = 2;
	public static final int MODE_TEXT = 3;
	public static final int MODE_CROP = 4;
	public static final int MODE_ROTATE = 5;
	/**
	 * 当前操作
	 */
	Action mCurrentAction;
	/**
	 * 当前模式
	 */
	private int mode = MODE_IDLE;

	/**
	 * 内存中创建的Canvas
	 */
	private Canvas mForeCanvas,mCropCanvas,mCropMasicCanvas,mBehindCanvas;
	/**
	 * Actions操作
	 */
	private LinkedList<Action> actions = new LinkedList<Action>();
	/**
	 * 是否初始化完成,图片是否加载完成
	 */
	private boolean isComplete = true;
	//Mark画笔
	private Paint mMarkPaint = new Paint();
	//Masic画笔
	private Paint mMasicPaint = new Paint();
	//清屏画笔
	private Paint mClearPaint = new Paint();
	/**
	 * 马赛克图
	 */
	private Bitmap masicBitmap;
	private Bitmap mBehindBackground,cropMasicBitmap;
	/**
	 * 原背景图
	 */
	Bitmap originBitmap;
	public Bitmap mForeBackground,cropBitmap;
	/**
	 * 控件长宽
	 */
	private int mWidth,mHeight;
	/**
	 * 当前旋转角度
	 */
	float mCurrentAngle = 0;
	/**
	 * 监听文字撤销
	 */
	private BackTextActionListener mBackTextActionListener;
	private RotateActionListener mRotateActionListener;
	private CropActionListener mCropActionListener;
	private TextActionCacheQuery mTextActionCacheQuery;

	public ActionImageView(Context context) {
		this(context, null);
	}

	public ActionImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ActionImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPaint();
	}

	private void initPaint() {
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

	public void init(){
		setVisibility(VISIBLE);
		originBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		// 初始化bitmap
		mForeBackground = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
		mForeCanvas = new Canvas(mForeBackground);
		mForeCanvas.drawBitmap(originBitmap, null, getmRectF(),null);
		//裁剪层
		cropBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
		mCropCanvas = new Canvas(cropBitmap);

		//马赛克层
		Bitmap srcBitmap = Bitmap.createBitmap(originBitmap.copy(Bitmap.Config.RGB_565, true));
		masicBitmap = PhotoProcessing.filterPhoto(srcBitmap, 12);
		mBehindBackground = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
		mBehindCanvas = new Canvas(mBehindBackground);
		mBehindCanvas.drawBitmap(masicBitmap,null, getmRectF(),null);
		//马赛克裁剪层
		cropMasicBitmap = Bitmap.createScaledBitmap(masicBitmap.copy(Bitmap.Config.RGB_565, true),
				getMeasuredWidth(),getMeasuredHeight(),false);
		mCropMasicCanvas = new Canvas(cropMasicBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//绘制masic背景
		if(masicBitmap!=null && isComplete) {
			drawBehindBackground(canvas);
			drawForeBackground(canvas);
		}else{
			super.onDraw(canvas);
		}
	}

	/**
	 * 绘制底片
	 * @param canvas
	 */
	private void drawBehindBackground(Canvas canvas){
		//清屏
		mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		mBehindCanvas.drawPaint(mClearPaint);
		mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

		mBehindCanvas.save();
		mBehindCanvas.drawBitmap(masicBitmap, null, getmRectF(),null);
		mBehindCanvas.restore();
		for(Action action:actions){
			if(action instanceof CropAction){
				action.start(mBehindCanvas,mCurrentAngle);
			}
		}
		//旋转底片
		canvas.save();
		canvas.rotate(mCurrentAngle,mWidth/2,mHeight/2);
		canvas.drawBitmap(mBehindBackground,getScalemRect(), getScalemRectF(),null);
		canvas.restore();
	}

	/**
	 * 绘制前景
	 * @param canvas
	 */
	private void drawForeBackground(Canvas canvas) {
		drawActions(mForeCanvas);
		canvas.save();
		canvas.rotate(mCurrentAngle,mWidth/2,mHeight/2);
		canvas.drawBitmap(mForeBackground,getScalemRect(), getScalemRectF(),null);
		canvas.restore();
	}

	private void drawActions(Canvas foreCanvas){
		//清屏
		mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		foreCanvas.drawPaint(mClearPaint);
		mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

		foreCanvas.save();
		mForeCanvas.drawBitmap(originBitmap, null, getmRectF(),null);
		foreCanvas.restore();


		//找到最后一个旋转角度,可能不存在
		RotateAction lastRotateAction = null;
		for(int i=actions.size()-1;i>=0;i--){
			if(actions.get(i) instanceof RotateAction){
				lastRotateAction = (RotateAction)actions.get(i);
				break;
			}
		}

		//开始绘制
		float startAngle = 0;
		for(Action action:actions){
			if(action instanceof RotateAction){
				startAngle = ((RotateAction) action).getmAngle();
				continue;
			}
			//在文字编辑模式下,不显示文字
			if(action instanceof TextAction && mode==MODE_TEXT && mTextActionCacheQuery.query((TextAction) action)){
				continue;
			}
			if(lastRotateAction!=null) {//至少一次旋转
				if(action instanceof CropAction){
					action.execute(foreCanvas);
				}else if(action instanceof TextAction){
					action.start(mCurrentAngle,mWidth/2.0f,mHeight/2.0f);
					action.execute(foreCanvas);
				}else{
					foreCanvas.save();
					foreCanvas.rotate(-startAngle, mWidth / 2.0f, mHeight / 2.0f);
					action.execute(foreCanvas);
					foreCanvas.restore();
				}
			}else{
				action.execute(foreCanvas);
			}
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
				mCurrentAction = produceMarkActionOrMasicAction();
				if (mCurrentAction==null) return false;
				mCurrentAction.start(event.getX(),event.getY());
				actions.add(mCurrentAction);
				return true;
			case MotionEvent.ACTION_MOVE:
				Log.i("tag","move");
				if (mCurrentAction==null) return false;
				mCurrentAction.next(event.getX(),event.getY());
				invalidate();
				return true;
			case MotionEvent.ACTION_UP:
				Log.i("tag","up");
				if (mCurrentAction==null) return false;
				mCurrentAction.stop(event.getX(),event.getY());
				invalidate();
				return true;
		}
		return true;
	}

	private Action produceMarkActionOrMasicAction(){
		Action action = null;
		switch (mode){
			case MODE_MARK:
				action = new MarkAction(new Path(),mMarkPaint);
				break;
			case MODE_MASIC:
				action = new MasicAction(new Path(),mMasicPaint);
				break;
		}
		return action;
	}

	/**
	 * 撤销
	 */
	public void back(){
		if(actions.size()==0) return;
		final Action action = actions.removeLast();
		post(new Runnable() {
			@Override
			public void run() {
				if(action instanceof RotateAction){
					//找到最后一个旋转角度,可能不存在
					int i = actions.size()-1;
					for(;i>=0;i--){
						if(actions.get(i) instanceof RotateAction){
							mCurrentAngle = ((RotateAction) actions.get(i)).getmAngle();
							break;
						}
					}
					if(i<0){
						mCurrentAngle = 0;
					}
					mRotateActionListener.onRotateBack(mCurrentAngle-((RotateAction) action).getmAngle());
					action.stop(getRotatedmRectF());
				}else if(action instanceof TextAction){
					if(mBackTextActionListener!=null) mBackTextActionListener.onBackTextAction((TextAction)action);
				}else if(action instanceof CropAction){
					//恢复之前的textAction
					mCropActionListener.onCropBack();
				}
				postInvalidate();
			}
		});
	}

	/**
	 * 裁剪
	 * @param rectf
	 */
	public void crop(RectF rectf){
		mCurrentAction = new CropAction(rectf, getScalemRectF(),
				cropBitmap,mForeBackground,mCropCanvas,
				cropMasicBitmap,mBehindBackground,mCropMasicCanvas,mCurrentAngle);
		actions.add(mCurrentAction);
		postInvalidate();
		mCropActionListener.onCrop();
	}

	/**
	 * 旋转
	 * @param angle
	 */
	public void rotate(float angle, RotateAction.RotateActionBackListener rotateActionBackListener, RotateActionListener rotateActionListener){
		mCurrentAction = new RotateAction(angle,rotateActionBackListener);
		mRotateActionListener = rotateActionListener;
		mRotateActionListener.onRotate(angle-mCurrentAngle,this);
		mCurrentAngle = angle;
		actions.add(mCurrentAction);
		invalidate();
	}

	/**
	 * 返回矩阵
	 * @return
	 */
	public RectF getmRectF(){
		return new RectF(getLeft(),getTop(),getRight(),getBottom());
	}

	public Rect getmRect(){
		return new Rect(getLeft(),getTop(),getRight(),getBottom());
	}

	/**
	 * 获取按w/h比例缩小的矩阵
	 * @return
	 */
	public RectF getScalemRectF(){
		if(mCurrentAngle/90%2==0) return new RectF(getLeft(),getTop(),getRight(),getBottom());
		float scale = 1.0f * mWidth/mHeight;
		RectF r = new RectF(getLeft(),getTop()+(1-scale*scale)*mHeight/2,getRight(),getBottom()-(1-scale*scale)*mHeight/2);
		Matrix m = new Matrix();
		m.setRotate(mCurrentAngle,mWidth/2,mHeight/2);
		m.mapRect(r);
		return r;
	}

	public Rect getScalemRect(){
		RectF rf = getScalemRectF();
		return new Rect((int)rf.left,(int)rf.top,(int)rf.right,(int)rf.bottom);
	}

	/**`
	 * 获取按w/h比例缩小,并且旋转的矩阵
	 * @return
	 */
	public RectF getRotatedmRectF(){
		if(mCurrentAngle/90%2==0) return new RectF(getLeft(),getTop(),getRight(),getBottom());
		float scale = 1.0f * mWidth/mHeight;
		RectF r = new RectF(getLeft(),getTop()+(1-scale*scale)*mHeight/2,getRight(),getBottom()-(1-scale*scale)*mHeight/2);
		return r;
	}

	public Rect getRotatedmRect(){
		RectF rf = getRotatedmRectF();
		return new Rect((int)rf.left,(int)rf.top,(int)rf.right,(int)rf.bottom);
	}

	public void setMode(int mode){
		this.mode = mode;
	}

	public int getMode(){
		return mode;
	}

	@Override
	public void onAddText(TextAction textAction) {
		actions.add(textAction);
		postInvalidate();
	}

	@Override
	public void onDeleteText(TextAction textAction) {
		actions.remove(textAction);
		postInvalidate();
	}

	public void setmBackTextActionListener(BackTextActionListener mBackTextActionListener) {
		this.mBackTextActionListener = mBackTextActionListener;
	}

	public void setmCropActionListener(CropActionListener mCropActionListener) {
		this.mCropActionListener = mCropActionListener;
	}

	public void setmTextActionCacheQuery(TextActionCacheQuery mTextActionCacheQuery) {
		this.mTextActionCacheQuery = mTextActionCacheQuery;
	}
}
