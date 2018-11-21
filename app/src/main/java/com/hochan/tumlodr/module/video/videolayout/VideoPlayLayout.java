package com.hochan.tumlodr.module.video.videolayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.exoplayer2.source.UnrecognizedInputFormatException;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.LayoutVideoPlayBinding;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.video.VideoTextureView;
import com.hochan.tumlodr.module.video.player.VideoPlayer;
import com.hochan.tumlodr.module.video.videocontrol.DefaultVideoControl;
import com.hochan.tumlodr.module.video.videocontrol.VideoControl;

import java.io.ByteArrayInputStream;

/**
 * .
 * Created by hochan on 2018/1/25.
 */

@SuppressWarnings("unused")
public class VideoPlayLayout extends FrameLayout implements VideoPlayer.OnPlayInfoListener,
		View.OnClickListener, GestureDetector.OnGestureListener,
		VideoControl.OnVideoControlListener, VideoTextureView.OnVideoTextureListener,
		GestureDetector.OnDoubleTapListener {

	public static final int MIN_VIDEO_HEIGHT_IN_DIP = 80;
	public static final int MIN_VIDEO_WIDTH_IN_DIP = 100;
	public static final int MINI_LAYOUT_MARGIN_IN_DIP = 15;

	public static boolean sIsInEuro = false;
	public static final LruCache<String, String> VIDEO_URL_INEURO = new LruCache<>(500);

	protected VideoControl mVideoControl;

	private GestureDetector mGestureDetector;

	public LayoutVideoPlayBinding mVideoPlayBinding;

	public boolean mIsSurfaceAttach = false;

	private OnSingleTapListener mOnSingleTapListener;

	private String mVideoUrl;
	private String mThumbnailUrl;

	private boolean mChangingPosition = false;
	private WebView mWebView;
	private Handler mHandler = new Handler();

	public VideoPlayLayout(@NonNull Context context) {
		this(context, null);
	}

	public VideoPlayLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VideoPlayLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		View view = LayoutInflater.from(context).inflate(R.layout.layout_video_play, null, false);
		addView(view, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mVideoPlayBinding = LayoutVideoPlayBinding.bind(view);
		mVideoPlayBinding.videoTextureView.setVideoTextureListener(this);
		mGestureDetector = new GestureDetector(context, this);
		mGestureDetector.setOnDoubleTapListener(this);

		initVideoControl();
	}

	public void initVideoControl() {
		mVideoControl = new DefaultVideoControl(getContext());
		mVideoControl.setOnVideoControlListener(this);
		setVideoControl(mVideoControl);
	}

	public void setVideoControl(VideoControl videoControl) {
		if (mVideoControl != null) {
			removeView(mVideoControl);
		}
		mVideoControl = videoControl;
		addView(mVideoControl);
	}

	public void setVideoUrl(String url) {
		mVideoUrl = url;
	}

	/**
	 * 开始播放
	 */
	public void play() {
		if (!TextUtils.isEmpty(mVideoUrl)) {
			if (sIsInEuro && mVideoUrl.contains("video_file")) {
				String realVideoUrl = VIDEO_URL_INEURO.get(mVideoUrl);
				if (!TextUtils.isEmpty(realVideoUrl)) {
					setVideoUrl(realVideoUrl);
				} else {
					getVideoControl().onPlayStateChange(VideoPlayer.STATE_BUFFERING_PLAYING);
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							getWebView().loadUrl(mVideoUrl);
						}
					}, 200);
					return;
				}
			}
			VideoPlayer.getInstance().play(this);
			if (!mIsSurfaceAttach) {
				attachToVideoPlayer();
			}
		}
	}

	/**
	 * 设置视频地址和封面地址
	 *
	 * @param videoUrl     视频地址
	 * @param thumbnailUrl 封面地址
	 */
	public void setData(String videoUrl, String thumbnailUrl) {
		mIsSurfaceAttach = false;
		mVideoUrl = videoUrl;
		mThumbnailUrl = thumbnailUrl;
		mVideoPlayBinding.ivVideoCover.setVisibility(VISIBLE);
		TumlodrGlide.with(getContext())
				.load(thumbnailUrl)
				.skipMemoryCache(true)
				.transition(new DrawableTransitionOptions().crossFade())
				.into(mVideoPlayBinding.ivVideoCover)
				.clearOnDetach();
	}

	public void setBackgroundTransparent() {
		mVideoPlayBinding.flVideoLayoutRoot.setBackgroundColor(Color.TRANSPARENT);
	}

	/**
	 * 播放状态改变
	 */
	@Override
	public void onPlayStateChange() {
		switch (VideoPlayer.getInstance().getCurrentState()) {
			case VideoPlayer.STATE_PLAYING: {
				mVideoPlayBinding.ivVideoCover.setVisibility(INVISIBLE);
				break;
			}
		}
		if (mVideoControl != null) {
			mVideoControl.onPlayStateChange(VideoPlayer.getInstance().getCurrentState());
		}
	}

	/**
	 * 当前VideoLayout被替换
	 */
	@Override
	public void onSurfaceReplace() {
		mIsSurfaceAttach = false;
		mVideoControl.stopSeekBarUpdate();
		mVideoControl.hideProgressBar();
		mVideoControl.hideLoadingIndicator();
	}

	@Override
	public void onVideoSizeChange(int width, int height) {
		mVideoPlayBinding.videoTextureView.setVideoSize(width, height);
	}

	@Override
	public void onBufferingUpdate(int percent) {
		if (mVideoControl != null) {
			mVideoControl.setLoadingProgress(percent);
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onPlayError(Exception e) {
		if (e.getCause() != null) {
			if (e.getCause() instanceof UnrecognizedInputFormatException) {
				sIsInEuro = true;
				getWebView().loadUrl(getVideoUrl());
			} else {
				if (mVideoControl != null) {
					mVideoControl.pause();
				}
				mVideoPlayBinding.ivVideoCover.setVisibility(VISIBLE);
				Toast.makeText(getContext(), e.getCause().getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onDownloadRateChange(int rate) {
		if (mVideoControl != null) {
			mVideoControl.setDownloadSpeed(rate);
		}
	}

	public String getVideoUrl() {
		return mVideoUrl;
	}

	public String getThumbnailUrl() {
		return mThumbnailUrl;
	}

	public Bitmap getCurrentFrame() {
		return mVideoPlayBinding.videoTextureView.getBitmap();
	}

	public ImageView getThumbnailImageView() {
		return mVideoPlayBinding.ivVideoCover;
	}

	public VideoTextureView getVideoTextureView() {
		return mVideoPlayBinding.videoTextureView;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == mVideoPlayBinding.ivVideoCover.getId()) {
			handlePlayPauseRequest();
		}
	}

	public void pause() {
		if (isVideoPlayActive()) {
			VideoPlayer.getInstance().pause();
		}
	}

	private void handlePlayPauseRequest() {
		if (!isVideoPlayActive()) {
			play();
			return;
		}

		switch (VideoPlayer.getInstance().getCurrentState()) {
			case VideoPlayer.STATE_ERROR:
			case VideoPlayer.STATE_NONE:
			case VideoPlayer.STATE_IDLE:
			case VideoPlayer.STATE_PAUSED:
			case VideoPlayer.STATE_BUFFERING_PAUSED:
			case VideoPlayer.STATE_COMPLETED: {
				play();
				break;
			}
			case VideoPlayer.STATE_PLAYING:
			case VideoPlayer.STATE_BUFFERING_PLAYING: {
				VideoPlayer.getInstance().pause();
				break;
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP: {
				if (mChangingPosition) {
					mVideoControl.seekTo(mVideoControl.getSeekBarProgress());
				}
				requestDisallowInterceptTouchEvent(false);
				break;
			}
		}
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		mChangingPosition = false;
		requestDisallowInterceptTouchEvent(false);
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	public boolean scrollToChangePosition() {
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if (!mChangingPosition) {
			if (Math.abs(distanceX) > Math.abs(distanceY) && isVideoPlayActive() && scrollToChangePosition()) {
				requestDisallowInterceptTouchEvent(true);
				mChangingPosition = true;
				mVideoControl.startSeek();
			} else {
				return false;
			}
		}

		if (mChangingPosition) {
			requestDisallowInterceptTouchEvent(true);
			int progress = mVideoControl.getSeekBarProgress();
			int duration = VideoPlayer.getInstance().getDuration();
			if (duration > 0) {
				mVideoControl.setProgress((int) (progress - distanceX * duration / getMeasuredWidth()));
			}
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	public void attachToVideoPlayer() {
		mVideoPlayBinding.videoTextureView.attachToVideoPlayer();
	}

	public void setVideoSize(int width, int height) {
		mVideoPlayBinding.videoTextureView.setVideoSize(width, height);
	}

	public int getVideoTextureWidth() {
		return mVideoPlayBinding.videoTextureView.getWidth();
	}

	public int getVideoTextureHeight() {
		return mVideoPlayBinding.videoTextureView.getHeight();
	}

	public float getVideoTextureW2HRatio() {
		return mVideoPlayBinding.videoTextureView.getW2HRatio();
	}

	public void resetVideoTexture() {
		mVideoPlayBinding.flTextureContainer.removeView(mVideoPlayBinding.videoTextureView);
		mVideoPlayBinding.flTextureContainer.addView(mVideoPlayBinding.videoTextureView);
	}

	public void setOnSingleTapListener(OnSingleTapListener listener) {
		mOnSingleTapListener = listener;
	}

	@Override
	public boolean isVideoPlayActive() {
		return this == VideoPlayer.getInstance().getVideoPlayLayout();
	}

	@Override
	public void onVisibilityChange(boolean visible) {
	}

	public float getW2HRatio() {
		return mVideoPlayBinding.videoTextureView.getW2HRatio();
	}

	@Override
	public void onPlayPauseClicked() {
		handlePlayPauseRequest();
	}

	public VideoControl getVideoControl() {
		return mVideoControl;
	}

	@SuppressLint("SetJavaScriptEnabled")
	private WebView getWebView() {
		if (mWebView == null) {
			mWebView = new WebView(getContext());
			mWebView.getSettings().setJavaScriptEnabled(true);
			addView(mWebView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			mWebView.setWebViewClient(new WebViewClient() {
				@Override
				public WebResourceResponse shouldInterceptRequest(WebView view, final String url) {
					if (Uri.parse(url).getPath().endsWith("mp4")) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								VIDEO_URL_INEURO.put(getVideoUrl(), url);
								setVideoUrl(url);
								play();
								if (mWebView != null) {
									mWebView.setVisibility(INVISIBLE);
									mWebView.destroy();
									removeView(mWebView);
									mWebView = null;
								}
							}
						});
						return new WebResourceResponse("video/mp4", null, new ByteArrayInputStream(new byte[0]));
					}
					return super.shouldInterceptRequest(view, url);
				}
			});
		}
		return mWebView;
	}

	@Override
	public void onSurfaceDestroyed() {
		mVideoPlayBinding.ivVideoCover.setVisibility(VISIBLE);
		mVideoControl.pause();
		if (isVideoPlayActive()) {
			VideoPlayer.getInstance().release();
		}
	}

	@Override
	public void onSurfaceAttach() {
		mIsSurfaceAttach = true;
	}

	@Override
	public boolean shouldAttachOnAvailable() {
		return isVideoPlayActive();
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		if (isVideoPlayActive()) {
			if (mVideoControl.getVideoControlVisibility() == VISIBLE) {
				boolean handled = false;
				if (mOnSingleTapListener != null) {
					handled = mOnSingleTapListener.onSingleTap(this);
				}
				if (!handled) {
					mVideoControl.hide();
				}
			} else {
				if (mVideoControl != null) {
					mVideoControl.show();
				}
			}
		} else {
			play();
			if (mVideoControl != null) {
				mVideoControl.show();
			}
		}
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		handlePlayPauseRequest();
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return true;
	}

	public interface OnSingleTapListener {
		boolean onSingleTap(VideoPlayLayout videoPlayLayout);
	}
}
