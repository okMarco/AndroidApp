package com.hochan.tumlodr.ui.fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hochan.tumlodr.jumblr.types.Blog;
import com.hochan.tumlodr.model.BaseObserver;
import com.hochan.tumlodr.model.TumlodrService;
import com.hochan.tumlodr.model.data.blog.FollowingBlog;
import com.hochan.tumlodr.model.data.blog.FollowingBlogDatabase;
import com.hochan.tumlodr.model.data.download.DownloadRecord;
import com.hochan.tumlodr.model.data.download.DownloadRecordDatabase;
import com.hochan.tumlodr.prensenter.BaseMvpPresenter;
import com.hochan.tumlodr.tools.ScreenTools;
import com.hochan.tumlodr.ui.adapter.BaseLoadingAdapter;
import com.hochan.tumlodr.ui.adapter.BlogListAdapter;
import com.hochan.tumlodr.ui.fragment.base.BaseMvpListFragment;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 * Created by Administrator on 2016/6/1.
 */
public class FollowingBlogListFragment extends BaseMvpListFragment {

	private BlogListAdapter mBlogListAdapter;
	public LiveData<PagedList<FollowingBlog>> mFollowingBlogList;

	public static FollowingBlogListFragment newInstance() {
		return new FollowingBlogListFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public BaseMvpPresenter initPresenter() {
		return null;
	}

	@Override
	protected void iniWidget(View view) {
		super.iniWidget(view);
		mBlogListAdapter = new BlogListAdapter();
		mRecyclerView.setAdapter(mBlogListAdapter);
		mSmartRefreshLayout.setPadding(ScreenTools.dip2px(mRecyclerView.getContext(), 5), 0,
				ScreenTools.dip2px(mRecyclerView.getContext(), 5), 0);

		mFollowingBlogList = new LivePagedListBuilder<>(FollowingBlogDatabase.getFollowingBlogs(), 1000).build();

		if (getActivity() != null) {
			mFollowingBlogList.observe(getActivity(), new Observer<PagedList<FollowingBlog>>() {
				@Override
				public void onChanged(@Nullable PagedList<FollowingBlog> followingBlogs) {
					mBlogListAdapter.submitList(followingBlogs);
					if (followingBlogs == null || followingBlogs.size() == 0) {
						mSmartRefreshLayout.autoRefresh();
					}
				}
			});
		}

	}

	@Override
	public void onLoadMore(RefreshLayout refreshLayout) {
		loadMore();
	}

	@Override
	public RecyclerView.LayoutManager initRecyclerLayoutManager() {
		return new GridLayoutManager(getContext(), 4);
	}

	@Override
	public BaseLoadingAdapter initAdapter() {
		return null;
	}

	@Override
	public void setRecyclerViewDivider() {
	}

	private void loadMore() {
		final int offset = mBlogListAdapter.getItemCount();
		TumlodrService.getUserFollowing(offset).subscribe(new BaseObserver<List<Blog>>() {
			@Override
			public void onNext(List<Blog> blogList) {
				mSmartRefreshLayout.finishRefresh();
				if (blogList == null) {
					return;
				}
				if (blogList.size() == 0) {
					mSmartRefreshLayout.setNoMoreData(true);
				}else {
					mSmartRefreshLayout.finishLoadMore();
					if (mBlogListAdapter.getItemCount() < 50) {
						mSmartRefreshLayout.autoLoadMore();
					}
				}
			}
		});
	}

	@Override
	public void onRefresh(RefreshLayout refreshLayout) {
		TumlodrService.refreshUserFollowings().compose(this.<List<Blog>>bindUntilEvent(FragmentEvent.DESTROY))
				.subscribe(new BaseObserver<List<Blog>>() {
					@Override
					public void onNext(List<Blog> blogs) {
						mSmartRefreshLayout.finishRefresh();
					}
				});	}
}
