package com.hochan.tumlodr.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.FragmentDeleteDialogBinding;
import com.hochan.tumlodr.tools.ScreenTools;

/**
 * .
 * Created by hochan on 2017/11/6.
 */

public class DeleteDialogFragment extends TumloadrDialogFragment {

	private OnDownloadTaskDeleteListener mDownloadTaskDeleteListener;

	@Override
	public View getContentView() {
		return FragmentDeleteDialogBinding.inflate(getLayoutInflater()).getRoot();
	}

	@Override
	public boolean onConfirmed() {
		if (mDownloadTaskDeleteListener != null) {
			CheckBox checkBox = (CheckBox) findViewById(R.id.rbtn_delete_file);
			mDownloadTaskDeleteListener.delete(checkBox.isChecked());
		}
		return true;
	}

	@Override
	public void onCancel() {

	}

	public void setDownloadTaskDeleteListener(OnDownloadTaskDeleteListener downloadTaskDeleteListener) {
		mDownloadTaskDeleteListener = downloadTaskDeleteListener;
	}

	public interface OnDownloadTaskDeleteListener {
		void delete(boolean deleteFile);
	}
}
