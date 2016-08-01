/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.savedInstanceState
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jeffen.note;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;

import com.widget.image.ImageAdapter;
import com.widget.image.ImageGridAdapter;

/**
 * @author Jeffen Cheung
 * 
 */
public class ImageSwitchActivity extends NoteActivity implements
		OnItemSelectedListener, ViewFactory {

	public static final String TAG = "ImageSwitchActivity";

	private ImageSwitcher is;
	private Gallery gallery;
	private ImageAdapter iamgeAdapter;

	private Bitmap mChooseBitmap;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		// home button to return
		ActionBar actionBar = getActionBar();
		// actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// or Intent.FLAG_ACTIVITY_NEW_TASK
		getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		mDbHelper = new NoteDbAdapter(this);
		mDbHelper.open();
		showImageSwitch();

	}

	/**
	 * my image switch(废弃)
	 */
	private void showMyImageSwitch() {
		setContentView(R.layout.image_switch);

		mPhotoGrid = (GridView) findViewById(R.id.photoGrid);
		mImageAdapter = new ImageGridAdapter(this);

		// 每一个intent都会带一个Bundle型的extras数据。
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			// 取得参数
			mRowId = extras.getLong(NoteDbAdapter.KEY_ROWID);
			mChooseImagePosition = extras.getInt("image_position");
			// 取得并设定图片组
			mImageAdapter.synByte2Bit(mPhotoGrid,
					mDbHelper.getByteImages(mRowId));

			// 设定预览选中图片
			mChooseImage = (ImageView) findViewById(R.id.chooseImage);
			mImageAdapter.mChoosedIndex = mChooseImagePosition;
			mChooseImage.setImageBitmap(mImageAdapter.getChoosedBitmap());
		}
	}

	/**
	 * android image switch
	 */
	private void showImageSwitch() {
		setContentView(R.layout.imageswitcher);

		is = (ImageSwitcher) findViewById(R.id.imageSwitcher_id);
		is.setFactory(this);
		is.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		is.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));

		gallery = (Gallery) findViewById(R.id.Imageswitcher_gallery_id);
		iamgeAdapter = new ImageAdapter(this);

		// 每一个intent都会带一个Bundle型的extras数据。
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			// 取得参数
			mRowId = extras.getLong(NoteDbAdapter.KEY_ROWID);
			mChooseImagePosition = extras.getInt("image_position");
			// 取得并设定图片组
			iamgeAdapter.synByte2Bit(mDbHelper.getByteImages(mRowId));
			gallery.setAdapter(iamgeAdapter);

			gallery.setOnItemSelectedListener(this);
			gallery.setSelection(mChooseImagePosition);

		}

	}

	@Override
	public View makeView() {

		ImageView i = new ImageView(this);
		i.setBackgroundColor(0xFF000000);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				ImageSwitcher.LayoutParams.MATCH_PARENT,
				ImageSwitcher.LayoutParams.MATCH_PARENT));

		return i;

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		mChooseImagePosition = position;
		mChooseBitmap = iamgeAdapter.getBitmapAtPosition(mChooseImagePosition);
		is.setImageDrawable(new BitmapDrawable(mChooseBitmap));

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// create action bar menus
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.image_switch, menu);

		return true;
	}

	/**
	 * action bar menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.iamge_switch_menu_rotate:
			// rotate 90 degree
			mChooseBitmap = RotateBitmap(mChooseBitmap, 90);
			is.setImageDrawable(new BitmapDrawable(mChooseBitmap));

			return true;
		case android.R.id.home:
			onBack();
			return super.onOptionsItemSelected(item);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * rotate the bitmap image
	 * 
	 * @param source
	 * @param angle
	 * @return
	 */
	public static Bitmap RotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}

	/**
	 * 取消按钮动作，放到onClick方法体，会发生intent创建不能！因为this指代对象不同
	 * 
	 * @param view
	 */
	private void onBack() {
		super.onBackPressed();
	}

}
