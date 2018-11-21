package com.hochan.tumlodr.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.FragmentSaverToolsBinding;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.ui.activity.InstagramParseActivity;
import com.hochan.tumlodr.ui.fragment.base.BaseFragment;

/**
 * .
 * Created by hochan on 2018/2/7.
 */

public class SaverToolsFragment extends BaseFragment implements View.OnClickListener {

	private FragmentSaverToolsBinding mViewBinding;
	private InstagramSaverDialogFragment mInstagramSaverDialogFragment;
	private InstagramBatchSaverDialogFragment mInstagramBatchSaverDialogFragment;

	@Override
	public int getLayoutRecourseId() {
		return R.layout.fragment_saver_tools;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mViewBinding = FragmentSaverToolsBinding.bind(view);

		mViewBinding.edtvSaver.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.edtvSaver.setHintTextColor(AppUiConfig.sSubTextColor);
		mViewBinding.edtvSaverInBatch.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.edtvSaverInBatch.setTextColor(AppUiConfig.sSubTextColor);
		mViewBinding.btnGetPostOfLink.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.btnGetPostOfLink.setBackgroundColor(AppUiConfig.sThemeColor);
		mViewBinding.btnGetBlogOfLink.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.btnGetBlogOfLink.setBackgroundColor(AppUiConfig.sThemeColor);


		Button btnSaverForIns = view.findViewById(R.id.btn_saver_for_instagram);
		btnSaverForIns.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mInstagramSaverDialogFragment == null) {
					mInstagramSaverDialogFragment = new InstagramSaverDialogFragment();
				}
				if (getActivity() != null) {
					mInstagramSaverDialogFragment.show(getActivity().getSupportFragmentManager(), InstagramSaverDialogFragment.TAG);
				}
			}
		});

		mViewBinding.btnSaverForInstagram.setOnClickListener(this);
		mViewBinding.btnSaverForInstagramBatch.setOnClickListener(this);

		mViewBinding.btnGetPostOfLink.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TextUtils.isEmpty(mViewBinding.edtvSaver.getText())) {
					Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_shake);//加载动画资源文件
					view.startAnimation(shake);
					return;
				}
				if (getActivity() != null) {
					InstagramParseResultFragment resultFragment = new InstagramParseResultFragment();
					resultFragment.setUrl(mViewBinding.edtvSaver.getText().toString());
					resultFragment.show(getActivity().getSupportFragmentManager(), null);
				}
			}
		});

		mViewBinding.btnGetBlogOfLink.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TextUtils.isEmpty(mViewBinding.edtvSaver.getText())) {
					Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_shake);//加载动画资源文件
					view.startAnimation(shake);
					return;
				}
				if (getActivity() != null) {
					Intent intent = new Intent(getActivity(), InstagramParseActivity.class);
					intent.putExtra(InstagramParseActivity.EXTRA_INSTAGRAM_BLOG_URL, mViewBinding.edtvSaver.getText().toString());
					getActivity().startActivity(intent);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (getActivity() == null) {
			return;
		}
		if (v.getId() == mViewBinding.btnSaverForInstagram.getId()) {
			if (mInstagramSaverDialogFragment == null) {
				mInstagramSaverDialogFragment = new InstagramSaverDialogFragment();
			}
			mInstagramSaverDialogFragment.show(getActivity().getSupportFragmentManager(), InstagramSaverDialogFragment.TAG);
		} else if (v.getId() == mViewBinding.btnSaverForInstagramBatch.getId()) {
			if (mInstagramBatchSaverDialogFragment == null) {
				mInstagramBatchSaverDialogFragment = new InstagramBatchSaverDialogFragment();
			}
			mInstagramBatchSaverDialogFragment.show(getActivity().getSupportFragmentManager(), InstagramBatchSaverDialogFragment.TAG);
		}
	}
}
