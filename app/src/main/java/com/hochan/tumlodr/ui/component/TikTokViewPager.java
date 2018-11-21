package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * .
 * <p>
 * Created by hochan on 2018/5/22.
 */

public class TikTokViewPager extends ViewPager {

	private boolean mEnable = true;

	public TikTokViewPager(@NonNull Context context) {
		super(context);
	}

	public TikTokViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public void setEnable(boolean enable) {
		mEnable = enable;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return mEnable && super.onInterceptTouchEvent(ev);
		} catch (Exception e) {
			return false;
		}
	}
}
