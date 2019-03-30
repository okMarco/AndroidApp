package com.hochan.tumlodr.prensenter;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.jumblr.types.Post;
import com.hochan.tumlodr.model.BaseObserver;
import com.hochan.tumlodr.model.TumlodrService;
import com.hochan.tumlodr.ui.view.IPostListView;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * .
 * Created by hochan on 2017/12/24.
 */

public abstract class PostListPresenter extends BaseMvpPresenter<IPostListView> {

	PostListPresenter(IPostListView view) {
		super(view);
	}

	@Override
	public void initData(Bundle bundle) {
	}

	public abstract void refreshPostList(long sinceId);

	public abstract void loadMorePostList(List<Post> posts);

	BaseObserver<List<Post>> getRefreshObserver() {
		return new BaseObserver<List<Post>>() {

			@Override
			public void onNext(List<Post> posts) {
				if (mView != null) {
					mView.refreshPostComplete(posts);
				}
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				if (mView != null) {
					mView.refreshPosFail(e);
				}
			}
		};
	}

	BaseObserver<List<Post>> getLoadMoreObserver() {
		return new BaseObserver<List<Post>>() {

			@Override
			public void onSubscribe(Disposable d) {
				super.onSubscribe(d);
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onNext(List<Post> posts) {
				if (mView != null) {
					mView.loadMorePostComplete(posts);
				}
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				if (mView != null) {
					mView.loadMorePostFail(e);
				}
			}
		};
	}

	public static void likePost(final Post post, final ImageView imageView, final boolean isDetail) {
		imageView.setTag(post.getId());
		if (isDetail) {
			imageView.setImageResource(R.drawable.ic_favorite_red);
		} else {
			imageView.setVisibility(View.VISIBLE);
		}
		TumlodrService.likePost(post.getId(), post.getReblogKey()).subscribe(new BaseObserver<Long>() {

			@Override
			public void onError(Throwable e) {
				if (imageView.getTag().equals(post.getId())) {
					if (!isDetail) {
						imageView.setVisibility(View.INVISIBLE);
					} else {
						imageView.setImageResource(R.drawable.ic_detail_like);
					}
				}
			}

			@Override
			public void onNext(Long aLong) {
				TumlodrService.setPostLiked(post, true);
			}
		});
	}

	public static void unlikePost(final Post post, final ImageView imageView, final boolean isDetail) {
		imageView.setTag(post.getId());
		if (isDetail) {
			imageView.setImageResource(R.drawable.ic_detail_like);
		} else {
			imageView.setVisibility(View.GONE);
		}
		TumlodrService.unlikePost(post).subscribe(new BaseObserver<Post>() {
			@Override
			public void onError(Throwable e) {
				if (imageView.getTag().equals(post.getId())) {
					if (!isDetail) {
						imageView.setVisibility(View.VISIBLE);
					} else {
						imageView.setImageResource(R.drawable.ic_favorite_red);
					}
				}
			}

			@Override
			public void onNext(Post post) {
				TumlodrService.setPostLiked(post, false);
			}
		});
	}

//	public int getOffset() {
//		int offset = 0;
//		if (mPostList != null && mPostList.size() > 0) {
//			offset = mPostList.size() - 1;
//		}
//		return offset;
//	}

	@Override
	public void releasePresenter() {
		super.releasePresenter();
		mView = null;
	}
}
