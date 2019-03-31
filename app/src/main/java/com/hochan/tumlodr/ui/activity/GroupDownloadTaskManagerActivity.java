package com.hochan.tumlodr.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.Menu;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseSingleFragmentActivity;
import com.hochan.tumlodr.ui.fragment.DownloadTaskFragment;

public class GroupDownloadTaskManagerActivity extends DownloadTasksManagerActivity {

	public static void showGroup(Activity activity, String groupName) {
		Intent intent = new Intent(activity, GroupDownloadTaskManagerActivity.class);
		intent.putExtra(Router.GROUP_NAME, groupName);
		activity.startActivity(intent);
	}

	@Override
	public void setUpObserver() {}

	@Override
	protected String getTitleString() {
		return getIntent().getStringExtra(Router.GROUP_NAME);
	}

	@Override
	public Fragment getContentFragment(int id) {
		mDownloadTaskFragment = (DownloadTaskFragment) getSupportFragmentManager().findFragmentById(id);
		if (mDownloadTaskFragment == null) {
			mDownloadTaskFragment = DownloadTaskFragment.newInstance(getIntent().getStringExtra(Router.GROUP_NAME));
		}
		return mDownloadTaskFragment;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_download_manager, menu);
		menu.findItem(R.id.menu_only_group).setVisible(false);
		return true;
	}
}
