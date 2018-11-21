package com.hochan.tumlodr.prensenter;

import com.hochan.tumlodr.ui.view.IPostListView;

import static com.hochan.tumlodr.model.TumlodrService.REQUEST_OFFSET;
import static com.hochan.tumlodr.model.TumlodrService.loadBlogLikePosts;

/**
 * .
 * Created by hochan on 2018/1/21.
 */

public class BlogLikePostListPresenter extends PostListPresenter {

	private String mBlogName;

	public BlogLikePostListPresenter(IPostListView view, String blogName) {
		super(view);
		mBlogName = blogName;
	}

	@Override
	public void refreshPostList(long sinceId) {
		loadBlogLikePosts(mBlogName, REQUEST_OFFSET, 0).subscribe(getRefreshObserver());
	}

	@Override
	public void loadMorePostList(long offset) {
		loadBlogLikePosts(mBlogName, REQUEST_OFFSET, offset).subscribe(getLoadMoreObserver());
	}
}
