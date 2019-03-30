package com.hochan.tumlodr.ui.fragment;

import android.animation.Animator;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.databinding.FragmentFullViewVideoListBinding;
import com.hochan.tumlodr.jumblr.types.Post;
import com.hochan.tumlodr.jumblr.types.VideoPost;
import com.hochan.tumlodr.model.viewmodel.PostListViewModel;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.glide.TumlodrGlideUtil;
import com.hochan.tumlodr.module.video.MiniVideoWindowManager;
import com.hochan.tumlodr.module.video.RoundCornerViewOutlineProvider;
import com.hochan.tumlodr.module.video.StaticAnimatorListenerAdapter;
import com.hochan.tumlodr.module.video.player.ExoVideoPlayer;
import com.hochan.tumlodr.module.video.player.VideoPlayer;
import com.hochan.tumlodr.module.video.videocontrol.TikTokViewVideoControl;
import com.hochan.tumlodr.module.video.videolayout.MiniVideoPlayLayout;
import com.hochan.tumlodr.module.video.videolayout.TikTokVideoLayout;
import com.hochan.tumlodr.module.video.videolayout.VideoPlayLayout;
import com.hochan.tumlodr.prensenter.BlogVideoPostListPresenter;
import com.hochan.tumlodr.prensenter.PostListPresenter;
import com.hochan.tumlodr.prensenter.VideoPostListPresenter;
import com.hochan.tumlodr.ui.activity.VideoViewPagerActivity;
import com.hochan.tumlodr.ui.adapter.VideoPostThumbnailAdapter;
import com.hochan.tumlodr.ui.component.WrapLinearLayoutManager;
import com.hochan.tumlodr.ui.view.IPostListView;
import com.hochan.tumlodr.util.FileDownloadUtil;
import com.hochan.tumlodr.util.SystemUtils;
import com.hochan.tumlodr.util.ViewUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.hochan.tumlodr.ui.activity.VideoViewPagerActivity.EXTRA_FROM_VIDEO_LAYOUT_AREA;
import static com.hochan.tumlodr.ui.activity.VideoViewPagerActivity.sLastVideoPost;
import static com.hochan.tumlodr.util.VideoTransitionUtils.prepareVideoLayoutForEnterAnimation;

/**
 * .
 * <p>
 * Created by hochan on 2018/5/22.
 */

public class FullViewVideoListFragment extends Fragment implements TikTokVideoLayout.OnFullScreenListener,
		TikTokVideoLayout.OnMiniLayoutTransitionListener, IPostListView {

	private boolean mRefreshOnResume = false;
	private boolean mIsFirstEnter = true;
	private FragmentFullViewVideoListBinding mViewBinding;

	private List<Post> mVideoDataList = new ArrayList<>();

	// 屏幕旋转相关
	private boolean mForceToPortrait = false;
	private boolean mForceToLandscape = false;
	private OrientationEventListener mOrientationEventListener;

	private boolean mLastOrientationIsLandscape = false;

	private RecyclerView.Adapter mAdapter = new RecyclerView.Adapter() {
		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ItemVideoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_titok_video, parent, false));
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			final TikTokVideoLayout tikTokVideoLayout = holder.itemView.findViewById(R.id.video_layout);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				tikTokVideoLayout.getVideoTextureView().setOutlineProvider(new RoundCornerViewOutlineProvider(10));
				tikTokVideoLayout.getVideoTextureView().setClipToOutline(true);
			}
			tikTokVideoLayout.getThumbnailImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);
			Post post = mVideoDataList.get(position);
			if (post.getType() != Post.PostType.VIDEO) {
				return;
			}
			VideoPost videoPost = (VideoPost) post;
			if (TumlodrGlideUtil.isContextValid(getActivity())
					&& mVideoDataList.get(position) != null
					&& mVideoDataList.get(position) instanceof VideoPost) {
				TumlodrGlide.with(getActivity())
						.load(videoPost.getThumbnailUrl())
						.skipMemoryCache(true)
						.into(tikTokVideoLayout.getThumbnailImageView())
						.clearOnDetach();
			}
			tikTokVideoLayout.setOnMiniLayoutTransitionListener(FullViewVideoListFragment.this);
			tikTokVideoLayout.setOnFullScreenListener(FullViewVideoListFragment.this);
			FileDownloadUtil.TumblrVideoDownloadInfo tumblrVideoDownloadInfo = FileDownloadUtil.getVideoDownloadInfo(videoPost);
			if (tumblrVideoDownloadInfo != null) {
				tikTokVideoLayout.setVideoUrl(tumblrVideoDownloadInfo.getVideoUrl());
			}
			if (tikTokVideoLayout.getVideoControl() != null) {
				tikTokVideoLayout.getVideoControl().showRotateButton();
				tikTokVideoLayout.getVideoControl().setRotateButtonClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						clickToChangeOrientation();
					}
				});
			}
			((TikTokViewVideoControl) (tikTokVideoLayout.getVideoControl())).setPost(mVideoDataList.get(position));
			if (position == 0 && mIsFirstEnter) {
				startFirstEnterAnimation(tikTokVideoLayout);
			}
		}

		@Override
		public int getItemCount() {
			return mVideoDataList.size();
		}
	};

	private PostListPresenter mVideoPostListPresenter;
	private PostListViewModel mPostListViewModel;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initOrientationEventListener();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getActivity() != null) {
			mPostListViewModel = ViewModelProviders.of(getActivity()).get(PostListViewModel.class);
			mPostListViewModel.getDashBoardPostList().observe(this, new Observer<List<Post>>() {
				@Override
				public void onChanged(@Nullable final List<Post> postList) {
					mViewBinding.smartRefreshLayout.finishLoadMore();
					mViewBinding.smartRefreshLayout.finishRefresh();
					if (postList == null) {
						return;
					}
					DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
						@Override
						public int getOldListSize() {
							return mVideoDataList.size();
						}

						@Override
						public int getNewListSize() {
							return postList.size();
						}

						@Override
						public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
							if (mVideoDataList == null) {
								return false;
							}
							Post oldPost = mVideoDataList.get(oldItemPosition);
							Post newPost = postList.get(newItemPosition);
							return oldPost != null && newPost != null && (oldPost.getId().longValue() == newPost.getId().longValue());
						}

						@Override
						public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
							return true;
						}
					});
					mVideoDataList = postList;
					diffResult.dispatchUpdatesTo(mAdapter);
				}
			});

			if (getArguments() != null) {
				String blogName = getArguments().getString(VideoViewPagerActivity.EXTRA_BLOG_NAME);
				if (!TextUtils.isEmpty(blogName)) {
					mVideoPostListPresenter = new BlogVideoPostListPresenter(this, blogName);
				}
			}
			if (mVideoPostListPresenter == null) {
				mVideoPostListPresenter = new VideoPostListPresenter(this);
			}
		}
	}

	/**
	 * 初始化屏幕旋转角度监听
	 */
	private void initOrientationEventListener() {
		mOrientationEventListener = new OrientationEventListener(getActivity(), SensorManager.SENSOR_DELAY_NORMAL) {
			@Override
			public void onOrientationChanged(int orientation) {
				if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
					return;
				}
				senorChangeOrientation(orientation);
			}
		};
	}

	/**
	 * 根据屏幕旋转角度来旋转屏幕
	 *
	 * @param orientation 屏幕旋转角度
	 */
	private void senorChangeOrientation(int orientation) {
		if (getActivity() != null) {
			if (orientation > 45 && orientation < 315) {
				if (mLastOrientationIsLandscape) {
					return;
				}
				mForceToLandscape = false;
				if (mForceToPortrait) {
					ViewUtils.setScreenOrientationPortrait(getActivity());
				} else {
					try {
						int screenChange = Settings.System.getInt(getActivity().getContentResolver(),
								Settings.System.ACCELEROMETER_ROTATION);
						if (screenChange == 1) {
							ViewUtils.setScreenOrientationUser(getActivity());
						}
					} catch (Settings.SettingNotFoundException e) {
						e.printStackTrace();
						ViewUtils.setScreenOrientationLandscape(getActivity());
					}
				}
				mLastOrientationIsLandscape = true;
			} else {
				if (!mLastOrientationIsLandscape) {
					return;
				}
				mForceToPortrait = false;
				if (mForceToLandscape) {
					ViewUtils.setScreenOrientationLandscape(getActivity());
				} else {
					ViewUtils.setScreenOrientationPortrait(getActivity());
				}
				mLastOrientationIsLandscape = false;
			}
		}
	}

	/**
	 * 点击按钮旋转屏幕
	 */
	private void clickToChangeOrientation() {
		if (getActivity() != null) {
			if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				ViewUtils.setScreenOrientationPortrait(getActivity());
				mForceToPortrait = true;
				mForceToLandscape = false;
			} else {
				mForceToLandscape = true;
				mForceToPortrait = false;
				ViewUtils.setScreenOrientationLandscape(getActivity());
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		enableOrientationEventListener();

		if (getArguments() != null) {
			mRefreshOnResume = getArguments().getBoolean(VideoViewPagerActivity.EXTRA_REFRESH_ON_RESUME, false);
		}
		if (mRefreshOnResume && (mVideoDataList == null || mVideoDataList.size() == 0)) {
			mViewBinding.flTitleLayout.setAlpha(1);
			mViewBinding.getRoot().setBackgroundColor(Color.BLACK);
			mIsFirstEnter = false;
			mRefreshOnResume = false;
			mViewBinding.smartRefreshLayout.autoRefresh();
		}
	}

	/**
	 * 启动屏幕旋转监听
	 */
	private void enableOrientationEventListener() {
		if (mOrientationEventListener.canDetectOrientation()) {
			mOrientationEventListener.enable();
		} else {
			mOrientationEventListener.disable();
		}
	}

	/**
	 * 但此页面不可见时，停止视频播放；当此页面恢复可见时，继续视频播放
	 *
	 * @param isVisibleToUser 当前页面是否可见
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isResumed()) {
			TikTokVideoLayout tikTokVideoLayout = findFirstVisibleVideoLayout();
			if (tikTokVideoLayout != null) {
				if (isVisibleToUser) {
					enableOrientationEventListener();
					tikTokVideoLayout.play();
				} else {
					tikTokVideoLayout.pause();
					mOrientationEventListener.disable();
				}
			}
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mViewBinding = FragmentFullViewVideoListBinding.inflate(inflater);
		return mViewBinding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		mViewBinding.rcvVideoList.setLayoutManager(new WrapLinearLayoutManager(getContext()));
		PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
		pagerSnapHelper.attachToRecyclerView(mViewBinding.rcvVideoList);
		mViewBinding.rcvVideoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					playFirstCompleteVisibleVideo();
				}
			}
		});
		mViewBinding.rcvVideoList.setRecyclerListener(new RecyclerView.RecyclerListener() {
			@Override
			public void onViewRecycled(RecyclerView.ViewHolder holder) {
				if (holder instanceof VideoPostThumbnailAdapter.ItemVideoViewHolder) {
					final TikTokVideoLayout tikTokVideoLayout = holder.itemView.findViewById(R.id.video_layout);
					if (TumlodrGlideUtil.isContextValid(getActivity())) {
						TumlodrGlide.with(getActivity())
								.clear(tikTokVideoLayout.getThumbnailImageView());
					}
				}
			}
		});
		mViewBinding.rcvVideoList.setAdapter(mAdapter);

		mViewBinding.toolbar.setNavigationIcon(ViewUtils.getArrowDrawable(Color.WHITE));
		mViewBinding.toolbar.setTitleTextAppearance(getActivity(), R.style.TitleTextStyle);

		initButtonListener();

		mViewBinding.smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(RefreshLayout refreshLayout) {
				if (mVideoPostListPresenter != null) {
					mVideoPostListPresenter.refreshPostList((mVideoDataList != null && mVideoDataList.size() > 0) ?
							mVideoDataList.get(0).getId() : 0);
				}
			}
		});

		mViewBinding.smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore(RefreshLayout refreshLayout) {
				if (mVideoPostListPresenter != null) {
					mVideoPostListPresenter.loadMorePostList(mVideoDataList);
				}
			}
		});

		mViewBinding.btnVideoList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (getActivity() != null && getActivity() instanceof VideoViewPagerActivity) {
					((VideoViewPagerActivity) getActivity()).scrollToVideoList();
				}
			}
		});
	}

	private void playFirstCompleteVisibleVideo() {
		TikTokVideoLayout tikTokVideoLayout = findFirstVisibleVideoLayout();
		if (tikTokVideoLayout != null) {
			tikTokVideoLayout.setOnFullScreenListener(FullViewVideoListFragment.this);
			tikTokVideoLayout.play();

			final int nextPosition = findFirstCompleteVisiblePosition() + 1;
			if (nextPosition > 0 && nextPosition < mVideoDataList.size()) {
				Completable.create(new CompletableOnSubscribe() {
					@Override
					public void subscribe(CompletableEmitter e) throws Exception {
						String url = FileDownloadUtil.getVideoDownloadInfo((VideoPost) mVideoDataList.get(nextPosition))
								.getVideoUrl();
						DataSpec dataSpec = new DataSpec(Uri.parse(TumlodrApp.getProxy().getProxyUrl(url)),
								0, 1024 * 1024, null);
						CacheDataSource dataSource = (CacheDataSource) new ExoVideoPlayer.ExoCacheDataSourceFactory().createDataSource();
						CacheUtil.cache(dataSpec, ExoVideoPlayer.ExoCacheDataSourceFactory.SIMPLE_CACHE,
								dataSource, new byte[1024], null, 0, null, null,
								false);
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
			}
		}
	}

	private void initButtonListener() {
		mViewBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mViewBinding.btnPip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ViewUtils.canDrawOverlays(getContext())) {
					if (getActivity() != null) {
						mForceToPortrait = true;
						ViewUtils.setScreenOrientationPortrait(getActivity());
					}
					TikTokVideoLayout tikTokVideoLayout = findFirstVisibleVideoLayout();
					if (tikTokVideoLayout != null) {
						onStartEnterMiniLayout();
						tikTokVideoLayout.startMiniWindowAnimation();
					}
				}
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			mViewBinding.flTitleLayout.animate().alpha(1);
			mViewBinding.flTitleLayout.setVisibility(View.VISIBLE);
			mViewBinding.btnVideoList.setVisibility(View.VISIBLE);
		}else {
			mViewBinding.btnVideoList.setVisibility(View.GONE);
		}
	}

	public void scrollToVideo(int position) {
		mViewBinding.rcvVideoList.scrollToPosition(position);
		mViewBinding.rcvVideoList.postDelayed(new Runnable() {
			@Override
			public void run() {
				playFirstCompleteVisibleVideo();
			}
		}, 200);
	}

	private void startFirstEnterAnimation(final TikTokVideoLayout tikTokVideoLayout) {
		if (getArguments() != null) {
			final Rect rect = getArguments().getParcelable(EXTRA_FROM_VIDEO_LAYOUT_AREA);
			if (rect != null) {
				prepareVideoLayoutForEnterAnimation(rect, tikTokVideoLayout);
				tikTokVideoLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						tikTokVideoLayout.getViewTreeObserver().removeOnPreDrawListener(this);
						tikTokVideoLayout.startFullViewAnimation();
						return false;
					}
				});
			} else {
				setVideoViewLayoutByPlayingVideo(tikTokVideoLayout);
			}
		} else {
			setVideoViewLayoutByPlayingVideo(tikTokVideoLayout);
		}
	}

	private void setVideoViewLayoutByPlayingVideo(final TikTokVideoLayout tikTokVideoLayout) {
		VideoPlayLayout playingVideoLayout = VideoPlayer.getInstance().getVideoPlayLayout();
		if (playingVideoLayout != null) {
			if (playingVideoLayout instanceof MiniVideoPlayLayout) {
				prepareVideoLayoutForEnterAnimation(playingVideoLayout, tikTokVideoLayout);
				tikTokVideoLayout.setAlpha(1);
				tikTokVideoLayout.setVisibility(View.VISIBLE);
				tikTokVideoLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						tikTokVideoLayout.getViewTreeObserver().removeOnPreDrawListener(this);
						ViewUtils.doAfterFadeOut(MiniVideoWindowManager.getInstance().getRootView(),
								new StaticAnimatorListenerAdapter<TikTokVideoLayout>(tikTokVideoLayout) {
									@Override
									public void onAnimationEnd(Animator animation) {
										if (getObjectWeakReference().get() != null) {
											tikTokVideoLayout.startFullViewAnimation();
											MiniVideoWindowManager.getInstance().removeMiniVideoLayout();
										}
									}
								});
						return false;
					}
				});
			} else {
				prepareVideoLayoutForEnterAnimation(playingVideoLayout, tikTokVideoLayout);
				tikTokVideoLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						tikTokVideoLayout.getViewTreeObserver().removeOnPreDrawListener(this);
						tikTokVideoLayout.startFullViewAnimation();
						return false;
					}
				});
			}
		}
	}

	public TikTokVideoLayout findFirstVisibleVideoLayout() {
		int position = findFirstCompleteVisiblePosition();
		if (position >= 0 && position < mVideoDataList.size()) {
			if (mVideoDataList.get(position) instanceof VideoPost) {
				sLastVideoPost = (VideoPost) mVideoDataList.get(position);
			}
			return findVideoPlayLayoutByPosition(position);
		}
		return null;
	}

	public int findFirstCompleteVisiblePosition() {
		WrapLinearLayoutManager linearLayoutManager = (WrapLinearLayoutManager) mViewBinding.rcvVideoList.getLayoutManager();
		linearLayoutManager.setInitialPrefetchItemCount(3);
		return linearLayoutManager.findFirstCompletelyVisibleItemPosition();
	}

	public TikTokVideoLayout findVideoPlayLayoutByPosition(int position) {
		WrapLinearLayoutManager linearLayoutManager = (WrapLinearLayoutManager) mViewBinding.rcvVideoList.getLayoutManager();
		View firstVisibleView = linearLayoutManager.findViewByPosition(position);
		if (firstVisibleView != null && firstVisibleView.findViewById(R.id.video_layout) instanceof TikTokVideoLayout) {
			return firstVisibleView.findViewById(R.id.video_layout);
		}
		return null;
	}

	@Override
	public void onStartEnterMiniLayout() {
		mViewBinding.flTitleLayout.animate().alpha(0);
	}

	@Override
	public void onReturnFullViewLayout() {
		mViewBinding.flTitleLayout.animate().alpha(1);
		if (mIsFirstEnter) {
			mIsFirstEnter = false;
		}
	}

	@Override
	public void onFullScreenChange(boolean fullScreen) {
		if (getContext() != null && getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (getContext() instanceof Activity) {
				ViewUtils.setUiFlags(((Activity) getContext()).getWindow(), fullScreen);
			}
			mViewBinding.flTitleLayout.animate().alpha(fullScreen ? 0 : 1);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mOrientationEventListener.disable();
	}

	public void onBackPressed() {
		if (getContext() != null && getActivity() != null) {
			if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				mForceToPortrait = true;
				ViewUtils.setScreenOrientationPortrait(getActivity());
			} else {
				VideoPlayer.getInstance().release();
				sLastVideoPost = null;
				getActivity().finish();
			}
		}
	}

	@Override
	public void refreshPostComplete(List<Post> newPosts) {
		mViewBinding.smartRefreshLayout.finishRefresh();
		if (newPosts != null && newPosts.size() > 0) {
			List<Post> newPostList = new ArrayList<>(newPosts);
			if (mVideoDataList != null && mVideoDataList.size() > 0) {
				newPostList.addAll(mVideoDataList);
			}
			mPostListViewModel.getDashBoardPostList().setValue(newPostList);
		}
		mViewBinding.getRoot().postDelayed(new Runnable() {
			@Override
			public void run() {
				mViewBinding.getRoot().setBackgroundColor(Color.TRANSPARENT);
				mViewBinding.rcvVideoList.scrollToPosition(0);
				mViewBinding.getRoot().postDelayed(new Runnable() {
					@Override
					public void run() {
						playFirstCompleteVisibleVideo();
					}
				}, 200);
			}
		}, 200);
	}

	@Override
	public void loadMorePostComplete(List<Post> morePosts) {
		mViewBinding.smartRefreshLayout.finishLoadMore();
		if (morePosts != null && morePosts.size() > 0) {
			List<Post> newPostList = new ArrayList<>();
			if (mVideoDataList != null && mVideoDataList.size() > 0) {
				newPostList.addAll(mVideoDataList);
			}
			newPostList.addAll(morePosts);
			mPostListViewModel.getDashBoardPostList().setValue(newPostList);
		}
	}

	@Override
	public void refreshPosFail(Throwable throwable) {
		mViewBinding.smartRefreshLayout.finishRefresh();
	}

	@Override
	public void loadMorePostFail(Throwable throwable) {
		mViewBinding.smartRefreshLayout.finishLoadMore();
	}

	public class ItemVideoHolder extends RecyclerView.ViewHolder {

		ItemVideoHolder(View itemView) {
			super(itemView);
		}
	}
}
