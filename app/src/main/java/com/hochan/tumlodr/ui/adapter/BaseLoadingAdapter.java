package com.hochan.tumlodr.ui.adapter;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.ui.component.WrapStaggeredGridLayoutManager;

import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * .
 * Created by Administrator on 2016/5/22.
 */
public abstract class BaseLoadingAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final String TAG = "BaseLoadingAdapter";

	//是否正在加载
	protected boolean mIsLoading = false;
	//正常条目
	private static final int TYPE_NORMAL_ITEM = 0;
	//加载条目
	protected static final int TYPE_LOADING_ITEM = -1;
	//加载ViewHolder
	private LoadingViewHolder mLoadingViewHolder;
	//瀑布流
	private WrapStaggeredGridLayoutManager mStaggeredGridLayoutManager;
	//数据集
	List<T> mDataList;
	//首次进入
	private boolean mFirstEnter = true;

	private boolean mNoMore = false;
	private RecyclerView mRecyclerView;

	BaseLoadingAdapter(RecyclerView recyclerView, List<T> dataList) {
		mDataList = dataList;
		mRecyclerView = recyclerView;
		setSpanCount(recyclerView);
	}

	@Override
	public int getItemViewType(int position) {
		T t = null;
		if (mDataList != null && position >= 0 && position < mDataList.size()) {
			t = mDataList.get(position);
		}
		if (t == null) {
			return TYPE_LOADING_ITEM;
		} else {
			return TYPE_NORMAL_ITEM;
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType != TYPE_LOADING_ITEM) {
			return onCreateNormalViewHolder(parent, viewType);
		} else {
			View view = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.layout_loading, parent, false);
			mLoadingViewHolder = new LoadingViewHolder(view);
			return mLoadingViewHolder;
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		int type = getItemViewType(position);
		if (type == TYPE_LOADING_ITEM) {
			if (mStaggeredGridLayoutManager != null) {
				WrapStaggeredGridLayoutManager.LayoutParams layoutParams =
						(WrapStaggeredGridLayoutManager.LayoutParams) mLoadingViewHolder.llLoading.getLayoutParams();
				layoutParams.setFullSpan(true);
			}
		} else {
			onBindNormalViewHolder(holder, position);
		}
	}

	@Override
	public int getItemCount() {
		return mDataList.size();
	}

	private OnLoadingListener mOnLoadingListener;

	public void clear() {
		if (mDataList != null) {
			mDataList.clear();
			notifyDataSetChanged();
		}
	}

	/**
	 * 加载更多接口
	 */
	public interface OnLoadingListener {
		void loading();
	}

	/**
	 * 设置监听接口
	 *
	 * @param onLoadingListener onLoadingListener
	 */
	public void setOnLoadingListener(OnLoadingListener onLoadingListener) {
		setScrollListener(mRecyclerView);
		mOnLoadingListener = onLoadingListener;
	}

	/**
	 * 加载完成
	 */
	public void setLoadingComplete() {
		mIsLoading = false;
		if (mDataList.size() > 0 && mDataList.get(mDataList.size() - 1) == null) {
			mDataList.remove(mDataList.size() - 1);
			notifyItemRemoved(mDataList.size() - 1);
		}
	}

	/**
	 * 没有更多数据
	 */
	public void setLoadingNoMore() {
		mIsLoading = false;
		mNoMore = true;
		if (mLoadingViewHolder != null) {
			mLoadingViewHolder.mProgressBar.setVisibility(View.GONE);
			mLoadingViewHolder.tvNoMore.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * @return Whether it is possible for the child view of this layout to
	 * scroll up. Override this if the child view is a custom view.
	 */
	private boolean canScrollDown(RecyclerView recyclerView) {
		return recyclerView.canScrollVertically(1);
	}

	/**
	 * 设置加载item占据一行
	 *
	 * @param recyclerView recycleView
	 */
	private void setSpanCount(RecyclerView recyclerView) {
		RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

		if (layoutManager == null) {
			Log.e(TAG, "LayoutManager 为空,请先设置 recycleView.setLayoutManager(...)");
		}

		//网格布局
		if (layoutManager instanceof GridLayoutManager) {
			final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
			gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
				@Override
				public int getSpanSize(int position) {
					int type = getItemViewType(position);
					if (type == TYPE_LOADING_ITEM) {
						return gridLayoutManager.getSpanCount();

					} else {
						return 1;
					}
				}
			});
		}

		//瀑布流布局
		if (layoutManager instanceof WrapStaggeredGridLayoutManager) {
			mStaggeredGridLayoutManager = (WrapStaggeredGridLayoutManager) layoutManager;
		}
	}

	/**
	 * 监听滚动事件
	 *
	 * @param recyclerView recycleView
	 */
	private void setScrollListener(RecyclerView recyclerView) {
		if (recyclerView == null) {
			Log.e(TAG, "recycleView 为空");
			return;
		}

		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				if (!canScrollDown(recyclerView)) {
					if (mNoMore) {
						setLoadingNoMore();
						return;
					}
					tryLoading();
				}

				if (mFirstEnter) {
					mFirstEnter = false;
				}
			}
		});
	}

	private void tryLoading() {
		if (!mIsLoading) {
			notifyLoading();
			mIsLoading = true;
			if (!mFirstEnter) {
				if (mLoadingViewHolder != null) {
					mLoadingViewHolder.mProgressBar.setVisibility(View.VISIBLE);
					mLoadingViewHolder.tvNoMore.setVisibility(View.GONE);
				}
			}
			if (mOnLoadingListener != null) {
				mOnLoadingListener.loading();
			}
		} else {
			notifyLoading();
		}
	}

	/**
	 * 显示加载
	 */
	private void notifyLoading() {
		if (mOnLoadingListener != null && mDataList.size() != 0 && mDataList.get(mDataList.size() - 1) != null) {
			mDataList.add(null);
			notifyItemInserted(mDataList.size() - 1);
		}
	}

	/**
	 * 创建ViewHolder
	 */
	public abstract RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType);

	/**
	 * 绑定viewHolder
	 */
	public abstract void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position);


	/**
	 * 加载布局
	 */
	@SuppressWarnings("WeakerAccess")
	private class LoadingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		public SmoothProgressBar mProgressBar;
		public TextView tvNoMore;
		public LinearLayout llLoading;

		public LoadingViewHolder(View view) {
			super(view);
			mProgressBar = view.findViewById(R.id.progress_loading);
			tvNoMore = view.findViewById(R.id.tv_nomore);
			llLoading = view.findViewById(R.id.ll_loading);
			tvNoMore.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			tryLoading();
		}
	}
}