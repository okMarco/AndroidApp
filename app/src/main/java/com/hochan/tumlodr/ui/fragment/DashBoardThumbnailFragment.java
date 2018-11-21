package com.hochan.tumlodr.ui.fragment;

import android.os.Bundle;

import com.hochan.tumlodr.prensenter.DashBoardPostListPresenter;
import com.hochan.tumlodr.ui.activity.Router;
import com.hochan.tumlodr.ui.adapter.PostAdapter;

/**
 * .
 * Created by hochan on 2017/12/24.
 */

public class DashBoardThumbnailFragment extends PostThumbnailFragment<DashBoardPostListPresenter> {

	private static final String TAG = DashBoardThumbnailFragment.class.getName();

	public static DashBoardThumbnailFragment newInstance(boolean refreshOnCreate) {
		Bundle args = new Bundle();
		args.putBoolean(Router.REFRESH_ON_CREATE, refreshOnCreate);
		DashBoardThumbnailFragment fragment = new DashBoardThumbnailFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public DashBoardPostListPresenter initPresenter() {
		return new DashBoardPostListPresenter(this);
	}

	public void showAll() {
		mAdapter.clear();
		mPresenter.showAll();
		pullToRefresh();
	}

	public void showOnlyPicture() {
		mAdapter.clear();
		mPresenter.showOnlyPicture();
		pullToRefresh();
	}

	public void showOnlyVideo() {
		mAdapter.clear();
		mPresenter.showOnlyVideo();
		pullToRefresh();
	}

	public void showOnlyText() {
		mAdapter.clear();
		mPresenter.showOnlyText();
		pullToRefresh();
	}

	private void pullToRefresh() {
		mSmartRefreshLayout.autoRefresh();
	}
}
