package com.hochan.tumlodr.module.video.videolayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.hochan.tumlodr.module.video.MiniVideoWindowManager;
import com.hochan.tumlodr.module.video.StaticAnimatorListenerAdapter;
import com.hochan.tumlodr.module.video.videocontrol.TikTokViewVideoControl;
import com.hochan.tumlodr.tools.ScreenTools;
import com.hochan.tumlodr.util.ViewUtils;

import static com.hochan.tumlodr.util.VideoTransitionUtils.startVideoLayoutAnimation;
import static com.hochan.tumlodr.util.VideoTransitionUtils.updateLayout;

/**
 * .
 * <p>
 * Created by hochan on 2018/5/22.
 */

public class TikTokVideoLayout extends VideoPlayLayout {

	private boolean mIsDragging = false;
	private boolean mScrollToPosition = false;

	private float mLastTouchRawX;

	private float mViewRatio = -1f;
	private int mMinVideoHeight;
	private int mMinVideoWidth;
	private int mMiniLayoutMargin;

	public final Rect mFullScreenLocation = new Rect();
	private final Rect mMiniWindowLocation = new Rect();

	private OnMiniLayoutTransitionListener mOnMiniLayoutTransitionListener;
	private OnFullScreenListener mOnFullScreenListener;

	private int mChangePositionAreaHeight = 0;

	public TikTokVideoLayout(@NonNull Context context) {
		this(context, null);
	}

	public TikTokVideoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TikTokVideoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mMinVideoHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MIN_VIDEO_HEIGHT_IN_DIP, getResources().getDisplayMetrics());
		mMinVideoWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MIN_VIDEO_WIDTH_IN_DIP, getResources().getDisplayMetrics());
		mMiniLayoutMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MINI_LAYOUT_MARGIN_IN_DIP, getResources().getDisplayMetrics());

		mChangePositionAreaHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

		initMiniWindowLocation();
	}

	@Override
	public void initVideoControl() {
		mVideoControl = new TikTokViewVideoControl(getContext());
		mVideoControl.setOnVideoControlListener(this);
		setVideoControl(mVideoControl);
	}

	public void setOnMiniLayoutTransitionListener(OnMiniLayoutTransitionListener onMiniLayoutTransitionListener) {
		mOnMiniLayoutTransitionListener = onMiniLayoutTransitionListener;
	}

	@Override
	public boolean scrollToChangePosition() {
		return mScrollToPosition;
	}

	public void startFullViewAnimation() {
		mVideoControl.hide();
		if (getParent() != null && getParent() instanceof ViewGroup) {
			mFullScreenLocation.set(0, 0, ((ViewGroup) getParent()).getWidth(),
					((ViewGroup) getParent()).getHeight());
		}
		startVideoLayoutAnimation(this, mFullScreenLocation, new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				ViewGroup.LayoutParams layoutParams = getLayoutParams();
				layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
				layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
				play();
				if (mOnMiniLayoutTransitionListener != null) {
					mOnMiniLayoutTransitionListener.onReturnFullViewLayout();
				}
			}
		});
	}

	public void startMiniWindowAnimation() {
		mVideoControl.hide();
		initMiniWindowLocation();
		StaticAnimatorListenerAdapter listenerAdapter = new StaticAnimatorListenerAdapter<TikTokVideoLayout>(TikTokVideoLayout.this) {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (getObjectWeakReference() != null && getObjectWeakReference().get() != null) {
					MiniVideoWindowManager.getInstance().showMiniVideoLayout(getObjectWeakReference().get(), mMiniWindowLocation);
				}
			}
		};
		startVideoLayoutAnimation(this, mMiniWindowLocation, listenerAdapter);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP && mIsDragging) {
			mIsDragging = false;
			if (getMeasuredHeight() > getResources().getDisplayMetrics().heightPixels / 2) {
				startFullViewAnimation();
			} else {
				if (ViewUtils.canDrawOverlays(getContext())) {
					startMiniWindowAnimation();
				} else {
					startFullViewAnimation();
				}
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if (!isVideoPlayActive()) {
			return false;
		}
		if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mScrollToPosition = true;
			super.onScroll(e1, e2, distanceX, distanceY);
			requestDisallowInterceptTouchEvent(true);
			return true;
		} else {
			mScrollToPosition = false;
			requestDisallowInterceptTouchEvent(false);

			if (e2.getY() > getMeasuredHeight() - mChangePositionAreaHeight && !mIsDragging) {
				mScrollToPosition = true;
				return super.onScroll(e1, e2, distanceX, distanceY);
			} else {
				if (!mIsDragging && Math.abs(distanceX) > Math.abs(distanceY)) {
					if (e1.getX() < e2.getX()) {
						mIsDragging = true;
						mLastTouchRawX = e2.getRawX();
						if (mViewRatio == -1) {
							mViewRatio = getMeasuredHeight() * 1.0f / getMeasuredWidth();
						}
						initMiniWindowLocation();
						requestDisallowInterceptTouchEvent(true);
						if (mVideoControl != null) {
							mVideoControl.hide();
						}
						if (mOnMiniLayoutTransitionListener != null) {
							mOnMiniLayoutTransitionListener.onStartEnterMiniLayout();
						}
						return true;
					}
				}
				if (mIsDragging) {
					requestDisallowInterceptTouchEvent(true);
					float dX = e2.getRawX() - mLastTouchRawX;
					updateLayout(this, getMeasuredWidth() - (int) dX, mFullScreenLocation.width(),
							mMiniWindowLocation.width(), mFullScreenLocation, mMiniWindowLocation);
					mLastTouchRawX = e2.getRawX();
					return true;
				}
			}
		}
		return false;
	}

	public void initMiniWindowLocation() {
		if (getMeasuredWidth() == 0 || getMeasuredHeight() == 0) {
			mFullScreenLocation.set(0, 0, getContext().getResources().getDisplayMetrics().widthPixels,
					getContext().getResources().getDisplayMetrics().heightPixels);
		} else {
			if (getParent() != null && getParent() instanceof View) {
				mFullScreenLocation.set(0, 0, ((View) getParent()).getMeasuredWidth(), ((View) getParent()).getMeasuredHeight());
			}
		}
		int miniVideoWidth;
		int miniVideoHeight;
		float videoRatio = getW2HRatio() == 0 ? 1 : getW2HRatio();
		if (videoRatio > 1) {
			miniVideoHeight = mMinVideoHeight;
			miniVideoWidth = (int) (miniVideoHeight * getW2HRatio());
		} else {
			miniVideoWidth = mMinVideoWidth;
			miniVideoHeight = (int) (miniVideoWidth * 1.0 / getW2HRatio());
		}
		int endTransitionX = mFullScreenLocation.width() - miniVideoWidth - mMiniLayoutMargin;
		int endTransitionY = ScreenTools.getScreenHeight(getContext()) - miniVideoHeight - mMiniLayoutMargin;
		mMiniWindowLocation.set(endTransitionX, endTransitionY,
				endTransitionX + miniVideoWidth, endTransitionY + miniVideoHeight);
	}

	@Override
	public void onVisibilityChange(boolean visible) {
		if (isVideoPlayActive() && mOnFullScreenListener != null) {
			mOnFullScreenListener.onFullScreenChange(!visible);
		}
	}

	public void setOnFullScreenListener(OnFullScreenListener onFullScreenListener) {
		mOnFullScreenListener = onFullScreenListener;
	}

	@Override
	public void onSurfaceReplace() {
		super.onSurfaceReplace();
		if (mOnFullScreenListener != null) {
			mOnFullScreenListener = null;
		}
	}

	public interface OnMiniLayoutTransitionListener {
		void onStartEnterMiniLayout();

		void onReturnFullViewLayout();
	}

	public interface OnFullScreenListener {
		void onFullScreenChange(boolean fullScreen);
	}
}
