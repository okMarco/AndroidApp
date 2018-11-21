package com.hochan.tumlodr.ui.activity;

import android.view.Menu;
import android.view.MenuItem;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.ui.fragment.LikePostsThumbnailFragment;
import com.hochan.tumlodr.ui.fragment.PostThumbnailFragment;

public class LikePostListActivity extends PostListActivity {

	private LikePostsThumbnailFragment mPostsThumbnailFragment;

	@Override
	protected PostThumbnailFragment getPostListFragment() {
		return mPostsThumbnailFragment = LikePostsThumbnailFragment.newInstance(true);
	}

	@Override
	public String getTitleString() {
		return getString(R.string.activity_title_like);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_likes_filter, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mPostsThumbnailFragment == null) {
			mPostsThumbnailFragment = (LikePostsThumbnailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		}
		if (mPostsThumbnailFragment == null) {
			return false;
		}
		if (item.getItemId() == R.id.menu_only_picture) {
			mPostsThumbnailFragment.showOnlyPicture();
		} else if (item.getItemId() == R.id.menu_only_video) {
			mPostsThumbnailFragment.showOnlyVideo();
		} else if (item.getItemId() == R.id.menu_all) {
			mPostsThumbnailFragment.showAll();
		}
		return true;
	}
}
