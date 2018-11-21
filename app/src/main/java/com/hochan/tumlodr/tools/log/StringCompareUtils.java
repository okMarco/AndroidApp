package com.hochan.tumlodr.tools.log;

import com.hochan.tumlodr.tools.log.constant.ConstString;

/**
 * @see StringCompareUtils 类
 * <pre>
 * 比较字符串的常用方法
 * </pre>
 * <ul>
 * <strong>空或NULL判断</strong>
 * <li>{@link #isNullOrEmpty(String)} 是否都为NULL或Empty</li>
 * <li>{@link #isBlank(String...)} 是否都为NULL或多个空格</li>
 * <li>{@link #isAnyBlank(String...)} 是否任意有一个为NULL或多个空格</li>
 * <li>{@link #isNotBlank(String...)} 是否都不为null或多个空格等</li>
 * <li>{@link #isAllEmpty(CharSequence...)} 是否都为空</li>
 * <li>{@link #isAnyEmpty(CharSequence...)} 是否任意有一个为空</li>
 * <li>{@link #isNotEmpty(CharSequence...)} 是否都不为空</li>
 * <li>{@link #isAllNull(CharSequence...)} 是否都为空</li>
 * <li>{@link #isAnyNull(CharSequence...)} 是否意意一个为空</li>
 * </ul>
 * <ul>
 * <strong>其它判断</strong>
 * <li>{@link #isWhitespace(String)} 是否空格</li>
 * <li>{@link #isNumeric(String)} 是否全为数字</li>
 * <li>{@link #isLetter(String)} 是否全是字母</li>
 * </ul>
 * <ul>
 * <strong>相等判断</strong>
 * <li>{@link #isEquals(String, String)} 是否相等，区分大小写</li>
 * <li>{@link #isEqualsIgnoreCase(String, String)} 是否相等，不区分大小写</li>
 * </ul>
 * <ul>
 * <strong>包含判断</strong>
 * <li>{@link #startsWith(String, String)} 是否字符串开始相等</li>
 * <li>{@link #endsWith(String, String)} 是否字符串结尾相等</li>
 * <li>{@link #isContains(String, String...)} 是否全部包含</li>
 * <li>{@link #isAnyContains(String, String...)} 是否任意一个包含</li>
 * <li>{@link #isNotContains(String, String...)} 是否都不包含</li>
 * </ul>
 * <p/>
 * Created by hnusr on 2015-01-01.
 */
public final class StringCompareUtils {

	//
	// ========================== 空或NULL判断 ========================= //
	//

	/**
	 * 是否为null或empty
	 */
	public static boolean isNullOrEmpty(String input) {
		return input == null || input.equals(ConstString.EMPTY);
	}

	/**
	 * 是否为NULL或多个空格或""，与empty的区别在于多个空格也算blank
	 * isBlank(null) = true;
	 * isBlank(&quot;&quot;) = true;
	 * isBlank(&quot; &quot;) = true;
	 * isBlank(&quot;a&quot;) = false;
	 * isBlank(&quot;a &quot;) = false;
	 * isBlank(&quot; a&quot;) = false;
	 * isBlank(&quot;a b&quot;) = false;
	 * </pre>
	 */
	public static boolean isBlank(final String... textArray) {
		for (String s : textArray) {
			if (s != null && s.trim().length() > 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否任意有一个为NULL或多个空格
	 */
	public static boolean isAnyBlank(final String... textArray) {
		for (String s : textArray) {
			if (isBlank(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否都不为空格
	 */
	public static boolean isNotBlank(final String... textArray) {
		return !isBlank(textArray);
	}

	/**
	 * 是否都为null或""
	 * <pre>
	 * isEmpty(null) = true;
	 * isEmpty(&quot;&quot;) = true;
	 * isEmpty(&quot; &quot;) = false;
	 * </pre>
	 */
	public static boolean isAllEmpty(final CharSequence... textArray) {
		for (CharSequence s : textArray) {
			if (s != null && s.length() > 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否任意有一个为空
	 */
	public static boolean isAnyEmpty(final CharSequence... textArray) {
		for (CharSequence s : textArray) {
			if (isAllEmpty(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否都不为null和""
	 */
	public static boolean isNotEmpty(final CharSequence... textArray) {
		for (CharSequence s : textArray) {
			if (s == null || s.length() == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否都为null
	 */
	public static boolean isAllNull(final CharSequence... textArray) {
		for (CharSequence s : textArray) {
			if (s != null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 任意一个null则为真
	 */
	public static boolean isAnyNull(final CharSequence... textArray) {
		for (CharSequence s : textArray) {
			if (s == null) {
				return true;
			}
		}
		return false;
	}

	//
	// ========================== 是否空格 ========================= //
	//

	/**
	 * 是否空格或连续的空格
	 */
	public static boolean isWhitespace(String input) {
		if (input == null) {
			return false;
		}
		int sz = input.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isWhitespace(input.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否全是数字
	 */
	public static boolean isNumeric(String input) {
		if (input == null) {
			return false;
		}
		int sz = input.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isDigit(input.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否全是字母
	 */
	public static boolean isLetter(String input) {
		if (input == null) {
			return false;
		}
		int sz = input.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isLetter(input.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	//
	// ========================== 相等判断 ========================= //
	//

	/**
	 * 是否相等，区分大小写
	 */
	public static boolean isEquals(final String str1, final String str2) {
		return str1 == null ? str2 == null : str1.equals(str2);
	}

	/**
	 * 是否相等，不区分大小写
	 */
	public static boolean isEqualsIgnoreCase(final String str1, final String str2) {
		return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
	}

	/**
	 * 第一个参数与后面参数相比较，只要任意相等就返回真
	 */
	public static boolean isAnyEqualsIgnoreCase(final String sourceStr, final String... values) {
		if (sourceStr == null || values == null) {
			return false;
		}
		for (String value : values) {
			if (isEqualsIgnoreCase(sourceStr, value)) {
				return true;
			}
		}
		return false;
	}

	//
	// ========================== 包含判断 ========================= //
	//

	/**
	 * 字符串开始相等
	 */
	public static boolean startsWith(String sourceText, String prefix) {
		sourceText = StringUtils.trim(sourceText);
		return sourceText.startsWith(prefix);
	}

	/**
	 * 字符串结尾相等
	 */
	public static boolean endsWith(String sourceText, String suffix) {
		sourceText = StringUtils.trim(sourceText);
		return sourceText.endsWith(suffix);
	}

	/**
	 * 是否全部包含
	 */
	public static boolean isContains(String sourceText, String... textArray) {
		sourceText = StringUtils.trim(sourceText);
		for (CharSequence s : textArray) {
			if (!sourceText.contains(s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否任意一个包含
	 */
	public static boolean isAnyContains(String sourceText, String... textArray) {
		sourceText = StringUtils.trim(sourceText);
		for (CharSequence s : textArray) {
			if (sourceText.contains(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否都不包含
	 */
	public static boolean isNotContains(String sourceText, String... textArray) {
		return isContains(sourceText, textArray);
	}

}
