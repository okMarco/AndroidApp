package com.hochan.tumlodr.util;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.facebook.rebound.SpringUtil;
import com.hochan.tumlodr.module.video.videolayout.VideoPlayLayout;

import java.lang.ref.WeakReference;

import static android.view.View.VISIBLE;

/**
 * .
 * <p>
 * Created by hochan on 2018/5/31.
 */

public class VideoTransitionUtils {

	public static void prepareVideoLayoutForEnterAnimation(VideoPlayLayout fromVideoLayout, VideoPlayLayout toVideoLayout) {
		if (fromVideoLayout == toVideoLayout) {
			return;
		}
		if (fromVideoLayout.getVideoControl() != null) {
			fromVideoLayout.getVideoControl().hide();
		}
		if (toVideoLayout.getVideoControl() != null) {
			toVideoLayout.getVideoControl().hide();
		}
		toVideoLayout.setAlpha(0);
		toVideoLayout.setVideoUrl(fromVideoLayout.getVideoUrl());
		int[] locationInScreen = new int[2];
		fromVideoLayout.getLocationOnScreen(locationInScreen);
		toVideoLayout.setTranslationX(locationInScreen[0]);
		toVideoLayout.setTranslationY(locationInScreen[1]);
		ViewGroup.LayoutParams layoutParams = toVideoLayout.getLayoutParams();
		layoutParams.width = fromVideoLayout.getWidth();
		layoutParams.height = fromVideoLayout.getHeight();
		toVideoLayout.getVideoTextureView().setVideoSize(fromVideoLayout.getVideoTextureWidth(), fromVideoLayout.getVideoTextureHeight());
		toVideoLayout.requestLayout();
		toVideoLayout.attachToVideoPlayer();
		toVideoLayout.setVisibility(VISIBLE);
	}

	public static void prepareVideoLayoutForEnterAnimation(Rect fromLayoutArea, VideoPlayLayout toVideoLayout) {
		if (toVideoLayout.getVideoControl() != null) {
			toVideoLayout.getVideoControl().hide();
		}
		toVideoLayout.setAlpha(0);
		toVideoLayout.setTranslationX(fromLayoutArea.left);
		toVideoLayout.setTranslationY(fromLayoutArea.top);
		ViewGroup.LayoutParams layoutParams = toVideoLayout.getLayoutParams();
		layoutParams.width = fromLayoutArea.width();
		layoutParams.height = fromLayoutArea.height();
		toVideoLayout.getVideoTextureView()
				.setVideoSize(fromLayoutArea.width(), fromLayoutArea.height());
		toVideoLayout.requestLayout();
		toVideoLayout.setVisibility(VISIBLE);
	}


	public static void startVideoLayoutAnimation(final VideoPlayLayout videoPlayLayout, final Rect endLocation, final AnimatorListenerAdapter animatorListenerAdapter) {
		videoPlayLayout.setAlpha(1);
		videoPlayLayout.setVisibility(VISIBLE);
		final Rect startLocation = new Rect();
		startLocation.left = (int) videoPlayLayout.getTranslationX();
		startLocation.right = startLocation.left + videoPlayLayout.getMeasuredWidth();
		startLocation.top = (int) videoPlayLayout.getTranslationY();
		startLocation.bottom = startLocation.top + videoPlayLayout.getMeasuredHeight();

		int animationFromValue = videoPlayLayout.getMeasuredHeight();
		int animationEndValue = endLocation.height();
		int[] startValue = new int[]{startLocation.left, startLocation.top, startLocation.width(), startLocation.height()};
		int[] endValue = new int[]{endLocation.left, endLocation.top, endLocation.width(), endLocation.height()};
		int index = 0;
		while (animationEndValue == animationFromValue) {
			if (index >= startValue.length) {
				break;
			}
			animationFromValue = startValue[index];
			animationEndValue = endValue[index];
			index++;
		}
		final ValueAnimator valueAnimator = ValueAnimator.ofInt(animationFromValue, animationEndValue);
		valueAnimator.setInterpolator(new AccelerateInterpolator());
		valueAnimator.setDuration(200);
		final int finalAnimationFromValue = animationFromValue;
		final int finalAnimationEndValue = animationEndValue;

		final WeakReference<VideoPlayLayout> videoPlayLayoutWeakReference = new WeakReference<>(videoPlayLayout);
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				if (videoPlayLayoutWeakReference.get() != null) {
					updateLayout(videoPlayLayoutWeakReference.get(),
							(int) animation.getAnimatedValue(), finalAnimationFromValue,
							finalAnimationEndValue, startLocation, endLocation);
				}
			}
		});
		if (animatorListenerAdapter != null) {
			valueAnimator.addListener(animatorListenerAdapter);
		}
		videoPlayLayout.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
			@Override
			public void onViewAttachedToWindow(View v) {
			}

			@Override
			public void onViewDetachedFromWindow(View v) {
				valueAnimator.cancel();
			}
		});
		valueAnimator.start();
	}

	public static void updateLayout(View view, int currentValue, int fromValue, int endValue,
	                                Rect startLocation, Rect endLocation) {
		final int startHeight = startLocation.height();
		final int endHeight = endLocation.height();
		final int startTransitionX = startLocation.left;
		final int endTransitionX = endLocation.left;
		final int startTransitionY = startLocation.top;
		final int endTransitionY = endLocation.top;
		final int startWidth = startLocation.width();
		final int endWidth = endLocation.width();

		int transitionX = (int) SpringUtil.mapValueFromRangeToRange(currentValue, fromValue, endValue, startTransitionX, endTransitionX);
		int transitionY = (int) SpringUtil.mapValueFromRangeToRange(currentValue, fromValue, endValue, startTransitionY, endTransitionY);
		int width = (int) SpringUtil.mapValueFromRangeToRange(currentValue, fromValue, endValue, startWidth, endWidth);
		int height = (int) SpringUtil.mapValueFromRangeToRange(currentValue, fromValue, endValue, startHeight, endHeight);

		if (transitionX < Math.min(startTransitionX, endTransitionX)) {
			transitionX = Math.min(startTransitionX, endTransitionX);
		} else if (transitionX > Math.max(startTransitionX, endTransitionX)) {
			transitionX = Math.max(startTransitionX, endTransitionX);
		}

		if (transitionY < Math.min(startTransitionY, endTransitionY)) {
			transitionY = Math.min(startTransitionY, endTransitionY);
		} else if (transitionY > Math.max(startTransitionY, endTransitionY)) {
			transitionY = Math.max(startTransitionY, endTransitionY);
		}

		if (width < Math.min(startWidth, endWidth)) {
			width = Math.min(startWidth, endWidth);
		} else if (width > Math.max(startWidth, endWidth)) {
			width = Math.max(startWidth, endWidth);
		}

		if (height < Math.min(startHeight, endHeight)) {
			height = Math.min(startHeight, endHeight);
		} else if (height > Math.max(startHeight, endHeight)) {
			height = Math.max(startHeight, endHeight);
		}

		view.setTranslationX(transitionX);
		view.setTranslationY(transitionY);
		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		layoutParams.width = width;
		layoutParams.height = height;
		view.requestLayout();
	}
}
