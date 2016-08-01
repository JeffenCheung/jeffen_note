package com.widget.item;

import android.view.View;

public class ItemLongPressRunnable implements Runnable {
	private View item;

	public View getItem() {
		return item;
	}

	public void setItem(View item) {
		this.item = item;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	public ItemLongPressRunnable(View v) {
		this.item = v;
	}

}
