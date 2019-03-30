package com.hochan.tumlodr.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.hochan.tumlodr.jumblr.types.Post;
import com.hochan.tumlodr.tools.Tools;
import com.hochan.tumlodr.ui.component.IPhotoLayout;
import com.hochan.tumlodr.util.Events;
import com.hochan.tumlodr.util.RxBus;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation;

/**
 * .
 * Created by hochan on 2017/9/27.
 */

@SuppressWarnings("WeakerAccess")
public class Router {

	static final String EXTRA_VIDEO_EMBED_CODE = "embed_code";
	public static final String EXTRA_VIDEO_STORAGE_PATH = "video_path";
	public static final String EXTRA_BLOG_NAME = "blog_name";
	static final String EXTRA_POST_ID = "post_id";
	static final String EXTRA_RELOAG_KEY = "reblog_key";
	public static final String EXTRA_IS_LIKED = "like";
	public static final String EXTRA_IMAGE_FILE_PATH_LIST = "image_file_path_list";
	public static final String EXTRA_DEFAULT_INDEX = "default_index";

	public static final String STORAGE_PATH = "storage_path";

	public static final String EXTRA_IMAGE_URL_LIST = "image_url_list";
	public static final String EXTRA_THUMBNAIL_IMAGE_URL_LIST = "thumbnail_image_url_list";
	public static final String EXTRA_NORMAL_IMAGE_URL_LIST = "normal_image_url_list";

	public static final String SHAREELEMENT_NAME = "image";

	public static final String REFRESH_ON_CREATE = "refresh_on_create";

	public static final String GROUP_NAME = "group_name";

	//public static Drawable sShareImageViewDrawable;

	public static void showDashboard(Activity activity) {
		Intent intent = new Intent(activity, SplashActivity.class);
		activity.startActivity(intent);
	}

	public static void showImage(Activity activity, IPhotoLayout postPhotoLayout, int imageIndex, Post post) {
		Intent intent = new Intent(activity, FullScreenPhotoViewActivity.class);
		intent.putExtra(EXTRA_DEFAULT_INDEX, imageIndex);
		intent.putStringArrayListExtra(EXTRA_IMAGE_URL_LIST,
				postPhotoLayout.getPhotoUrls());
		intent.putStringArrayListExtra(EXTRA_NORMAL_IMAGE_URL_LIST,
				postPhotoLayout.getPhotoNormalUrls());
		FullScreenPhotoViewActivity.sPost = post;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			View shareView = postPhotoLayout.getImageViewInPosition(imageIndex);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				shareView.setTransitionName(SHAREELEMENT_NAME);
			}
			activity.startActivity(intent, makeSceneTransitionAnimation(activity, shareView, SHAREELEMENT_NAME).toBundle());
		} else {
			activity.startActivity(intent);
		}
		RxBus.getInstance().send(new Events<>(Events.EVENT_IMAGE_SHAREELEMENT_CONTAINER, postPhotoLayout));
		RxBus.getInstance().send(new Events<>(Events.EVENT_SHAREELEMENT_ENTER_INDEX_CHANGE, imageIndex));
	}

	public static void showImage(Activity activity, String blogName, View shareView) {
		Intent intent = new Intent(activity, FullScreenPhotoViewActivity.class);
		intent.putExtra(EXTRA_DEFAULT_INDEX, 0);
		List<String> imageUrl = new ArrayList<>();
		imageUrl.add(Tools.getAvatarUrlByBlogName(blogName, 512));
		intent.putStringArrayListExtra(EXTRA_IMAGE_URL_LIST, (ArrayList<String>) imageUrl);
		List<String> normalUrl = new ArrayList<>();
		normalUrl.add(Tools.getAvatarUrlByBlogName(blogName, 128));
		intent.putStringArrayListExtra(EXTRA_NORMAL_IMAGE_URL_LIST, (ArrayList<String>) normalUrl);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				shareView.setTransitionName(SHAREELEMENT_NAME);
			}
			activity.startActivity(intent, makeSceneTransitionAnimation(activity, shareView, SHAREELEMENT_NAME).toBundle());
		} else {
			activity.startActivity(intent);
		}
	}

	public static void showDownloadFile(Activity activity, List<String> filePaths, List<String> thumbnails, int defaultIndex, View shareView) {
		RxBus.getInstance().send(new Events<>(Events.EVENT_SHAREELEMENT_ENTER_INDEX_CHANGE, defaultIndex));
		Intent intent = new Intent(activity, DownloadFileFullScreenActivity.class);
		intent.putExtra(EXTRA_DEFAULT_INDEX, defaultIndex);
		intent.putStringArrayListExtra(EXTRA_IMAGE_URL_LIST, (ArrayList<String>) filePaths);
		intent.putStringArrayListExtra(EXTRA_NORMAL_IMAGE_URL_LIST, (ArrayList<String>) thumbnails);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				shareView.setTransitionName(SHAREELEMENT_NAME);
			}
			activity.startActivity(intent, makeSceneTransitionAnimation(activity, shareView, SHAREELEMENT_NAME).toBundle());
		} else {
			activity.startActivity(intent);
		}
	}

	public static void showDownloadFile(Activity activity, int defaultIndex, View shareView) {
		RxBus.getInstance().send(new Events<>(Events.EVENT_SHAREELEMENT_ENTER_INDEX_CHANGE, defaultIndex));
		Intent intent = new Intent(activity, DownloadFileFullScreenActivity.class);
		intent.putExtra(EXTRA_DEFAULT_INDEX, defaultIndex);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				shareView.setTransitionName(SHAREELEMENT_NAME);
			}
			activity.startActivity(intent, makeSceneTransitionAnimation(activity, shareView, SHAREELEMENT_NAME).toBundle());
		} else {
			activity.startActivity(intent);
		}
	}
}
