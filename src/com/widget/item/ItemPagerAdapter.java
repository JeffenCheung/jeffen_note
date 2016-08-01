package com.widget.item;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ItemPagerAdapter extends PagerAdapter {
	private List<View> mListViews;

	public ItemPagerAdapter(List<View> mListViews) {
		this.mListViews = mListViews;// 构造方法，参数是我们的页卡，这样比较方便。
	}

	/**
	 * 销毁position位置的界面
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mListViews.get(position));// 删除页卡
	}

	/**
	 * 初始化position位置的界面
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
		container.addView(mListViews.get(position), 0);// 添加页卡
		return mListViews.get(position);
	}

	/**
	 * 获得当前界面数
	 */
	@Override
	public int getCount() {
		if (mListViews != null) {
			return mListViews.size();// 返回页卡的数量
		}
		return 0;
	}

	/**
	 * 判断是否由对象生成界面
	 */
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;// 官方提示这样写
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
