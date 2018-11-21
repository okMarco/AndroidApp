package com.hochan.tumlodr.util;

import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.FragmentEvent;


import io.reactivex.Observable;

/**
 * .
 * Created by hochan on 2017/10/27.
 */

public class FragmentLifecycleProvider implements LifecycleProvider<FragmentEvent> {

	private LifecycleProvider<FragmentEvent> mFragmentLifecycleProvider;

	public FragmentLifecycleProvider(LifecycleProvider<FragmentEvent> provider) {
		mFragmentLifecycleProvider = provider;
	}

	@NonNull
	@Override
	public Observable<FragmentEvent> lifecycle() {
		return mFragmentLifecycleProvider.lifecycle();
	}

	@NonNull
	@Override
	public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
		return mFragmentLifecycleProvider.bindUntilEvent(event);
	}

	@NonNull
	@Override
	public <T> LifecycleTransformer<T> bindToLifecycle() {
		return mFragmentLifecycleProvider.bindToLifecycle();
	}
}
