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
	 * ��ScrollView��Ƕ��ListView��
	 * ԭ��Ĭ�������Android�ǽ�ֹ��ScrollView�з��������ScrollView�ģ����ĸ߶����޷�����ġ�</p>
	 * ˼·��������������ListView��Adapter��
	 * ������ListView������Ŀ���¼���ListView�ĸ߶ȣ�Ȼ��Ѹ߶�����ΪLayoutParams���ø�ListView
	 * ���������ĸ߶Ⱦ���ȷ��</p> ע�⣺��ListView��ÿ��Item������LinearLayout��������������
	 * ����Ϊ������Layout(��RelativeLayout
	 * )û����дonMeasure()�����Ի���onMeasure()ʱ�׳�NullPoint�쳣��
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