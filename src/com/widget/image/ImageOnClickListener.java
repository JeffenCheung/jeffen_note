package com.widget.image;

import android.view.View;
import android.view.View.OnClickListener;

public class ImageOnClickListener implements OnClickListener {

	private int imageId;
	private long rowId;

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}

	public ImageOnClickListener(int position) {
		imageId = position;
	}

	public ImageOnClickListener(long mRowId, int mPosition) {
		this.rowId = mRowId;
		this.imageId = mPosition;
	}

	@Override
	public void onClick(View v) {

	}

}