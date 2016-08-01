package com.jeffen.sys;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class SlidingMenu extends ViewGroup {

	// ===========================================================
	// Constants
	// ===========================================================


	
	// ===========================================================
	// Fields
	// ===========================================================

	private View mRightView;
	private View mLeftView;
	private ScrollRunnable mScrollRunnable;
	
	/**
	 * ��¼һ���ƶ�λ�ã����ڼ����ƶ�ƫ����
	 */
	private int mLastX;
	
	/**
	 * ����ʱ��¼�������жϵ�ǰ����ʱ����������
	 */
	private int mMotionX;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public SlidingMenu(Context context) {
		super(context);
	}

	public SlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// ===========================================================
	// Public Methods
	// ===========================================================

	public void addLeftView(View leftView) {
		mLeftView = leftView;
		addView(leftView);
	}
	
	/**
	 * �ṩ�Ҳ���ʾ��ͼ
	 * 
	 * @param rightView
	 */
	public void addRightView(View rightView) {
		mRightView = rightView;
		addView(rightView);
		
	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);

		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		setMeasuredDimension(widthSize, heightSize);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);
			
			int measuredWidth = childView.getMeasuredWidth();
			int measuredHeight = childView.getMeasuredHeight();
			
			childView.layout(l, 0, l + measuredWidth, measuredHeight);
		}
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		final int x = (int) event.getX();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastX = x;
			mMotionX = x;
			
			if (mScrollRunnable != null) {
				mScrollRunnable.endScroll();
				mScrollRunnable = null;
			}
			
			boolean inRegion = canSliding(event);
			
			return inRegion;

		case MotionEvent.ACTION_MOVE:
			
			scrollIfNeed(x);
			return true;
			
		case MotionEvent.ACTION_UP:
			
			autoScrollIfNeed(x);
			
			break;
		}
		
		return true;
	}


	// ===========================================================
	// Private Methods
	// ===========================================================
	
	/**
	 * ֻ���Ҳ���ͼ�����ƶ�
	 * 
	 * @param event
	 * @return true ���Թ���
	 */
	private boolean canSliding(MotionEvent event) {
		int[] location = new int[2];
		// ��ȡ�Ҳ���ͼ�������Ļ����ֵ
		mRightView.getLocationOnScreen(location);
		RectF region = new RectF();
		region.set(location[0] , location[1] ,
				location[0] + mRightView.getWidth(),
				location[1] + mRightView.getHeight());
		
		// ��ǰ��ָ���λ���Ƿ����Ҳ���ͼ������
		//boolean inRegion = region.contains(event.getRawX(), event.getRawY());
		boolean inRegion = region.contains(event.getX(), event.getY());
		return inRegion;
	}
	
	
	private void scrollIfNeed(final int x) {
		// �������ϴε�ƫ����
		int deltaX = x - mLastX;
		
		// �����ƶ�����
		if (x != mLastX) {
			int l = mRightView.getLeft();
			int t = mRightView.getTop();
			int b = mRightView.getBottom();
			
			// �Ҳ���ͼ�Ļ�������ֻ���������ͼ��Χ�ڻ���
			int rightViewLeft = Math.max(mLeftView.getLeft(), l + deltaX);
			rightViewLeft = Math.min(mLeftView.getRight(), rightViewLeft);
			
			// ��������ָ����
			mRightView.layout(rightViewLeft, t, rightViewLeft + mRightView.getWidth(), b);
		}
		
		// ��¼��ǰֵ���´μ���
		mLastX = x;
	}
	
	
	private void autoScrollIfNeed(final int x) {
		mScrollRunnable = new ScrollRunnable();
		
		// �����жϻ�������
		final int deltaX = x - mMotionX;
		// x�����������ε�������ָ���µ��ֵ��С��0˵������ָ���󻬶�
		boolean moveLeft = deltaX <= 0;
		
		// �������볬�������ͼһ�룬�Ż�������ָ�������
		final int distance = Math.abs(deltaX);
		if (distance < mLeftView.getWidth() / 2) {
			// ����������ȥ
			moveLeft = !moveLeft;
		}
		
		// �����Զ�����
		mScrollRunnable.startScroll(moveLeft);
	}

	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	private class ScrollRunnable implements Runnable {
		// ���������࣬�ṩ��ʼλ�ã��ƶ�ƫ�ƣ��ƶ���ʱ�䣬���Ի�ȡÿ�ι�������
		private Scroller mScroller = new Scroller(getContext());
		
		@Override
		public void run() {
			final Scroller scroller = mScroller;
			// �������ƫ�ƣ������Ƿ���Խ��Ź���
			boolean more = scroller.computeScrollOffset();
			// ������ȡ��Ҫ��������λ��
			final int x = scroller.getCurrX();
			
			if (more) {
				// ���ֶ��������õķ�����ͬ
				scrollIfNeed(x);
				// ��ǰ���߳��Ѿ�ִ���꣬������Ҫ���Ź���
				// ���԰ѵ�ǰRunnable�ٴ���ӵ���Ϣ������
				post(this);
			} else {
				// ����Ҫ����
				endScroll();
			}
			
		}
		
		
		private void startScroll(boolean moveLeft) {
			// ����ǰ���ó�ʼֵ
			mLastX = mRightView.getLeft();
			
			int dx = 0;
			
			// �����ƶ��ܾ���
			if (moveLeft) {
				// ��ǰ������ͼ���߽����
				dx = mLeftView.getLeft() - mRightView.getLeft();
			} else {
				// ���Ҳ�߽�
				dx = mLeftView.getRight() - mRightView.getLeft();
			}
			
			// ��ʼ����
			mScroller.startScroll(mRightView.getLeft(), 0, dx, 0, 300);
			// �ѵ�ǰRunnable��ӵ���Ϣ������
			post(this);
		}
		
		private void endScroll() {
			// ����Ϣ�����аѵ�ǰRunnableɾ������ֹͣ����
			removeCallbacks(this);
		}
		
	}
	

	
}
