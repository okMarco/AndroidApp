package com.hochan.tumlodr.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.LayoutSaverForInsBinding;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.glide.OkHoGlideUtil;
import com.hochan.tumlodr.tools.AppUiConfig;

/**
 * .
 * Created by hochan on 2018/2/7.
 */

public class InstagramSaverDialogFragment extends ColdSoupDialogFragment {

	public static final String TAG = "InstagramSaverDialogFragment";
	public LayoutSaverForInsBinding mLayoutSaverForInsBinding;

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		setTitleString(R.string.saver_tools_for_ins);
		setCancelString(R.string.saver_tools_ok);
		setConfirmString(R.string.saver_tools_have_a_try);
		viewBinding.llRootContainer.setBackgroundColor(AppUiConfig.sThemeColor);
		viewBinding.tvTitle.setTextColor(AppUiConfig.sTextColor);
		viewBinding.btnDelete.setTextColor(AppUiConfig.sTextColor);
		viewBinding.btnCancle.setTextColor(AppUiConfig.sTextColor);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public View getContentView() {
		mLayoutSaverForInsBinding = LayoutSaverForInsBinding.inflate(getLayoutInflater());
		mLayoutSaverForInsBinding.tvStepSecond.setTextColor(AppUiConfig.sTextColor);
		mLayoutSaverForInsBinding.tvStepFirst.setTextColor(AppUiConfig.sTextColor);

		if (OkHoGlideUtil.isContextValid(this)) {
			TumlodrGlide.with(getActivity())
					.load(R.drawable.tip_saver_for_ins)
					.fitCenter()
					.skipMemoryCache(true)
					.into(mLayoutSaverForInsBinding.ivTip);
		}
		return mLayoutSaverForInsBinding.getRoot();
	}

	@Override
	public boolean onConfirmed() {
		try {
			if (getActivity() != null) {
				Intent intent = new Intent();
				intent.setPackage("com.instagram.android");
				getActivity().startActivity(intent);
			}
		} catch (Exception ignored) {

		}

		return true;
	}

	@Override
	public void onCancel() {
	}
}
