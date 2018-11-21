package com.hochan.tumlodr.tools.log;

import com.hochan.tumlodr.tools.log.constant.ConstSign;
import com.hochan.tumlodr.tools.log.constant.ConstString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @see ArrayConvertUtils 类
 * <pre>
 * 转换，从字符串转换成列表，或从列表转换成字符串
 * </pre>
 * <ul>
 * <strong>字符串间隔与列表相互转换</strong>
 * <li>{@link #toString(List)} 用默认的逗号把列表转成字符串</li>
 * <li>{@link #toString(Object[])} 用默认的逗号把列表转成字符串</li>
 * <li>{@link #toString(List, char)} 用间隔字符转换成字符串</li>
 * <li>{@link #toString(Object[], char)} 用间隔字符转换成字符串</li>
 * <li>{@link #toString(List, String)} 用间隔字符串转换成字符串</li>
 * <li>{@link #toString(Object[], String)} 用间隔字符串转换成字符串</li>
 * <li>{@link #fromString(String, char)} 从间隔字符串转换成列表</li>
 * <li>{@link #fromString(String, String)} 从间隔字符串转换成列表</li>
 * </ul>
 * <ul>
 * <strong>列表转换成数组</strong>
 * <li>{@link #toArrayByString(List<String>)} 从字符串列表转换成数组</li>
 * <li>{@link #toArrayByInt(List<Integer>)} 从整数列表转换成数组</li>
 * <li>{@link #toArrayByLong(List<Long>)} 从长整数列表转换成数组</li>
 * </ul>
 * <ul>
 * <strong>数组转换成列表</strong>
 * <li>{@link #fromArray(Object[])} 从数组转换成列表</li>
 * </ul>
 * <p/>
 * Created by hnusr on 2015-01-01.
 */
public final class ArrayConvertUtils {

	private static final String DEFAULT_JOIN_SEPARATOR = ConstSign.SIGN_COMMA;

	//
	// ========================== 从列表转换到字符串 ========================= //
	//

	/**
	 * 用默认的逗号把列表转成字符串
	 */
	public static String toString(final List<?> sourceList) {
		return toString(sourceList, DEFAULT_JOIN_SEPARATOR);
	}

	/**
	 * 用默认的逗号把列表转成字符串
	 */
	public static <T> String toString(final T[] sourceArray) {
		return toString(sourceArray, DEFAULT_JOIN_SEPARATOR);
	}

	/**
	 * 用间隔字符转换成字符串
	 */
	public static String toString(final List<?> sourceList, final char separator) {
		return toString(sourceList, new String(new char[]{separator}));
	}

	/**
	 * 用间隔字符转换成字符串
	 */
	public static <T> String toString(final T[] sourceArray, final char separator) {
		return toString(sourceArray, new String(new char[]{separator}));
	}

	/**
	 * 用间隔字符转换成字符串
	 */
	public static <T> String toString(final T[] sourceArray, final String separator) {
		return toString(Arrays.asList(sourceArray), separator);
	}

	/**
	 * 用某个字符串把列表连成字符串
	 */
	public static String toString(final List<?> sourceList, String separator) {
		if (ArrayUtils.isListNullOrEmpty(sourceList)) {
			return ConstString.EMPTY;
		}
		if (separator == null) {
			separator = DEFAULT_JOIN_SEPARATOR;
		}
		final StringBuilder joinStr = new StringBuilder();
		for (int i = 0; i < sourceList.size(); i++) {
			joinStr.append(sourceList.get(i).toString());
			if (i != (sourceList.size() - 1)) {
				joinStr.append(separator);
			}
		}
		return joinStr.toString();
	}

	//
	// ========================== 从字符串转换成列表 ========================= //
	//

	/**
	 * 从字符串用分隔符转换成字符串列表
	 */
	public static List<String> fromString(final String input) {
		return fromString(input, DEFAULT_JOIN_SEPARATOR);
	}

	/**
	 * 从字符串用分隔符转换成字符串列表
	 */
	public static List<String> fromString(final String input, char splitChar) {
		return fromString(input, String.valueOf(splitChar));
	}

	/**
	 * 从字符串用分隔符转换成字符串列表
	 */
	public static List<String> fromString(final String input, final String regularExpression) {
		final List<String> list = new ArrayList<String>();
		if (StringUtils.isEmpty(input)) {
			return list;
		}
		final String[] array = input.split(regularExpression);
		return Arrays.asList(array);
	}

	//
	// ========================== 从列表转换到数组 ========================= //
	//

	/**
	 * 从字符串列表转换成数组
	 */
	public static String[] toArrayByString(final List<String> list) {
		String[] array = new String[list.size()];
		return list.toArray(array);
	}

	/**
	 * 从数字型数组转换成字符型数组
	 */
	public static String[] toStringArrayByIntArray(final Integer[] array) {
		final int n = array.length;
		final String[] strArray = new String[n];
		for (int i = 0; i < n; i++) {
			strArray[i] = String.valueOf(array[i]);
		}
		return strArray;
	}

	/**
	 * 从整数列表转换成数组
	 */
	public static Integer[] toArrayByInt(final List<Integer> list) {
		Integer[] array = new Integer[list.size()];
		return list.toArray(array);
	}

	/**
	 * 从长整数列表转换成数组
	 */
	public static Long[] toArrayByLong(final List<Long> list) {
		Long[] array = new Long[list.size()];
		return list.toArray(array);
	}

	//
	// ========================== 从数组转换到列表 ========================= //
	//

	/**
	 * 从数组转换成列表
	 */
	public static <T> List<T> fromArray(final T[] array) {
		return Arrays.asList(array);
	}

}
