package com.widget.image;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;

	private View view = null;
	private int mBiCnt = 0;// bitmap image count

	public Bitmap[] mBitmapImages = new Bitmap[0];
	public List<byte[]> mByteImages = new ArrayList<byte[]>();
	public int mChoosedIndex = 0;

	public ImageAdapter(Context c) {
		mContext = c;
	}

	public ImageAdapter(Context c, List<ImageView> il) {
		mContext = c;
	}

	public Bitmap getBitmapAtPosition(int position) {
		return mBitmapImages[position];
	}

	public Bitmap getChoosedBitmap() {
		return mBitmapImages[mChoosedIndex];
	}

	@Override
	public int getCount() {
		if (mBitmapImages != null) {
			return mBitmapImages.length;
		}
		return 0;

	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ImageView i = new ImageView(mContext);
		i.setImageBitmap(mBitmapImages[position]);
		i.setAdjustViewBounds(true);
		i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		return i;

	}

	/**
	 * bytes images to bitmap iamges
	 * 
	 * @param b
	 */
	public void synByte2Bit(List<byte[]> b) {
		if (b == null)
			b = new ArrayList<byte[]>();
		mByteImages = b;
		mBiCnt = mByteImages.size();
		Bitmap[] newBitmap = new Bitmap[mBiCnt];
		for (int i = 0; i < mBiCnt; i++) {
			ByteArrayInputStream imageStream = new ByteArrayInputStream(
					mByteImages.get(i));
			newBitmap[i] = BitmapFactory.decodeStream(imageStream);
		}

		mBitmapImages = newBitmap;

	}

}