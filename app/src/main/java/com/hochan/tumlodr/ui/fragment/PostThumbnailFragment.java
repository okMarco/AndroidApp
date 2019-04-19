package com.hochan.tumlodr.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.jumblr.types.Post;
import com.hochan.tumlodr.jumblr.types.VideoPost;
import com.hochan.tumlodr.model.viewmodel.PostListViewModel;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.glide.OkHoGlideUtil;
import com.hochan.tumlodr.module.video.videolayout.VideoPlayLayout;
import com.hochan.tumlodr.prensenter.PostListPresenter;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.tools.ScreenTools;
import com.hochan.tumlodr.ui.activity.PostDetailActivity;
import com.hochan.tumlodr.ui.activity.Router;
import com.hochan.tumlodr.ui.activity.VideoViewPagerActivity;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseActivity;
import com.hochan.tumlodr.ui.adapter.PostAdapter;
import com.hochan.tumlodr.ui.adapter.PostDetailAdapter;
import com.hochan.tumlodr.ui.adapter.PostThumbnailAdapter;
import com.hochan.tumlodr.ui.component.IPhotoLayout;
import com.hochan.tumlodr.ui.component.PostPhotoLayout;
import com.hochan.tumlodr.ui.component.RecycleViewDivider;
import com.hochan.tumlodr.ui.component.WrapLinearLayoutManager;
import com.hochan.tumlodr.ui.component.WrapStaggeredGridLayoutManager;
import com.hochan.tumlodr.ui.fragment.base.BaseMvpListFragment;
import com.hochan.tumlodr.ui.view.IPostListView;
import com.hochan.tumlodr.util.Events;
import com.hochan.tumlodr.util.FragmentLifecycleProvider;
import com.hochan.tumlodr.util.RxBus;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * .
 * Created by hochan on 2018/1/21.
 */

public abstract class PostThumbnailFragment<T extends PostListPresenter> extends BaseMvpListFragment<PostAdapter, T> implements IPostListView,
		PostThumbnailAdapter.OnPostCommandEventListener {

	public PostListViewModel mPostListViewModel;
	private int mShareEnterIndex;
	private int mShareExitIndex;
	private WeakReference<IPhotoLayout> mIPhotoLayoutWeakReference;
	private boolean mIsLoadingMore;
	private boolean mIsRefreshing;
	private boolean mScrollToTopOnRefreshComplete;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RxBus.with(new FragmentLifecycleProvider(this))
				.setEndEvent(FragmentEvent.DESTROY)
				.onNext(new Consumer<Object>() {
					@Override
					public void accept(Object o) {
						if (o instanceof Events) {
							observeTransitionEvent((Events) o);
						}
					}
				}).create();
	}

	private void observeTransitionEvent(Events o) {
		switch (o.mCode) {
			case Events.EVENT_IMAGE_SHAREELEMENT_CONTAINER: {
				mIPhotoLayoutWeakReference = new WeakReference<>((IPhotoLayout) o.mContent);
				break;
			}
			case Events.EVENT_SHAREELEMENT_ENTER_INDEX_CHANGE: {
				changeEnterIndex(o);
				break;
			}
			case Events.EVENT_SHAREELEMENT_EXIT_INDEX_CHANGE: {
				mShareExitIndex = (int) o.mContent;
				break;
			}
			case Events.EVENT_UPDATE_COLUMN_COUNT: {
				changeColumnCount();
				break;
			}
			case Events.EVENT_UPDATE_LAYOUT_STYLE: {
				changeLayoutStyle();
				break;
			}
		}
	}

	private void changeEnterIndex(Events o) {
		mShareEnterIndex = (int) o.mContent;
		if (getActivity() != null) {
			getActivity().setExitSharedElementCallback(new SharedElementCallback() {
				@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
				@Override
				public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
					if (mShareExitIndex != mShareEnterIndex &&
							mIPhotoLayoutWeakReference != null
							&& mIPhotoLayoutWeakReference.get() != null) {
						sharedElements.clear();
						sharedElements.put(Router.SHARE_ELEMENT_NAME,
								mIPhotoLayoutWeakReference.get().getImageViewInPosition(mShareExitIndex));
						if (mIPhotoLayoutWeakReference.get().getImageViewInPosition(mShareEnterIndex) != null) {
							mIPhotoLayoutWeakReference.get().getImageViewInPosition(mShareEnterIndex)
									.setTransitionName(null);
						}
					}
					if (getActivity() != null) {
						getActivity().setExitSharedElementCallback((android.app.SharedElementCallback) null);
					}
				}

				@Override
				public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
					if (mIPhotoLayoutWeakReference != null && mIPhotoLayoutWeakReference.get() != null) {
						mIPhotoLayoutWeakReference.get().reloadImage();
						mIPhotoLayoutWeakReference.clear();
					}
				}
			});
		}
	}

	private void changeLayoutStyle() {
		if (AppConfig.sPostListLayoutStyle == AppConfig.LAYOUT_DETAIL) {
			if (mRecyclerView.getLayoutManager() instanceof WrapStaggeredGridLayoutManager) {
				int[] into = new int[((WrapStaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).getSpanCount()];
				((WrapStaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPositions(into);
				toggleToDetailLayout(into[0]);
			}
		} else {
			if (mRecyclerView.getLayoutManager() instanceof WrapLinearLayoutManager) {
				toggleToThumbnailLayout();
			}
		}
	}

	private void changeColumnCount() {
		if (mAdapter instanceof PostThumbnailAdapter) {
			mRecyclerView.setLayoutManager(new WrapStaggeredGridLayoutManager(AppConfig.mPostListColumnCount,
					WrapStaggeredGridLayoutManager.VERTICAL));
			if (mAdapter instanceof PostAdapter) {
				mAdapter.initImageViewWidth();
			}
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getActivity() != null) {
			setUpPostListViewModel();
		}
	}

	protected void setUpPostListViewModel() {
		if (getActivity() != null) {
			mPostListViewModel = ViewModelProviders.of(getActivity()).get(PostListViewModel.class);
			mPostListViewModel.getDashBoardPostList().observe(this, new Observer<List<Post>>() {
				@Override
				public void onChanged(@Nullable List<Post> newPostList) {
					if (mSmartRefreshLayout != null) {
						mSmartRefreshLayout.finishRefresh();
						mSmartRefreshLayout.finishLoadMore();
					}
					mAdapter.setData(newPostList);
				}
			});
		}
	}

	@Override
	protected void iniWidget(View view) {
		super.iniWidget(view);
		loadDataOnResume();

		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (mAdapter instanceof PostThumbnailAdapter) {
					((PostThumbnailAdapter) mAdapter).dismissPopupWindow();
				}
			}
		});

		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				if (OkHoGlideUtil.isContextValid(PostThumbnailFragment.this)) {
					if (newState == RecyclerView.SCROLL_STATE_IDLE) {
						TumlodrGlide.with(PostThumbnailFragment.this).resumeRequests();
						if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager
								&& AppConfig.sPlayVideoAuto) {
							playFirstVisibleVideo();
						}
					} else {
						TumlodrGlide.with(PostThumbnailFragment.this).pauseRequests();
					}
				}
				loadMoreWillReachBottom();
			}
		});

		mRecyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
			@Override
			public void onViewRecycled(RecyclerView.ViewHolder holder) {
				try {
					if (OkHoGlideUtil.isContextValid(getActivity())) {
						if (holder instanceof PostThumbnailAdapter.PostThumbnailViewHolder) {
							for (int i = 0; i < ((PostThumbnailAdapter.PostThumbnailViewHolder) holder).mItemViewBinding.plPostPhotos.getImageViewCount();
							     i++) {
								TumlodrGlide.with(getActivity())
										.clear(((PostThumbnailAdapter.PostThumbnailViewHolder) holder).mItemViewBinding.plPostPhotos.getImageViewInPosition(i));
							}
						} else if (holder instanceof PostDetailAdapter.PhotoPostDetailViewHolder) {
							for (int i = 0; i < ((PostDetailAdapter.PhotoPostDetailViewHolder) holder).mViewBinding.llPhotos.getImageViewCount(); i++) {
								TumlodrGlide.with(getActivity())
										.clear(((PostDetailAdapter.PhotoPostDetailViewHolder) holder).mViewBinding.llPhotos.getImageViewInPosition(i));
							}
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
		mRecyclerView.setItemAnimator(null);
	}

	private void loadMoreWillReachBottom() {
		if (mRecyclerView.getLayoutManager() instanceof WrapStaggeredGridLayoutManager) {
			WrapStaggeredGridLayoutManager layoutManager = (WrapStaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
			int[] tmpPositions = new int[layoutManager.getSpanCount()];
			layoutManager.findLastVisibleItemPositions(tmpPositions);
			int maxPosition = tmpPositions[0];
			for (int tmpPosition : tmpPositions) {
				maxPosition = maxPosition < tmpPosition ? tmpPosition : maxPosition;
			}
			if (maxPosition >= mAdapter.getItemCount() - 10 && !mIsLoadingMore) {
				mIsLoadingMore = true;
				mPresenter.loadMorePostList(mAdapter.getPostList());
			}
		}
	}

	private void playFirstVisibleVideo() {
		LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
		int firstPosition = layoutManager.findFirstVisibleItemPosition();
		int lastPosition = layoutManager.findLastVisibleItemPosition();
		int[] location = new int[2];
		for (int i = firstPosition; i <= lastPosition; i++) {
			if (mRecyclerView.getAdapter() != null && i > 0
					&& i < mRecyclerView.getAdapter().getItemCount()) {
				View view = layoutManager.findViewByPosition(i);
				View videoView = view.findViewById(R.id.video_play_layout);
				if (videoView != null) {
					videoView.getLocationOnScreen(location);
					if (location[1] + videoView.getMeasuredHeight() < ScreenTools.dip2px(TumlodrApp.getContext(), 50)) {
						continue;
					}
					VideoPlayLayout videoPlayLayout = (VideoPlayLayout) videoView;
					videoPlayLayout.play();
					return;
				}
			}
		}
	}

	public void loadDataOnResume() {
		mSmartRefreshLayout.autoRefresh();
	}

	@Override
	public RecyclerView.LayoutManager initRecyclerLayoutManager() {
		if (AppConfig.sPostListLayoutStyle == AppConfig.LAYOUT_DETAIL) {
			return new WrapLinearLayoutManager(getActivity());
		} else {
			return new WrapStaggeredGridLayoutManager(AppConfig.mPostListColumnCount, WrapStaggeredGridLayoutManager.VERTICAL);
		}
	}

	@Override
	public PostAdapter initAdapter() {
		if (AppConfig.sPostListLayoutStyle == AppConfig.LAYOUT_DETAIL) {
			return new PostDetailAdapter(mRecyclerView, new ArrayList<Post>(), this);
		} else {
			return new PostThumbnailAdapter(mRecyclerView, new ArrayList<Post>(),
					this);
		}
	}

	@Override
	public void setRecyclerViewDivider() {
		if (getContext() != null && getContext().getResources() != null) {
			mRecyclerView.setPadding(0, 0, 10, 0);
			int gap = ScreenTools.dip2px(getContext(), getContext().getResources().getDimension(R.dimen.post_list_gap));
			mRecyclerView.addItemDecoration(new RecycleViewDivider(getContext(),
					RecycleViewDivider.ORIENTATION_BOTH, gap, Color.parseColor("#00000000")));
		}
	}

	@Override
	public void onRefresh(RefreshLayout refreshLayout) {
		if (mIsRefreshing) {
			return;
		}
		mIsRefreshing = true;
		mScrollToTopOnRefreshComplete = true;
		mPresenter.refreshPostList(mAdapter.getPostList() != null && mAdapter.getPostList().size() > 0 ?
				mAdapter.getPostList().get(0).getId() : 0);
	}

	@Override
	public void onLoadMore(RefreshLayout refreshLayout) {
		if (mPresenter != null && !mSmartRefreshLayout.isRefreshing() && !mIsLoadingMore) {
			mIsLoadingMore = true;
			mPresenter.loadMorePostList(mAdapter.getPostList());
		}
	}

	@Override
	public void refreshPostComplete(List<Post> newPosts) {
		mSmartRefreshLayout.finishRefresh();
		if (newPosts == null || newPosts.size() == 0) {
			return;
		}
		List<Post> newPostList = mAdapter.refreshPosts(newPosts);
		mPostListViewModel.getDashBoardPostList().setValue(newPostList);
		if (mScrollToTopOnRefreshComplete) {
			mRecyclerView.postDelayed(new Runnable() {
				@Override
				public void run() {
					mRecyclerView.smoothScrollToPosition(0);
					loadMoreWillReachBottom();
				}
			}, 500);
		}
		mIsRefreshing = false;
	}

	@Override
	public void loadMorePostComplete(List<Post> newPost) {
		mIsLoadingMore = false;
		if (newPost == null || newPost.size() == 0) {
			return;
		}
		List<Post> newPostList = mAdapter.loadMorePosts(newPost);
		mPostListViewModel.getDashBoardPostList().setValue(newPostList);
		mSmartRefreshLayout.finishLoadMore();
	}

	@Override
	public void refreshPosFail(Throwable throwable) {
		mSmartRefreshLayout.finishRefresh();
	}

	@Override
	public void loadMorePostFail(Throwable throwable) {
		mSmartRefreshLayout.finishLoadMore();
	}

	@Override
	public void onShowPostDetail(int adapterPosition, PostPhotoLayout postPhotoLayout) {
		if (getActivity() != null) {
			PostDetailActivity.sDetailPost = mAdapter.getPostList().get(adapterPosition);
			Intent intent = new Intent(getActivity(), PostDetailActivity.class);
			getActivity().startActivity(intent);
		}
	}

	@Override
	public void onShowImageDetail(int index, IPhotoLayout postPhotoLayout, Post post) {
		Router.showImage(getActivity(), postPhotoLayout, index, post);
	}

	@Override
	public void onPlayVideo(int index) {
		VideoPost videoPost = (VideoPost) mAdapter.getPostList().get(index);
		VideoViewPagerActivity.playVideo(getActivity(), mRecyclerView.findViewHolderForAdapterPosition(index).itemView,
				videoPost);
	}

	@Override
	public void onLikePost(Post post, ImageView likeView, boolean isDetail) {
		PostListPresenter.likePost(post, likeView, isDetail);
	}

	@Override
	public void onDisLike(Post post, ImageView likeView, boolean isDetail) {
		PostListPresenter.unlikePost(post, likeView, isDetail);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (OkHoGlideUtil.isContextValid(PostThumbnailFragment.this)) {
			TumlodrGlide.with(this).pauseRequests();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (OkHoGlideUtil.isContextValid(PostThumbnailFragment.this)) {
			TumlodrGlide.with(this).resumeRequests();
		}
	}

	public void toggleToDetailLayout(int adapterPosition) {
		mRecyclerView.setLayoutManager(new WrapLinearLayoutManager(getContext()));
		List<Post> postList = mAdapter.getPostList();
		mAdapter = new PostDetailAdapter(mRecyclerView, postList, this);
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.scrollToPosition(adapterPosition);
		mRecyclerView.requestLayout();
	}

	public void toggleToThumbnailLayout() {
		WrapLinearLayoutManager linearLayoutManager = (WrapLinearLayoutManager) mRecyclerView.getLayoutManager();
		int position = linearLayoutManager.findFirstVisibleItemPosition();
		mRecyclerView.setLayoutManager(new WrapStaggeredGridLayoutManager(AppConfig.mPostListColumnCount, OrientationHelper.VERTICAL));
		mAdapter = new PostThumbnailAdapter(mRecyclerView, mAdapter.getPostList(), this);
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.scrollToPosition(position);
		mRecyclerView.requestLayout();
	}

	public void dismissPopupWindow() {
		if (mAdapter instanceof PostThumbnailAdapter) {
			((PostThumbnailAdapter) mAdapter).dismissPopupWindow();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mAdapter.release();
	}

	public void scrollToTop() {
		if (mRecyclerView != null && mRecyclerView.getAdapter() != null
				&& mRecyclerView.getAdapter().getItemCount() > 0) {
			mRecyclerView.smoothScrollToPosition(0);
		}
	}

	public VideoPost getFirstVideoPost() {
		if (mAdapter != null && mAdapter.getPostList() != null && mAdapter.getPostList().size() > 0) {
			for (int i = 0; i < mAdapter.getPostList().size(); i++) {
				Post post = mAdapter.getPostList().get(i);
				if (post != null && post.getType() == Post.PostType.VIDEO && post instanceof VideoPost) {
					return (VideoPost) post;
				}
			}
		}
		return null;
	}
}
