package com.hochan.tumlodr.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.crashlytics.android.Crashlytics;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.model.data.download.DownloadRecordDatabase;
import com.hochan.tumlodr.module.listener.SimpleFileDownloadListener;
import com.hochan.tumlodr.ui.component.SingleMediaScanner;
import com.hochan.tumlodr.util.Events;
import com.hochan.tumlodr.util.RxBus;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;

import java.io.File;
import java.io.IOException;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

import static android.content.Context.MODE_PRIVATE;

/**
 * .
 * <p>
 * Created by hochan on 2016/5/22.
 */
public class AppConfig {

	public final static String APP_NAME = "Tumlodr";

	public final static String CONSUMER_KEY = "7heCKuKOlE9ogNpYnv0tebQuczJvjzYqJsSVq7TwqUwZZ27yzr";
	public final static String CONSUMER_SECRET = "ES8dYCiqhOCH3DhxL45Pq4UJ0wLbKKDWxKVKR9jTZVrkkN1sAx";

	public static final String CALLBACK_URL = "http://www.tumblr.com/dashboard";

	public static final String SHARE_USER = "user";
	public static final String SHARE_USER_OAUTH_TOKEN = "OAuthToken";
	public static final String SHARE_USER_OAUTH_TOKEN_SECRET = "OAuthTokenSecret";

	public static final String SHARE_CUSTOM_CONSUMER_KEY = "custom_consumer_key";
	public static final String SHARE_CUSTOM_SECRET_KEY = "custom_secret_key";

	public static final String SHARE_THEME = "theme";
	public static final String SHARE_THEME_ID = "theme_id";
	public static final String SHARE_POST_LIST_COLUMN_COUNT = "post_list_column_count";
	public static final String SHARE_POST_LIST_LAYOUT_STYLE = "post_list_layout_style";

	public static final String SHARE_RESOLUTION = "resolution";
	public static final String SHARE_PLAY_VIDEO_AUTO = "paly_gif";
	public static final String SHARE_FORCE_STITCHING_VIDEO_URL = "force_stiching_video_url";
	public static final String SHARE_PIC_STORAGE = "pic_storage";

	public static final String SHARE_USER_INFO = "user_info";
	public static final String SHARE_USER_NAME = "user_name";
	public static final String SHARE_USER_INFO_BLOG = "user_blogs";
	public static final String SHARE_USER_INFO_LIKE_COUNT = "user_like_count";
	public static final String SHARE_USER_INFO_FOLLOWING_COUNT = "user_following_count";


	public static final String SHARE_VISITED_BLOG_LIST = "visited_blog_list";


	public static final int RESOLUTION_DEFAULT = 0;
	public static final int RESOLUTION_LOW = 1;
	public static final int RESOLUTION_HIGH = 2;

	public static final int LAYOUT_WATER_FALL = 0;
	public static final int LAYOUT_DETAIL = 1;

	public static String mStoragePath = "";
	private static String VIDEO_THUMBNAIL_DIR = "";
	private static String VIDEO_THUMBNAIL_SUB_DIR = "/video_thumtail/";
	private static String AVATAR_DIR = "";
	private static String AVATAR_SUB_DIR = "/blog_avatar/";

	public static int sPostListLayoutStyle = LAYOUT_WATER_FALL;
	public static int mPostListColumnCount = 3;
	public static int mResolution = AppConfig.RESOLUTION_DEFAULT;
	public static boolean sPlayVideoAuto = true;
	public static boolean sForceStitchingVideoUrl = false;

	public static BaseDownloadTask.FinishListener mDownloadFinishListener;
	public static FileDownloadListener mFileDownloadListener;

	public static void initAppConfig(Context context) {
		SharedPreferences spTheme = context.getSharedPreferences(AppConfig.SHARE_THEME, MODE_PRIVATE);
		mPostListColumnCount = spTheme.getInt(AppConfig.SHARE_POST_LIST_COLUMN_COUNT, 3);
		sPostListLayoutStyle = spTheme.getInt(AppConfig.SHARE_POST_LIST_LAYOUT_STYLE, LAYOUT_WATER_FALL);
		mResolution = spTheme.getInt(AppConfig.SHARE_RESOLUTION, AppConfig.RESOLUTION_DEFAULT);
		sPlayVideoAuto = spTheme.getBoolean(AppConfig.SHARE_PLAY_VIDEO_AUTO, true);
		sForceStitchingVideoUrl = spTheme.getBoolean(AppConfig.SHARE_FORCE_STITCHING_VIDEO_URL, false);
		initStoragePath();
		initFileDownloadListener();
		setRxJavaErrorHandler();
	}

	private static void setRxJavaErrorHandler() {
		RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
			@Override
			public void accept(Throwable e) throws Exception {
				if (e instanceof UndeliverableException) {
					e = e.getCause();
				}
				if (e instanceof IOException) {
					// fine, irrelevant network problem or API that throws on cancellation
					return;
				}
				if (e instanceof InterruptedException) {
					// fine, some blocking code was interrupted by a dispose call
					return;
				}
				if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
					// that's likely a bug in the application
					Thread.currentThread().getUncaughtExceptionHandler()
							.uncaughtException(Thread.currentThread(), e);
					return;
				}
				if (e instanceof IllegalStateException) {
					// that's a bug in RxJava or in a custom operator
					Thread.currentThread().getUncaughtExceptionHandler()
							.uncaughtException(Thread.currentThread(), e);
				}
			}
		});
	}

	private static void initStoragePath() {
		SharedPreferences sharedPreferences = TumlodrApp.mContext.getSharedPreferences(SHARE_PIC_STORAGE, MODE_PRIVATE);
		AppConfig.mStoragePath = sharedPreferences.getString(AppConfig.SHARE_PIC_STORAGE, Tools.getDefaultPicStoragePath());
		File file = new File(AppConfig.mStoragePath);
		if (!(file.exists() || file.mkdirs())) {
			AppConfig.mStoragePath = Tools.getDefaultPicStoragePath();
		}
		sharedPreferences.edit().putString(AppConfig.SHARE_PIC_STORAGE, AppConfig.mStoragePath).apply();

		AppConfig.VIDEO_THUMBNAIL_DIR = TumlodrApp.mContext.getCacheDir().getAbsolutePath() + AppConfig.VIDEO_THUMBNAIL_SUB_DIR;
		File videoThumbnailDir = new File(AppConfig.VIDEO_THUMBNAIL_DIR);
		if (!videoThumbnailDir.exists()) {
			//noinspection ResultOfMethodCallIgnored
			videoThumbnailDir.mkdirs();
		}

		AppConfig.AVATAR_DIR = TumlodrApp.mContext.getCacheDir().getAbsolutePath() + AppConfig.AVATAR_SUB_DIR;
		File blogAvatar = new File(AppConfig.AVATAR_DIR);
		if (!blogAvatar.exists()) {
			//noinspection ResultOfMethodCallIgnored
			blogAvatar.mkdirs();
		}
	}

	private static void initFileDownloadListener() {
		mDownloadFinishListener = new BaseDownloadTask.FinishListener() {
			@Override
			public void over(BaseDownloadTask task) {
				if (new File(task.getPath()).exists()) {
					RxBus.getInstance().send(new Events<>(Events.EVENT_CODE_DOWNLOAD_FINISH, task));
					DownloadRecordDatabase.updateDownloadFinish(task.getId());
					new SingleMediaScanner(task.getPath());
				}
			}
		};

		mFileDownloadListener = new SimpleFileDownloadListener() {

			@Override
			protected void error(BaseDownloadTask task, Throwable e) {
				e.printStackTrace();
				Crashlytics.logException(e);
				RxBus.getInstance().send(new Events<>(Events.EVENT_CODE_DOWNLOAD_FINISH, task));
			}
		};
	}

	public static void changeStoragePathToExternalStorage() {
		SharedPreferences sharedPreferences = TumlodrApp.mContext.getSharedPreferences(SHARE_PIC_STORAGE, MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		AppConfig.mStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
		editor.putString(SHARE_PIC_STORAGE, AppConfig.mStoragePath);
		editor.apply();
	}
}
