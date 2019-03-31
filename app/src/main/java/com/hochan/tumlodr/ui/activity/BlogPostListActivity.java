package com.hochan.tumlodr.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.ActivityBlogBinding;
import com.hochan.tumlodr.jumblr.types.Blog;
import com.hochan.tumlodr.model.BaseObserver;
import com.hochan.tumlodr.model.TumlodrService;
import com.hochan.tumlodr.model.data.blog.FollowingBlogDatabase;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.tools.HtmlTool;
import com.hochan.tumlodr.tools.Tools;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseViewBindingActivity;
import com.hochan.tumlodr.ui.fragment.BlogLikePostListThumbnailFragment;
import com.hochan.tumlodr.ui.fragment.BlogPostsThumbnailFragment;
import com.hochan.tumlodr.ui.fragment.PostThumbnailFragment;
import com.hochan.tumlodr.util.BlurTransformation;
import com.hochan.tumlodr.util.ColorFilterTransformation;
import com.hochan.tumlodr.util.Events;
import com.hochan.tumlodr.util.RxBus;
import com.hochan.tumlodr.util.ViewUtils;
import com.hochan.tumlodr.util.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;


public class BlogPostListActivity extends BaseViewBindingActivity<ActivityBlogBinding> {

	public static final String BLOG_NAME = "blog_name";
	public static final String IS_FOLLOW = "follow";

	private PostThumbnailFragment mBlogPostListFragment, mBlogLikeFragment;

	private boolean mIsFollowing = false;

	public static void start(Context context, String blogName, boolean isFollow) {
		Intent intent = new Intent(context, BlogPostListActivity.class);
		intent.putExtra(BLOG_NAME, blogName);
		intent.putExtra(IS_FOLLOW, isFollow);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIsFollowing = getIntent().getBooleanExtra(IS_FOLLOW, false);
		setUpFollowBtn();

	}

	@Override
	public void initStatusBar() {
		// StatusBarCompat.setStatusBarHalfTranslucent(getWindow());
		StatusBarCompat.setStatusBarTranslucent(getWindow());
	}

	@Override
	public void initWidget() {
		mViewBinding.tlBlogItem.setSelectedTabIndicatorColor(Color.WHITE);
		mViewBinding.tlBlogItem.setTabTextColors(ContextCompat.getColor(this, R.color.colorSubTextLight),
				Color.WHITE);
		mViewBinding.tvBlogTitle.setTextColor(Color.WHITE);
		mViewBinding.tvBlogDescription.setTextColor(Color.WHITE);
		mViewBinding.ablBlog.setBackgroundColor(AppUiConfig.sThemeColor);

		final String blogName = getIntent().getStringExtra(BLOG_NAME);
		if (TextUtils.isEmpty(blogName)) {
			return;
		}

		mViewBinding.tvBlogTitle.setText(blogName);

		FollowingBlogDatabase.updateFollowingBlog(blogName);

		TumlodrGlide.with(BlogPostListActivity.this)
				.load(Tools.getAvatarUrlByBlogName(blogName, 128))
				.transform(new RoundedCorners(15))
				.skipMemoryCache(true)
				.into(mViewBinding.rivBlogAvatar);

		TumlodrGlide.with(BlogPostListActivity.this)
				.asBitmap()
				.load(Tools.getAvatarUrlByBlogName(blogName, 16))
				.placeholder(AppUiConfig.sPicHolderResource)
				.transition(BitmapTransitionOptions.withCrossFade())
				.skipMemoryCache(true)
				.transform(new MultiTransformation<>(new CenterCrop(),
						new BlurTransformation(this, 25)))
				.into(mViewBinding.ivBigAvatar);

		mViewBinding.rivBlogAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Router.showImage(BlogPostListActivity.this, blogName, mViewBinding.rivBlogAvatar);
			}
		});

		TumlodrService.getBlogInfo(blogName).subscribe(new BaseObserver<Blog>() {
			@SuppressLint("ApplySharedPref")
			@Override
			public void onNext(Blog blog) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					TransitionManager.beginDelayedTransition((ViewGroup) mViewBinding.llBlogInfo.getParent());
				}
				mViewBinding.llBlogInfo.setVisibility(View.VISIBLE);
				mIsFollowing = blog.followed;
				setUpFollowBtn();
				if (blog.getDescription().length() > 0) {
					mViewBinding.tvBlogDescription.setVisibility(View.VISIBLE);
					SpannableStringBuilder spannableStringBuilder = HtmlTool.fromHtml(blog.getDescription(), mViewBinding.tvBlogDescription);
					mViewBinding.tvBlogDescription.setText(spannableStringBuilder);
				}else {
					mViewBinding.tvBlogDescription.setVisibility(View.GONE);
				}


				updateLastVisited(blogName);
			}
		});

		ViewPager viewPager = findViewById(R.id.vp_content_container);
		viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public Fragment getItem(int position) {
				switch (position) {
					case 0:
						mBlogPostListFragment = BlogPostsThumbnailFragment.newInstance(getIntent().getStringExtra(BLOG_NAME));
						return mBlogPostListFragment;
					default:
						mBlogLikeFragment = BlogLikePostListThumbnailFragment.newInstance(getIntent().getStringExtra(BLOG_NAME));
						return mBlogLikeFragment;
				}
			}

			@Override
			public int getCount() {
				return 2;
			}

			@Override
			public CharSequence getPageTitle(int position) {
				if (position == 0) {
					return getString(R.string.blog_post_list);
				} else {
					return getString(R.string.blog_post_likes);
				}
			}
		});

		mViewBinding.tlBlogItem.setupWithViewPager(viewPager);

		mViewBinding.btnArrowBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mViewBinding.btnFollow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mIsFollowing) {
					TumlodrService.unFollowBlog(getIntent().getStringExtra(BLOG_NAME));
					mIsFollowing = false;
					setUpFollowBtn();
				} else {
					TumlodrService.followBlog(getIntent().getStringExtra(BLOG_NAME))
							.subscribe(new BaseObserver<Object>() {
								@Override
								public void onNext(Object o) {
									showSimpleGreenSnackBar(getString(R.string.follow_success));
								}

								@Override
								public void onError(Throwable e) {
									super.onError(e);
									mIsFollowing = false;
									setUpFollowBtn();
								}
							});
					mIsFollowing = true;
					setUpFollowBtn();
				}
			}
		});

		mViewBinding.tlBlogItem.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				mViewBinding.tlBlogItem.getViewTreeObserver().removeOnPreDrawListener(this);
				mViewBinding.flTopContainer.setMinimumHeight(mViewBinding.tlBlogItem.getMeasuredHeight());
				mViewBinding.flTopContainer.requestLayout();
				return false;
			}
		});
	}

	private void updateLastVisited(String blogName) {
		SharedPreferences visitedBlogList = getSharedPreferences(AppConfig.SHARE_VISITED_BLOG_LIST, Context.MODE_PRIVATE);
		String blogString = visitedBlogList.getString(AppConfig.SHARE_VISITED_BLOG_LIST, null);
		List<String> lastVisitedBlogNameList = new ArrayList<>();
		if (!TextUtils.isEmpty(blogString)) {
			JsonArray jsonArray = (JsonArray) new JsonParser().parse(blogString);
			for (int i = 0; i < jsonArray.size(); i++) {
				lastVisitedBlogNameList.add(jsonArray.get(i).getAsString());
			}
		}
		if (lastVisitedBlogNameList.contains(blogName)) {
			lastVisitedBlogNameList.remove(blogName);
			lastVisitedBlogNameList.add(0, blogName);
		} else {
			lastVisitedBlogNameList.add(0, blogName);
			if (lastVisitedBlogNameList.size() > 10) {
				lastVisitedBlogNameList.remove(lastVisitedBlogNameList.size() - 1);
			}
		}
		visitedBlogList.edit().putString(AppConfig.SHARE_VISITED_BLOG_LIST,
				new Gson().toJson(lastVisitedBlogNameList)).apply();
		RxBus.getInstance().send(new Events<>(Events.EVENT_UPDATE_LAST_VISITED_BLOG, null));
	}

	private void setUpFollowBtn() {
		if (mIsFollowing) {
			mViewBinding.btnFollow.setText(R.string.blog_unfollow);
			mViewBinding.btnFollow.setBackgroundResource(R.drawable.bg_following);
		} else {
			mViewBinding.btnFollow.setText(R.string.blog_follow);
			mViewBinding.btnFollow.setBackgroundResource(R.drawable.bg_unfollow);
		}
	}

	@Override
	public Drawable getToolbarNavigationIcon() {
		return ViewUtils.getArrowDrawable();
	}

	@Override
	protected String getTitleString() {
		return getIntent().getStringExtra(BLOG_NAME);
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_blog;
	}
}
