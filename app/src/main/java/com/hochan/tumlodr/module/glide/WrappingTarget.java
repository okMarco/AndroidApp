package com.hochan.tumlodr.module.glide;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.hochan.tumlodr.ui.component.DragToFinishPhotoView;

public class WrappingTarget<Z> implements Target<Z> {

	protected final @NonNull
	Target<Z> target;

	public WrappingTarget(@NonNull Target<Z> target) {
		this.target = target;
	}

	public @NonNull
	Target<? super Z> getWrappedTarget() {
		return target;
	}

	@Override
	public void onLoadStarted(@Nullable Drawable placeholder) {
		target.onLoadStarted(placeholder);
	}

	@Override
	public void onLoadFailed(@Nullable Drawable errorDrawable) {
		target.onLoadFailed(errorDrawable);
	}

	@Override
	public void onResourceReady(Z resource, Transition<? super Z> transition) {
		target.onResourceReady(resource, transition);
	}

	@Override
	public void onLoadCleared(@Nullable Drawable placeholder) {
		target.onLoadCleared(placeholder);
	}

	@Override
	public void getSize(SizeReadyCallback cb) {
		target.getSize(cb);

	}

	@Override
	public void removeCallback(SizeReadyCallback cb) {

	}

	public Drawable getDrawable() {
		if (target instanceof DrawableImageViewTarget) {
			return ((DrawableImageViewTarget) target).getCurrentDrawable();
		}
		return null;
	}

	@Override
	public Request getRequest() {
		return target.getRequest();
	}

	@Override
	public void setRequest(Request request) {
		target.setRequest(request);
	}

	@Override
	public void onStart() {
		target.onStart();
	}

	@Override
	public void onStop() {
		target.onStop();
	}

	@Override
	public void onDestroy() {
		target.onDestroy();
	}
}
