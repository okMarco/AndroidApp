package com.hochan.tumlodr.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.databinding.FragSettingBinding;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.ui.activity.ChangeApiActivity;
import com.hochan.tumlodr.ui.activity.Router;
import com.hochan.tumlodr.ui.activity.SplashActivity;
import com.hochan.tumlodr.ui.fragment.base.BaseFragment;
import com.hochan.tumlodr.util.Events;
import com.hochan.tumlodr.util.RxBus;

import static com.hochan.tumlodr.ui.activity.SplashActivity.REQUEST_CODE_CHANGE_API;

/**
 * .
 * Created by hochan on 2016/8/29.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final	String TEEHUB_URL = "https://play.google.com/store/apps/details?id=com.okmarco.teehub";


    private FragSettingBinding mViewBinding;
	private SelectStoragePathFragment mSelectStoragePathFragment;

	@Override
	public int getLayoutRecourseId() {
		return R.layout.frag_setting;
	}

	@Override
	public void bindView(View rootView) {
		mViewBinding = FragSettingBinding.bind(rootView);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mViewBinding.swNightMode.setChecked(!AppUiConfig.sIsLightTheme);
		mViewBinding.swNightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				AppUiConfig.setTumlodrTheme(getContext(), isChecked ? R.style.AppTheme_NoActionBar_Dark : R.style.AppTheme_NoActionBar_White);
				SharedPreferences spTheme = getActivity().getSharedPreferences(AppConfig.SHARE_THEME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = spTheme.edit();
				editor.putInt(AppConfig.SHARE_THEME_ID, isChecked ? R.style.AppTheme_NoActionBar_Dark : R.style.AppTheme_NoActionBar_White);
				RxBus.getInstance().send(new Events<>(Events.EVENT_CHANGE_THEME, null));
				editor.apply();
				goToDashboard();
			}
		});

		initTextColor();
		mViewBinding.llSettingContainerTop.setBackgroundColor(AppUiConfig.sThemeColor);
		mViewBinding.llSettingContainerBottom.setBackgroundColor(AppUiConfig.sThemeColor);

		if (AppConfig.sPostListLayoutStyle == AppConfig.LAYOUT_DETAIL) {
			mViewBinding.rbLayoutDetail.setChecked(true);
		} else {
			mViewBinding.rbLayoutWaterfall.setChecked(true);
		}

		if (AppUiConfig.sIsLightTheme) {
			mViewBinding.rbLayoutDetail.setButtonDrawable(R.drawable.bg_dark_checkbox);
			mViewBinding.rbLayoutWaterfall.setButtonDrawable(R.drawable.bg_dark_checkbox);
		} else {
			mViewBinding.rbLayoutDetail.setButtonDrawable(R.drawable.bg_white_checkbox);
			mViewBinding.rbLayoutWaterfall.setButtonDrawable(R.drawable.bg_white_checkbox);
		}

		mViewBinding.rgLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (getActivity() == null) {
					return;
				}
				SharedPreferences spTheme = getActivity().getSharedPreferences(AppConfig.SHARE_THEME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = spTheme.edit();
				if (checkedId == mViewBinding.rbLayoutDetail.getId()) {
					AppConfig.sPostListLayoutStyle = AppConfig.LAYOUT_DETAIL;

				} else {
					AppConfig.sPostListLayoutStyle = AppConfig.LAYOUT_WATER_FALL;
				}
				RxBus.getInstance().send(new Events<>(Events.EVENT_UPDATE_LAYOUT_STYLE, null));
				editor.putInt(AppConfig.SHARE_POST_LIST_LAYOUT_STYLE, AppConfig.sPostListLayoutStyle);
				editor.apply();
			}
		});

		mViewBinding.llStoragePath.setOnClickListener(this);

		initSettingColumn();

		initSettingResolution();

		mViewBinding.spSetColumn.setSelection(AppConfig.mPostListColumnCount - 1);
		mViewBinding.spSetResolution.setSelection(AppConfig.mResolution);

		LinearLayout llSetLogout = view.findViewById(R.id.ll_set_logout);
		llSetLogout.setOnClickListener(this);

		mViewBinding.swSetVideoPlay.setChecked(AppConfig.sPlayVideoAuto);
		mViewBinding.swSetVideoPlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				if (getActivity() == null) {
					return;
				}
				AppConfig.sPlayVideoAuto = isChecked;
				SharedPreferences spTheme = getActivity().getSharedPreferences(AppConfig.SHARE_THEME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = spTheme.edit();
				editor.putBoolean(AppConfig.SHARE_PLAY_VIDEO_AUTO, isChecked);
				editor.apply();
			}
		});

		mViewBinding.swForceToChangeUrl.setChecked(AppConfig.sForceStitchingVideoUrl);
		mViewBinding.swForceToChangeUrl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (getActivity() == null) {
					return;
				}
				AppConfig.sForceStitchingVideoUrl = isChecked;
				SharedPreferences spTheme = getActivity().getSharedPreferences(AppConfig.SHARE_THEME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = spTheme.edit();
				editor.putBoolean(AppConfig.SHARE_FORCE_STITCHING_VIDEO_URL, isChecked);
				editor.apply();
			}
		});

		mViewBinding.tvSelectedStoragePath.setText(AppConfig.mStoragePath);

		mViewBinding.tvChangeApi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivityForResult(new Intent(getActivity(), ChangeApiActivity.class), REQUEST_CODE_CHANGE_API);
			}
		});

		mViewBinding.llTeehub.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(TEEHUB_URL);
                intent.setData(content_url);
                startActivity(intent);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void initSettingResolution() {
		if (getContext() == null) {
			return;
		}
		mViewBinding.spSetResolution.setAdapter(new ArrayAdapter(getContext(), AppUiConfig.sSpinnerTextLayout, getResources().getStringArray(R.array.resolution)));
		mViewBinding.spSetResolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (getActivity() == null) {
					return;
				}
				SharedPreferences spTheme = getActivity().getSharedPreferences(AppConfig.SHARE_THEME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = spTheme.edit();
				AppConfig.mResolution = position;
				editor.putInt(AppConfig.SHARE_RESOLUTION, position);
				editor.apply();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	@SuppressWarnings({"unchecked", "ConstantConditions"})
	private void initSettingColumn() {
		mViewBinding.spSetColumn.setAdapter(new ArrayAdapter(getContext(),
				AppUiConfig.sSpinnerTextLayout, getResources().getStringArray(R.array.column)));
		mViewBinding.spSetColumn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				SharedPreferences spTheme = getActivity().getSharedPreferences(AppConfig.SHARE_THEME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = spTheme.edit();
				AppConfig.mPostListColumnCount = position + 1;
				RxBus.getInstance().send(new Events<>(Events.EVENT_UPDATE_COLUMN_COUNT, null));
				editor.putInt(AppConfig.SHARE_POST_LIST_COLUMN_COUNT, position + 1);
				editor.apply();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	private void initTextColor() {
		mViewBinding.tvChosseTheme.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.tvSetColumn.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.tvSetVideoPaly.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.tvSetLogout.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.tvSetResolution.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.tvSetLayout.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.tvForceToChangeUrl.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.tvForceToChangeUrlTip.setTextColor(AppUiConfig.sSubTextColor);
		mViewBinding.tvChangeApi.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.tvTeehub.setTextColor(AppUiConfig.sTextColor);

		mViewBinding.rbLayoutWaterfall.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.rbLayoutDetail.setTextColor(AppUiConfig.sTextColor);

		mViewBinding.tvSelectedStoragePath.setTextColor(AppUiConfig.sSubTextColor);
		mViewBinding.tvStoragePath.setTextColor(AppUiConfig.sTextColor);
	}

	@Override
	public void onClick(View v) {
		if (getActivity() == null) {
			return;
		}
		SharedPreferences spTheme = getActivity().getSharedPreferences(AppConfig.SHARE_THEME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spTheme.edit();

		switch (v.getId()) {
			case R.id.ll_set_logout: {
				logout();
				break;
			}
			case R.id.ll_storage_path: {
				if (mSelectStoragePathFragment == null) {
					mSelectStoragePathFragment = new SelectStoragePathFragment();
					mSelectStoragePathFragment.setOnStoragePathSelectedListener(new SelectStoragePathFragment.OnStoragePathSelectedListener() {
						@Override
						public void onStoragePathSelected(String path) {
							mViewBinding.tvSelectedStoragePath.setText(path);
						}
					});
				}
				if (mSelectStoragePathFragment.isAdded()) {
					return;
				}
				mSelectStoragePathFragment.show(getActivity().getSupportFragmentManager(), SelectStoragePathFragment.TAG);
			}
		}
		editor.apply();
	}

	private void goToDashboard() {
		mViewBinding.llSettingContainerTop.postDelayed(new Runnable() {
			@Override
			public void run() {
				Router.showDashboard(getActivity());
			}
		}, 200);
	}

	private void logout() {
		SharedPreferences sharedPreferences = TumlodrApp.mContext
				.getSharedPreferences(AppConfig.SHARE_USER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.apply();
		startActivity(new Intent(getContext(), SplashActivity.class));
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (getActivity() == null) {
			return;
		}
		SharedPreferences spTheme = getActivity().getSharedPreferences(AppConfig.SHARE_THEME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spTheme.edit();

		switch (view.getId()) {
			case R.id.sp_set_column: {
				AppConfig.mPostListColumnCount = position + 1;
				editor.putInt(AppConfig.SHARE_POST_LIST_COLUMN_COUNT, position + 1);
				break;
			}
			case R.id.sp_set_resolution: {
				AppConfig.mResolution = position;
				editor.putInt(AppConfig.SHARE_RESOLUTION, position);
				break;
			}
		}
		editor.apply();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_CHANGE_API) {
			if (data != null) {
				String consumerKey = data.getStringExtra(ChangeApiActivity.EXTRA_CONSUMER_KEY);
				String secretKey = data.getStringExtra(ChangeApiActivity.EXTRA_SECRET_KEY);
				if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(secretKey)) {
					return;
				}
				if (getActivity() != null) {
					startActivity(new Intent(getActivity(), SplashActivity.class));
					getActivity().finish();
				}
			}
		}
	}
}
