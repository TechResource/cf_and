package com.flightpathcore.utilities;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SwipeableViewPager extends ViewPager {

	private boolean swipeAble = false;

	public SwipeableViewPager(Context context, AttributeSet att) {
		super(context, att);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (swipeAble) {
			return super.onTouchEvent(ev);
		} else {
			return false;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (swipeAble) {
			return super.onInterceptTouchEvent(ev);
		} else {
			return false;
		}
	}

	public void setSwipeAble(boolean swipeAble) {
		this.swipeAble = swipeAble;
	}

	public boolean isSwipeAble(){
		return swipeAble;
	}
}
