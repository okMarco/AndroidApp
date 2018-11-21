package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * .
 * Created by hochan on 2018/3/4.
 */

public class SquareImageView extends android.support.v7.widget.AppCompatImageView{

	public SquareImageView(Context context) {
		super(context);
	}

	public SquareImageView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public SquareImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
	}
}
