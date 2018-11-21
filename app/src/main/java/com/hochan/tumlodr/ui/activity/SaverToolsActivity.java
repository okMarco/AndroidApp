package com.hochan.tumlodr.ui.activity;

import android.support.v4.app.Fragment;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseDrawerActivity;
import com.hochan.tumlodr.ui.fragment.SaverToolsFragment;

public class SaverToolsActivity extends BaseDrawerActivity {

	@Override
	public void onToolbarClick() {

	}

	@Override
	protected String getTitleString() {
		return getString(R.string.left_menu_saver_tools);
	}

	@Override
	public Fragment getContentFragment(int id) {
		SaverToolsFragment fragment = (SaverToolsFragment) getSupportFragmentManager().findFragmentById(id);
		if (fragment == null) {
			fragment = new SaverToolsFragment();
		}
		return fragment;
	}
}
