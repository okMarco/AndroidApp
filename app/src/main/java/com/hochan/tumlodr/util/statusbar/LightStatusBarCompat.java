package com.hochan.tumlodr.util.statusbar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 设置状态栏字体颜色
 * .
 * Created by czd on 2018/1/27.
 */

public class LightStatusBarCompat {

	interface ILightStatusBar {
		void setLightStatusBar(Window window, boolean lightStatusBar);
	}

	private static final ILightStatusBar IMPL;

	static void setLightStatusBar(Window window, boolean lightStatusBar) {
		IMPL.setLightStatusBar(window, lightStatusBar);
	}

	static {
		if (PhoneSystemCompat.isMIUI()) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				IMPL = new MLightStatusBarImpl() {
					private final ILightStatusBar DELEGATE = new MIUILightStatusBarImpl();

					@Override
					public void setLightStatusBar(Window window, boolean lightStatusBar) {
						super.setLightStatusBar(window, lightStatusBar);
						DELEGATE.setLightStatusBar(window, lightStatusBar);
					}
				};
			} else {
				IMPL = new MIUILightStatusBarImpl();
			}
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			IMPL = new MLightStatusBarImpl();
		} else if (PhoneSystemCompat.isMEIZU()) {
			IMPL = new MeizuLightStatusBarImpl();
		} else if (PhoneSystemCompat.isOPPO()) {
			IMPL = new OppoLightStatusBarImpl();
		} else {
			IMPL = new ILightStatusBar() {
				@Override
				public void setLightStatusBar(Window window, boolean lightStatusBar) {
				}
			};
		}
	}

	private static class MLightStatusBarImpl implements ILightStatusBar {

		@TargetApi(Build.VERSION_CODES.M)
		@Override
		public void setLightStatusBar(Window window, boolean lightStatusBar) {
			// 设置浅色状态栏时的界面显示
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			View decor = window.getDecorView();
			int ui = decor.getSystemUiVisibility();
			if (lightStatusBar) {
				ui |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
			} else {
				ui &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
			}
			decor.setSystemUiVisibility(ui);
		}
	}

	private static class MIUILightStatusBarImpl implements ILightStatusBar {

		@Override
		public void setLightStatusBar(Window window, boolean lightStatusBar) {
			Class<? extends Window> clazz = window.getClass();
			try {
				@SuppressLint("PrivateApi")
				Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
				Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
				int darkModeFlag = field.getInt(layoutParams);
				Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
				extraFlagField.invoke(window, lightStatusBar ? darkModeFlag : 0, darkModeFlag);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static class MeizuLightStatusBarImpl implements ILightStatusBar {

		@Override
		public void setLightStatusBar(Window window, boolean lightStatusBar) {
			WindowManager.LayoutParams params = window.getAttributes();
			try {
				Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
				Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
				darkFlag.setAccessible(true);
				meizuFlags.setAccessible(true);
				int bit = darkFlag.getInt(null);
				int value = meizuFlags.getInt(params);
				if (lightStatusBar) {
					value |= bit;
				} else {
					value &= ~bit;
				}
				meizuFlags.setInt(params, value);
				window.setAttributes(params);
				darkFlag.setAccessible(false);
				meizuFlags.setAccessible(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static class OppoLightStatusBarImpl implements ILightStatusBar {

		static final int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010;

		@Override
		public void setLightStatusBar(Window window, boolean lightStatusBar) {
			int vis = window.getDecorView().getSystemUiVisibility();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				if (lightStatusBar) {
					vis |= SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
				} else {
					vis &= ~SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
				}
			}
			window.getDecorView().setSystemUiVisibility(vis);
		}
	}
}
