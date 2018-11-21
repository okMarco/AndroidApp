package com.hochan.tumlodr.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hochan.tumlodr.model.viewmodel.PostListViewModel;
import com.hochan.tumlodr.prensenter.BlogPostListPresenter;
import com.hochan.tumlodr.ui.activity.Router;
import com.tumblr.jumblr.types.Post;

import java.util.List;

/**
 * .
 * Created by hochan on 2017/12/2.
 */

public class BlogPostsThumbnailFragment extends PostThumbnailFragment<BlogPostListPresenter> {

	public static BlogPostsThumbnailFragment newInstance(String blogName) {
		Bundle args = new Bundle();
		args.putString(Router.EXTRA_BLOG_NAME, blogName);
		BlogPostsThumbnailFragment fragment = new BlogPostsThumbnailFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public BlogPostListPresenter initPresenter() {
		if (getArguments() != null) {
			String blogName = getArguments().getString(Router.EXTRA_BLOG_NAME);
			return new BlogPostListPresenter(this, blogName);
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
