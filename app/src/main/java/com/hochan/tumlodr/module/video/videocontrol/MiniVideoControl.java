package com.hochan.tumlodr.module.video.videocontrol;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.LayoutMiniVideoControlBinding;
import com.hochan.tumlodr.module.video.MiniVideoWindowManager;
import com.hochan.tumlodr.module.video.player.VideoPlayer;


/**
 * .
 * <p>
 * Created by hochan on 2018/5/29.
 */

public class MiniVideoControl extends VideoControl {

	public MiniVideoControl(@NonNull Context context) {
		super(context);
	}

	@Override
	protected int getRecourseLayout() {
		return R.layout.layout_mini_video_control;
	}

	@Override
	protected void retrieveViews(View contentView) {
		LayoutMiniVideoControlBinding viewBinding = LayoutMiniVideoControlBinding.bind(contentView);
		bottomProgress = viewBinding.bottomProgress;
		loadingProgressBar = viewBinding.loadingProgressBar;
		viewBinding.btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				VideoPlayer.getInstance().release();
				MiniVideoWindowManager.getInstance().dismiss();
			}
		});
	}

	@Override
	protected void initViews() {
		loadingProgressBar.getIndeterminateDrawable()
				.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.SRC_IN);
	}

	@Override
	public void updateProgress(int position) {
		bottomProgress.setProgress(position);
	}

	@Override
	public int getVideoControlVisibility() {
		return 0;
	}

	@Override
	public void startSeek() {

	}

	@Override
	public void seekTo(int position) {

	}

	@Override
	public void setProgress(int position) {
		bottomProgress.setProgress(position);
	}

	@Override
	public int getSeekBarProgress() {
		return 0;
	}

	@Override
	public void hideProgressBar() {

	}

	@Override
	public void setLoadingProgress(int percent) {

	}

	@Override
	public void hideLoadingIndicator() {
		loadingProgressBar.setVisibility(INVISIBLE);
	}

	@Override
	public void hide() {
		super.hide();
		bottomProgress.setVisibility(VISIBLE);
	}

	@Override
	public void show() {
		super.show();
		bottomProgress.setVisibility(VISIBLE);
	}

	@Override
	public void onPlayStateChange(int playState) {
		switch (playState) {
			case VideoPlayer.STATE_PREPARING:
			case VideoPlayer.STATE_BUFFERING_PLAYING: {
				loadingProgressBar.setVisibility(VISIBLE);
				stopSeekBarUpdate();
				hide();
				break;
			}
			case VideoPlayer.STATE_BUFFERING_PAUSED:
			case VideoPlayer.STATE_PAUSED: {
				loadingProgressBar.setVisibility(VISIBLE);
				stopSeekBarUpdate();
				break;
			}
			case VideoPlayer.STATE_PLAYING: {
				bottomProgress.setMax(VideoPlayer.getInstance().getDuration());
				loadingProgressBar.setVisibility(INVISIBLE);
				show();
				scheduleSeekBarUpdate();
				break;
			}
			case VideoPlayer.STATE_COMPLETED: {
				loadingProgressBar.setVisibility(INVISIBLE);
				stopSeekBarUpdate();
				bottomProgress.setProgress(0);
			}
		}
	}

	@Override
	public void onClick(View v) {

	}
}
