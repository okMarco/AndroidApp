package com.hochan.tumlodr.ui.fragment;

import android.view.View;
import android.widget.CheckBox;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.FragmentDeleteDialogBinding;

/**
 * .
 * Created by hochan on 2017/11/6.
 */

public class DeleteDialogFragment extends ColdSoupDialogFragment {

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
	public void onCancel() {}

	public void setDownloadTaskDeleteListener(OnDownloadTaskDeleteListener downloadTaskDeleteListener) {
		mDownloadTaskDeleteListener = downloadTaskDeleteListener;
	}

	public interface OnDownloadTaskDeleteListener {
		void delete(boolean deleteFile);
	}
}
