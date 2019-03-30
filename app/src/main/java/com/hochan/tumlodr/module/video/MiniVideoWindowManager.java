package com.hochan.tumlodr.module.video;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.databinding.LayoutFloatVideoWindowBinding;
import com.hochan.tumlodr.module.video.player.VideoPlayer;
import com.hochan.tumlodr.module.video.videolayout.MiniVideoPlayLayout;
import com.hochan.tumlodr.module.video.videolayout.VideoPlayLayout;
import com.hochan.tumlodr.ui.activity.VideoViewPagerActivity;
import com.hochan.tumlodr.util.ViewUtils;


/**
 * .
 * Created by hochan on 2018/1/29.
 */

public class MiniVideoWindowManager {

	private WindowManager mWindowManager;
	private boolean isVideoLayoutAdded = false;
	private LayoutFloatVideoWindowBinding mMiniVideoWindowBinding;
	private boolean mIsReturningFullScreen;

	private OnMiniWindowListener mOnMiniWindowListener;

	public static MiniVideoWindowManager getInstance() {
		return Holder.INSTANCE;
	}

	private static final class Holder {
		private static final MiniVideoWindowManager INSTANCE = new MiniVideoWindowManager();
	}

	private void init() {
		mWindowManager = (WindowManager) TumlodrApp.getContext().getSystemService(Context.WINDOW_SERVICE);
		mMiniVideoWindowBinding = LayoutFloatVideoWindowBinding.inflate(LayoutInflater.from(TumlodrApp.getContext()));
		mMiniVideoWindowBinding.miniVideoLayout.setOnMiniWindowListener(new MiniVideoPlayLayout.MiniVideoWindowListener() {
			@Override
			public void onDrag(int x, int y) {
				WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mMiniVideoWindowBinding.getRoot().getLayoutParams();
				layoutParams.x = x;
				layoutParams.y = y;
				mWindowManager.updateViewLayout(mMiniVideoWindowBinding.getRoot(), layoutParams);
			}

			@Override
			public void onScale(float factor) {
				WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mMiniVideoWindowBinding.getRoot().getLayoutParams();
				layoutParams.height = (int) (layoutParams.height * factor);
				layoutParams.width = (int) (layoutParams.height * mMiniVideoWindowBinding.miniVideoLayout.getVideoTextureW2HRatio());
				mWindowManager.updateViewLayout(mMiniVideoWindowBinding.getRoot(), layoutParams);
			}

			@Override
			public void returnFullScreen() {
				mIsReturningFullScreen = true;
				if (mOnMiniWindowListener != null) {
					mOnMiniWindowListener.onRenterFromMiniWindow();
				}
				Intent intent = new Intent(TumlodrApp.mContext, VideoViewPagerActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				TumlodrApp.mContext.startActivity(intent);
			}

			@Override
			public void onSurfaceReplace() {
				if (!mIsReturningFullScreen) {
					dismiss();
				}
			}

			@Override
			public void onPlayComplete() {
				VideoPlayer.getInstance().release();
				dismiss();
			}
		});
	}

	private void release() {
		if (mWindowManager != null) {
			mWindowManager = null;
			mMiniVideoWindowBinding = null;
		}
	}

	public void dismiss() {
		if (mMiniVideoWindowBinding == null) {
			return;
		}
		ViewUtils.doAfterFadeOut(mMiniVideoWindowBinding.getRoot(),
				new StaticAnimatorListenerAdapter<MiniVideoWindowManager>(this) {
					@Override
					public void onAnimationEnd(Animator animation) {
						if (getObjectWeakReference() != null && getObjectWeakReference().get() != null) {
							getObjectWeakReference().get().removeMiniVideoLayout();
							if (getObjectWeakReference().get().mOnMiniWindowListener != null) {
								getObjectWeakReference().get().mOnMiniWindowListener.onMiniWindowDismiss();
							}
						}
					}
				});
	}

	public View getRootView() {
		return mMiniVideoWindowBinding == null ? null : mMiniVideoWindowBinding.getRoot();
	}

	public void removeMiniVideoLayout() {
		if (mMiniVideoWindowBinding == null) {
			return;
		}
		if (mMiniVideoWindowBinding.miniVideoLayout.isVideoPlayActive()) {
			VideoPlayer.getInstance().release();
		}
		mWindowManager.removeViewImmediate(mMiniVideoWindowBinding.getRoot());
		isVideoLayoutAdded = false;
		release();
	}

	public void showMiniVideoLayout(final VideoPlayLayout fromVideoLayout, Rect miniWindowLocation) {
		if (!isVideoLayoutAdded) {
			if (mWindowManager == null) {
				init();
			}
			mMiniVideoWindowBinding.miniVideoLayout.setData(fromVideoLayout.getVideoUrl(), fromVideoLayout.getThumbnailUrl());
			mMiniVideoWindowBinding.getRoot().setAlpha(0);
			mWindowManager.addView(mMiniVideoWindowBinding.getRoot(), getLayoutParams(miniWindowLocation));
			mMiniVideoWindowBinding.miniVideoLayout.setVideoSize(fromVideoLayout.getVideoTextureWidth(),
					fromVideoLayout.getVideoTextureHeight());

			if (!VideoPlayer.getInstance().isPlaying()) {
				mMiniVideoWindowBinding.miniVideoLayout.play();
			}
			mMiniVideoWindowBinding.miniVideoLayout.attachToVideoPlayer();
			ViewUtils.doAfterFadeIn(mMiniVideoWindowBinding.getRoot(),
					new StaticAnimatorListenerAdapter<VideoPlayLayout>(fromVideoLayout) {
						@Override
						public void onAnimationEnd(Animator animation) {
							if (getObjectWeakReference().get() != null) {
								getObjectWeakReference().get().setVisibility(View.INVISIBLE);
							}
							if (mOnMiniWindowListener != null) {
								mOnMiniWindowListener.onEnterMiniWindow();
							}
						}

						@Override
						public void onAnimationCancel(Animator animation) {
							super.onAnimationCancel(animation);
							if (mOnMiniWindowListener != null) {
								mOnMiniWindowListener.onEnterMiniWindow();
							}
						}
					});
			isVideoLayoutAdded = true;
			mIsReturningFullScreen = false;
		}
	}

	@SuppressLint("RtlHardcoded")
	private WindowManager.LayoutParams getLayoutParams(Rect miniWindowLocation) {
		WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			windowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
		} else {
			windowLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		}
		windowLayoutParams.format = PixelFormat.TRANSLUCENT; // 设置图片格式，效果为背景透明(RGBA_8888)
		windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 设置悬浮窗的长宽
		windowLayoutParams.width = miniWindowLocation.width();
		windowLayoutParams.height = miniWindowLocation.height();

		windowLayoutParams.x = miniWindowLocation.left;
		windowLayoutParams.y = miniWindowLocation.top;
		return windowLayoutParams;
	}

	public void registerOnEnterMiniWindowListener(OnMiniWindowListener onEnterMiniWindowListener) {
		mOnMiniWindowListener = onEnterMiniWindowListener;
	}

	public void unRegisterOnEnterMiniWindowListener(OnMiniWindowListener onEnterMiniWindowListener) {
		if (mOnMiniWindowListener == onEnterMiniWindowListener) {
			mOnMiniWindowListener = null;
		}
	}

	public interface OnMiniWindowListener {
		void onEnterMiniWindow();

		void onMiniWindowDismiss();

		void onRenterFromMiniWindow();
	}
}
