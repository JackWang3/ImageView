package com.example.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ZoomImageView extends ImageView implements OnGlobalLayoutListener, 
OnScaleGestureListener, OnTouchListener {

private boolean mOnce = false;
	
	/**
	 * 初始化时缩放的值
	 */
	private float mInitScale;
	
	/**
	 * 双击放大时到达的值
	 */
	private float mMidScale;
	
	/**
	 * 放大的最大值
	 */
	private float mMaxScale;
	
	private Matrix mScaleMatrix;
	
	/**
	 * 捕获用户多指触控时缩放比例
	 */
	private ScaleGestureDetector mScaleGestureDetector;
	
	public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//init
		mScaleMatrix = new Matrix();
		setScaleType(ScaleType.MATRIX);
		
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		setOnTouchListener(this);
		
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ZoomImageView(Context context) {
		this(context, null);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}
	
	/**
	 *获取ImageView加载完成的图片 
	 */
	public void onGlobalLayout() {
		if (!mOnce) {
			//得到控件的宽和高
			int width = getWidth();
			int height = getHeight();
			//得到我们的图片，以及宽和高
			Drawable d = getDrawable();
			if (d == null) 
				return;
			int dw = d.getIntrinsicWidth();
			int dh = d.getIntrinsicHeight();
			
			float scale = 1.0f;
			
			/**
			 * 如果图片宽度大于控件宽度，但是小于控件高度；我们将其缩小
			 */
			if (dw > width && dh < height) {
				scale = width * 1.0f / dw;
			}
			
			/**
			 * 如果图片高度大于控件高度，但是小于控件宽度；我们将其缩小
			 */
			if (dh > height && dw < width) {
				scale = height * 1.0f / dh;
			}
			
			if ((dw > width && dh > height) || (dw < width && dh < height)) {
				scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
			}
			
			/**
			 * 得到了初始化时缩放的比例
			 */
			mInitScale = scale;
			mMaxScale = mInitScale * 4;
			mMidScale = mInitScale * 2;
			
			//将图片移动至控件的中心
			int dx = getWidth() / 2 - dw / 2; 
			int dy = getHeight() / 2 - dh / 2;
			
			mScaleMatrix.postTranslate(dx, dy);
			mScaleMatrix.postScale(mInitScale, mInitScale, width / 2, height / 2);
			setImageMatrix(mScaleMatrix);
			
			mOnce = true;
		}
	}
	
	public float getScale() {
		float[] values = new float[9];
		mScaleMatrix.getValues(values);
		return values[Matrix.MSCALE_X];
	}
	
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float scale = getScale();
		float scaleFactor = detector.getScaleFactor();
		
		if (getDrawable() == null) 
			return true;
		
		//缩放范围的控制
		if ((scale < mMaxScale && scaleFactor > 1.0f) ||
				(scale > mInitScale && scaleFactor < 1.0f)) {
			if (scale * scaleFactor < mInitScale) {
				scaleFactor = mInitScale / scale;
			}
		
			if (scale * scaleFactor > mMaxScale) {
				scaleFactor = mMaxScale / scale;
			}
		
			mScaleMatrix.postScale(scaleFactor, scaleFactor, 
					getWidth() / 2, getHeight() / 2);
			setImageMatrix(mScaleMatrix);
		}
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector arg0) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector arg0) {
		
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		mScaleGestureDetector.onTouchEvent(event);
		return true;
	}
}
