package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * .
 * Created by hochan on 2018/6/12.
 */

public class WrapLinearLayoutManager extends LinearLayoutManager {

	public WrapLinearLayoutManager(Context context) {
		super(context);
	}

	public WrapLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
		super(context, orientation, reverseLayout);
	}

	public WrapLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
		try {
			super.onLayoutChildren(recycler, state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
