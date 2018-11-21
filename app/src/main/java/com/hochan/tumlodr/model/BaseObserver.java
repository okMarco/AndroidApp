package com.hochan.tumlodr.model;


import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * .
 * Created by hochan on 2017/12/2.
 */

public class BaseObserver<T> implements Observer<T> {

	@Override
	public void onSubscribe(@NonNull Disposable d) {

	}

	@Override
	public void onNext(@NonNull T t) {

	}

	@Override
	public void onError(@NonNull Throwable e) {
		e.printStackTrace();
	}

	@Override
	public void onComplete() {

	}
}
