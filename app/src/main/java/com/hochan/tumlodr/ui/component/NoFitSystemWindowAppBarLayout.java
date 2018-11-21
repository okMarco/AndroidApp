package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;

/**
 * .
 * <p>
 * Created by hochan on 2018/4/10.
 */

public class NoFitSystemWindowAppBarLayout extends AppBarLayout {
	public NoFitSystemWindowAppBarLayout(Context context) {
		super(context);
	}

	public NoFitSystemWindowAppBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		ViewCompat.setOnApplyWindowInsetsListener(this, null);
	}
}
