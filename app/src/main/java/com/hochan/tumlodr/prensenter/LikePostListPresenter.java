package com.hochan.tumlodr.prensenter;

import com.hochan.tumlodr.ui.view.IPostListView;

import static com.hochan.tumlodr.model.TumlodrService.REQUEST_OFFSET;
import static com.hochan.tumlodr.model.TumlodrService.REQUEST_AFTER;
import static com.hochan.tumlodr.model.TumlodrService.loadLikePagePosts;

/**
 * .
 * Created by hochan on 2018/1/21.
 */

public class LikePostListPresenter extends PostListPresenter {

	public LikePostListPresenter(IPostListView view) {
		super(view);
	}

	@Override
	public void refreshPostList(long sinceId) {
		loadLikePagePosts(REQUEST_AFTER, 0).subscribe(getRefreshObserver());
	}

	@Override
	public void loadMorePostList(long offset) {
		loadLikePagePosts(REQUEST_OFFSET, offset).subscribe(getLoadMoreObserver());
	}
}
