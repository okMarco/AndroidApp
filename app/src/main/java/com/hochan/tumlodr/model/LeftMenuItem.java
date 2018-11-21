package com.hochan.tumlodr.model;

import android.app.Activity;
import android.content.Intent;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.ui.activity.DownloadTasksManagerActivity;
import com.hochan.tumlodr.ui.activity.FollowingActivity;
import com.hochan.tumlodr.ui.activity.LikePostListActivity;
import com.hochan.tumlodr.ui.activity.MainActivity;
import com.hochan.tumlodr.ui.activity.SaverToolsActivity;
import com.hochan.tumlodr.ui.activity.SearchPostActivity;
import com.hochan.tumlodr.ui.activity.SettingActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 * Created by hochan on 2017/12/9.
 */
public class LeftMenuItem {

	public static final LeftMenuItem HOME_ACTIVITY = new LeftMenuItem(MainActivity.class, R.string.left_menu_home, R.drawable.ic_left_menu_dashboard);
	public static final LeftMenuItem SEARCH_TAG_ACTIVITY = new LeftMenuItem(SearchPostActivity.class, R.string.left_menu_search, R.drawable.ic_left_menu_dashboard);
	public static final LeftMenuItem LIKE_ACTIVITY = new LeftMenuItem(LikePostListActivity.class, R.string.left_menu_like, R.drawable.ic_account_like);
	public static final LeftMenuItem FOLLOWING_ACTIVITY = new LeftMenuItem(FollowingActivity.class, R.string.left_menu_follow, R.drawable.ic_account_followers);
	public static final LeftMenuItem DOWNLOAD_MANAGER_ACTIVITY = new LeftMenuItem(DownloadTasksManagerActivity.class, R.string.left_menu_download, R.drawable.ic_account_download);
	public static final LeftMenuItem SETTING_ACTIVITY = new LeftMenuItem(SettingActivity.class, R.string.left_menu_setting, R.drawable.ic_account_setting);
	public static final LeftMenuItem SAVER_TOOLS_ACTIVITY = new LeftMenuItem(SaverToolsActivity.class, R.string.left_menu_saver_tools, R.drawable.ic_account_saver_tools);

	public static final List<LeftMenuItem> LEFT_MENU_ITEMS = new ArrayList<>();

	static {
		LEFT_MENU_ITEMS.add(HOME_ACTIVITY);
		// LEFT_MENU_ITEMS.add(SEARCH_TAG_ACTIVITY);
		LEFT_MENU_ITEMS.add(LIKE_ACTIVITY);
		LEFT_MENU_ITEMS.add(FOLLOWING_ACTIVITY);
		LEFT_MENU_ITEMS.add(DOWNLOAD_MANAGER_ACTIVITY);
		LEFT_MENU_ITEMS.add(SETTING_ACTIVITY);
		LEFT_MENU_ITEMS.add(SAVER_TOOLS_ACTIVITY);
	}

	public Class<? extends Activity> mActivityClass;
	public int mTitleResourceId;
	public int mIconResourceId;

	public LeftMenuItem(Class<? extends Activity> activity, int titleResourceId, int iconResourceId) {
		this.mActivityClass = activity;
		this.mTitleResourceId = titleResourceId;
		this.mIconResourceId = iconResourceId;
	}

	public void launch(Activity activity) {
		if (activity.getClass() == mActivityClass) {
			return;
		}
		Intent intent = new Intent(activity, mActivityClass);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.activity_right_in, R.anim.activity_stay);
	}
}
