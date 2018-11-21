package com.hochan.tumlodr.util;

import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;


import io.reactivex.Observable;

/**
 * .
 * Created by hochan on 2017/10/27.
 */

public class ActivityLifecycleProvider implements LifecycleProvider<ActivityEvent>{

	private LifecycleProvider<ActivityEvent> mLifecycleProvider;

	public ActivityLifecycleProvider(LifecycleProvider<ActivityEvent> provider){
		mLifecycleProvider = provider;
	}

	@NonNull
	@Override
	public Observable<ActivityEvent> lifecycle() {
		return mLifecycleProvider.lifecycle();
	}

	@NonNull
	@Override
	public <T> LifecycleTransformer<T> bindUntilEvent(ActivityEvent event) {
		return mLifecycleProvider.bindUntilEvent(event);
	}

	@NonNull
	@Override
	public <T> LifecycleTransformer<T> bindToLifecycle() {
		return mLifecycleProvider.bindToLifecycle();
	}
}
