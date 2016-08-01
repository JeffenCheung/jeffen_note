package com.widget.item;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ItemPagerAdapter extends PagerAdapter {
	private List<View> mListViews;

	public ItemPagerAdapter(List<View> mListViews) {
		this.mListViews = mListViews;// ���췽�������������ǵ�ҳ���������ȽϷ��㡣
	}

	/**
	 * ����positionλ�õĽ���
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mListViews.get(position));// ɾ��ҳ��
	}

	/**
	 * ��ʼ��positionλ�õĽ���
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) { // �����������ʵ����ҳ��
		container.addView(mListViews.get(position), 0);// ���ҳ��
		return mListViews.get(position);
	}

	/**
	 * ��õ�ǰ������
	 */
	@Override
	public int getCount() {
		if (mListViews != null) {
			return mListViews.size();// ����ҳ��������
		}
		return 0;
	}

	/**
	 * �ж��Ƿ��ɶ������ɽ���
	 */
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;// �ٷ���ʾ����д
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

}
