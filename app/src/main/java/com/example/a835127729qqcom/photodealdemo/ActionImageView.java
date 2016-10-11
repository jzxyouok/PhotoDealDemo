package com.example.a835127729qqcom.photodealdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
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
import android.support.annotation.NonNull;
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
import com.example.a835127729qqcom.photodealdemo.util.DrawMode;
import com.example.a835127729qqcom.photodealdemo.util.MasicUtil;
import com.example.a835127729qqcom.photodealdemo.util.PhotoProcessing;
import com.example.a835127729qqcom.photodealdemo.util.SaveBitmap2File;
import com.example.a835127729qqcom.photodealdemo.widget.ColorPickBox;
import com.example.a835127729qqcom.photodealdemo.widget.ColorPickBox.ColorPickListener;
import com.example.a835127729qqcom.photodealdemo.widget.MasicSizePickBox;
import com.example.a835127729qqcom.photodealdemo.widget.listener.BackTextActionListener;
import com.example.a835127729qqcom.photodealdemo.widget.listener.CropActionListener;
import com.example.a835127729qqcom.photodealdemo.widget.listener.RotateActionListener;
import com.example.a835127729qqcom.photodealdemo.widget.listener.TextsControlListener;
import com.example.a835127729qqcom.photodealdemo.widget.query.CurrentRotateRectQuery;
import com.example.a835127729qqcom.photodealdemo.widget.query.TextActionCacheQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ActionImageView extends ImageView implements TextsControlListener,CurrentRotateRectQuery,ColorPickListener,
		MasicSizePickBox.MasicSizePickListener{
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
	private boolean isComplete = false;
	//Mark画笔
	private Paint mMarkPaint = new Paint();
	//Masic画笔
	private Paint mMasicPaint = new Paint();
	//清屏画笔
	private Paint mClearPaint = new Paint();
	//文字画笔
	private Paint mTextPaint = new Paint();
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
	 * 工具矩阵
	 */
	private RectF originBitmapRectF;
	private RectF normalRectF;
	private Rect normalRect;
	private RectF rotateRectF;
	private RectF scaleRectF;
	/**
	 * 监听文字撤销
	 */
	private BackTextActionListener mBackTextActionListener;
	private RotateActionListener mRotateActionListener;
	private List<CropActionListener> mCropActionListeners = new ArrayList<CropActionListener>();
	private TextActionCacheQuery mTextActionCacheQuery;
	private int currentColor = Color.WHITE;
	private float currentStrokeWidth = 1;

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
		setUpTextPaint();
	}

	private void setUpTextPaint() {
		mTextPaint.setStyle(Style.FILL_AND_STROKE);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setDither(true);
		mTextPaint.setColor(Color.WHITE);
	}

	private void setUpMarkPaint(){
		mMarkPaint.setStyle(Style.STROKE);
		mMarkPaint.setColor(Color.RED);
		mMarkPaint.setStrokeWidth(20);
		mMarkPaint.setAntiAlias(true);
		mMarkPaint.setDither(true);
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

	/**
	 * 建议在非主线程中调用该方法,并且需要Imageview宽高加载完成,所以建议在onWindowFocusChanged()方法中调用,并且hasFocus为true
	 * @param path
     */
	public synchronized void init(String path){
		originBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		originBitmapRectF = decodeBounds(path);
		recaculateRects(originBitmapRectF);
		// 初始化bitmap
		mForeBackground = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
		mForeCanvas = new Canvas(mForeBackground);
		//裁剪层
		cropBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
		mCropCanvas = new Canvas(cropBitmap);

		//马赛克层
		Bitmap srcBitmap = originBitmap.copy(Bitmap.Config.ARGB_4444, true);
		masicBitmap = PhotoProcessing.filterPhoto(srcBitmap, 12);
		//如果你不希望使用native包,可以调用该方法来生成马赛克
		//masicBitmap = MasicUtil.getMosaicsBitmaps(srcBitmap,0.1);
		mBehindBackground = Bitmap.createScaledBitmap(masicBitmap,getMeasuredWidth(), getMeasuredHeight(), false);
		mBehindCanvas = new Canvas(mBehindBackground);
		//马赛克裁剪层
		cropMasicBitmap = Bitmap.createBitmap(getMeasuredWidth(),getMeasuredHeight(),Config.ARGB_4444);
		mCropMasicCanvas = new Canvas(cropMasicBitmap);
		//使用MasicUtil.getMosaicsBitmap()来生成马赛克,可以回收srcBitmap,否则不能
		//srcBitmap.recycle();
		isComplete = true;
	}

	private RectF decodeBounds(String path){
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		opts.inSampleSize = 1;
		BitmapFactory.decodeFile(path,opts);
		return new RectF(0,0,opts.outWidth,opts.outHeight);
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
		if(isComplete==false) return;
		recaculateRects(originBitmapRectF);
		//清屏
		mClearPaint.setXfermode(DrawMode.CLEAR);
		mBehindCanvas.drawPaint(mClearPaint);
		mClearPaint.setXfermode(DrawMode.SRC);

		mBehindCanvas.save();
		mBehindCanvas.drawBitmap(masicBitmap, null, normalRectF,null);
		mBehindCanvas.restore();
		if(cropSnapshot!=null && cropSnapshot.cropAction!=null && actions.contains(cropSnapshot.cropAction)){
			RectF lastNormalRectf = new RectF(normalRectF);
			RectF lastScaleRectf = getCurrentScaleRectF();
			RectF lastRotateRectf = getCurrentRotateRectF();
			recaculateRects(cropSnapshot.cropAction.mCropRect);
			cropSnapshot.cropAction.start(getCurrentRotateRectF(),mCurrentAngle,getCurrentScaleRectF(), new Rect(normalRect),
					lastNormalRectf,lastScaleRectf,lastRotateRectf);
			cropSnapshot.cropAction.drawCropMasicBitmapDirectly(mBehindCanvas);
		}else {
			for (Action action : actions) {
				if (action instanceof CropAction) {
					RectF lastNormalRectf = new RectF(normalRectF);
					RectF lastScaleRectf = getCurrentScaleRectF();
					RectF lastRotateRectf = getCurrentRotateRectF();
					recaculateRects(((CropAction) action).mCropRect);
					action.start(getCurrentRotateRectF(),mCurrentAngle,getCurrentScaleRectF(), new Rect(normalRect),
							lastNormalRectf,lastScaleRectf,lastRotateRectf);
					action.next(mBehindCanvas, mCurrentAngle);
				}
			}
		}
		//旋转底片
		canvas.save();
		canvas.rotate(mCurrentAngle,mWidth/2,mHeight/2);
		canvas.drawBitmap(mBehindBackground,normalRect, getCurrentScaleRectF(),null);
		canvas.restore();
	}

	/**
	 * 绘制前景
	 * @param canvas
	 */
	private void drawForeBackground(Canvas canvas) {
		if(isComplete==false) return;
		recaculateRects(originBitmapRectF);
		drawActions(mForeCanvas);
		canvas.save();
		canvas.rotate(mCurrentAngle,mWidth/2,mHeight/2);
		canvas.drawBitmap(mForeBackground,normalRect, getCurrentScaleRectF(),null);
		canvas.restore();
	}

	private void drawActions(Canvas foreCanvas){
		//清屏
		mClearPaint.setXfermode(DrawMode.CLEAR);
		foreCanvas.drawPaint(mClearPaint);
		mClearPaint.setXfermode(DrawMode.SRC);

		foreCanvas.save();
		mForeCanvas.drawBitmap(originBitmap, null, normalRectF,null);
		foreCanvas.restore();


		//找到最后一个旋转角度,可能不存在
		RotateAction lastRotateAction = null;
		for(int i=actions.size()-1;i>=0;i--){
			if(actions.get(i) instanceof RotateAction){
				lastRotateAction = (RotateAction)actions.get(i);
				break;
			}
		}

		float startAngle = 0;
		int actionIndex = 0;
		//先从快照中取最后一次crop操作
		if(cropSnapshot!=null && cropSnapshot.cropAction!=null && actions.contains(cropSnapshot.cropAction)){
			actionIndex = actions.indexOf(cropSnapshot.cropAction);
			for(int i=actionIndex-1;i>=0;i--){//查找上次startAngles
				if(actions.get(i) instanceof RotateAction){
					startAngle = ((RotateAction) actions.get(i)).getmAngle();
					break;
				}
			}
		}

		//开始绘制
		for(;actionIndex<actions.size();actionIndex++){
			Action action = actions.get(actionIndex);
			if(action instanceof RotateAction){
				startAngle = ((RotateAction) action).getmAngle();
				continue;
			}
			//在文字编辑模式下,不显示文字
			if(action instanceof TextAction && mode==MODE_TEXT && mTextActionCacheQuery.query((TextAction) action)){
				continue;
			}
			if(action instanceof CropAction){
				if(cropSnapshot.cropAction!=null && cropSnapshot.cropAction==action){
					RectF lastNormalRectf = new RectF(normalRectF);
					RectF lastScaleRectf = getCurrentScaleRectF();
					RectF lastRotateRectf = getCurrentRotateRectF();
					recaculateRects(cropSnapshot.cropAction.mCropRect);
					cropSnapshot.cropAction.start(getCurrentRotateRectF(),mCurrentAngle,getCurrentScaleRectF(), new Rect(normalRect),
							lastNormalRectf,lastScaleRectf,lastRotateRectf);
					cropSnapshot.cropAction.drawCropBitmapDirectly(foreCanvas);
				}else {
					RectF lastNormalRectf = new RectF(normalRectF);
					RectF lastScaleRectf = getCurrentScaleRectF();
					RectF lastRotateRectf = getCurrentRotateRectF();
					recaculateRects(((CropAction) action).mCropRect);
					action.start(getCurrentRotateRectF(),mCurrentAngle,getCurrentScaleRectF(), new Rect(normalRect),
							lastNormalRectf,lastScaleRectf,lastRotateRectf);
					action.execute(foreCanvas);
				}
				action.stop(cropSnapshot);
			}else if(action instanceof TextAction){
				action.start(mCurrentAngle,mWidth/2.0f,mHeight/2.0f,mTextPaint);
				action.execute(foreCanvas);
			}else {
				if (lastRotateAction != null) {//至少一次旋转
					foreCanvas.save();
					foreCanvas.rotate(-startAngle, mWidth / 2.0f, mHeight / 2.0f);
					action.execute(foreCanvas);
					foreCanvas.restore();
				} else {
					action.execute(foreCanvas);
				}
			}
		}
	}

	private CropSnapshot cropSnapshot = new CropSnapshot();

	@Override
	public RectF query() {
		return getCurrentRotateRectF();
	}

	@Override
	public void notifyColorChange(int color) {
		currentColor = color;
		mMarkPaint.setColor(color);
		mTextPaint.setColor(color);
	}

	@Override
	public void notify(float size) {
		currentStrokeWidth = size;
	}

	/**
	 * 裁剪后的快照
	 */
	public class CropSnapshot{
		public boolean isCache = true;
		public void setCropAction(CropAction cropAction) {
			if(!isCache){
				this.cropAction = null;
				return;
			}
			this.cropAction = cropAction;
		}

		CropAction cropAction;
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
				action = new MarkAction(new Path(),mMarkPaint,currentColor);
				break;
			case MODE_MASIC:
				action = new MasicAction(new Path(),mMasicPaint,currentStrokeWidth);
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
					action.stop(getCurrentRotateRectF());
				}else if(action instanceof TextAction){
					if(mBackTextActionListener!=null) mBackTextActionListener.onBackTextAction((TextAction)action);
				}else if(action instanceof CropAction){
					CropAction lastCropAction = null;
					//找到之前的rect
					for(int i=actions.size()-1;i>=0;i--){
						if(actions.get(i) instanceof CropAction){
							lastCropAction = (CropAction) actions.get(i);
							break;
						}
					}
					if(lastCropAction!=null){
						recaculateRects((((CropAction) action).mCropRect));
					}else{
						recaculateRects(originBitmapRectF);
					}
					//恢复之前的textAction
					for(CropActionListener cropActionListener:mCropActionListeners) {
						cropActionListener.onCropBack(getCurrentRotateRectF());
					}
					cropSnapshot.setCropAction(null);
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
		mCurrentAction = new CropAction(rectf,getmRectF().centerX(),getmRectF().centerY(),
				cropBitmap,mForeBackground,mCropCanvas,
				cropMasicBitmap,mBehindBackground,mCropMasicCanvas,mCurrentAngle);
		actions.add(mCurrentAction);

		cropSnapshot.setCropAction(null);
		postInvalidate();
		for(CropActionListener cropActionListener:mCropActionListeners) {
			cropActionListener.onCrop();
		}
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
	 * 生成图片文件
     */
	public void output(){
		new Thread(){
			@Override
			public void run() {
				Rect srcrect = new Rect((int)normalRectF.left,(int)normalRectF.top,(int)normalRectF.right,(int)normalRectF.bottom);
				RectF destrect;// = new RectF(0,0,getCurrentRotateRect().width(),getCurrentRotateRect().height());
				RectF rotateRect = getCurrentRotateRectF();
				if(originBitmapRectF.width()<mWidth&&originBitmapRectF.height()<mHeight) {
					float scale;
					if(originBitmapRectF.width()<originBitmapRectF.height()){
						scale = originBitmapRectF.width()/rotateRect.width();
					}else if(originBitmapRectF.width()==originBitmapRectF.height()){
						scale = rotateRect.width()<rotateRect.height()?originBitmapRectF.width()/rotateRect.width():originBitmapRectF.height()/rotateRect.height();
					}else{
						scale = originBitmapRectF.height()/rotateRect.height();
					}
					destrect = new RectF(0,0,rotateRect.width()*scale,rotateRect.height()*scale);
				}else{
					destrect = new RectF(0,0,rotateRect.width(),rotateRect.height());
				}
				RectF rect1 = new RectF();
				Matrix matrix = new Matrix();
				matrix.postRotate(mCurrentAngle,destrect.centerX(),destrect.centerY());
				matrix.mapRect(rect1,destrect);

				Bitmap bitmap = Bitmap.createBitmap((int)destrect.width(),(int)destrect.height(), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				canvas.save();
				canvas.rotate(mCurrentAngle,destrect.centerX(),destrect.centerY());
				canvas.drawRect(rect1,mMarkPaint);
				canvas.drawBitmap(mBehindBackground,srcrect, rect1,null);
				canvas.drawBitmap(mForeBackground,srcrect, rect1,null);
				canvas.restore();
				SaveBitmap2File.saveImageToGallery(ActionImageView.this.getContext(),bitmap);
			}
		}.start();
	}

	/**
	 * 重新初始化工具矩阵
	 * @param rectF
	 */
	private void recaculateRects(RectF rectF) {
		normalRectF = generateRectF(rectF);
		normalRect = new Rect((int)normalRectF.left,(int)normalRectF.top,(int)normalRectF.right,(int)normalRectF.bottom);
		rotateRectF = generateRotateRectF(rectF);
		scaleRectF = generateScaleRectF(rectF);
	}

	/**
	 * 根据长宽缩放到控件大小
	 * @param rectF
	 * @return
	 */
	private RectF generateRectF(RectF rectF){
		RectF rf = new RectF(rectF);
		float scaleW = mWidth/rectF.width();
		float scaleH = mHeight/rectF.height();
		float scale = scaleW<scaleH?scaleW:scaleH;

		Matrix matrix = new Matrix();
		matrix.postTranslate(mWidth/2.0f-rf.centerX(),mHeight/2.0f-rf.centerY());
		matrix.postScale(scale,scale,mWidth/2.0f,mHeight/2.0f);
		matrix.mapRect(rf);
		return rf;
	}

	/**
	 * 旋转后,根据长宽缩放到控件大小
	 * @param rectF
	 * @return
	 */
	private RectF generateRotateRectF(RectF rectF){
		RectF rf = new RectF(rectF);
		float scaleW = mWidth/rectF.height();
		float scaleH = mHeight/rectF.width();
		float scale = scaleW<scaleH?scaleW:scaleH;

		Matrix matrix = new Matrix();
		matrix.postTranslate(mWidth/2.0f-rf.centerX(),mHeight/2.0f-rf.centerY());
		matrix.postRotate(90,mWidth/2.0f,mHeight/2.0f);
		matrix.postScale(scale,scale,mWidth/2.0f,mHeight/2.0f);
		matrix.mapRect(rf);
		return rf;
	}

	/**
	 * 根据长宽缩放到控件大小,但是不旋转
	 * @param rectF
	 * @return
	 */
	private RectF generateScaleRectF(RectF rectF){
		RectF rf = new RectF(rectF);
		float scaleW = mWidth/rectF.height();
		float scaleH = mHeight/rectF.width();
		float scale = scaleW<scaleH?scaleW:scaleH;

		Matrix matrix = new Matrix();
		matrix.postTranslate(mWidth/2.0f-rf.centerX(),mHeight/2.0f-rf.centerY());
		matrix.postScale(scale,scale,mWidth/2.0f,mHeight/2.0f);
		matrix.mapRect(rf);
		return rf;
	}

	public RectF getCurrentRotateRectF(){
		if(mCurrentAngle/90%2==0){
			return new RectF(normalRectF);
		}else {
			return new RectF(rotateRectF);
		}
	}

	private Rect getCurrentRotateRect(){
		if(mCurrentAngle/90%2==0){
			return new Rect((int)normalRectF.left,(int)normalRectF.top,(int)normalRectF.right,(int)normalRectF.bottom);
		}else {
			return new Rect((int)rotateRectF.left,(int)rotateRectF.top,(int)rotateRectF.right,(int)rotateRectF.bottom);
		}
	}

	private RectF getCurrentScaleRectF(){
		if(mCurrentAngle/90%2==0){
			return new RectF(normalRectF);
		}else {
			return new RectF(scaleRectF);
		}
	}

	private Rect getCurrentScaleRect(){
		if(mCurrentAngle/90%2==0){
			return new Rect((int)normalRectF.left,(int)normalRectF.top,(int)normalRectF.right,(int)normalRectF.bottom);
		}else {
			return new Rect((int)scaleRectF.left,(int)scaleRectF.top,(int)scaleRectF.right,(int)scaleRectF.bottom);
		}
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
		mCropActionListeners.add(mCropActionListener);
	}

	public void setmTextActionCacheQuery(TextActionCacheQuery mTextActionCacheQuery) {
		this.mTextActionCacheQuery = mTextActionCacheQuery;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean complete) {
		isComplete = complete;
	}

	public synchronized void recycleResource(){
		isComplete = false;
		if(masicBitmap!=null){
			masicBitmap.recycle();
			masicBitmap = null;
		}

		if(mBehindBackground!=null){
			mBehindBackground.recycle();
			mBehindBackground = null;
		}

		if(cropMasicBitmap!=null){
			cropMasicBitmap.recycle();
			cropMasicBitmap = null;
		}

		if(mForeBackground!=null){
			mForeBackground.recycle();
			mForeBackground = null;
		}

		if(cropBitmap!=null){
			cropBitmap.recycle();
			cropBitmap = null;
		}
		System.gc();
	}
}
