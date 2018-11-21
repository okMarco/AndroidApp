package com.hochan.tumlodr.ui.component;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.hochan.tumlodr.module.glide.ProgressTarget;

import java.util.Locale;

/**
 * .
 * Created by hochan on 2018/1/6.
 */

public class FullPhotoViewProgressTarget<Z> extends ProgressTarget<String, Z> {

	private ProgressBar mProgressBar;
	private TextView mTextView;

	public FullPhotoViewProgressTarget(String url, Target<Z> target, ProgressBar progressBar, TextView textView) {
		super(url, target);
		mProgressBar = progressBar;
		mTextView = textView;
	}

	@Override
	public float getGranualityPercentage() {
		return 0.1f;
	}

	@Override
	protected void onConnecting() {
		if (mProgressBar != null) {
			mProgressBar.setVisibility(View.VISIBLE);
			mProgressBar.setIndeterminate(true);
		}
	}

	@Override
	protected void onDownloading(long bytesRead, long expectedLength) {
		if (mProgressBar != null) {
			mProgressBar.setIndeterminate(false);
			mProgressBar.setProgress((int) (100 * bytesRead / expectedLength));
		}
	}

	@Override
	protected void onDownloaded(long totalLength) {
		mTextView.setVisibility(View.VISIBLE);
		cleanup();
	}

	@Override
	protected void onDelivered() {
		if (mProgressBar != null)
			mProgressBar.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onResourceReady(Z resource, Transition<? super Z> transition) {
		super.onResourceReady(resource, transition);
	}

	public void hideProgress() {
		if (mProgressBar != null) {
			mProgressBar.setVisibility(View.INVISIBLE);
			if (mProgressBar.getParent() != null
					&& mProgressBar.getParent() instanceof ViewGroup) {
				((ViewGroup) mProgressBar.getParent()).removeView(mProgressBar);
			}
		}
		mProgressBar = null;
	}
}
