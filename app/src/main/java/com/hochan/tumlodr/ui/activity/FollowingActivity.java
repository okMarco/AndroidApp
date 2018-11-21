package com.hochan.tumlodr.ui.activity;

import android.support.v4.app.Fragment;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseDrawerActivity;
import com.hochan.tumlodr.ui.fragment.FollowingBlogListFragment;

public class FollowingActivity extends BaseDrawerActivity {

	@Override
	public Fragment getContentFragment(int id) {
		FollowingBlogListFragment blogListFragment = (FollowingBlogListFragment) getSupportFragmentManager().findFragmentById(id);
		if (blogListFragment == null) {
			blogListFragment = FollowingBlogListFragment.newInstance();
		}
		return blogListFragment;
	}

	@Override
	public void onToolbarClick() {

	}

	@Override
	public String getTitleString() {
		return getString(R.string.activity_title_follow);
	}

}
