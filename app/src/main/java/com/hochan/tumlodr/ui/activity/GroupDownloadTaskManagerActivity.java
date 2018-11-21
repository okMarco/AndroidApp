package com.hochan.tumlodr.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.hochan.tumlodr.ui.activity.baseactivity.BaseSingleFragmentActivity;
import com.hochan.tumlodr.ui.fragment.DownloadTaskFragment;

public class GroupDownloadTaskManagerActivity extends BaseSingleFragmentActivity {

	public static void showGroup(Activity activity, String groupName) {
		Intent intent = new Intent(activity, GroupDownloadTaskManagerActivity.class);
		intent.putExtra(Router.GROUP_NAME, groupName);
		activity.startActivity(intent);
	}

	@Override
	protected String getTitleString() {
		return getIntent().getStringExtra(Router.GROUP_NAME);
	}

	@Override
	public Fragment getContentFragment(int id) {
		DownloadTaskFragment downloadTaskFragment = (DownloadTaskFragment) getSupportFragmentManager().findFragmentById(id);
		if (downloadTaskFragment == null) {
			downloadTaskFragment = DownloadTaskFragment.newInstance(getIntent().getStringExtra(Router.GROUP_NAME));
		}
		return downloadTaskFragment;
	}
}
