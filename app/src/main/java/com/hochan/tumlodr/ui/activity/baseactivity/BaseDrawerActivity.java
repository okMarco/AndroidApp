package com.hochan.tumlodr.ui.activity.baseactivity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.ActivityBaseDrawerBinding;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.ui.component.DrawerToggleDrawable;
import com.hochan.tumlodr.ui.fragment.LeftMenuFragment;

/**
 * .
 * Created by zhendong_chen on 2016/8/22.
 */
public abstract class BaseDrawerActivity extends BaseViewBindingActivity<ActivityBaseDrawerBinding> {

	@Override
	protected void onStart() {
		super.onStart();
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@SuppressLint("RtlHardcoded")
			@Override
			public void onClick(View v) {
				viewBinding.drawerlayout.openDrawer(Gravity.LEFT);
			}
		});
		toolbar.setOverflowIcon(AppUiConfig.sIsLightTheme ? ContextCompat.getDrawable(this, R.drawable.ic_menu_over_flow) :
				ContextCompat.getDrawable(this, R.drawable.ic_menu_over_flow_white));
		toolbar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onToolbarClick();
			}
		});
	}

	@Override
	public void initWidget() {
		Fragment contentFragment = getContentFragment(R.id.fragment_container);
		if (contentFragment != null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, contentFragment)
					.commit();
		}

		LeftMenuFragment leftMenuFragment = (LeftMenuFragment) getSupportFragmentManager()
				.findFragmentById(R.id.rl_left_drawer_container);
		if (leftMenuFragment == null) {
			leftMenuFragment = new LeftMenuFragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.rl_left_drawer_container, leftMenuFragment).commit();
		}
	}

	public abstract Fragment getContentFragment(int id);

	@SuppressLint("RtlHardcoded")
	@Override
	public void onBackPressed() {
		if (viewBinding.drawerlayout.isDrawerOpen(Gravity.LEFT)) {
			closeDrawer();
		} else {
			super.onBackPressed();
		}
	}

	@SuppressLint("RtlHardcoded")
	public void closeDrawer() {
		viewBinding.drawerlayout.closeDrawer(Gravity.LEFT);
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_base_drawer;
	}

	@Override
	public Drawable getToolbarNavigationIcon() {
		return new DrawerToggleDrawable(AppUiConfig.sTextColor);
	}

	public abstract void onToolbarClick();
}
