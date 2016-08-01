package com.widget.item;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ItemOnCheckedChangeListener implements OnCheckedChangeListener {
	private View view;
	private int position;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public ItemOnCheckedChangeListener(View v) {
		this.view = v;
	}

	public ItemOnCheckedChangeListener(int position) {
		this.position = position;
	}

	public ItemOnCheckedChangeListener(View v, int position) {
		this.view = v;
		this.position = position;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub

	}

}
