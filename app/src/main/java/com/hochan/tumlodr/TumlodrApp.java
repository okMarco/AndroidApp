package com.hochan.tumlodr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.FileNameGenerator;
import com.google.android.gms.ads.MobileAds;
import com.hochan.tumlodr.model.ApiConstants;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.ui.component.TumlodrBottomAdsLayout;
import com.hochan.tumlodr.util.FileDownloadUtil;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.util.FileDownloadLog;
import com.squareup.leakcanary.LeakCanary;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * .
 * Created by hochan on 2016/5/22.
 */
public class TumlodrApp extends MultiDexApplication {

	public static String sOAuthToken;
	public static String sOAuthTokenSecret;
	@SuppressLint("StaticFieldLeak")
	public static Context mContext;

	public static final List<WeakReference<Activity>> ACTIVITY_WEAK_REFERENCE_LIST = new ArrayList<>();

	private ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {

		@Override
		public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
			ACTIVITY_WEAK_REFERENCE_LIST.add(new WeakReference<>(activity));
		}

		@Override
		public void onActivityStarted(Activity activity) {

		}

		@Override
		public void onActivityResumed(Activity activity) {

		}

		@Override
		public void onActivityPaused(Activity activity) {

		}

		@Override
		public void onActivityStopped(Activity activity) {

		}

		@Override
		public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

		}

		@Override
		public void onActivityDestroyed(Activity activity) {
			for (int i = ACTIVITY_WEAK_REFERENCE_LIST.size() - 1; i >= 0; i--) {
				WeakReference<Activity> activityWeakReference = ACTIVITY_WEAK_REFERENCE_LIST.get(i);
				if (activityWeakReference != null && activityWeakReference.get() != null && activityWeakReference.get() == activity) {
					ACTIVITY_WEAK_REFERENCE_LIST.remove(activityWeakReference);
				}
			}
		}
	};

	@Override
	public void onCreate() {
		SharedPreferences spTheme = getSharedPreferences(AppConfig.SHARE_THEME, MODE_PRIVATE);
		AppUiConfig.setTumlodrTheme(this, spTheme.getInt(AppConfig.SHARE_THEME_ID, R.style.AppTheme_NoActionBar_White));
		setTheme(AppUiConfig.sThemId);
		super.onCreate();
		mContext = getApplicationContext();

		AppConfig.initAppConfig(this);

		// just for open the log in this demo project.
		FileDownloadLog.NEED_LOG = BuildConfig.DEBUG;

		MobileAds.initialize(this, TumlodrBottomAdsLayout.MOBILE_ADS_KEY);

		FileDownloader.setupOnApplicationOnCreate(this)
				.connectionCreator(new FileDownloadUrlConnection
						.Creator(new FileDownloadUrlConnection.Configuration()
						.connectTimeout(150000) // set connection timeout.
						.readTimeout(150000) // set read timeout.
						.proxy(Proxy.NO_PROXY) // set proxy
				))
				.commit();

		registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);

		if (LeakCanary.isInAnalyzerProcess(this)) {
			return;
		}
		LeakCanary.install(this);
	}

	public static Context getContext() {
		return mContext;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		TumlodrGlide.get(this).onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		TumlodrGlide.get(this).onTrimMemory(level);
	}

	private HttpProxyCacheServer proxy;

	public static HttpProxyCacheServer getProxy() {
		TumlodrApp app = (TumlodrApp) TumlodrApp.getContext();
		return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
	}

	private HttpProxyCacheServer newProxy() {
		return new HttpProxyCacheServer.Builder(this)
				.cacheDirectory(getAppCacheDir())
				.maxCacheFilesCount(100)
				.fileNameGenerator(new FileNameGenerator() {
					@Override
					public String generate(String url) {
						return FileDownloadUtil.geVideoNameByVideoUrl(url);
					}
				})
				.build();
	}

	public static File getAppCacheDir() {
		return new File(TumlodrApp.getContext().getCacheDir(), "video_cache");
	}
}
