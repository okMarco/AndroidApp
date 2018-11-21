package com.hochan.tumlodr.ui.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.hochan.tumlodr.R;

/**
 * .
 * Created by hochan on 2018/6/4.
 */

public class DisallowInterceptTouchEventDrawerLayout extends DrawerLayout {

	private View mRecyclerView;
	private View mDrawer;
	private final Rect mRect = new Rect();

	public DisallowInterceptTouchEventDrawerLayout(@NonNull Context context) {
		super(context);
	}

	public DisallowInterceptTouchEventDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public DisallowInterceptTouchEventDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@SuppressLint("RtlHardcoded")
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mRecyclerView == null) {
			mRecyclerView = findViewById(R.id.rcy_visited_blog);
		}
		if (mDrawer == null) {
			mDrawer = findViewById(R.id.ll_left_menu);
		}
		if (mRecyclerView != null && isDrawerOpen(Gravity.LEFT) && mDrawer != null &&
				ev.getX() <= mDrawer.getMeasuredWidth()) {
			mRecyclerView.getGlobalVisibleRect(mRect);
			if (ev.getY() >= mRect.top && ev.getY() <= mRect.bottom) {
				return false;
			}
		}
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (Exception e) {
			return false;
		}
	}
}
