package com.hochan.tumlodr.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;

import com.hochan.tumlodr.ui.activity.baseactivity.BaseDrawerActivity;
import com.hochan.tumlodr.ui.fragment.PostThumbnailFragment;

/**
 * .
 * <p>
 * Created by zhendong_chen on 2016/8/23.
 */
public abstract class PostListActivity extends BaseDrawerActivity {

	public PostThumbnailFragment mPostListFragment;

	@Override
	public void initWidget() {
		super.initWidget();
		mViewBinding.drawerlayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
			@Override
			public void onDrawerStateChanged(int newState) {
				super.onDrawerStateChanged(newState);
				if (mPostListFragment != null) {
					mPostListFragment.dismissPopupWindow();
				}
			}
		});
	}

	@Override
	public Fragment getContentFragment(int id) {
		mPostListFragment = (PostThumbnailFragment) getSupportFragmentManager().findFragmentById(id);
		if (mPostListFragment == null) {
			mPostListFragment = getPostListFragment();
		}
		return mPostListFragment;
	}

	protected abstract PostThumbnailFragment getPostListFragment();

	@Override
	public void onToolbarClick() {
		if (mPostListFragment != null) {
			mPostListFragment.scrollToTop();
		}
	}

	@Override
	public abstract String getTitleString();

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
