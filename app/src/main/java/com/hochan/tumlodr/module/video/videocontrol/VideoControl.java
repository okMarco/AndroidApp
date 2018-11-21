package com.hochan.tumlodr.module.video.videocontrol;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import com.hochan.tumlodr.R;
import com.hochan.tumlodr.module.video.player.VideoPlayer;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * .
 * <p>
 * Created by hochan on 2018/5/24.
 */

@SuppressWarnings("unused")
public abstract class VideoControl extends FrameLayout implements View.OnClickListener {

	private static final long PROGRESS_UPDATE_INTERVAL = 200;
	private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 0;

	protected static final long HIDE_PROGRESS_LAYOUT_DELAY = 3000;

	protected OnVideoControlListener mOnVideoControlListener;

	protected TextView tvChangePosition, tvProgress, tvDuration, tvLoadingProgress, tvDownloadSpeed;
	protected SeekBar videoSeekBar;
	protected ProgressBar bottomProgress, loadingProgressBar;
	protected View llLoadingProgressBar;
	protected ImageButton btnPlayAndPause, btnSmallPlayPause, btnRotate, btnFullScreen;
	protected ViewGroup llProgressLayout;

	protected boolean mIsSeeking = false;

	// 更新进度条
	protected Handler mHandler = new Handler();
	private final ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> mScheduleFuture;

	private UpdateProgressTask mUpdateProgressTask;
	protected HideProgressLayoutDelay mHideProgressLayoutDelay;

	public VideoControl(@NonNull Context context) {
		this(context, null);
	}

	public VideoControl(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VideoControl(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setUp();
	}

	public void setOnVideoControlListener(OnVideoControlListener onVideoControlListener) {
		mOnVideoControlListener = onVideoControlListener;
	}

	private void setUp() {
		View contentView = LayoutInflater.from(getContext()).inflate(getRecourseLayout(), this, false);
		addView(contentView);
		retrieveViews(contentView);
		initViews();

		mUpdateProgressTask = new UpdateProgressTask(this);
		mHideProgressLayoutDelay = new HideProgressLayoutDelay(this);
	}

	protected abstract int getRecourseLayout();

	protected abstract void retrieveViews(View contentView);

	protected abstract void initViews();

	public abstract void updateProgress(int position);

	public abstract int getVideoControlVisibility();

	public abstract void startSeek();

	public abstract void seekTo(int position);

	public abstract void setProgress(int position);

	public abstract int getSeekBarProgress();

	@CallSuper
	public void hide() {
		mHandler.removeCallbacks(mHideProgressLayoutDelay);
		if (mOnVideoControlListener != null) {
			mOnVideoControlListener.onVisibilityChange(false);
		}
	}

	@CallSuper
	public void show() {
		mHandler.removeCallbacks(mHideProgressLayoutDelay);
		if (mOnVideoControlListener != null) {
			mOnVideoControlListener.onVisibilityChange(true);
		}
		if (!mIsSeeking) {
			mHandler.postDelayed(mHideProgressLayoutDelay, HIDE_PROGRESS_LAYOUT_DELAY);
		}
	}

	/**
	 * 更新进度条
	 */
	public void scheduleSeekBarUpdate() {
		stopSeekBarUpdate();
		if (!mExecutorService.isShutdown()) {
			mScheduleFuture = mExecutorService.scheduleAtFixedRate(
					new Runnable() {
						@Override
						public void run() {
							mHandler.post(mUpdateProgressTask);
						}
					}, PROGRESS_UPDATE_INITIAL_INTERVAL, PROGRESS_UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * 停止进度条刷新
	 */
	public void stopSeekBarUpdate() {
		if (mScheduleFuture != null) {
			mScheduleFuture.cancel(false);
		}
	}

	public abstract void hideProgressBar();

	public abstract void setLoadingProgress(int percent);

	public abstract void hideLoadingIndicator();

	public abstract void onPlayStateChange(int playState);

	public void setDownloadSpeed(int speed) {
		if (tvDownloadSpeed != null) {
			tvDownloadSpeed.setText(String.format(Locale.US, "%dkb/s  ", speed));
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stopSeekBarUpdate();
		mHandler.removeCallbacks(mHideProgressLayoutDelay);
		hideProgressBar();
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (btnRotate != null) {
			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				btnRotate.setImageResource(R.drawable.ic_mediaplayer_videoplayer_portrait);
			} else {
				btnRotate.setImageResource(R.drawable.ic_mediaplayer_videoplayer_landscape);
			}
		}
	}

	public void showRotateButton() {
		if (btnRotate != null) {
			btnRotate.setVisibility(VISIBLE);
		}
	}

	public void setRotateButtonClickListener(OnClickListener onClickListener) {
		if (btnRotate != null) {
			btnRotate.setOnClickListener(onClickListener);
		}
	}

	public void showFullScreenButton() {
		if (btnFullScreen != null) {
			btnFullScreen.setVisibility(VISIBLE);
		}
	}

	public void setFullScreenClickListener(OnClickListener onClickListener) {
		if (btnFullScreen != null) {
			btnFullScreen.setOnClickListener(onClickListener);
		}
	}

	public void pause() {
		if (btnPlayAndPause != null) {
			btnPlayAndPause.setVisibility(VISIBLE);
		}
		if (btnSmallPlayPause != null) {
			btnSmallPlayPause.setImageResource(R.drawable.ic_mediaplayer_musicplayer_play);
		}
		if (llLoadingProgressBar != null) {
			llLoadingProgressBar.setVisibility(INVISIBLE);
		}
		stopSeekBarUpdate();
		hideProgressBar();
	}

	public void play() {
		if (btnPlayAndPause != null) {
			btnPlayAndPause.setVisibility(INVISIBLE);
		}
		if (btnSmallPlayPause != null) {
			btnSmallPlayPause.setImageResource(R.drawable.ic_mediaplayer_musicplayer_pause);
		}
		if (!mIsSeeking) {
			scheduleSeekBarUpdate();
		}
	}

	public interface OnVideoControlListener {
		void onPlayPauseClicked();

		boolean isVideoPlayActive();

		void onVisibilityChange(boolean visible);
	}

	private static final class UpdateProgressTask implements Runnable {
		WeakReference<VideoControl> mControlWeakReference;

		UpdateProgressTask(VideoControl control) {
			mControlWeakReference = new WeakReference<>(control);
		}

		@Override
		public void run() {
			if (mControlWeakReference != null && mControlWeakReference.get() != null) {
				mControlWeakReference.get().updateProgress(VideoPlayer.getInstance().getCurrentPosition());
			}
		}
	}

	/**
	 * 延迟隐藏进度栏
	 */
	private static final class HideProgressLayoutDelay implements Runnable {
		WeakReference<VideoControl> mControlWeakReference;

		HideProgressLayoutDelay(VideoControl videoControl) {
			mControlWeakReference = new WeakReference<>(videoControl);
		}

		@Override
		public void run() {
			if (mControlWeakReference != null && mControlWeakReference.get() != null) {
				mControlWeakReference.get().hide();
			}
		}
	}
}
