package com.hochan.tumlodr.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * .
 * Created by hochan on 2017/10/27.
 */

public class Events<T> {

	public static final int EVENT_CODE_DOWNLOAD_FINISH = 1000 << 1;
	public static final int EVENT_CODE_REBLOG_SUCCESS = 1000 << 2;
	public static final int EVENT_CODE_REBLOG_FAILE = 1000 << 3;
	public static final int EVENT_USER_INFO_UPDATE = 1000 << 4;
	public static final int EVENT_CHANGE_THEME = 1000 << 5;
	public static final int EVENT_REMOVE_FLOATING_VIDEO = 1000 << 6;
	public static final int EVENT_PLAY_VIDEO = 1000 << 7;
	public static final int EVENT_SHAREELEMENT_ENTER_INDEX_CHANGE = 1000 << 8;
	public static final int EVENT_SHAREELEMENT_EXIT_INDEX_CHANGE = 1000 << 9;
	public static final int EVENT_IMAGE_SHAREELEMENT_CONTAINER = 1000 << 10;
	public static final int EVENT_IMAGE_NEW_DOWNLOAD = 1000 << 11;
	public static final int EVENT_DETAIL_POST_LIST_PRE_TO_DRAW = 1000 << 12;
	public static final int EVENT_FILE_ADD_TO_DOWNLOAD = 1000 << 13;
	public static final int EVENT_VIDEO_ADD_TO_DOWNLOAD_FAIL = 1000 << 14;
	public static final int EVENT_LIKE_POST_FAIL = 1000 << 15;
	public static final int EVENT_UPDATE_COLUMN_COUNT = 1000 << 16;
	public static final int EVENT_UPDATE_LAYOUT_STYLE = 1000 << 19;

	public static final int EVENT_SCROLL_TO_POSITION = 1000 << 17;

	public static final int EVENT_UPDATE_LAST_VISITED_BLOG = 1000 << 18;


	public int mCode;
	public T mContent;

	public Events(int code, T content) {
		mCode = code;
		mContent = content;
	}
}