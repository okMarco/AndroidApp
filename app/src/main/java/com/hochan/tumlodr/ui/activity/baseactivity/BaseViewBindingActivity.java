package com.hochan.tumlodr.ui.activity.baseactivity;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;

/**
 * .
 * Created by hochan on 2018/6/18.
 */

public abstract class BaseViewBindingActivity<VB extends ViewDataBinding> extends BaseAppUiActivity {

	protected VB viewBinding;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewBinding = DataBindingUtil.inflate(getLayoutInflater(), getLayoutResourceId(), null, false);
		setContentView(viewBinding.getRoot());
		initData();
		initWidget();
	}

	public abstract int getLayoutResourceId();

	public void initData() {}

	public void initWidget() {}
}
