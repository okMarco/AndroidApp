package com.hochan.tumlodr.ui.viewholder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;

/**
 * .
 * Created by hochan on 2017/12/16.
 */

public class ExactOffsetLinearLayoutManager extends LinearLayoutManager {

	private SparseIntArray a = new SparseIntArray();

	public ExactOffsetLinearLayoutManager(Context context) {
		super(context);
	}

	public ExactOffsetLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
		super(context, orientation, reverseLayout);
	}

	public ExactOffsetLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public void measureChild(View child, int widthUsed, int heightUsed) {
		super.measureChild(child, widthUsed, heightUsed);
		int layoutPosition = ((RecyclerView.LayoutParams) child.getLayoutParams()).getViewLayoutPosition();
		a.put(layoutPosition, child.getMeasuredHeight());
	}

	// 设置更多的预留空间
	@Override
	protected int getExtraLayoutSpace(RecyclerView.State state) {
		int var2 = getItemCount();
		int var3 = 0;
		if (var2 != 0) {
			int var4 = findFirstCompletelyVisibleItemPosition();
			int var7;
			for (int i = 0; i < var4; var3 = var7) {
				var7 = var3 + a.get(i);
				i++;
			}

			View var6 = findViewByPosition(var4);
			if (var6 != null && var6.getTop() < 0) {
				return var3 + -var6.getTop();
			}
		}
		return var3;
	}
}
