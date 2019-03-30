package com.hochan.tumlodr.ui.fragment;

import android.view.View;

import com.hochan.tumlodr.jumblr.types.Post;
import com.hochan.tumlodr.prensenter.SearchPostPresenter;
import com.hochan.tumlodr.ui.adapter.PostAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.Collections;
import java.util.List;

/**
 * .
 * Created by hochan on 2018/6/20.
 */

public class SearchPostFragment extends PostThumbnailFragment<SearchPostPresenter> {

	@Override
	public SearchPostPresenter initPresenter() {
		return new SearchPostPresenter(this);
	}

	@Override
	public void loadDataOnResume() {
	}

	@Override
	protected void iniWidget(View view) {
		super.iniWidget(view);
	}

	@Override
	public void onRefresh(RefreshLayout refreshLayout) {
		mSmartRefreshLayout.finishRefresh();
	}

	@SuppressWarnings("ConstantConditions")
	public void search(String tag) {
		if (mPostListViewModel.getDashBoardPostList() != null) {
			mPostListViewModel.getDashBoardPostList().setValue(Collections.<Post>emptyList());
		}
		mSmartRefreshLayout.autoRefresh();
		mPresenter.search(tag);
	}

	@Override
	public void onLoadMore(RefreshLayout refreshLayout) {
		if (mPresenter != null && mPostListViewModel.getDashBoardPostList() != null) {
			List<Post> postList = mPostListViewModel.getDashBoardPostList().getValue();
			if (postList != null && postList.size() > 0) {
				mPresenter.loadMorePostList(postList);
			}
		}
	}

	public PostAdapter getAdapter() {
		return mAdapter;
	}
}
