package com.hochan.tumlodr.ui.activity.baseactivity;

import android.support.v4.app.Fragment;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.ActivityBaseSingleFragmentBinding;

/**
 * .
 * Created by hochan on 2018/7/29.
 */

public abstract class BaseSingleFragmentActivity extends BaseViewBindingActivity<ActivityBaseSingleFragmentBinding> {

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_base_single_fragment;
	}

	@Override
	public void initWidget() {
		Fragment fragment = getContentFragment(R.id.fragment_container);
		if (fragment != null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, fragment)
					.commit();
		}
	}

	public abstract Fragment getContentFragment(int id);
}
