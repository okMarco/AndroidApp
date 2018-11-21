package com.hochan.tumlodr.util;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

/**
 * .
 * <p>
 * Created by hochan on 2018/5/30.
 */

public class SimpleCompletableObserver implements CompletableObserver {

	@Override
	public void onSubscribe(Disposable d) {

	}

	@Override
	public void onComplete() {

	}

	@Override
	public void onError(Throwable e) {
		e.printStackTrace();
	}
}
