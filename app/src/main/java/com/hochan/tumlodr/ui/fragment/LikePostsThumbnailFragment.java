package com.hochan.tumlodr.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import com.hochan.tumlodr.prensenter.LikePostListPresenter;
import com.hochan.tumlodr.ui.activity.Router;
import com.hochan.tumlodr.ui.adapter.PostAdapter;
import com.tumblr.jumblr.types.Post;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.hochan.tumlodr.ui.adapter.PostAdapter.TYPE_PHOTO;
import static com.hochan.tumlodr.ui.adapter.PostAdapter.TYPE_VIDEO;

/**
 * .
 * Created by hochan on 2018/1/21.
 */

public class LikePostsThumbnailFragment extends PostThumbnailFragment<LikePostListPresenter> {

	private static final String TAG = LikePostsThumbnailFragment.class.getName();

	private String mFilter;

	public static LikePostsThumbnailFragment newInstance(boolean refreshOnCreate) {
		Bundle args = new Bundle();
		args.putBoolean(Router.REFRESH_ON_CREATE, refreshOnCreate);
		LikePostsThumbnailFragment fragment = new LikePostsThumbnailFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public LikePostListPresenter initPresenter() {
		return new LikePostListPresenter(this);
	}

	@Override
	public void refreshPostComplete(List<Post> newPosts) {
		filterPostByType(newPosts);
		super.refreshPostComplete(newPosts);
	}

	private void filterPostByType(List<Post> newPosts) {
		if (!TextUtils.isEmpty(mFilter)) {
			for (Iterator<Post> iterator = newPosts.iterator(); iterator.hasNext(); ) {
				Post post = iterator.next();
				if (post != null && !mFilter.equals(post.getType())) {
					iterator.remove();
				}
			}
		}
	}

	@Override
	public void loadMorePostComplete(List<Post> newPost) {
		filterPostByType(newPost);
		super.loadMorePostComplete(newPost);
	}

	public void showOnlyPicture() {
		mFilter = TYPE_PHOTO;
		changeData();
	}

	public void showOnlyVideo() {
		mFilter = TYPE_VIDEO;
		changeData();
	}

	public void showAll() {
		mFilter = null;
		mAdapter.clear();
		mSmartRefreshLayout.autoRefresh();
	}

	private void changeData() {
		List<Post> newPostList = new ArrayList<>();
		if (mAdapter.getPostList() != null && mAdapter.getPostList().size() > 0) {
			newPostList.addAll(mAdapter.getPostList());
		}
		filterPostByType(newPostList);
		mPostListViewModel.getDashBoardPostList().setValue(newPostList);
	}
}
