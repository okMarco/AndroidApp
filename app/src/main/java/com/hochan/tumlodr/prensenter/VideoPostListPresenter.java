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
	public void loadMorePostList(List<Post> posts) {
		long before = 0;
		if (posts != null && posts.size() > 0) {
			before = posts.get(posts.size() - 1).getId();
		}
		loadDashBoardPosts(REQUEST_BEFORE_ID, before, "video").subscribe(getLoadMoreObserver());
	}
}
