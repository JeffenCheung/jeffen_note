package com.widget.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

public class LinedEditText extends EditText {

	private Paint mPaint = null;
	private Rect mRect = null;

	public LinedEditText(Context context) {
		super(context);
		init();
	}

	public LinedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mRect = new Rect();
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.STROKE); // ������Ϊʵ��
		mPaint.setColor(Color.BLUE); // �����ߵ���ɫ
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int count = getLineCount();
		for (int i = 0; i < count; i++) {
			int baseline = getLineBounds(i, mRect); // ��ȡ�����еĻ���
			canvas.drawLine(mRect.left, baseline + 1, mRect.right,
					baseline + 1, mPaint);
		}
	}

}
