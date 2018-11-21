package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.crashlytics.android.Crashlytics;

/**
 * .
 * Created by hochan on 2018/6/23.
 */

public class WrapImageView extends android.support.v7.widget.AppCompatImageView {

	public WrapImageView(Context context) {
		super(context);
	}

	public WrapImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WrapImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		try {
			super.onDraw(canvas);
		} catch (Exception e) {
			Crashlytics.logException(e);
		}
	}
}
