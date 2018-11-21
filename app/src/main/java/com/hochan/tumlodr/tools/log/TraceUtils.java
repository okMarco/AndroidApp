package com.hochan.tumlodr.tools.log;


import com.hochan.tumlodr.tools.log.constant.ConstAscii;

/**
 * ClassUtils 类
 * <pre>
 * 与类有关的方法
 * </pre>
 * <ul>
 * <strong>常用</strong>
 * <li>{@link #getCallerClassName()} 得到调用者当前的类名</li>
 * <li>{@link #getCallerMethodName()} 得到调用者当前的方法名</li>
 * <li>{@link #getCallerFileName()} 得到调用者当前的文件名</li>
 * <li>{@link #getCallerLineNumber()} 得到调用者当前行数</li>
 * <li>{@link #getCallerCurrentStack()} 得到调用者当前的类名、方法名、文件名、行数，一般用于debug日志</li>
 * <li>{@link #getCallerAllStack()} 得到调用者当前到最开始的所有调用信息</li>
 * </ul>
 * <p/>
 * Created by hnusr on 2015-01-17.
 */
public final class TraceUtils {

	/**
	 * 得到调用者当前的类名
	 */
	public static String getCallerClassName() {
		return getCurrentCaller().getClassName();
	}

	/**
	 * 得到调用者当前的方法名
	 */
	public static String getCallerMethodName() {
		return getCurrentCaller().getMethodName();
	}

	/**
	 * 得到调用者当前的文件名
	 */
	public static String getCallerFileName() {
		return getCurrentCaller().getFileName();
	}

	/**
	 * 得到调用者当前行数
	 */
	public static int getCallerLineNumber() {
		return getCurrentCaller().getLineNumber();
	}

	/**
	 * 得到调用者当前的类名、方法名、文件名、行数，一般用于debug日志
	 */
	public static String getCallerCurrentStack() {
		final StackTraceElement caller = getCurrentCaller();
		return caller.toString();
	}

	/**
	 * 得到调用者当前到最开始的所有调用信息
	 */
	public static String getCallerAllStack() {
		return getStackTraceElement(ConstAscii.CHAR_LF);
	}

	//
	// ========================== 私有方法 ========================= //
	//

	/**
	 * 得到当前调用者
	 */
	private static StackTraceElement getCurrentCaller() {
		return new Throwable().fillInStackTrace().getStackTrace()[3];
	}

	/**
	 * 得到所有调用
	 */
	private static String getStackTraceElement(char separator) {
		StackTraceElement[] elementArray = new Throwable().fillInStackTrace().getStackTrace();
		return ArrayConvertUtils.toString(elementArray, separator);
	}


}
