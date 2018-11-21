package com.hochan.tumlodr.module.webbrowser;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * .
 * Created by hochan on 2018/7/29.
 */

public class WebVideoImageView extends android.support.v7.widget.AppCompatImageView {

	public WebVideoImageView(Context context) {
		super(context);
	}

	public WebVideoImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WebVideoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int height = (int) (getMeasuredWidth() / (16 / 9.0f));
		setMeasuredDimension(getMeasuredWidth(), height);
	}
}
