package com.widget.star;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.jeffen.note.R;

public class StarButtonOnClickListener implements OnClickListener {

	private long rowId;
	private String isStared;

	public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}

	public String getIsStared() {
		return isStared;
	}

	public void setIsStared(String isStared) {
		this.isStared = isStared;
	}

	public StarButtonOnClickListener(long rowId, String isStared) {
		this.rowId = rowId;
		this.isStared = isStared;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.d("StarButtonOnClickListener-onClick",
				"new StarButton click listener.");
		StarButton sb = (StarButton) v;
		isStared = "";
		if (sb.getImageResourceId() == R.drawable.star_pink) {
			sb.setImageResource(R.drawable.star_grey);
		} else {
			sb.setImageResource(R.drawable.star_pink);
			isStared = "1";
		}
	}

}