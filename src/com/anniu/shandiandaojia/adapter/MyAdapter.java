package com.anniu.shandiandaojia.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

public class MyAdapter extends PagerAdapter {
	private ImageView[] mImageViews;

	public MyAdapter(ImageView[] mImageViews) {
		super();
		this.mImageViews = mImageViews;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(mImageViews[position % mImageViews.length]);
	}

	/**
	 * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
	 */
	@Override
	public Object instantiateItem(View container, int position) {
		ImageView child = mImageViews[position % mImageViews.length];
		((ViewPager) container).removeView(child);
		((ViewPager) container).addView(child, 0);
		return child;
	}

}
