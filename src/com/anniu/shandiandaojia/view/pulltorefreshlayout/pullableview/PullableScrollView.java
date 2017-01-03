package com.anniu.shandiandaojia.view.pulltorefreshlayout.pullableview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

public class PullableScrollView extends ScrollView implements Pullable {
    //TODO 设置下拉刷新、上拉加载更多
    private boolean enablePullDown = true;
    private boolean enablePullUp = false;

    public PullableScrollView(Context context) {
        super(context);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    public PullableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    public PullableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        if (!enablePullDown) {
            return false;
        }
        if (getScrollY() == 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean canPullUp() {
        if (!enablePullUp) {
            return false;
        }
        if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
            return true;
        else
            return false;
    }

    public void setEnablePullDown(boolean enablePullDown) {
        this.enablePullDown = enablePullDown;
    }

    public void setEnablePullUp(boolean enablePullUp) {
        this.enablePullUp = enablePullUp;
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }

    private float mLastMotionX;
    private float mLastMotionY;
    private int mTouchSlop;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int xDiff = (int) Math.abs(x - mLastMotionX);
                int yDiff = (int) Math.abs(y - mLastMotionY);
                final int x_yDiff = xDiff * xDiff + yDiff * yDiff;
                boolean xMoved = x_yDiff > mTouchSlop * mTouchSlop;

                if (xMoved) {
                    if (xDiff * 2 < yDiff) {
                        setEnablePullDown(true);
                        setEnablePullUp(true);
                        return true;
                    } else {
                        setEnablePullDown(false);
                        setEnablePullUp(false);
                        return false;
                    }
                } else {
                    setEnablePullDown(true);
                    setEnablePullUp(true);
                }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
