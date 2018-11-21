package com.hochan.tumlodr.ui.viewholder;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hochan.tumlodr.ui.activity.baseactivity.BaseViewBindingActivity;

/**
 * .
 * Created by hochan on 2018/6/23.
 */

public class BaseViewBindingViewHolder<VB extends ViewDataBinding> extends RecyclerView.ViewHolder {

	public VB mViewBinding;

	public BaseViewBindingViewHolder(VB viewBinding) {
		super(viewBinding.getRoot());
		mViewBinding = viewBinding;
	}
}
