package com.hochan.tumlodr.module.video.videocontrol;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.LayoutDefaultVideoControlBinding;
import com.hochan.tumlodr.module.video.player.VideoPlayer;

import java.util.Locale;


/**
 * .
 * <p>
 * Created by hochan on 2018/5/24.
 */

public class DefaultVideoControl extends VideoControl {

	private boolean mShowBottomProgress = true;

	public DefaultVideoControl(@NonNull Context context) {
		super(context);
	}

	public DefaultVideoControl(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public DefaultVideoControl(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected int getRecourseLayout() {
		return R.layout.layout_default_video_control;
	}

	@Override
	protected void retrieveViews(View contentView) {
		LayoutDefaultVideoControlBinding viewBinding = LayoutDefaultVideoControlBinding.bind(contentView);
		tvChangePosition = viewBinding.tvChangePosition;
		tvProgress = viewBinding.tvProgress;
		tvDuration = viewBinding.tvDuration;
		videoSeekBar = viewBinding.videoSeekBar;
		bottomProgress = viewBinding.bottomProgress;
		llLoadingProgressBar = viewBinding.llLoadingProgress;
		loadingProgressBar = viewBinding.loadingProgressBar;
		btnPlayAndPause = viewBinding.btnPlay;
		btnSmallPlayPause = viewBinding.btnSmallPlayPause;
		llProgressLayout = viewBinding.llProgressLayout;
		btnRotate = viewBinding.btnRotate;
		btnFullScreen = viewBinding.btnFullscreen;
		tvLoadingProgress = viewBinding.tvLoadingProgress;
	}

	@Override
	protected void initViews() {
		btnPlayAndPause.setOnClickListener(this);
		btnSmallPlayPause.setOnClickListener(this);

		videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setProgress(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				startSeek();
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				seekTo(seekBar.getProgress());
			}
		});

		loadingProgressBar.getIndeterminateDrawable()
				.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.SRC_IN);
	}

	@Override
	public void updateProgress(int currentPosition) {
		bottomProgress.setProgress(currentPosition);
		videoSeekBar.setProgress(currentPosition);
		tvProgress.setText(DateUtils.formatElapsedTime(currentPosition / 1000));

		videoSeekBar.setSecondaryProgress(VideoPlayer.getInstance().getBufferedPosition());
		bottomProgress.setSecondaryProgress(VideoPlayer.getInstance().getBufferedPosition());
	}

	private void toggleProgressLayoutVisibility(boolean visible) {
		mHandler.removeCallbacks(mHideProgressLayoutDelay);
		llProgressLayout.setVisibility(visible ? VISIBLE : GONE);
		if (mShowBottomProgress) {
			bottomProgress.setVisibility(visible ? INVISIBLE : VISIBLE);
		} else {
			bottomProgress.setVisibility(INVISIBLE);
		}
		if (visible && !mIsSeeking) {
			mHandler.postDelayed(mHideProgressLayoutDelay, HIDE_PROGRESS_LAYOUT_DELAY);
		}
	}

	@SuppressWarnings("unused")
	public void setShowBottomProgress(boolean showBottomProgress) {
		mShowBottomProgress = showBottomProgress;
	}

	@Override
	public void hideProgressBar() {
		llProgressLayout.setVisibility(INVISIBLE);
		bottomProgress.setVisibility(INVISIBLE);
		mHandler.removeCallbacks(mHideProgressLayoutDelay);
	}

	@Override
	public void setLoadingProgress(int percent) {
		tvLoadingProgress.setText(String.format(Locale.US, "%d%%", percent));
	}

	@Override
	public void hideLoadingIndicator() {
		llLoadingProgressBar.setVisibility(INVISIBLE);
	}

	@Override
	public int getVideoControlVisibility() {
		return llProgressLayout.getVisibility();
	}

	@Override
	public void startSeek() {
		mIsSeeking = true;
		stopSeekBarUpdate();
		mHandler.removeCallbacks(mHideProgressLayoutDelay);
		tvChangePosition.animate().alpha(1);
	}

	@Override
	public void seekTo(int position) {
		mIsSeeking = false;
		if (mOnVideoControlListener != null && mOnVideoControlListener.isVideoPlayActive()) {
			VideoPlayer.getInstance().seekTo(position);
		}
		mHandler.postDelayed(mHideProgressLayoutDelay, HIDE_PROGRESS_LAYOUT_DELAY);
		tvChangePosition.animate().alpha(0);
		scheduleSeekBarUpdate();
	}

	@Override
	public void setProgress(int progress) {
		tvProgress.setText(DateUtils.formatElapsedTime(progress / 1000));
		tvChangePosition.setText(String.format("%s/%s", DateUtils.formatElapsedTime(progress / 1000),
				DateUtils.formatElapsedTime(VideoPlayer.getInstance().getDuration() / 1000)));
		bottomProgress.setProgress(progress);
		videoSeekBar.setProgress(progress);
		btnPlayAndPause.setVisibility(INVISIBLE);
	}

	@Override
	public int getSeekBarProgress() {
		return videoSeekBar.getProgress();
	}

	@Override
	public void hide() {
		super.hide();
		toggleProgressLayoutVisibility(false);
	}

	@Override
	public void show() {
		super.show();
		toggleProgressLayoutVisibility(true);
	}

	@Override
	public void onPlayStateChange(int playState) {
		switch (playState) {
			case VideoPlayer.STATE_PREPARING:
			case VideoPlayer.STATE_BUFFERING_PLAYING:
			case VideoPlayer.STATE_BUFFERING_PAUSED: {
				btnPlayAndPause.setVisibility(INVISIBLE);
				llLoadingProgressBar.setVisibility(VISIBLE);
				stopSeekBarUpdate();
				hide();
				break;
			}
			case VideoPlayer.STATE_PAUSED: {
				llLoadingProgressBar.setVisibility(INVISIBLE);
				pause();
				break;
			}
			case VideoPlayer.STATE_PLAYING: {
				videoSeekBar.setMax(VideoPlayer.getInstance().getDuration());
				bottomProgress.setMax(VideoPlayer.getInstance().getDuration());
				tvDuration.setText(DateUtils.formatElapsedTime(VideoPlayer.getInstance().getDuration() / 1000));
				llLoadingProgressBar.setVisibility(INVISIBLE);
				play();
				break;
			}
			case VideoPlayer.STATE_COMPLETED: {
				llLoadingProgressBar.setVisibility(INVISIBLE);
				bottomProgress.setProgress(0);
				pause();
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (mOnVideoControlListener != null) {
			if (v.getId() == btnPlayAndPause.getId() || v.getId() == btnSmallPlayPause.getId()) {
				mOnVideoControlListener.onPlayPauseClicked();
			}
		}
	}
}
