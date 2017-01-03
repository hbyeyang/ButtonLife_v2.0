package com.anniu.shandiandaojia.view.pulltorefreshlayout.pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.anniu.shandiandaojia.utils.MyLog;

public class PullableListView extends ListView implements Pullable
{
	
	private boolean enablePullDown = true;
	private boolean enablePullUp = true;

	public PullableListView(Context context)
	{
		super(context);
	}

	public PullableListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullableListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown()
	{
		if(!enablePullDown){
			return false;
		}
		if (getCount() == 0)
		{
			// 没有item的时候也可以下拉刷新
			return true;
		} else if (getFirstVisiblePosition() == 0
				&& getChildAt(0)!=null && getChildAt(0).getTop() >= 0)
		{
			// 滑到ListView的顶部了
			return true;
		} else
			return false;
	}

	@Override
	public boolean canPullUp()
	{
		if(!enablePullUp){
			return false;
		}
		if (getCount() == 0)
		{
			// 没有item的时候也可以上拉加载
			return true;
		} else if (getLastVisiblePosition() == (getCount() - 1))
		{
			MyLog.i("PullableListView", "getLastVisiblePosition:"+getLastVisiblePosition());
			// 滑到底部了
			if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
					&& getChildAt(
							getLastVisiblePosition()
									- getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
				return true;
		}
		return false;
	}

	public boolean isEnablePullDown() {
		return enablePullDown;
	}

	public void setEnablePullDown(boolean enablePullDown) {
		this.enablePullDown = enablePullDown;
	}

	public boolean isEnablePullUp() {
		return enablePullUp;
	}

	public void setEnablePullUp(boolean enablePullUp) {
		this.enablePullUp = enablePullUp;
	}
}
