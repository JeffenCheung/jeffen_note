package com.widget.item;

import com.util.CommonCheck;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

public class ScoreOnFocusChangeListener implements OnFocusChangeListener {

	public EditText scoreET;

	public ScoreOnFocusChangeListener(EditText et) {
		this.scoreET = et;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			if (scoreET.getText() != null
					&& "0".equals(scoreET.getText().toString())) {
				scoreET.setText("");
			}
		} else {
			if (scoreET.getText() == null
					|| CommonCheck.isEmpty((scoreET.getText().toString()))) {
				scoreET.setText("0");
			}
		}
	}
}