package com.hochan.tumlodr.module.glide;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.LruCache;
import android.view.View;

import com.hochan.tumlodr.jumblr.types.Photo;

/**
 * .
 * Created by hochan on 2018/6/9.
 */

public class TumlodrGlideUtil {

	public static final LruCache<Photo, String> PHOTO_NORMAL_URL_CACHE = new LruCache<>(100);

	public static boolean isContextValid(View view) {
		return view != null && isContextValid(view.getContext());
	}

	public static boolean isContextValid(Fragment fragment) {
		return fragment != null && isContextValid(fragment.getActivity());
	}

	public static boolean isContextValid(Context context) {
		if (context == null) {
			return false;
		}
		if (context instanceof Activity) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				return !((Activity) context).isDestroyed();
			}
			return !((Activity) context).isFinishing();
		}
		return false;
	}
}
