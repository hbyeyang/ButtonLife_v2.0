package com.anniu.shandiandaojia.view;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.anniu.shandiandaojia.utils.MyLog;

/**
 * Created by Administrator on 2015/9/10.
 */
public class ChildViewPager extends ViewPager {
    //触摸时按下的点 
    PointF downP = new PointF();
    //触摸时当前的点 
    PointF curP = new PointF();

    public ChildViewPager(Context context) {
        super(context);
    }

    public ChildViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        curP.x = arg0.getX();
        curP.y = arg0.getY();
        if (arg0.getAction() == MotionEvent.ACTION_DOWN) {
            //记录按下时候的坐标
            // 切记不可用 downP = curP ，这样在改变curP的时候，downP也会改变  
            downP.x = arg0.getX();
            downP.y = arg0.getY();
            //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        if (arg0.getAction() == MotionEvent.ACTION_MOVE) {
            //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
            getParent().requestDisallowInterceptTouchEvent(true);
            if (Math.abs(downP.x - curP.x) > 100) {
                MotionEvent motionEvent = MotionEvent.obtain(arg0);
                motionEvent.setAction(MotionEvent.ACTION_CANCEL);
                motionEvent.recycle();
            }
        }
        if (arg0.getAction() == MotionEvent.ACTION_UP) {
            float mx = curP.x - downP.x;
            float my = curP.y - downP.y;
            MyLog.e("--zxl--", "--zxl--mx=" + Math.abs(mx) + " my=" + Math.abs(my) + "---");
            if (Math.abs(mx) > Math.abs(my)) {
                return true;
            }
        }
        return super.onTouchEvent(arg0);
    }
}
