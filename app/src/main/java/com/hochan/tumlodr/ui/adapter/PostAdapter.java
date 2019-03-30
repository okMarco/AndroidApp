package com.hochan.tumlodr.ui.adapter;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.jumblr.types.Post;
import com.hochan.tumlodr.model.BaseObserver;
import com.hochan.tumlodr.tools.ScreenTools;
import com.hochan.tumlodr.ui.component.WrapLinearLayoutManager;
import com.hochan.tumlodr.ui.component.WrapStaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * .
 * Created by hochan on 2017/12/30.
 */

public abstract class PostAdapter extends RecyclerView.Adapter {

	public RecyclerView mRecyclerView;
	int mImageViewWidth;
	int mColumn;
	List<Post> mDataList;
	private CompositeDisposable mCompositeDisposable;

	PostAdapter(RecyclerView recyclerView, List<Post> postList) {
		mRecyclerView = recyclerView;
		mDataList = postList;
		initImageViewWidth();
	}

	public void initImageViewWidth() {
		if (mRecyclerView != null) {
			if (mRecyclerView.getLayoutManager() instanceof WrapStaggeredGridLayoutManager) {
				mColumn = ((WrapStaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).getSpanCount();
				mImageViewWidth = (ScreenTools.getScreenWidth(TumlodrApp.mContext) - (mColumn + 1) * 10) / mColumn;
			} else if (mRecyclerView.getLayoutManager() instanceof WrapLinearLayoutManager) {
				mColumn = 1;
				mImageViewWidth = ScreenTools.getScreenWidth(TumlodrApp.mContext) - 10 * 2;
			}
		}
	}

	public void setData(final List<Post> newPostList) {
		final List<Post> oldPostList = getPostList();
		io.reactivex.Observable.create(new ObservableOnSubscribe<DiffUtil.DiffResult>() {
			@Override
			public void subscribe(ObservableEmitter<DiffUtil.DiffResult> e) throws Exception {
				DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
					@Override
					public int getOldListSize() {
						return oldPostList == null ? 0 : oldPostList.size();
					}

					@Override
					public int getNewListSize() {
						return newPostList == null ? 0 : newPostList.size();
					}

					@Override
					public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
						Post oldPost = oldPostList.get(oldItemPosition);
						Post newPost = newPostList.get(newItemPosition);
						return oldPost != null && newPost != null
								&& oldPost.getId().longValue() == newPost.getId().longValue();
					}

					@Override
					public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
						Post oldPost = oldPostList.get(oldItemPosition);
						Post newPost = newPostList.get(newItemPosition);
						return oldPost != null && newPost != null && oldPost.isLiked() != null && newPost.isLiked() != null
								&& oldPost.isLiked() == newPost.isLiked();
					}
				});
				e.onNext(diffResult);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new BaseObserver<DiffUtil.DiffResult>() {

					@Override
					public void onSubscribe(Disposable d) {
						if (mCompositeDisposable == null) {
							mCompositeDisposable = new CompositeDisposable();
						}
						mCompositeDisposable.add(d);
					}

					@Override
					public void onNext(DiffUtil.DiffResult diffResult) {
						mDataList = newPostList;
						diffResult.dispatchUpdatesTo(PostAdapter.this);
					}

					@Override
					public void onError(Throwable e) {
						Crashlytics.logException(e);
					}
				});
	}


	public List<Post> refreshPosts(List<Post> posts) {
		List<Post> newPostList = new ArrayList<>();
		newPostList.addAll(posts);
		if (getPostList() != null && getPostList().size() > 0) {
			newPostList.addAll(getPostList());
		}
		return newPostList;
	}

	public List<Post> loadMorePosts(List<Post> posts) {
		List<Post> newPostList = new ArrayList<>();
		if (getPostList() != null && getPostList().size() > 0) {
			newPostList.addAll(getPostList());
		}
		if (newPostList.size() > 0 && newPostList.get(newPostList.size() - 1) == null) {
			newPostList.remove(newPostList.size() - 1);
		}
		newPostList.addAll(posts);
		return newPostList;
	}

	public List<Post> getPostList() {
		return mDataList;
	}

	public Post getItem(int position) {
		if (mDataList != null && (position >= 0 || position < mDataList.size())) {
			return mDataList.get(position);
		}
		return null;
	}

	@Override
	public int getItemCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	public void clear() {
		setData(null);
	}

	public void release() {
		mRecyclerView = null;
		if (mCompositeDisposable != null) {
			if (!mCompositeDisposable.isDisposed()) {
				mCompositeDisposable.dispose();
			}
			mCompositeDisposable.clear();
		}
		mCompositeDisposable = null;
		mDataList = null;
	}
}
