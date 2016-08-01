package com.widget;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jeffen.note.R;

public class ItemListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Resources mResources;
	private String[] array;

	public ItemListAdapter(LayoutInflater inflater, Resources resources,
			String[] array) {
		this.mResources = resources;
		this.mInflater = inflater;
		this.array = array;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return array.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return array[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = mInflater.inflate(R.layout.item, null);
		// 由于圆角listview一般都是固定的迹象，所以在这里没有做优化处理，需要的话可自行
		TextView tv = (TextView) convertView.findViewById(R.id.text);
		tv.setText(array[position]);
		if (array.length == 1) {
			setBackgroundDrawable(convertView, R.drawable.list_round_selector);
		} else if (array.length == 2) {
			if (position == 0) {
				setBackgroundDrawable(convertView, R.drawable.list_top_selector);
			} else if (position == array.length - 1) {
				setBackgroundDrawable(convertView,
						R.drawable.list_bottom_selector);
			}
		} else {
			if (position == 0) {
				setBackgroundDrawable(convertView, R.drawable.list_top_selector);
			} else if (position == array.length - 1) {
				setBackgroundDrawable(convertView,
						R.drawable.list_bottom_selector);
			} else {
				setBackgroundDrawable(convertView,
						R.drawable.list_rect_selector);
			}
		}
		return convertView;
	}

	private void setBackgroundDrawable(View view, int resID) {
		view.setBackgroundDrawable(mResources.getDrawable(resID));
	}
}