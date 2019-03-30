package com.hochan.tumlodr.model.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.hochan.tumlodr.jumblr.types.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 * Created by hochan on 2017/12/24.
 */

public class PostListViewModel extends ViewModel {

	private MutableLiveData<List<Post>> mCircularArrayLiveData = new MutableLiveData<>();

	public PostListViewModel() {
		mCircularArrayLiveData.setValue(new ArrayList<Post>());
	}

	public MutableLiveData<List<Post>> getDashBoardPostList() {
		return mCircularArrayLiveData;
	}
}
