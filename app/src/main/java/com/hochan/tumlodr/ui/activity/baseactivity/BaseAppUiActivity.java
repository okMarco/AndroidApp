package com.hochan.tumlodr.ui.activity.baseactivity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.util.ViewUtils;
import com.hochan.tumlodr.util.statusbar.StatusBarCompat;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.M;
import static com.hochan.tumlodr.util.statusbar.PhoneSystemCompat.isEMUI;
import static com.hochan.tumlodr.util.statusbar.PhoneSystemCompat.isMEIZU;
import static com.hochan.tumlodr.util.statusbar.PhoneSystemCompat.isMIUI;
import static com.hochan.tumlodr.util.statusbar.PhoneSystemCompat.isOPPO;

/**
 * .
 * Created by hochan on 2018/7/29.
 */

public abstract class BaseAppUiActivity extends BaseAppObserverActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		setTheme(getThemeId());
		super.onCreate(savedInstanceState);
		initStatusBar();
	}

	public int getThemeId() {
		return AppUiConfig.sThemId;
	}

	public void initStatusBar() {
		if (Build.VERSION.SDK_INT >= M || isOPPO() || isMIUI() || isMEIZU()) {
			StatusBarCompat.setStatusBarColor(getWindow(), AppUiConfig.sThemeColor);
			StatusBarCompat.setLightStatusBar(getWindow(), AppUiConfig.sIsLightTheme);
		} else if (Build.VERSION.SDK_INT >= LOLLIPOP) {
			if (isEMUI()) {
				StatusBarCompat.setStatusBarColor(getWindow(), AppUiConfig.sThemeColor);
			} else {
				StatusBarCompat.setStatusBarColor(getWindow(), AppUiConfig.sStatusBarColor);
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		setUpTitleBar();
	}

	protected void setUpTitleBar() {
		Toolbar toolbar = findViewById(R.id.toolbar);
		if (toolbar != null) {
			toolbar.setTitleTextColor(AppUiConfig.sTextColor);
			toolbar.setTitle(getTitleString());
			toolbar.setTitleTextAppearance(this, R.style.TitleTextStyle);
			toolbar.setNavigationIcon(getToolbarNavigationIcon());
			toolbar.setBackgroundColor(AppUiConfig.sThemeColor);
			setSupportActionBar(toolbar);

			toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onBackPressed();
				}
			});
		}
	}

	protected String getTitleString() {
		return null;
	}

	protected Drawable getToolbarNavigationIcon() {
		return ViewUtils.getArrowDrawable();
	}
}
