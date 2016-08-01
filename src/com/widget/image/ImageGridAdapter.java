package com.widget.image;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.jeffen.note.ImageSwitchActivity;
import com.jeffen.note.NoteEditActivity;
import com.jeffen.note.R;

public class ImageGridAdapter extends BaseAdapter implements Serializable {

	private Context mContext;

	private Integer[] mThumbIds = {};
	private List<ImageView> mImageViewList;

	private View view = null;
	private int mBiCnt = 0;// bitmap image count

	public Bitmap[] mBitmapImages = new Bitmap[0];
	public List<byte[]> mByteImages = new ArrayList<byte[]>();
	public int mChoosedIndex = 0;

	public ImageGridAdapter(Context c) {
		mContext = c;
	}

	public ImageGridAdapter(Context c, Integer[] i) {
		mContext = c;
		mThumbIds = i;
	}

	public ImageGridAdapter(Context c, List<ImageView> il) {
		mContext = c;
		mImageViewList = il;
	}

	public Bitmap getBitmapAtPosition(int position) {
		return mBitmapImages[position];
	}

	public Bitmap getChoosedBitmap() {
		return mBitmapImages[mChoosedIndex];
	}

	@Override
	public int getCount() {
		// if (mThumbIds != null) {
		// return mThumbIds.length;
		// }
		// if (mImageViewList != null) {
		// return mImageViewList.size();
		// }
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

	/**
	 * 封装成 arraylist<iamgeview> [废弃]
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
	public View getImageView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = mImageViewList.get(position);
		} else {
			imageView = (ImageView) convertView;
		}

		return imageView;
	}

	/**
	 * 封装成 imageview [废弃]
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
	public View getDrawableView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(45, 45));// 设置ImageView宽高
			imageView.setAdjustViewBounds(false);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}

		imageView.setImageResource(mThumbIds[position]);

		return imageView;
	}

	/*
	 * 代码创建 [废弃]
	 */
	public View getNewView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(150, 150));// 设置ImageView宽高
			imageView.setAdjustViewBounds(false);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

		} else {
			imageView = (ImageView) convertView;
		}
		imageView.setImageBitmap(mBitmapImages[position]);

		return imageView;
	}

	/**
	 * xml创建
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		view = convertView;
		View view = View.inflate(mContext, R.layout.image_grid, null);

		ImageView image = (ImageView) view.findViewById(R.id.chooseImage);
		// image.setLayoutParams(new
		// android.widget.FrameLayout.LayoutParams(150,
		// 150));// 设置ImageView宽高
		image.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		image.setAdjustViewBounds(false);
		image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		image.setImageBitmap(mBitmapImages[position]);

		TextView text = (TextView) view.findViewById(R.id.chooseText);
		text.setText("" + (position + 1) + "-"
				+ mBitmapImages[position].getWidth() + "*"
				+ mBitmapImages[position].getHeight());

		ImageView delete = (ImageView) view.findViewById(R.id.imageDelete);
		delete.setOnClickListener(new ImageOnClickListener(position) {

			@Override
			public void onClick(View v) {
				super.onClick(v);
				NoteEditActivity a = (NoteEditActivity) mContext;
				a.removeImage(super.getImageId());
			}

		});

		image.setOnClickListener(new ImageOnClickListener(position) {

			@Override
			public void onClick(View v) {
				super.onClick(v);

				if (mContext instanceof NoteEditActivity) {
					NoteEditActivity a = (NoteEditActivity) mContext;
					a.goToImageSwitch(super.getImageId());
				}

				if (mContext instanceof ImageSwitchActivity) {
					ImageSwitchActivity a = (ImageSwitchActivity) mContext;
					a.goToImageSwitch(super.getImageId());
				}
			}

		});

		return view;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 追加图片[废弃]
	 * 
	 * @param photo
	 */
	public void addBitmapImageByDouble(Bitmap photo) {
		if (mBiCnt >= mBitmapImages.length) {
			Bitmap[] newBitmap = new Bitmap[mBitmapImages.length * 2];
			for (int i = 0; i < mBitmapImages.length; i++) {
				newBitmap[i] = mBitmapImages[i];
			}

			mBitmapImages = newBitmap;

		}

		mBitmapImages[mBiCnt++] = photo;
	}

	/**
	 * 追加图片
	 * 
	 * @param photo
	 */
	public void addBitmapImage(GridView mPhotoGrid, Bitmap photo, byte[] b) {
		Bitmap[] newBitmap = new Bitmap[mBitmapImages.length + 1];
		for (int i = 0; i < mBitmapImages.length; i++) {
			newBitmap[i] = mBitmapImages[i];
		}

		mBitmapImages = newBitmap;
		mBitmapImages[mBiCnt++] = photo;

		mByteImages.add(b);

		layoutAdaption(mPhotoGrid);
	}

	/**
	 * bytes images to bitmap iamges
	 * 
	 * @param b
	 */
	public void synByte2Bit(GridView mPhotoGrid, List<byte[]> b) {
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

		layoutAdaption(mPhotoGrid);
	}

	/**
	 * 删除图片[废弃]
	 * 
	 * @param position
	 */
	public void cutBitmapImage(int position) {
		if (position < mBitmapImages.length) {
			if (position == mBitmapImages.length - 1) {
				// 最后一个
				mBitmapImages[position] = null;
			} else {
				Bitmap[] newBitmap = new Bitmap[mBitmapImages.length];
				for (int i = 0; i < mBitmapImages.length; i++) {
					if (i >= position) {
						if (i == mBitmapImages.length - 1) {
							mBitmapImages[position] = null;
						} else {
							newBitmap[i] = mBitmapImages[i + 1];
						}
					} else {
						newBitmap[i] = mBitmapImages[i];
					}
				}
				mBitmapImages = newBitmap;
			}

			mBiCnt--;
		}

	}

	/**
	 * 删除图片
	 * 
	 * @param position
	 */
	public void removeBitmapImage(GridView mPhotoGrid, int position) {

		Bitmap[] newBitmap = new Bitmap[mBitmapImages.length - 1];
		int newI = 0;
		for (int i = 0; i < mBitmapImages.length; i++) {
			if (i != position) {
				newBitmap[newI++] = mBitmapImages[i];
			}
		}
		mBitmapImages = newBitmap;

		mBiCnt--;

		mByteImages.remove(position);

		layoutAdaption(mPhotoGrid);
	}

	/**
	 * 宽度自适应
	 */
	private void layoutAdaption(GridView mPhotoGrid) {
		int width = mPhotoGrid.getColumnWidth()
				+ mPhotoGrid.getHorizontalSpacing();
		// int h = mPhotoGrid.getHeight();
		if (width < 173) {
			// 初始实例化：32 + 3? 70+5?
			width = 173;
		}
		LayoutParams params = new LayoutParams(mBiCnt * width,
				LayoutParams.WRAP_CONTENT);
		// LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT);

		mPhotoGrid.setLayoutParams(params); // gridView的整体宽度

		mPhotoGrid.setAdapter(this);
	}

	/**
	 * [废弃] You are passing Bitmap into Intent and get bitmap in next activity
	 * from bundle, but the problem is if your Bitmap/Image size is big at that
	 * time the image is not load in next activity. so,shows nothing (as you
	 * would expect with a null bitmap)
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] bitmapToByteArray(Bitmap bm) {
		// Create the buffer with the correct size
		int iBytes = bm.getWidth() * bm.getHeight() * 4;
		ByteBuffer buffer = ByteBuffer.allocate(iBytes);

		// Log.e("DBG", buffer.remaining()+""); -- Returns a correct number
		// based on dimensions
		// Copy to buffer and then into byte array
		bm.copyPixelsToBuffer(buffer);
		// Log.e("DBG", buffer.remaining()+""); -- Returns 0
		return buffer.array();
	}

}