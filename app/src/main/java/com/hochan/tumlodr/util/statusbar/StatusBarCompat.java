package com.hochan.tumlodr.util.statusbar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
import static com.hochan.tumlodr.util.statusbar.PhoneSystemCompat.isEMUI;

/**
 * .
 * Created by czd on 2018/1/27.
 */

public class StatusBarCompat {

	// 状态栏和底部控制栏同时透明
	public static void setWindowLayoutNoLimits(Window window) {
		window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	public static void setWindowLayoutLimits(Window window) {
		window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	// 只隐藏底部操作栏
	public static void hideNavigationBar(Window window) {
		int newUiOptions = window.getDecorView().getSystemUiVisibility();
		if (Build.VERSION.SDK_INT >= ICE_CREAM_SANDWICH) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		}
		window.getDecorView().setSystemUiVisibility(newUiOptions);
	}

	public static void setNavigationBarTranslucent(Window window) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}

	// 设置全屏 隐藏状态栏和底部操作栏
	@SuppressLint("ObsoleteSdkInt")
	public static void setWindowFullScreen(Window window) {
		int newUiOptions = window.getDecorView().getSystemUiVisibility();
		if (Build.VERSION.SDK_INT >= ICE_CREAM_SANDWICH) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		}
		window.getDecorView().setSystemUiVisibility(newUiOptions);
	}

	// 设置状态栏透明 注意fitSystemWindow
	public static void setStatusBarTranslucent(Window window) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			View decorView = window.getDecorView();
			decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			WindowManager.LayoutParams attributes = window.getAttributes();
			attributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			window.setAttributes(attributes);
		}
	}

	// 设置状态栏半透明
	public static void setStatusBarHalfTranslucent(Window window) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	// 设置状态栏字体颜色
	public static void setLightStatusBar(Window window, boolean lightMode) {
		LightStatusBarCompat.setLightStatusBar(window, lightMode);
	}

	// 设置状态栏颜色
	public static void setStatusBarColor(Window window, int color) {
		IMPL.setStatusBarColor(window, color);
	}

	interface IStatusBarColor {
		void setStatusBarColor(Window window, int color);
	}

	private static IStatusBarColor IMPL;

	static {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			IMPL = new MStatusBarColorImpl();
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !isEMUI()) {
			IMPL = new LollipopStatusBarColorImpl();
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			IMPL = new KitkatStatusBarColorImpl();
		} else {
			IMPL = new IStatusBarColor() {
				@Override
				public void setStatusBarColor(Window window, @ColorInt int color) {
				}
			};
		}
	}

	public static class KitkatStatusBarColorImpl implements IStatusBarColor {
		private static final String STATUS_BAR_VIEW_TAG = "ghStatusBarView";

		@TargetApi(Build.VERSION_CODES.KITKAT)
		@Override
		public void setStatusBarColor(Window window, int color) {
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
			View statusBarView = decorViewGroup.findViewWithTag(STATUS_BAR_VIEW_TAG);
			if (statusBarView == null) {
				statusBarView = new StatusBarView(window.getContext());
				statusBarView.setTag(STATUS_BAR_VIEW_TAG);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.TOP;
				statusBarView.setLayoutParams(params);
				decorViewGroup.addView(statusBarView);
			}
			statusBarView.setBackgroundColor(color);
			StatusBarCompat.internalSetFitsSystemWindows(window, true);
		}
	}

	public static class LollipopStatusBarColorImpl implements IStatusBarColor {
		@TargetApi(Build.VERSION_CODES.LOLLIPOP)
		@Override
		public void setStatusBarColor(Window window, int color) {
			//取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			//设置状态栏颜色
			window.setStatusBarColor(color);
		}
	}

	public static class MStatusBarColorImpl implements IStatusBarColor {

		@TargetApi(Build.VERSION_CODES.M)
		@Override
		public void setStatusBarColor(Window window, int color) {
			//取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			//设置状态栏颜色
			window.setStatusBarColor(color);

			// 去掉系统状态栏下的windowContentOverlay
			View v = window.findViewById(android.R.id.content);
			if (v != null) {
				v.setForeground(null);
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private static void internalSetFitsSystemWindows(Window window, boolean fitSystemWindows) {
		final ViewGroup contentView = window.findViewById(Window.ID_ANDROID_CONTENT);
		final View childView = contentView.getChildAt(0);
		if (childView != null) {
			//注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
			childView.setFitsSystemWindows(fitSystemWindows);
		}
	}

	public static int getStatusBarHeight(Context context) {
		int statusBarHeight = 0;
		Resources res = context.getResources();
		int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = res.getDimensionPixelSize(resourceId);
		}
		return statusBarHeight;
	}

	public static class StatusBarView extends View {

		private int mStatusBarHeight;

		public StatusBarView(Context context) {
			this(context, null);
		}

		public StatusBarView(Context context, @Nullable AttributeSet attrs) {
			super(context, attrs);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				mStatusBarHeight = getStatusBarHeight(context);
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mStatusBarHeight);
		}
	}
}
