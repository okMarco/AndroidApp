package com.hochan.tumlodr.ui.component;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.LinearLayout;

/**
 * .
 * Created by hochan on 2018/6/4.
 */

public class FitsSystemWindowLinearLayout extends LinearLayout {

	private Rect originalPadding;

	public FitsSystemWindowLinearLayout(Context context) {
		super(context);
		this.setup();
	}

	public FitsSystemWindowLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setup();
	}

	public FitsSystemWindowLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.setup();
	}

	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (this.navBarCanMove()) {
			this.setup();
		}
		ViewCompat.requestApplyInsets(this);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		ViewCompat.requestApplyInsets(this);
	}

	protected boolean fitSystemWindows(@NonNull Rect insets) {
		this.updatePadding(insets);
		return false;
	}

	@TargetApi(20)
	public WindowInsets onApplyWindowInsets(WindowInsets insets) {
		Rect windowInsets = new Rect(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(), insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
		this.fitSystemWindows(windowInsets);
		return insets;
	}

	private void setup() {
		this.setFitsSystemWindows(true);

		if (this.originalPadding == null) {
			this.originalPadding = new Rect(this.getPaddingLeft(), this.getPaddingTop(), this.getPaddingRight(), this.getPaddingBottom());
		}

		this.updatePadding(new Rect());
	}

	private void updatePadding(Rect insets) {
		int bottomPadding = this.originalPadding.bottom + insets.bottom;
		int topPadding = this.originalPadding.top + insets.top;
		this.setPadding(0, topPadding, 0, bottomPadding);
	}

	private boolean navBarCanMove() {
		return this.getResources().getConfiguration().smallestScreenWidthDp <= 600;
	}
}
