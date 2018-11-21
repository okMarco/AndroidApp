package com.hochan.tumlodr.prensenter;

import com.hochan.tumlodr.model.TumlodrService;
import com.hochan.tumlodr.ui.view.IPostListView;

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
	public void loadMorePostList(long offset) {
		TumlodrService.loadTaggedPosts(mSearchTag, offset)
				.subscribe(getLoadMoreObserver());
	}
}
