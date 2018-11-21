package com.hochan.tumlodr.prensenter;

import com.hochan.tumlodr.ui.view.IPostListView;

import static com.hochan.tumlodr.model.TumlodrService.REQUEST_OFFSET;
import static com.hochan.tumlodr.model.TumlodrService.REQUEST_SINCE_ID;
import static com.hochan.tumlodr.model.TumlodrService.loadDashBoardPosts;

/**
 * .
 * Created by hochan on 2018/1/25.
 */

public class VideoPostListPresenter extends PostListPresenter {

	private String mBlogName;

	public VideoPostListPresenter(IPostListView view) {
		super(view);
	}

	@Override
	public void refreshPostList(long sinceId) {
		loadDashBoardPosts(REQUEST_SINCE_ID, sinceId, "video").subscribe(getRefreshObserver());
	}

	@Override
	public void loadMorePostList(long offset) {
		loadDashBoardPosts(REQUEST_OFFSET, offset, "video").subscribe(getLoadMoreObserver());
	}
}
