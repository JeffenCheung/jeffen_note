package com.widget.star;

import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;

public class StarButtonOnLongClickListener implements OnLongClickListener {

	private long rowId;

	public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}

	public StarButtonOnLongClickListener(long rowId) {
		this.rowId = rowId;
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		Log.d("StarButtonOnLongClickListener-onLongClick",
				"new StarButton click listener.");
		return this.onLongClick(v);
	}

}