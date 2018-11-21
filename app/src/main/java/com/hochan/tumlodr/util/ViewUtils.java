package com.hochan.tumlodr.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.hochan.arrowlib.ArrowDrawable;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.module.video.StaticAnimatorListenerAdapter;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.tools.ScreenTools;
import com.hochan.tumlodr.ui.activity.DownloadFileFullScreenActivity;
import com.hochan.tumlodr.ui.activity.FullScreenPhotoViewActivity;
import com.hochan.tumlodr.ui.activity.VideoViewPagerActivity;

import java.lang.ref.WeakReference;

/**
 * .
 * Created by hochan on 2018/1/31.
 */

public class ViewUtils {

	public static Drawable getArrowDrawable() {
		return getArrowDrawable(AppUiConfig.sTextColor);
	}

	public static Drawable getArrowDrawable(int color) {
		return new ArrowDrawable.Builder().arrowColor(color)
				.arrowDirection(ArrowDrawable.Builder.DIRECTON_LEFT)
				.arrowThickness(ScreenTools.dip2px(TumlodrApp.mContext, 2))
				.withTail(true)
				.margin(ScreenTools.dip2px(TumlodrApp.mContext, 19))
				.build();
	}

	public static void doAfterFadeOut(final View view, StaticAnimatorListenerAdapter animatorListenerAdapter) {
		if (view == null) {
			return;
		}
		final ViewPropertyAnimator viewPropertyAnimator = view.animate().alpha(0).setListener(animatorListenerAdapter);
		view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
			@Override
			public void onViewAttachedToWindow(View v) {
			}

			@Override
			public void onViewDetachedFromWindow(View v) {
				viewPropertyAnimator.cancel();
			}
		});
	}

	public static void doAfterFadeIn(final View view, final StaticAnimatorListenerAdapter animatorListenerAdapter) {
		final ViewPropertyAnimator viewPropertyAnimator = view.animate().alpha(1).setListener(animatorListenerAdapter);
		view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
			@Override
			public void onViewAttachedToWindow(View v) {
			}

			@Override
			public void onViewDetachedFromWindow(View v) {
				viewPropertyAnimator.cancel();
			}
		});
	}

	public static void setUiFlags(Window window, boolean fullscreen) {
		View decorView = window.getDecorView();
		if (decorView != null) {
			int flags = getLimitedUiFlags();
			decorView.setSystemUiVisibility(fullscreen ? getFullscreenUiFlags() : flags);
		}
	}

	private static int getLimitedUiFlags() {
		int flags = View.SYSTEM_UI_FLAG_VISIBLE;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE // 保持View Layout不变，隐藏状态栏或者导航栏后，View不会拉伸。
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // 让View全屏显示，Layout会被拉伸到StatusBar下面，不包含NavigationBar。
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;// 让View全屏显示，Layout会被拉伸到StatusBar和NavigationBar下面
		}
		return flags;
	}

	public static int getFullscreenUiFlags() {
		int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE // 保持View Layout不变，隐藏状态栏或者导航栏后，View不会拉伸。
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // 让View全屏显示，Layout会被拉伸到StatusBar下面，不包含NavigationBar。
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // 让View全屏显示，Layout会被拉伸到StatusBar和NavigationBar下面。
					| View.SYSTEM_UI_FLAG_FULLSCREEN // Activity全屏显示，且状态栏被隐藏覆盖掉。等同于（WindowManager.LayoutParams.FLAG_FULLSCREEN）。
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION; // 隐藏虚拟按键(导航栏)。有些手机会用虚拟按键来代替物理按键。
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				// 这个flag只有当设置了SYSTEM_UI_FLAG_HIDE_NAVIGATION才起作用。如果没有设置这个flag，
				// 任意的View相互动作都退出SYSTEM_UI_FLAG_HIDE_NAVIGATION模式。如果设置就不会退出。
				flags |= View.SYSTEM_UI_FLAG_IMMERSIVE
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			}
		}
		return flags;
	}

	public static boolean canDrawOverlays(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (Settings.canDrawOverlays(context)) {
				return true;
			} else {
				try {
					//若没有权限，提示获取.
					Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
					context.startActivity(intent);
				} catch (Exception ignored) {
					Toast.makeText(context, R.string.allow_draw_overlay, Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		} else {
			return true;
		}
	}

	public static void setScreenOrientationPortrait(Activity activity) {
		setScreenOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	public static void setScreenOrientationLandscape(Activity activity) {
		setScreenOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	public static void setScreenOrientationUser(Activity activity) {
		setScreenOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_USER);
	}

	private static void setScreenOrientation(Activity activity, int orientation) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			activity.setRequestedOrientation(orientation);
		} else {
			int index = -1;
			for (int i = TumlodrApp.ACTIVITY_WEAK_REFERENCE_LIST.size() - 1; i >= 0; i--) {
				WeakReference<Activity> activityWeakReference = TumlodrApp.ACTIVITY_WEAK_REFERENCE_LIST.get(i);
				if (activityWeakReference != null && activityWeakReference.get() != null
						&& !(activityWeakReference.get() instanceof VideoViewPagerActivity)
						&& !(activityWeakReference.get() instanceof FullScreenPhotoViewActivity)
						&& !(activityWeakReference.get() instanceof DownloadFileFullScreenActivity)) {
					index = i;
				}
			}
			if (index >= 0 && index < TumlodrApp.ACTIVITY_WEAK_REFERENCE_LIST.size() - 1) {
				if (TumlodrApp.ACTIVITY_WEAK_REFERENCE_LIST.get(index).get() != null) {
					try {
						TumlodrApp.ACTIVITY_WEAK_REFERENCE_LIST.get(index).get().setRequestedOrientation(orientation);
					} catch (IllegalStateException exception) {
						Crashlytics.logException(exception);
					}
				}
			}
		}
	}

	public static void likeAnimation(View likeView) {
		if (likeView == null || likeView.getContext() == null) {
			return;
		}
		if (!(likeView.getContext() instanceof Activity)) {
			return;
		}
		Activity activity = (Activity) likeView.getContext();
		if (activity == null) {
			return;
		}
		if (activity.getWindow().getDecorView() instanceof ViewGroup) {
			final ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
			final ImageView view = new ImageView(activity);
			view.setPivotX(0.5f);
			view.setPivotY(0.5f);
			view.setBackgroundResource(R.drawable.ic_popup_liked);
			ViewGroup.MarginLayoutParams layoutParams =
					new ViewGroup.MarginLayoutParams(likeView.getMeasuredWidth(),
							likeView.getMeasuredWidth());
			final int[] location = new int[2];
			likeView.getLocationInWindow(location);
			layoutParams.leftMargin = location[0];
			layoutParams.topMargin = location[1];
			viewGroup.addView(view, layoutParams);
			final WeakReference<ImageView> viewWeakReference = new WeakReference<>(view);
			SpringConfig springConfig = SpringConfig.fromOrigamiTensionAndFriction(0, 0);
			springConfig.tension = 0;

			final double scale = 2.5;
			final int viewHeight = (int) (likeView.getMeasuredWidth() * scale);
			final Spring ySpring = SpringSystem.create().createSpring().setSpringConfig(springConfig);
			final Spring sSpring = SpringSystem.create().createSpring();
			final Spring xSpring = SpringSystem.create().createSpring().setSpringConfig(springConfig);
			final Spring rSpring = SpringSystem.create().createSpring().setSpringConfig(springConfig);

			ySpring.addListener(new SimpleSpringListener() {
				@Override
				public void onSpringUpdate(Spring spring) {
					if (viewWeakReference.get() != null) {
						viewWeakReference.get().setTranslationY((float) spring.getCurrentValue());
						viewWeakReference.get().setAlpha((float) (1 - spring.getCurrentValue() / spring.getEndValue()));
						if (spring.getCurrentValue() <= spring.getEndValue()) {
							xSpring.destroy();
							ySpring.destroy();
							sSpring.destroy();
							rSpring.destroy();
							if (viewWeakReference.get().getParent() != null) {
								((ViewGroup) viewWeakReference.get().getParent()).removeView(viewWeakReference.get());
							}
						}
					}
				}
			});

			xSpring.addListener(new SimpleSpringListener() {
				@Override
				public void onSpringUpdate(Spring spring) {
					if (viewWeakReference.get() != null) {
						viewWeakReference.get().setTranslationX((float) spring.getCurrentValue());
					}
				}
			});

			rSpring.addListener(new SimpleSpringListener() {
				@Override
				public void onSpringUpdate(Spring spring) {
					if (viewWeakReference.get() != null) {
						viewWeakReference.get().setRotation((float) spring.getCurrentValue());
					}
				}
			});


			sSpring.addListener(new SimpleSpringListener() {
				@Override
				public void onSpringUpdate(Spring spring) {
					if (viewWeakReference.get() != null) {
						viewWeakReference.get().setScaleX((float) spring.getCurrentValue());
						viewWeakReference.get().setScaleY((float) spring.getCurrentValue());
					}
				}
			});
			sSpring.setCurrentValue(1);
			sSpring.setEndValue(scale);
			ySpring.setEndValue((location[1] + viewHeight) * -1);
			rSpring.setEndValue(-180 + Math.random() * 360);

			double magnitude = 2000;
			double angle = Math.PI - (Math.PI / 12 + Math.random() * Math.PI / 12 * 11);
			final double y = magnitude * 3 / -4;
			final double x = magnitude / 10 * Math.cos(angle);
			xSpring.setVelocity(x);
			ySpring.setVelocity(y);
			rSpring.setVelocity(x);
		}
	}
}
