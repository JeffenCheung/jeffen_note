package com.widget.item;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.TextView;

public class ItemOnFocusChangeListener implements OnFocusChangeListener {

	public TextView itemLine;

	public ItemOnFocusChangeListener(TextView itemLine) {
		this.itemLine = itemLine;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			itemLine.setBackgroundResource(com.jeffen.note.R.color.KleinBlue);
		} else {
			itemLine.setBackgroundResource(com.jeffen.note.R.color.cTableLine);
		}
	}

}