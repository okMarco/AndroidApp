package com.hochan.tumlodr.ui.fragment;

import android.view.View;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.glide.OkHoGlideUtil;

/**
 * .
 * Created by hochan on 2018/3/4.
 */

public class InstagramBatchSaverDialogFragment extends InstagramSaverDialogFragment{

	public static final String TAG = "InstagramBatchSaverDialogFragment";

	@Override
	public View getContentView() {
		View view = super.getContentView();
		mLayoutSaverForInsBinding.ivTip.setImageResource(R.drawable.tip_saver_batch_for_ins);
		if (OkHoGlideUtil.isContextValid(this)) {
			TumlodrGlide.with(getActivity())
					.load(R.drawable.tip_saver_batch_for_ins)
					.fitCenter()
					.skipMemoryCache(true)
					.into(mLayoutSaverForInsBinding.ivTip);
		}
		mLayoutSaverForInsBinding.tvStepFirst.setText(R.string.copy_profile_url);
		return view;
	}
}
