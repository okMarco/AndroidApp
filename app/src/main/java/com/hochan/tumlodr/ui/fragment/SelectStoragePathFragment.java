package com.hochan.tumlodr.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.databinding.LayoutSelectStoragePathBinding;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.tools.Tools;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;
import static com.hochan.tumlodr.tools.AppConfig.SHARE_PIC_STORAGE;

/**
 * .
 * Created by hochan on 2018/1/23.
 */

public class SelectStoragePathFragment extends ColdSoupDialogFragment {

	public static final String TAG = "SelectStoragePathFragment";

	LayoutSelectStoragePathBinding mViewBinding;
	String mPathDefault;
	String mPathSDCard0;
	String mPathSDCard1;

	private View.OnLongClickListener mClickToCopy = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
			// 将文本内容放到系统剪贴板里。
			if (cm != null) {
				cm.setPrimaryClip(ClipData.newPlainText(null, v == mViewBinding.rbSdcard0 ? mPathSDCard0 : mPathSDCard1));
			}
			Toast.makeText(getContext(), getString(R.string.string_copy) + (v == mViewBinding.rbSdcard0 ? mPathSDCard0 : mPathSDCard1), Toast.LENGTH_LONG).show();
			return true;
		}
	};

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setTitleString(R.string.storage_path);
		setConfirmString(R.string.save);

		mPathDefault = Tools.getDefaultPicStoragePath();
		mViewBinding.rbPathDefault.setText(getDisplayString(mPathDefault));

		String sdCard0 = Tools.getStoragePath(getContext(), false);
		if (TextUtils.isEmpty(sdCard0)) {
			mViewBinding.rbSdcard0.setVisibility(View.GONE);
		} else {
			mViewBinding.rbSdcard0.setVisibility(View.VISIBLE);
			mPathSDCard0 = sdCard0 + File.separator + AppConfig.APP_NAME;
			mViewBinding.rbSdcard0.setText(getDisplayString(mPathSDCard0));
		}

		String sdCard1 = Tools.getStoragePath(getContext(), true);
		if (TextUtils.isEmpty(sdCard1)) {
			mViewBinding.rbSdcard1.setVisibility(View.GONE);
		} else {
			mViewBinding.rbSdcard1.setVisibility(View.VISIBLE);
			mPathSDCard1 = sdCard1 + File.separator + AppConfig.APP_NAME;
			mViewBinding.rbSdcard1.setText(getDisplayString(mPathSDCard1));
		}

		if (AppConfig.mStoragePath.equals(mPathDefault)) {
			mViewBinding.rbPathDefault.setChecked(true);
		} else if (AppConfig.mStoragePath.equals(mPathSDCard0)) {
			mViewBinding.rbSdcard0.setChecked(true);
		} else if (AppConfig.mStoragePath.equals(mPathSDCard1)) {
			mViewBinding.rbSdcard1.setChecked(true);
		} else {
			mViewBinding.rbCustomPath.setChecked(true);
			mViewBinding.edCustomPath.setText(AppConfig.mStoragePath);
		}

		mViewBinding.rbPathDefault.setOnLongClickListener(mClickToCopy);
		mViewBinding.rbSdcard0.setOnLongClickListener(mClickToCopy);
		mViewBinding.rbSdcard1.setOnLongClickListener(mClickToCopy);

		mViewBinding.edCustomPath.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(s)) {
					File file = new File(s.toString());
					if (file.exists()) {
						mViewBinding.tvWrongDirector.setVisibility(View.VISIBLE);
						mViewBinding.tvWrongDirector.setText(String.format("%s %s", Tools.getSizeFormat(file.getUsableSpace()),
								getResources().getString(R.string.file_usable_size)));
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private String getDisplayString(String path) {
		File file = new File(path);
		if (!file.exists()) {
			//noinspection ResultOfMethodCallIgnored
			file.mkdirs();
		}
		return path + "\n(" + Tools.getSizeFormat(file.getUsableSpace()) + " " + getString(R.string.file_usable_size) + ")";
	}

	@Override
	public View getContentView() {
		mViewBinding = LayoutSelectStoragePathBinding.inflate(getLayoutInflater());
		return mViewBinding.getRoot();
	}

	@Override
	public boolean onConfirmed() {
		String selectedStoragePath = "";
		if (mViewBinding.rbCustomPath.isChecked()) {
			selectedStoragePath = mViewBinding.edCustomPath.getText().toString();
		} else if (mViewBinding.rbSdcard0.isChecked()) {
			selectedStoragePath = mPathSDCard0;
		} else if (mViewBinding.rbSdcard1.isChecked()) {
			selectedStoragePath = mPathSDCard1;
		} else if (mViewBinding.rbPathDefault.isChecked()) {
			selectedStoragePath = Tools.getDefaultPicStoragePath();
		}
		File file = new File(selectedStoragePath);
		if (file.exists() || file.mkdirs()) {
			SharedPreferences sharedPreferences = TumlodrApp.mContext.getSharedPreferences(SHARE_PIC_STORAGE, MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(SHARE_PIC_STORAGE, selectedStoragePath);
			AppConfig.mStoragePath = selectedStoragePath;
			if (mOnStoragePathSelectedListener != null) {
				mOnStoragePathSelectedListener.onStoragePathSelected(selectedStoragePath);
			}
			editor.apply();
			return true;
		} else {
			Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_shake);//加载动画资源文件
			getRootView().startAnimation(shake); //给组件播放动画效果
			mViewBinding.tvWrongDirector.setVisibility(View.VISIBLE);
			mViewBinding.tvWrongDirector.setText(R.string.invalid_storage_path);
			return false;
		}
	}

	@Override
	public void onCancel() {

	}

	private OnStoragePathSelectedListener mOnStoragePathSelectedListener;

	public void setOnStoragePathSelectedListener(OnStoragePathSelectedListener listener) {
		mOnStoragePathSelectedListener = listener;
	}

	interface OnStoragePathSelectedListener {
		void onStoragePathSelected(String path);
	}
}
