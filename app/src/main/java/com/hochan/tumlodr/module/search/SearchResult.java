package com.hochan.tumlodr.module.search;

import com.tumblr.jumblr.types.Post;

import java.util.List;

/**
 * .
 * Created by hochan on 2018/6/23.
 */

public class SearchResult {

	List<SearchBlogInfo> mSearchBlogInfoList;
	List<Post> mSearchPostList;
	String mFormKey;

	public List<SearchBlogInfo> getSearchBlogInfoList() {
		return mSearchBlogInfoList;
	}

	public void setSearchBlogInfoList(List<SearchBlogInfo> searchBlogInfoList) {
		mSearchBlogInfoList = searchBlogInfoList;
	}

	public List<Post> getSearchPostList() {
		return mSearchPostList;
	}

	public void setSearchPostList(List<Post> searchPostList) {
		mSearchPostList = searchPostList;
	}

	public String getFormKey() {
		return mFormKey;
	}

	public void setFormKey(String formKey) {
		mFormKey = formKey;
	}

}
