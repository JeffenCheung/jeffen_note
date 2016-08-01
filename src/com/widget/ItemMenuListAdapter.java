package com.widget;

import com.jeffen.note.NoteListActivity;
import com.jeffen.note.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItemMenuListAdapter extends BaseAdapter {

	private Context mContext;
	private int[] imgIds = { R.drawable.list_bullets_silver, R.drawable.top, R.drawable.paper_airplane_silver,
			R.drawable.alarm, R.drawable.share };

	public ItemMenuListAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return imgIds.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		TextView textView = new TextView(mContext);
		// ���array.xml�е�������ԴgetStringArray���ص���һ��String����
		String text = mContext.getResources().getStringArray(
				R.array.item_dialog_menu)[position];
		textView.setText(text);
		// ���������С
		textView.setTextSize(24);
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		textView.setLayoutParams(layoutParams);
		// ����ˮƽ�����Ͼ���
		textView.setGravity(android.view.Gravity.CENTER_VERTICAL);
		textView.setMinHeight(120);
		// ����������ɫ
		textView.setTextColor(Color.BLACK);
		// ����ͼ�������ֵ����
		textView.setCompoundDrawablesWithIntrinsicBounds(imgIds[position], 0,
				0, 0);
		// ����textView���������µ�padding��С
		textView.setPadding(30, 0, 30, 0);
		// �������ֺ�ͼ��֮���padding��С
		textView.setCompoundDrawablePadding(30);
		return textView;
	}

}