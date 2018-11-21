package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class HackyViewPager extends ViewPager {

	private int mTouchSlopLeft;
	private int mTouchSlopRight;

	public HackyViewPager(Context context) {
		this(context, null);
	}

	public HackyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
		mTouchSlopLeft = viewConfiguration.getScaledTouchSlop();
		mTouchSlopRight = getResources().getDisplayMetrics().widthPixels - mTouchSlopLeft;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return ev.getX() < mTouchSlopLeft || ev.getX() > mTouchSlopRight || super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}