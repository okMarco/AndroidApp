package com.hochan.tumlodr.tools;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.widget.ImageView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.glide.TumlodrGlideUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * .
 * Created by hochan on 2016/5/24.
 */
public class Tools {

	public static String getDefaultPicStoragePath() {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
	}

	public static String getStoragePath(Context mContext, boolean is_removale) {
		StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
		Class<?> storageVolumeClazz;
		try {
			storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
			Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
			@SuppressWarnings("JavaReflectionMemberAccess")
			Method getPath = storageVolumeClazz.getMethod("getPath");
			Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
			Object result = getVolumeList.invoke(mStorageManager);
			final int length = Array.getLength(result);
			for (int i = 0; i < length; i++) {
				Object storageVolumeElement = Array.get(result, i);
				String path = (String) getPath.invoke(storageVolumeElement);
				boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
				if (is_removale == removable) {
					return path;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final long ONE_KB = 1024;
	private static final long ONE_MB = 1024 * 1024;
	private static final long ONE_GB = 1024 * ONE_MB;

	public static String getSizeFormat(long size) {
		DecimalFormat df = new DecimalFormat("#.##");
		if (size < ONE_KB) {
			return String.valueOf(size) + "B";
		} else if (size < ONE_MB) {
			double result = size / 1024.0;
			return df.format(result) + "KB";
		} else if (size < ONE_GB) {
			double result = size * 1.0 / (ONE_MB * 1.0);
			return df.format(result) + "MB";
		} else {
			double result = size * 1.0 / (ONE_GB * 1.0);
			return df.format(result) + "GB";
		}
	}

	public static String getPicNameByUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return String.valueOf(System.currentTimeMillis()) + ".jpg";
		}
		String name = url.substring(url.lastIndexOf("/") + 1, url.length());
		if (isNumeric(name.replace(".jpg", ""))) {
			return url.replace("://", "_").replace("/", "_").replace(":", "").replace(".", "_");
		}
		return name;
	}

	public static String getStoragePathByFileName(@NonNull String fileName) {
		if (AppConfig.mStoragePath.endsWith(File.separator)) {
			return AppConfig.mStoragePath + fileName;
		} else {
			return AppConfig.mStoragePath + File.separator + fileName;
		}
	}

	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

	public static String getAvatarUrlByBlogName(String blogName) {
		return String.format("https://api.tumblr.com/v2/blog/%s.tumblr.com/avatar/128",
				blogName);
	}

	/**
	 * 16, 24, 30, 40, 48, 64, 96, 128, 512
	 */
	public static String getAvatarUrlByBlogName(String blogName, int size) {
		return String.format("https://api.tumblr.com/v2/blog/%s.tumblr.com/avatar/%s",
				blogName, String.valueOf(size));
	}

	public static void loadAvatar(final ImageView imageView, String blogName) {
		loadAvatar(imageView, blogName, 128);
	}

	@SuppressWarnings("SameParameterValue")
    private static void loadAvatar(final ImageView imageView, String blogName, int size) {
		String avatarUrl = Tools.getAvatarUrlByBlogName(blogName, size);
		if (TumlodrGlideUtil.isContextValid(imageView)) {
			TumlodrGlide.with(imageView)
					.asBitmap()
					.load(avatarUrl)
					.placeholder(AppUiConfig.sPicHolderResource)
					.skipMemoryCache(true)
					.transform(new MultiTransformation<>(new RoundedCorners(5),
							new FitCenter()))
					.into(imageView);
		}
	}

	public static CharSequence getRelativeTime(String dateGmt, CharSequence lastPostDate) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'GMT'", Locale.US);
		format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
		Date date = null;
		try {
			date = format.parse(dateGmt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (date != null) {
			lastPostDate = getRelativeTimeSpanString(TumlodrApp.mContext,
					System.currentTimeMillis(), date.getTime(),
					DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_YEAR);
		}
		return lastPostDate;
	}

	public static CharSequence getRelativeTime(long timeMs) {
		return getRelativeTimeSpanString(TumlodrApp.mContext,
				System.currentTimeMillis(), timeMs * 1000,
				DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_YEAR);
	}

	@SuppressWarnings("SameParameterValue")
    private static CharSequence getRelativeTimeSpanString(@NonNull Context context, long nowMs,
                                                          long timeMs, int flags) {
		if (nowMs - timeMs < DateUtils.SECOND_IN_MILLIS) {
			return context.getString(R.string.post_detail_time_just_now);
		} else {
			return DateUtils.getRelativeTimeSpanString(timeMs, nowMs,
					DateUtils.SECOND_IN_MILLIS, flags);
		}
	}

}
