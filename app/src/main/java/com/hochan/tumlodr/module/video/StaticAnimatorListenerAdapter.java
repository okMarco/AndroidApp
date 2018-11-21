package com.hochan.tumlodr.module.video;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import java.lang.ref.WeakReference;

/**
 * .
 * Created by hochan on 2018/6/9.
 */

public class StaticAnimatorListenerAdapter<T> extends AnimatorListenerAdapter {
	private WeakReference<T> mObjectWeakReference;

	public StaticAnimatorListenerAdapter(T object) {
		mObjectWeakReference = new WeakReference<>(object);
	}

	@Override
	public void onAnimationEnd(Animator animation) {
	}

	public WeakReference<T> getObjectWeakReference() {
		return mObjectWeakReference;
	}
}
