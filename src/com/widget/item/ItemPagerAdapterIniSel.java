package com.widget.item;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.jeffen.note.R;

public class ItemPagerAdapterIniSel extends PagerAdapter {
	public List<View> viewList = new ArrayList<View>();

	public void ItemPagerAdapter() {
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public Object instantiateItem(View collection, int position) {

		LayoutInflater inflater = (LayoutInflater) collection.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		int resId = 0;
		switch (position) {
		case 0:
			resId = R.layout.note_row_pocket;
			break;
		case 1:
			resId = R.layout.note_row;
			break;
		case 2:
			resId = R.layout.note_row_bookmark;
			break;
		}

		View view = inflater.inflate(resId, null);
		((ViewPager) collection).addView(view, 0);

		return view;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);

	}

	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == ((View) arg1);

	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		// TODO Auto-generated method stub

	}
}
