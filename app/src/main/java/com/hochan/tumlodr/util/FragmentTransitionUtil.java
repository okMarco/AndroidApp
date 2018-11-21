package com.hochan.tumlodr.util;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * .
 * Created by hochan on 2017/12/30.
 */

public class FragmentTransitionUtil {

	private static final long DURATION_TRANSITION_FADE = 200;
	private static final long DURATION_TRANSITION_SHARE = 200;

	private volatile static WeakHashMap<FragmentManager, FragmentTransitionUtil> mTransitionUtilMap = new WeakHashMap<>();

	public static FragmentTransitionUtil getInstance(FragmentManager fragmentManager) {
		if (mTransitionUtilMap.get(fragmentManager) == null) {
			synchronized (FragmentTransitionUtil.class) {
				if (mTransitionUtilMap.get(fragmentManager) == null) {
					FragmentTransitionUtil fragmentTransitionUtil = new FragmentTransitionUtil(fragmentManager);
					mTransitionUtilMap.put(fragmentManager, fragmentTransitionUtil);
				}
			}
		}
		return mTransitionUtilMap.get(fragmentManager);
	}

	private WeakReference<FragmentManager> mFragmentManagerWeakReference;
	private List<WeakReference<View>> mShareViews = new ArrayList<>();
	public int mExitAdapterPosition;
	public int mFirstVisiblePosition;
	public int mLastVisiblePosition;
	private Fragment mFromFragment;
	private Fragment mToFragment;
	private boolean mShouldOverrideBackward = false;
	private int mContainerId;

	private FragmentTransitionUtil(FragmentManager fragmentManager) {
		mFragmentManagerWeakReference = new WeakReference<>(fragmentManager);
	}

	public void transition(int containerId, Fragment fromFragment, Fragment toFragment,
	                       int exitAdapterPosition, int firstVisiblePosition, int lastVisiblePosition, List<ImageView> views) {
		mShouldOverrideBackward = true;
		mShareViews.clear();
		FragmentManager fragmentManager = mFragmentManagerWeakReference.get();
		if (fragmentManager != null) {
			mContainerId = containerId;
			mFirstVisiblePosition = firstVisiblePosition;
			mLastVisiblePosition = lastVisiblePosition;
			mExitAdapterPosition = exitAdapterPosition;
			mFromFragment = fromFragment;
			mToFragment = toFragment;
			for (View view : views) {
				mShareViews.add(new WeakReference<>(view));
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				initEnterTransition(toFragment);
				initExitTransition(fromFragment);
			}
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(containerId, mToFragment);
			for (View view : views) {
				if (view != null && !TextUtils.isEmpty(ViewCompat.getTransitionName(view))) {
					fragmentTransaction.addSharedElement(view, ViewCompat.getTransitionName(view));
				}
			}
			fragmentTransaction.commit();
			fragmentManager.beginTransaction().show(mFromFragment).hide(mFromFragment).commit();
		}
	}

	public Fragment getToFragment() {
		return mToFragment;
	}

	public boolean shouldScroll() {
		return !(mExitAdapterPosition >= mFirstVisiblePosition && mExitAdapterPosition <= mLastVisiblePosition);
	}

	public boolean onBackward() {
		if (mShouldOverrideBackward) {
			mShouldOverrideBackward = false;
			FragmentManager fragmentManager = mFragmentManagerWeakReference.get();
			if (fragmentManager != null) {
				if (mToFragment instanceof TransitionEnterFragment) {
					if (!((TransitionEnterFragment) mToFragment).isPositionVisible(mExitAdapterPosition)) {
						mExitAdapterPosition = ((TransitionEnterFragment) mToFragment).findFirstVisiblePosition();
						List<ImageView> views = ((TransitionEnterFragment) mToFragment).findFirstVisibleViews();
						mShareViews.clear();
						for (View view : views) {
							mShareViews.add(new WeakReference<>(view));
						}
					}
				}

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					initEnterTransition(mFromFragment);
					initExitTransition(mToFragment);
				}

				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
						.replace(mContainerId, mFromFragment).show(mFromFragment);
				for (WeakReference<View> view : mShareViews) {
					if (view.get() != null) {
						fragmentTransaction.addSharedElement(view.get(), ViewCompat.getTransitionName(view.get()));
					}
				}
				fragmentTransaction.commit();
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	private void initExitTransition(Fragment fragment) {
		if (fragment == null) {
			return;
		}
		Fade exitFade = new Fade();
		exitFade.setDuration(DURATION_TRANSITION_FADE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			for (WeakReference<View> viewWeakReference : mShareViews) {
				if (viewWeakReference.get() != null) {
					exitFade.excludeTarget(ViewCompat.getTransitionName(viewWeakReference.get()), true);
				}
			}
		}
		fragment.setExitTransition(exitFade);
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	private void initEnterTransition(Fragment fragment) {
		if (fragment == null) {
			return;
		}
		Fade enterFade = new Fade();
		enterFade.setDuration(DURATION_TRANSITION_FADE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			for (WeakReference<View> viewWeakReference : mShareViews) {
				if (viewWeakReference.get() != null) {
					enterFade.excludeTarget(ViewCompat.getTransitionName(viewWeakReference.get()), true);
				}
			}
		}
		fragment.setEnterTransition(enterFade);
		TransitionSet enterShareTransition = new TransitionSet();
		enterShareTransition.setDuration(DURATION_TRANSITION_SHARE);
		enterShareTransition.addTransition(new ChangeBounds());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			enterShareTransition.addTransition(new ChangeImageTransform());
			enterShareTransition.addTransition(new ChangeTransform());
		}
		fragment.setSharedElementEnterTransition(enterShareTransition);
	}

	public interface TransitionEnterFragment {
		boolean isPositionVisible(int position);

		int findFirstVisiblePosition();

		List<ImageView> findFirstVisibleViews();
	}
}
