package com.hochan.tumlodr.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hochan.tumlodr.prensenter.BaseMvpPresenter;

/**
 * .
 * Created by hochan on 2017/12/24.
 */

public abstract class BaseMvpFragment<T extends BaseMvpPresenter> extends BaseFragment{

	public T mPresenter;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPresenter = initPresenter();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mPresenter != null) {
			mPresenter.initData(getArguments());
		}
	}

	public abstract T initPresenter();

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mPresenter != null) {
			mPresenter.releasePresenter();
		}
	}
}
