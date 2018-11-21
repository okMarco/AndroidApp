package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import com.crashlytics.android.Crashlytics;

/**
 * .
 * Created by hochan on 2018/6/13.
 */

public class WrapStaggeredGridLayoutManager extends StaggeredGridLayoutManager {

	public WrapStaggeredGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public WrapStaggeredGridLayoutManager(int spanCount, int orientation) {
		super(spanCount, orientation);
	}

	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
		try {
			super.onLayoutChildren(recycler, state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
		try {
			return super.scrollVerticallyBy(dy, recycler, state);
		} catch (Exception e) {
			Crashlytics.logException(e);
			return 0;
		}
	}
}
