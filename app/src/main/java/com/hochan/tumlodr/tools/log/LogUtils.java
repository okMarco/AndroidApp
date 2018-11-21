package com.hochan.tumlodr.tools.log;

import android.util.Log;

/**
 *
 * Created by zhendong_chen on 2016/8/22.
 */
public class LogUtils {
	/**
	 * 在 debug 下记录一些日志信息
	 */
	public static void log(String tag, Object msg, boolean isDebug) {
		if (isDebug) {
			System.out.println(tag + " = " + msg.toString());
		}
	}

	/**
	 * 在 debug 下记录一些日志信息
	 */
	public static void log(Object msg, boolean isDebug) {
		if (isDebug) {
			System.out.println(TraceUtils.getCallerCurrentStack() + "(自定义日志) = " + ObjectUtils.toString(msg));
		}
	}

	public static void debugInfo(Object msg) {
		log(msg, true);
	}

	public static void debugInfo(String tag, String msg) {
		Log.d(tag, msg);
	}

	public static void debugErrorInfo(Object msg) {
		log(msg, true);
	}
}
