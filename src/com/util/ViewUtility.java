package com.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * View Adapter tools
 * 
 * @author Jeffen
 * 
 */
public class ViewUtility {
	/**
	 * 【ScrollView中嵌套ListView】
	 * 原因：默认情况下Android是禁止在ScrollView中放入另外的ScrollView的，它的高度是无法计算的。</p>
	 * 思路：就是在设置完ListView的Adapter后
	 * ，根据ListView的子项目重新计算ListView的高度，然后把高度再作为LayoutParams设置给ListView
	 * ，这样它的高度就正确了</p> 注意：子ListView的每个Item必须是LinearLayout，不能是其他的
	 * ，因为其他的Layout(如RelativeLayout
	 * )没有重写onMeasure()，所以会在onMeasure()时抛出NullPoint异常。
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		if (listView == null || listView.getCount() == 0)
			return;
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
}