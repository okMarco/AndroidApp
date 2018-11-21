package com.hochan.tumlodr.prensenter;

import com.hochan.tumlodr.ui.view.IPostListView;

import static com.hochan.tumlodr.model.TumlodrService.REQUEST_OFFSET;
import static com.hochan.tumlodr.model.TumlodrService.loadBlogPosts;

/**
 * .
 * Created by hochan on 2018/1/21.
 */

public class BlogPostListPresenter extends PostListPresenter {

	protected String mBlogName;

	public BlogPostListPresenter(IPostListView view, String blogName) {
		super(view);
		mBlogName = blogName;
	}

	@Override
	public void refreshPostList(long since) {
		loadBlogPosts(mBlogName, REQUEST_OFFSET, 0, null).subscribe(getRefreshObserver());
	}

	@Override
	public void loadMorePostList(long offset) {
		loadBlogPosts(mBlogName, REQUEST_OFFSET, offset, null).subscribe(getLoadMoreObserver());
	}
}
