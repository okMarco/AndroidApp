package com.hochan.tumlodr.prensenter;

import com.hochan.tumlodr.ui.view.IPostListView;

import static com.hochan.tumlodr.model.TumlodrService.REQUEST_OFFSET;
import static com.hochan.tumlodr.model.TumlodrService.REQUEST_SINCE_ID;
import static com.hochan.tumlodr.model.TumlodrService.loadDashBoardPosts;
import static com.hochan.tumlodr.ui.adapter.PostAdapter.TYPE_PHOTO;
import static com.hochan.tumlodr.ui.adapter.PostAdapter.TYPE_TEXT;
import static com.hochan.tumlodr.ui.adapter.PostAdapter.TYPE_VIDEO;

/**
 * .
 * Created by hochan on 2017/12/24.
 */

public class DashBoardPostListPresenter extends PostListPresenter {

	private String mType;

	public DashBoardPostListPresenter(IPostListView view) {
		super(view);
	}

	@Override
	public void refreshPostList(long sinceId) {
		loadDashBoardPosts(REQUEST_SINCE_ID, sinceId, mType).subscribe(getRefreshObserver());
	}

	@Override
	public void loadMorePostList(long offset) {
		loadDashBoardPosts(REQUEST_OFFSET, offset, mType).subscribe(getLoadMoreObserver());
	}

	public void showAll() {
		mType = null;
	}

	public void showOnlyPicture() {
		mType = TYPE_PHOTO;
	}

	public void showOnlyVideo() {
		mType = TYPE_VIDEO;
	}

	public void showOnlyText() {
		mType = TYPE_TEXT;
	}
}
