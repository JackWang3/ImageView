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
	 * ��ʼ��ʱ���ŵ�ֵ
	 */
	private float mInitScale;
	
	/**
	 * ˫���Ŵ�ʱ�����ֵ
	 */
	private float mMidScale;
	
	/**
	 * �Ŵ�����ֵ
	 */
	private float mMaxScale;
	
	private Matrix mScaleMatrix;
	
	/**
	 * �����û���ָ����ʱ���ű���
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
	 *��ȡImageView������ɵ�ͼƬ 
	 */
	public void onGlobalLayout() {
		if (!mOnce) {
			//�õ��ؼ��Ŀ�͸�
			int width = getWidth();
			int height = getHeight();
			//�õ����ǵ�ͼƬ���Լ���͸�
			Drawable d = getDrawable();
			if (d == null) 
				return;
			int dw = d.getIntrinsicWidth();
			int dh = d.getIntrinsicHeight();
			
			float scale = 1.0f;
			
			/**
			 * ���ͼƬ��ȴ��ڿؼ���ȣ�����С�ڿؼ��߶ȣ����ǽ�����С
			 */
			if (dw > width && dh < height) {
				scale = width * 1.0f / dw;
			}
			
			/**
			 * ���ͼƬ�߶ȴ��ڿؼ��߶ȣ�����С�ڿؼ���ȣ����ǽ�����С
			 */
			if (dh > height && dw < width) {
				scale = height * 1.0f / dh;
			}
			
			if ((dw > width && dh > height) || (dw < width && dh < height)) {
				scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
			}
			
			/**
			 * �õ��˳�ʼ��ʱ���ŵı���
			 */
			mInitScale = scale;
			mMaxScale = mInitScale * 4;
			mMidScale = mInitScale * 2;
			
			//��ͼƬ�ƶ����ؼ�������
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
		
		//���ŷ�Χ�Ŀ���
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
