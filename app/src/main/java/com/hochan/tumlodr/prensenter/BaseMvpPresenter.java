package com.hochan.tumlodr.prensenter;

import android.os.Bundle;

import com.hochan.tumlodr.ui.view.IBaseMvpView;

/**
 * .
 * Created by hochan on 2017/12/24.
 */

public abstract class BaseMvpPresenter<T extends IBaseMvpView> {

	public T mView;

	public BaseMvpPresenter(T view){
		mView = view;
	}

	public abstract void initData(Bundle bundle);

	public void releasePresenter() {
		mView = null;
	}
}
