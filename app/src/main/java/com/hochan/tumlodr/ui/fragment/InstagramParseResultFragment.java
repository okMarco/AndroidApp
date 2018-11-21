package com.hochan.tumlodr.ui.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.LayoutSaverForInsBinding;
import com.hochan.tumlodr.model.data.InstagramDownloadInfo;
import com.hochan.tumlodr.model.data.download.DownloadRecord;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.tools.InstagramParse;
import com.hochan.tumlodr.util.FileDownloadUtil;

import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * .
 * Created by hochan on 2018/2/10.
 */

public class InstagramParseResultFragment extends InstagramSaverDialogFragment {

	public static final String TAG = "InstagramParseResultFragment";

	private String mPostUrl;
	private List<InstagramDownloadInfo> mDownloadInfos;

	@Override
	public View getContentView() {
		mLayoutSaverForInsBinding = LayoutSaverForInsBinding.inflate(getLayoutInflater());
		mLayoutSaverForInsBinding.tvStepSecond.setTextColor(AppUiConfig.sTextColor);
		mLayoutSaverForInsBinding.tvStepFirst.setTextColor(AppUiConfig.sTextColor);
		return mLayoutSaverForInsBinding.getRoot();
	}


	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mLayoutSaverForInsBinding.tvStepFirst.setVisibility(View.GONE);
		mLayoutSaverForInsBinding.tvStepSecond.setVisibility(View.GONE);
		mLayoutSaverForInsBinding.ivTip.setImageDrawable(null);
		mLayoutSaverForInsBinding.loadingProgressBar.getIndeterminateDrawable()
				.setColorFilter(AppUiConfig.sTextColor, PorterDuff.Mode.SRC_IN);
		mLayoutSaverForInsBinding.loadingProgressBar.setVisibility(View.VISIBLE);
		setConfirmString(R.string.save);
		setCancleString(R.string.delete_download_task_grament_cancel);
		mViewBinding.btnDelete.setEnabled(false);
		io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
			@Override
			public void subscribe(ObservableEmitter<String> e) throws Exception {
				mDownloadInfos = InstagramParse.parsePostByUrl(mPostUrl);
				if (mDownloadInfos == null || mDownloadInfos.size() == 0) {
					e.onError(new Throwable());
				} else {
					e.onNext(mDownloadInfos.get(0).getThumbnailUrl());
				}
			}
		}).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
				.subscribe(new Consumer<String>() {
					@Override
					public void accept(String url) throws Exception {
						mViewBinding.btnDelete.setEnabled(true);
						mLayoutSaverForInsBinding.loadingProgressBar.setVisibility(View.GONE);
						TumlodrGlide.with(getActivity()).load(url)
								.fitCenter()
								.skipMemoryCache(true)
								.into(mLayoutSaverForInsBinding.ivTip);
						mLayoutSaverForInsBinding.tvDownloadCount.setVisibility(View.VISIBLE);
						mLayoutSaverForInsBinding.tvDownloadCount.setText(String.valueOf(mDownloadInfos.size()));
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) throws Exception {
						mLayoutSaverForInsBinding.tvParseFail.setVisibility(View.VISIBLE);
					}
				});
	}

	@Override
	public boolean onConfirmed() {
		if (mDownloadInfos != null && mDownloadInfos.size() > 0) {
			for (InstagramDownloadInfo downloadInfo : mDownloadInfos) {
				if (downloadInfo.getType().equals(DownloadRecord.TYPE_VIDEO)) {
					FileDownloadUtil.addInstagramVideoDownload(downloadInfo.mThumbnailUrl, downloadInfo.mUrl, DownloadRecord.GROUP_INSTAGRAM);
				} else {
					FileDownloadUtil.addInstagramPicDownload(downloadInfo.mUrl, DownloadRecord.GROUP_INSTAGRAM);
				}
			}
		}
		return true;
	}

	public void setUrl(String url) {
		mPostUrl = url;
	}
}
