package com.hochan.tumlodr.ui.activity;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.model.viewmodel.PostListViewModel;
import com.hochan.tumlodr.module.video.MiniVideoWindowManager;
import com.hochan.tumlodr.module.video.player.VideoPlayer;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseAppObserverActivity;
import com.hochan.tumlodr.ui.component.TikTokViewPager;
import com.hochan.tumlodr.ui.fragment.FullViewVideoListFragment;
import com.hochan.tumlodr.ui.fragment.VideoPostThumbnailFragment;
import com.hochan.tumlodr.util.SystemUtils;
import com.hochan.tumlodr.util.ViewUtils;
import com.hochan.tumlodr.util.statusbar.StatusBarCompat;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.VideoPost;

import java.util.ArrayList;
import java.util.List;


public class VideoViewPagerActivity extends BaseAppObserverActivity implements MiniVideoWindowManager.OnMiniWindowListener {

	public static final String EXTRA_SCROLL_TO_INDEX = "scroll_to_index";
	public static final String EXTRA_FROM_VIDEO_LAYOUT_AREA = "from_video_layout";
	public static final String EXTRA_BLOG_NAME = "blog_name";
	public static final String EXTRA_REFRESH_ON_RESUME = "refresh_on_resume";

	public static VideoPost sLastVideoPost = null;

	private TikTokViewPager mViewPager;
	private FullViewVideoListFragment mFullViewVideoListFragment;

	public static void playVideo(Activity context, View videoLayout, VideoPost videoData) {
		Intent intent = new Intent(context, VideoViewPagerActivity.class);
		sLastVideoPost = videoData;
		int[] location = new int[2];
		videoLayout.getLocationOnScreen(location);
		Rect rect = new Rect(location[0], location[1], location[0] + videoLayout.getMeasuredWidth(),
				location[1] + videoLayout.getMeasuredHeight());
		intent.putExtra(EXTRA_FROM_VIDEO_LAYOUT_AREA, rect);
		if (context instanceof BlogPostListActivity) {
			intent.putExtra(EXTRA_BLOG_NAME, videoData.getBlogName());
		}
		context.startActivity(intent);
	}

	public static void playVideo(Activity activity, VideoPost videoPost) {
		Intent intent = new Intent(activity, VideoViewPagerActivity.class);
		sLastVideoPost = videoPost;
		if (activity instanceof BlogPostListActivity) {
			intent.putExtra(EXTRA_BLOG_NAME, videoPost.getBlogName());
		}
		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PostListViewModel videoListViewModel = ViewModelProviders.of(this).get(PostListViewModel.class);
		List<Post> videoPostList = new ArrayList<>();
		if (sLastVideoPost != null) {
			videoPostList.add(sLastVideoPost);
		}
		videoListViewModel.getDashBoardPostList().setValue(videoPostList);

		setContentView(R.layout.activity_video_view_pager);

		StatusBarCompat.setStatusBarTranslucent(getWindow());
		StatusBarCompat.setNavigationBarTranslucent(getWindow());

		mViewPager = findViewById(R.id.view_pager);
		mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public Fragment getItem(int position) {
				switch (position) {
					case 0: {
						mFullViewVideoListFragment = new FullViewVideoListFragment();
						mFullViewVideoListFragment.setArguments(getIntent().getExtras());
						return mFullViewVideoListFragment;
					}
					case 1: {
						VideoPostThumbnailFragment videoPostThumbnailFragment = new VideoPostThumbnailFragment();
						videoPostThumbnailFragment.setArguments(getIntent().getExtras());
						return videoPostThumbnailFragment;
					}
				}
				return null;
			}

			@Override
			public int getCount() {
				return 2;
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mViewPager.setCurrentItem(0, true);
		int scrollToPosition = intent.getIntExtra(EXTRA_SCROLL_TO_INDEX, -1);
		if (mFullViewVideoListFragment != null && scrollToPosition >= 0) {
			mFullViewVideoListFragment.scrollToVideo(scrollToPosition);
		}
	}

	public void scrollToVideoList() {
		if (mViewPager != null) {
			mViewPager.setCurrentItem(1, true);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MiniVideoWindowManager.getInstance().registerOnEnterMiniWindowListener(this);
	}

	@Override
	public void onEnterMiniWindow() {
		finish();
	}

	@Override
	public void onMiniWindowDismiss() {
	}

	@Override
	public void onRenterFromMiniWindow() {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MiniVideoWindowManager.getInstance().unRegisterOnEnterMiniWindowListener(this);
		SystemUtils.fixInputMethod(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mViewPager.setEnable(false);
			ViewUtils.setUiFlags(getWindow(), true);
		} else {
			mViewPager.setEnable(true);
			ViewUtils.setUiFlags(getWindow(), false);
		}
	}

	public void scrollToPosition(int position) {
		if (mFullViewVideoListFragment != null) {
			mViewPager.setCurrentItem(0, true);
			mFullViewVideoListFragment.scrollToVideo(position);
		}
	}

	@Override
	public void onBackPressed() {
		if (mFullViewVideoListFragment != null) {
			mFullViewVideoListFragment.onBackPressed();
		} else {
			VideoPlayer.getInstance().release();
			sLastVideoPost = null;
			super.onBackPressed();
		}
	}
}
