package com.hochan.tumlodr.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.jumblr.types.VideoPost;
import com.hochan.tumlodr.model.sharedpreferences.UserInfo;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.tools.ScreenTools;
import com.hochan.tumlodr.ui.fragment.DashBoardThumbnailFragment;
import com.hochan.tumlodr.ui.fragment.PostThumbnailFragment;
import com.hochan.tumlodr.ui.fragment.TeeHubDialogFragment;
import com.hochan.tumlodr.util.ActivityLifecycleProvider;
import com.hochan.tumlodr.util.Events;
import com.hochan.tumlodr.util.RxBus;
import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.functions.Consumer;

import static com.hochan.tumlodr.util.PermissionUtil.checkStoragePermission;

public class MainActivity extends PostListActivity {

	private DashBoardThumbnailFragment mPostsThumbnailFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//检查系统是否开启了地理位置权限;
		//注意：此时的Manifest的导入包路径import android.Manifest;
		checkStoragePermission(this);

		UserInfo.initUserInfo();

		RxBus.with(new ActivityLifecycleProvider(this)).setEndEvent(ActivityEvent.DESTROY)
				.onNext(new Consumer<Object>() {
					@Override
					public void accept(Object o) throws Exception {
						if (o instanceof Events && ((Events) o).mCode == Events.EVENT_CHANGE_THEME) {
							MainActivity.this.finish();
						}
					}
				}).create();

		SharedPreferences sharedPreferences = getSharedPreferences(AppConfig.SHARE_THEME, MODE_PRIVATE);
		if (!sharedPreferences.getBoolean(AppConfig.KEY_HAS_SHOW_TEEHUB, false)) {
			sharedPreferences.edit().putBoolean(AppConfig.KEY_HAS_SHOW_TEEHUB, true).apply();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					new TeeHubDialogFragment().show(getSupportFragmentManager(), TeeHubDialogFragment.TAG);
				}
			}, 5);
		}
	}

	@Override
	protected PostThumbnailFragment getPostListFragment() {
		return mPostsThumbnailFragment = DashBoardThumbnailFragment.newInstance(true);
	}

	@SuppressLint("RtlHardcoded")
	@Override
	public void initWidget() {
		super.initWidget();
		Toolbar toolbar = findViewById(R.id.toolbar);
		if (toolbar != null) {
			LinearLayout llRightBtnContainer = new LinearLayout(this);
			llRightBtnContainer.setOrientation(LinearLayout.HORIZONTAL);
			Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.gravity = Gravity.RIGHT;
			llRightBtnContainer.setLayoutParams(layoutParams);
			toolbar.addView(llRightBtnContainer);

			ImageView btnVideoList = new ImageView(this);
			btnVideoList.setImageResource(AppUiConfig.sIsLightTheme ? R.drawable.ic_main_video_list : R.drawable.ic_main_video_list_white);
			btnVideoList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			btnVideoList.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mPostsThumbnailFragment != null) {
						VideoPost firstVideoPost = mPostsThumbnailFragment.getFirstVideoPost();
						if (firstVideoPost != null) {
							VideoViewPagerActivity.playVideo(MainActivity.this, view, firstVideoPost);
							return;
						}
					}
					Intent intent = new Intent(MainActivity.this, VideoViewPagerActivity.class);
					intent.putExtra(VideoViewPagerActivity.EXTRA_REFRESH_ON_RESUME, true);
					MainActivity.this.startActivity(intent);
					MainActivity.this.overridePendingTransition(R.anim.activity_right_in, R.anim.activity_stay);
				}
			});
			llRightBtnContainer.addView(btnVideoList);

			final ImageView btnSearch = new ImageView(this);
			btnSearch.setImageResource(AppUiConfig.sIsLightTheme ? R.drawable.ic_search_black : R.drawable.ic_search_white);
			LinearLayout.LayoutParams btnSearchLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			btnSearchLayoutParams.leftMargin = ScreenTools.dip2px(this, 10);
			btnSearch.setLayoutParams(btnSearchLayoutParams);
			ViewCompat.setTransitionName(btnSearch, "search");
			btnSearch.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(MainActivity.this, SearchPostActivity.class),
							ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, btnSearch, "search").toBundle());
				}
			});
			llRightBtnContainer.addView(btnSearch);
		}
	}

	@Override
	public String getTitleString() {
		return getString(R.string.left_menu_home);
	}

	/**
	 * 一般的Activity跳转，都会新建一个Activity，然后调用onCreate()->onStart()->onResume()
	 * 但是如果设置了singleTask，这时候回到这个Activity，就不会新建一个实例了，而是把后台的Activity转到
	 * 前台，这时就不会调用onCreate()，而是调用onNewIntent()->onRestart()->onStart()->onResume()。
	 * 但是Activity随时有可能被回收，这时候在跳回就又是onCreate()了。
	 * 所以要在onCreate()和onNewIntance()对intent中的数据进行相同的处理，而且还要在onNewIntent()中setIntent(intent).
	 * <p>
	 * 设置了singleTop的Activity，在当前Activity跳转到相同的Activity时，不会进行跳转，但会调用onNewIntent()函数。
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_likes_filter, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mPostsThumbnailFragment = (DashBoardThumbnailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (mPostsThumbnailFragment == null) {
			return false;
		}
		if (item.getItemId() == R.id.menu_only_picture) {
			mPostsThumbnailFragment.showOnlyPicture();
		} else if (item.getItemId() == R.id.menu_only_video) {
			mPostsThumbnailFragment.showOnlyVideo();
		} else if (item.getItemId() == R.id.menu_all) {
			mPostsThumbnailFragment.showAll();
		} else if (item.getItemId() == R.id.menu_only_text) {
			mPostsThumbnailFragment.showOnlyText();
		}
		return true;
	}
}
