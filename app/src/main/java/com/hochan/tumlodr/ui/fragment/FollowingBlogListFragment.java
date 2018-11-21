package com.hochan.tumlodr.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hochan.tumlodr.model.BaseObserver;
import com.hochan.tumlodr.model.TumlodrService;
import com.hochan.tumlodr.prensenter.BaseMvpPresenter;
import com.hochan.tumlodr.tools.ScreenTools;
import com.hochan.tumlodr.ui.adapter.BaseLoadingAdapter;
import com.hochan.tumlodr.ui.adapter.BlogListAdapter;
import com.hochan.tumlodr.ui.fragment.base.BaseMvpListFragment;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.tumblr.jumblr.types.Blog;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 * Created by Administrator on 2016/6/1.
 */
public class FollowingBlogListFragment extends BaseMvpListFragment {

	private List<Blog> mTumBlogList = new ArrayList<>();

	private BlogListAdapter mBlogListAdapter;

	private boolean showRefreshOnVisible = true;

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
		mBlogListAdapter = new BlogListAdapter(mTumBlogList);
		mRecyclerView.setAdapter(mBlogListAdapter);
		mSmartRefreshLayout.setPadding(ScreenTools.dip2px(mRecyclerView.getContext(), 5), 0,
				ScreenTools.dip2px(mRecyclerView.getContext(), 5), 0);
		loadMore();
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
		final int offset = mTumBlogList.size();

		TumlodrService.getUserFollowing(offset).subscribe(new BaseObserver<List<Blog>>() {
			@Override
			public void onNext(List<Blog> blogList) {
				showRefreshOnVisible = false;
				mSmartRefreshLayout.finishRefresh();
				if (blogList == null) {
					return;
				}
				if (blogList.size() == 0) {
					mSmartRefreshLayout.setNoMoreData(true);
					return;
				}
				if (mTumBlogList.size() > 0) {
					mTumBlogList.remove(mTumBlogList.size() - 1);
					mBlogListAdapter.notifyItemRemoved(mTumBlogList.size());
				}
				mTumBlogList.addAll(blogList);
				mBlogListAdapter.notifyItemRangeInserted(offset, blogList.size());
				mSmartRefreshLayout.finishLoadMore();
			}
		});
	}

	@Override
	public void onRefresh(RefreshLayout refreshLayout) {
		TumlodrService.refreshUserFollowings().compose(this.<List<Blog>>bindUntilEvent(FragmentEvent.DESTROY))
				.subscribe(new BaseObserver<List<Blog>>() {
					@Override
					public void onNext(List<Blog> blogs) {
						mTumBlogList.clear();
						mTumBlogList.addAll(blogs);
						mBlogListAdapter.notifyDataSetChanged();
						showRefreshOnVisible = false;
						mSmartRefreshLayout.finishRefresh();
					}
				});	}
}
