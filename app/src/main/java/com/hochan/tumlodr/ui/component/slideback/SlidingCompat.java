package com.hochan.tumlodr.ui.component.slideback;

import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * Created by zhendong_chen on 2016/8/22.
 */
public class SlidingCompat {

	static final SlidingPanelLayoutImpl IMPL;

	static {
		final int deviceVersion = Build.VERSION.SDK_INT;
		if (deviceVersion >= 17) {
			IMPL = new SlidingPanelLayoutImplJBMR1();
		} else if (deviceVersion >= 16) {
			IMPL = new SlidingPanelLayoutImplJB();
		} else {
			IMPL = new SlidingPanelLayoutImplBase();
		}
	}

	public static void invalidateChildRegion(SlidingLayout parent, View child){
		IMPL.invalidateChildRegion(parent, child);
	}

	interface SlidingPanelLayoutImpl {
		void invalidateChildRegion(SlidingLayout parent, View child);
	}

	static class SlidingPanelLayoutImplBase implements SlidingPanelLayoutImpl {
		public void invalidateChildRegion(SlidingLayout parent, View child) {
			ViewCompat.postInvalidateOnAnimation(parent, child.getLeft(), child.getTop(),
					child.getRight(), child.getBottom());
		}
	}

	static class SlidingPanelLayoutImplJB extends SlidingPanelLayoutImplBase {
		/*
		 * Private API hacks! Nasty! Bad!
		 *
		 * In Jellybean, some optimizations in the hardware UI renderer
		 * prevent a changed Paint on a View using a hardware layer from having
		 * the intended effect. This twiddles some internal bits on the view to force
		 * it to recreate the display list.
		 */
		private Method mGetDisplayList;
		private Field mRecreateDisplayList;

		SlidingPanelLayoutImplJB() {
			try {
				mGetDisplayList = View.class.getDeclaredMethod("getDisplayList", (Class[]) null);
			} catch (NoSuchMethodException e) {
			}
			try {
				mRecreateDisplayList = View.class.getDeclaredField("mRecreateDisplayList");
				mRecreateDisplayList.setAccessible(true);
			} catch (NoSuchFieldException e) {
			}
		}

		@Override
		public void invalidateChildRegion(SlidingLayout parent, View child) {
			if (mGetDisplayList != null && mRecreateDisplayList != null) {
				try {
					mRecreateDisplayList.setBoolean(child, true);
					mGetDisplayList.invoke(child, (Object[]) null);
				} catch (Exception e) {
				}
			} else {
				// Slow path. REALLY slow path. Let's hope we don't get here.
				child.invalidate();
				return;
			}
			super.invalidateChildRegion(parent, child);
		}
	}

	static class SlidingPanelLayoutImplJBMR1 extends SlidingPanelLayoutImplBase {
		@Override
		public void invalidateChildRegion(SlidingLayout parent, View child) {
			ViewCompat.setLayerPaint(child, ((SlidingLayout.LayoutParams) child.getLayoutParams()).dimPaint);
		}
	}
}
