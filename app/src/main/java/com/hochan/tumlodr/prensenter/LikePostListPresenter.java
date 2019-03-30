package com.hochan.tumlodr.prensenter;

import com.hochan.tumlodr.jumblr.types.Post;
import com.hochan.tumlodr.ui.view.IPostListView;

import java.util.List;

import static com.hochan.tumlodr.model.TumlodrService.REQUEST_BEFORE;
import static com.hochan.tumlodr.model.TumlodrService.REQUEST_BEFORE_ID;
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
	public void loadMorePostList(List<Post> posts) {
		long before = 0;
		if (posts != null && posts.size() > 0) {
			before = posts.get(posts.size() - 1).getLikedTimestamp();
		}
		loadLikePagePosts(REQUEST_BEFORE, before).subscribe(getLoadMoreObserver());
	}
}
