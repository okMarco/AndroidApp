package com.hochan.tumlodr.prensenter;

import com.hochan.tumlodr.ui.view.IPostListView;

import static com.hochan.tumlodr.model.TumlodrService.REQUEST_OFFSET;
import static com.hochan.tumlodr.model.TumlodrService.loadBlogPosts;

/**
 * .
 * Created by hochan on 2018/7/11.
 */

public class BlogVideoPostListPresenter extends BlogPostListPresenter{

	public BlogVideoPostListPresenter(IPostListView view, String blogName) {
		super(view, blogName);
	}

	@Override
	public void refreshPostList(long since) {
		loadBlogPosts(mBlogName, REQUEST_OFFSET, 0, "video").subscribe(getRefreshObserver());
	}

	@Override
	public void loadMorePostList(long offset) {
		loadBlogPosts(mBlogName, REQUEST_OFFSET, offset, "video").subscribe(getLoadMoreObserver());
	}
}
