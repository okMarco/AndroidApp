package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * .
 * Created by Administrator on 2016/5/14.
 */
public class RecycleViewDivider extends RecyclerView.ItemDecoration {

	public static final int DEFAULT_DIVIDER_HEIGHT = 10;

	private Paint mPaint;
	private Drawable mDivider;
	private int mDividerHeight = 10;//分割线高度，默认为1px
	private int mOrientation;
	private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
	public static final int ORIENTATION_BOTH = 2;

	/**
	 * 默认分割线：高度为2px，颜色为灰色
	 *
	 * @param context
	 * @param orientation 列表方向
	 */
	public RecycleViewDivider(Context context, int orientation) {
		if (orientation != WrapLinearLayoutManager.VERTICAL
				&& orientation != WrapLinearLayoutManager.HORIZONTAL
				&& orientation != ORIENTATION_BOTH) {
			throw new IllegalArgumentException("请输入正确的参数！");
		}
		mOrientation = orientation;

		final TypedArray a = context.obtainStyledAttributes(ATTRS);
		mDivider = a.getDrawable(0);
		a.recycle();
	}

	/**
	 * 自定义分割线
	 *
	 * @param context
	 * @param orientation 列表方向
	 * @param drawableId  分割线图片
	 */
	public RecycleViewDivider(Context context, int orientation, int drawableId) {
		this(context, orientation);
		mOrientation = orientation;
		mDivider = ContextCompat.getDrawable(context, drawableId);
		mDividerHeight = mDivider.getIntrinsicHeight();
	}

	/**
	 * 自定义分割线
	 *
	 * @param context
	 * @param orientation   列表方向
	 * @param dividerHeight 分割线高度
	 * @param dividerColor  分割线颜色
	 */
	public RecycleViewDivider(Context context, int orientation, int dividerHeight, int dividerColor) {
		this(context, orientation);
		mDividerHeight = dividerHeight;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mDivider = null;
		if (dividerColor != 0) {
			mPaint.setColor(dividerColor);
			mPaint.setStyle(Paint.Style.FILL);
		}
	}

	//获取分割线尺寸
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		if (mOrientation == WrapLinearLayoutManager.HORIZONTAL)
			outRect.set(0, 0, 0, mDividerHeight);
		if (mOrientation == WrapLinearLayoutManager.VERTICAL)
			outRect.set(0, 0, 0, mDividerHeight);
		if (mOrientation == ORIENTATION_BOTH) {
			if (parent.getLayoutManager() instanceof WrapLinearLayoutManager ||
					(parent.getLayoutManager() instanceof WrapStaggeredGridLayoutManager &&
							((WrapStaggeredGridLayoutManager) parent.getLayoutManager()).getSpanCount() == 1)) {
				parent.setPadding(0, 0, 0, 0);
				outRect.set(15, 15, 15, 0);
			} else {
				parent.setPadding(0, 0, 10, 0);
				outRect.set(10, 10, 0, 0);
			}

		}
	}

	//绘制分割线
	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDraw(c, parent, state);
		if (mDivider != null || mPaint != null) {
			if (mOrientation == WrapLinearLayoutManager.VERTICAL) {
				drawVertical(c, parent);
			} else if (mOrientation == WrapLinearLayoutManager.HORIZONTAL) {
				drawHorizontal(c, parent);
			}
		}
	}

	//绘制横向 item 分割线
	private void drawHorizontal(Canvas canvas, RecyclerView parent) {
		final int left = parent.getPaddingLeft();
		final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
		final int childSize = parent.getChildCount();
		for (int i = 0; i < childSize; i++) {
			final View child = parent.getChildAt(i);
			RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
			final int top = child.getBottom() + layoutParams.bottomMargin;
			final int bottom = top + mDividerHeight;
			if (mDivider != null) {
				mDivider.setBounds(left, top, right, bottom);
				mDivider.draw(canvas);
			}
			if (mPaint != null) {
				canvas.drawRect(left, top, right, bottom, mPaint);
			}
		}
	}

	//绘制纵向 item 分割线
	private void drawVertical(Canvas canvas, RecyclerView parent) {
		final int top = parent.getPaddingTop();
		final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
		final int childSize = parent.getChildCount();
		for (int i = 0; i < childSize; i++) {
			final View child = parent.getChildAt(i);
			RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
			final int left = child.getRight() + layoutParams.rightMargin;
			final int right = left + mDividerHeight;
			if (mDivider != null) {
				mDivider.setBounds(left, top, right, bottom);
				mDivider.draw(canvas);
			}
			if (mPaint != null) {
				canvas.drawRect(left, top, right, bottom, mPaint);
			}
		}
	}
}
