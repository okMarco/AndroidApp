package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * .
 * Created by czd on 2017/9/13.
 */

public class ZoomChangeSpanCountViewGroup extends FrameLayout {

	private static final String TAG = "ZoomChangeSpanCountViewGroup";

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private final TextView tvSpanCount;

	private int mMode = NONE;

	// 初始的两个手指按下的触摸点的距离
	private float oriDis = 1f;

	private RecyclerView mRecyclerView;

	private boolean mIsSpanCountChanged = false;
	private float scale;

	public ZoomChangeSpanCountViewGroup(@NonNull Context context) {
		this(context, null);
	}

	public ZoomChangeSpanCountViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ZoomChangeSpanCountViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		tvSpanCount = new TextView(context);
		tvSpanCount.setTextSize(250);
		tvSpanCount.setTextColor(ContextCompat.getColor(context, android.R.color.white));
		tvSpanCount.getPaint().setFakeBoldText(true);
		tvSpanCount.setGravity(Gravity.CENTER);
		tvSpanCount.setVisibility(INVISIBLE);
		addView(tvSpanCount, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		bringChildToFront(tvSpanCount);
		super.onLayout(changed, left, top, right, bottom);
		if (mRecyclerView == null) {
			findRecyclerView(this);
		}
	}

	private void findRecyclerView(ViewGroup parent) {
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = parent.getChildAt(i);
			if (child instanceof RecyclerView) {
				mRecyclerView = (RecyclerView) child;
			} else if (child instanceof ViewGroup) {
				findRecyclerView((ViewGroup) child);
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (mRecyclerView == null) {
			return super.onInterceptTouchEvent(event);
		}
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				// 第一个手指按下事件
				mMode = DRAG;
				return false;
			case MotionEvent.ACTION_POINTER_DOWN:
				// 第二个手指按下事件
				oriDis = distance(event);
				if (oriDis > 10f) {
					mMode = ZOOM;
					// 在onInterceptTouchEvent返回true之后会往子View传MotionEvent.Cancel
					// 之后直接调用onTouchEvent
					mIsSpanCountChanged = false;
					scale = 1;
					return true;
				}
		}

		return false;
	}

	// 计算两个触摸点之间的距离
	private float distance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mRecyclerView == null) {
			return super.onTouchEvent(event);
		}
		// 进行与操作是为了判断多点触摸
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP: {
				// 手指放开事件
				mMode = NONE;
				tvSpanCount.animate().alpha(0).setDuration(200).start();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				// 手指滑动事件
				if (mMode == DRAG) {
					// 是一个手指拖动
					return false;
				} else if (mMode == ZOOM) {
					// 两个手指滑动
					float newDist = distance(event);
					if (newDist > 10f) {
						scale = newDist / oriDis;
						if (scale < 0.7) {
							changeSpanCount(true);
							break;
						} else if (scale > 1.5) {
							changeSpanCount(false);
							break;
						}
					}
				}
				break;
			}
		}
		return false;
	}

	private void changeSpanCount(boolean isMore) {
		if (mIsSpanCountChanged || mRecyclerView == null) {
			return;
		}
		int[] pos = new int[5];
		if (isMore) {
		} else {
		}
		mIsSpanCountChanged = true;
	}

	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
	}
}