package com.widget.item;

import android.text.Editable;
import android.text.TextWatcher;

public class ItemTextWatcher implements TextWatcher {
	private int position;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public ItemTextWatcher(int position) {
		this.position = position;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

}
