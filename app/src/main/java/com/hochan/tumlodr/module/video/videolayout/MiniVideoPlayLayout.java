package com.hochan.tumlodr.module.video.videolayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.hochan.tumlodr.module.video.player.VideoPlayer;
import com.hochan.tumlodr.module.video.videocontrol.MiniVideoControl;


/**
 * .
 * Created by hochan on 2018/1/29.
 */

public class MiniVideoPlayLayout extends VideoPlayLayout {

	private ScaleGestureDetector mScaleGestureDetector;
	private GestureDetector mGestureDetector;
	private MiniVideoWindowListener mOnDragListener;

	private float mTouchDownX;
	private float mTouchDownY;
	private boolean mIsScaling = false;

	public MiniVideoPlayLayout(@NonNull Context context) {
		this(context, null);
	}

	public MiniVideoPlayLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MiniVideoPlayLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				if (mOnDragListener != null) {
					mOnDragListener.onScale(detector.getScaleFactor());
				}
				return true;
			}
		});

		mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				if (mOnDragListener != null) {
					mOnDragListener.returnFullScreen();
				}
				return true;
			}
		});

		MiniVideoControl miniVideoControl = new MiniVideoControl(getContext());
		miniVideoControl.setOnVideoControlListener(this);
		setVideoControl(miniVideoControl);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mScaleGestureDetector.onTouchEvent(event);
		mGestureDetector.onTouchEvent(event);
		if (mScaleGestureDetector.isInProgress()) {
			mIsScaling = true;
			return true;
		}
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				mTouchDownX = event.getX();
				mTouchDownY = event.getY();
				mIsScaling = false;
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				if (mOnDragListener != null && !mIsScaling) {
					mOnDragListener.onDrag((int) (event.getRawX() - mTouchDownX), (int) (event.getRawY() - mTouchDownY));
				}
				break;
			}
		}
		return true;
	}

	public void setOnMiniWindowListener(MiniVideoWindowListener listener) {
		mOnDragListener = listener;
	}

	@Override
	public void onSurfaceReplace() {
		if (mOnDragListener != null) {
			mOnDragListener.onSurfaceReplace();
		}
	}

	@Override
	public void onPlayStateChange() {
		super.onPlayStateChange();
		if (VideoPlayer.getInstance().getCurrentState() == VideoPlayer.STATE_COMPLETED) {
			if (mOnDragListener != null) {
				mOnDragListener.onPlayComplete();
			}
		}
	}

	public interface MiniVideoWindowListener {
		void onDrag(int x, int y);

		void onScale(float factor);

		void returnFullScreen();

		void onSurfaceReplace();

		void onPlayComplete();
	}
}
