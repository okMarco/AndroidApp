package com.hochan.tumlodr.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hochan.tumlodr.model.viewmodel.PostListViewModel;
import com.hochan.tumlodr.prensenter.BlogLikePostListPresenter;
import com.hochan.tumlodr.ui.activity.Router;
import com.tumblr.jumblr.types.Post;

import java.util.List;

/**
 * .
 * Created by hochan on 2017/12/2.
 */

public class BlogLikePostListThumbnailFragment extends PostThumbnailFragment<BlogLikePostListPresenter> {

	public static BlogLikePostListThumbnailFragment newInstance(String blogName) {

		Bundle args = new Bundle();
		args.putString(Router.EXTRA_BLOG_NAME, blogName);
		BlogLikePostListThumbnailFragment fragment = new BlogLikePostListThumbnailFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public BlogLikePostListPresenter initPresenter() {
		if (getArguments() != null) {
			String blogName = getArguments().getString(Router.EXTRA_BLOG_NAME);
			return new BlogLikePostListPresenter(this, blogName);
		}
		return null;
	}

	@Override
	protected void setUpPostListViewModel() {
		mPostListViewModel = ViewModelProviders.of(this).get(PostListViewModel.class);
		mPostListViewModel.getDashBoardPostList().observe(this, new Observer<List<Post>>() {
			@Override
			public void onChanged(@Nullable List<Post> newPostList) {
				mAdapter.setData(newPostList);
			}
		});
	}
}
