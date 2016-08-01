package com.widget.star;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * custom star type imagebutton that can be obtain the background image resource
 * id.
 * 
 * </p> your layout xml resource might be like the following code: <br>
 * <view class="com.widget.StarButton" <br>
 * 		android:id="@+id/starButton" <br>
 * 		android:layout_width="wrap_content" <br>
 * 		android:layout_height="wrap_content" <br>
 * 		android:background="@android:color/background_light" <br>
 * 		android:paddingTop="10dp" <br>
 * 		android:src="@drawable/star" /> <br>
 * 
 * @author Jeffen
 * 
 */
public class StarButton extends ImageButton {
	private int mLastResourceId = -1;

	public StarButton(Context context) {
		super(context);
	}

	public StarButton(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.imageButtonStyle);
	}

	public StarButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setFocusable(true);
	}

	/**
	 * set image resource and sync image resource id.
	 */
	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);

		setImageResourceId(resId);
	}

	public int getImageResourceId() {
		return mLastResourceId;
	}

	public void setImageResourceId(int resId) {
		mLastResourceId = resId;
	}

}
