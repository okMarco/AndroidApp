package com.hochan.tumlodr.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.prensenter.BaseMvpPresenter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

/**
 * .
 * Created by zhendong_chen on 2016/8/25.
 */
public abstract class BaseMvpListFragment<AP extends RecyclerView.Adapter, T extends BaseMvpPresenter> extends BaseMvpFragment<T>
		implements OnLoadMoreListener, OnRefreshListener {

	public AP mAdapter;
	public RecyclerView.LayoutManager mLayoutManager;
	public RecyclerView mRecyclerView;
	protected SmartRefreshLayout mSmartRefreshLayout;

	@Override
	public int getLayoutRecourseId() {
		return R.layout.frag_base_list;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		iniWidget(view);
		initData();
	}

	@CallSuper
	protected void iniWidget(View view) {
		mRecyclerView = view.findViewById(R.id.recycler_view);
		mLayoutManager = initRecyclerLayoutManager();
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = initAdapter();
		mRecyclerView.setAdapter(mAdapter);
		setRecyclerViewDivider();

		mSmartRefreshLayout = view.findViewById(R.id.smart_refresh_layout);
		if (mSmartRefreshLayout != null) {
			mSmartRefreshLayout.setOnLoadMoreListener(this);
			mSmartRefreshLayout.setOnRefreshListener(this);
		}
	}

	@Override
	public void onLoadMore(RefreshLayout refreshLayout) {
		refreshLayout.finishLoadMore();
	}

	@Override
	public void onRefresh(RefreshLayout refreshLayout) {
		refreshLayout.finishRefresh();
	}

	protected void initData() {
	}

	public abstract RecyclerView.LayoutManager initRecyclerLayoutManager();

	public abstract AP initAdapter();

	public abstract void setRecyclerViewDivider();
}
