package com.hochan.tumlodr.prensenter;

import com.hochan.tumlodr.jumblr.types.Post;
import com.hochan.tumlodr.ui.view.IPostListView;

import java.util.List;

import static com.hochan.tumlodr.model.TumlodrService.REQUEST_BEFORE_ID;
import static com.hochan.tumlodr.model.TumlodrService.REQUEST_OFFSET;
import static com.hochan.tumlodr.model.TumlodrService.REQUEST_SINCE_ID;
import static com.hochan.tumlodr.model.TumlodrService.loadDashBoardPosts;

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
	public void loadMorePostList(List<Post> posts) {
		long beforeId = 0;
		if (posts != null && posts.size() > 0) {
			beforeId = posts.get(posts.size() - 1).getId();
		}
		loadDashBoardPosts(REQUEST_BEFORE_ID, beforeId, mType).subscribe(getLoadMoreObserver());
	}

	public void showAll() {
		mType = null;
	}

	public void showOnlyPicture() {
		mType = Post.PostType.PHOTO.getValue();
	}

	public void showOnlyVideo() {
		mType = Post.PostType.VIDEO.getValue();
	}

	public void showOnlyText() {
		mType = Post.PostType.TEXT.getValue();
	}
}
