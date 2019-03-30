package com.hochan.tumlodr.prensenter;

import com.hochan.tumlodr.jumblr.types.Post;
import com.hochan.tumlodr.model.TumlodrService;
import com.hochan.tumlodr.ui.view.IPostListView;

import java.util.List;

/**
 * .
 * Created by hochan on 2018/6/20.
 */

public class SearchPostPresenter extends PostListPresenter {

	private String mSearchTag;

	public SearchPostPresenter(IPostListView view) {
		super(view);
	}

	@Override
	public void refreshPostList(long sinceId) {
	}

	public void search(String tag) {
		mSearchTag = tag;
		TumlodrService.loadTaggedPosts(mSearchTag, 0)
				.subscribe(getRefreshObserver());
	}

	@Override
	public void loadMorePostList(List<Post> posts) {
		for (int i = posts.size() - 1; i >= 0; i--) {
			if (posts.get(i) != null) {
				TumlodrService.loadTaggedPosts(mSearchTag, posts.get(i).getTimestamp())
						.subscribe(getLoadMoreObserver());
				return;
			}
		}
	}
}
