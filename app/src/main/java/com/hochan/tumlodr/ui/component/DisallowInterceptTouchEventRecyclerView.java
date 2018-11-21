package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * .
 * Created by hochan on 2018/6/4.
 */

public class DisallowInterceptTouchEventRecyclerView extends RecyclerView {

	public DisallowInterceptTouchEventRecyclerView(Context context) {
		super(context);
	}

	public DisallowInterceptTouchEventRecyclerView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public DisallowInterceptTouchEventRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		requestDisallowInterceptTouchEvent(true);
		super.onTouchEvent(e);
		return true;
	}
}
