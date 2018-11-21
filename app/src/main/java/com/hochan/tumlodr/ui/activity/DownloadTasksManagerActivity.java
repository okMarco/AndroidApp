/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hochan.tumlodr.ui.activity;

import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseDrawerActivity;
import com.hochan.tumlodr.ui.fragment.DownloadTaskFragment;

/**
 * .
 * Created by Jacksgong on 1/9/16.
 */
public class DownloadTasksManagerActivity extends BaseDrawerActivity {

	DownloadTaskFragment mDownloadTaskFragment;
	boolean mIsDeleteMode = false;
	int mFragmentId;

	@Override
	protected String getTitleString() {
		return getString(R.string.activity_title_download_manager);
	}

	@Override
	public Fragment getContentFragment(int id) {
		mFragmentId = id;
		mDownloadTaskFragment = (DownloadTaskFragment) getSupportFragmentManager().findFragmentById(id);
		if (mDownloadTaskFragment == null) {
			mDownloadTaskFragment = new DownloadTaskFragment();
		}
		return mDownloadTaskFragment;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_download_manager, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mDownloadTaskFragment = (DownloadTaskFragment) getSupportFragmentManager().findFragmentById(mFragmentId);
		if (mDownloadTaskFragment == null) {
			return false;
		}
		switch (item.getItemId()) {
			case R.id.menu_all: {
				mDownloadTaskFragment.showAll();
				break;
			}
			case R.id.menu_only_picture: {
				mDownloadTaskFragment.showOnlyPic();
				break;
			}
			case R.id.menu_only_video: {
				mDownloadTaskFragment.showOnlyVideo();
				break;
			}
			case R.id.menu_only_unfinish: {
				mDownloadTaskFragment.showUnFinish();
				break;
			}
		}
		return true;
	}

	@Override
	public void onToolbarClick() {
	}

	public void setDeleteMode(boolean deleteMode) {
		mIsDeleteMode = deleteMode;
		invalidateOptionsMenu();
	}
}
