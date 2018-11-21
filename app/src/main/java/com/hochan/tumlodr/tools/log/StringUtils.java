package com.hochan.tumlodr.tools.log;

import com.hochan.tumlodr.tools.log.constant.ConstString;

/**
 * @see StringUtils 类
 * <pre>
 * 常用字符串工具类
 * </pre>
 * <ul>
 * <strong>常用</strong>
 * <li>{@link #toString()}</li>
 * <li>{@link #isEmpty(String)} 是空</li>
 * <li>{@link #isNotEmpty(String)} 不是空</li>
 * <li>{@link #trim(String)} null to empty, trim前后空格</li>
 * <li>{@link #nullToEmpty(String)} 为null就转换成empty</li>
 * <li>{@link #emptyToNull(String)} 为empty就转换成null</li>
 * </ul>
 * <p/>
 * Created by hnusr on 2015-02-01.
 */
public final class StringUtils {

	private StringUtils() {
	}

	/**
	 * 是空
	 */
	public static boolean isEmpty(String input) {
		return StringCompareUtils.isAllEmpty(input);
	}

	/**
	 * 不是空
	 */
	public static boolean isNotEmpty(String input) {
		return StringCompareUtils.isNotEmpty(input);
	}

	/**
	 * null to empty, trim前后空格
	 */
	public static String trim(String input) {
		return input == null ? ConstString.EMPTY : input.trim();
	}

	/**
	 * 为null就转换成empty
	 */
	public static String nullToEmpty(String input) {
		return input == null ? ConstString.EMPTY : input;
	}

	/**
	 * 为empty就转换成null
	 */
	public static String emptyToNull(String input) {
		return StringCompareUtils.isNullOrEmpty(input) ? null : input;
	}

}
