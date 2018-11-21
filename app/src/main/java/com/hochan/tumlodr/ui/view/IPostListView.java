package com.hochan.tumlodr.ui.view;

import com.tumblr.jumblr.types.Post;

import java.util.List;

/**
 * .
 * Created by hochan on 2017/12/24.
 */

public interface IPostListView extends IBaseMvpView{

	void refreshPostComplete(List<Post> newPosts);
	void loadMorePostComplete(List<Post> morePosts);
	void refreshPosFail(Throwable throwable);
	void loadMorePostFail(Throwable throwable);
}
