package com.hochan.tumlodr.tools.log.constant;

/**
 * @see ConstSign 类
 * <pre>
 * 字符常量
 * </pre>
 * <ul>
 * <strong>常用符号</strong>
 * <li>{@link #SIGN_COMMA} 逗号</li>
 * <li>{@link #SIGN_DOT} 点号</li>
 * <li>{@link #SIGN_EQUAL} 等号</li>
 * <li>{@link #SIGN_CONNECTOR} 连接号</li>
 * <li>{@link #SIGN_QUESTION} 问号</li>
 * <li>{@link #SIGN_SEMICOLONS} 分号</li>
 * <li>{@link #SIGN_LT} 小于号</li>
 * <li>{@link #SIGN_GT} 大于号</li>
 * <li>{@link #SIGN_QUOT} 双引号</li>
 * </ul>
 * <p/>
 * Created by hnusr on 2015-01-01.
 */
public final class ConstSign {

	/**
	 * 逗号
	 */
	public static final String SIGN_COMMA = ",";

	/**
	 * 点号
	 */
	public static final String SIGN_DOT = ".";

	/**
	 * 等号
	 */
	public static final String SIGN_EQUAL = "=";

	/**
	 * 连接号
	 */
	public static final String SIGN_CONNECTOR = "&";

	/**
	 * 问号
	 */
	public static final String SIGN_QUESTION = "?";

	/**
	 * 分号
	 */
	public static final String SIGN_SEMICOLONS = ";";

	/**
	 * 小于号
	 */
	public static final String SIGN_LT = "<";

	/**
	 * 大于号
	 */
	public static final String SIGN_GT = ">";

	/**
	 * 双引号
	 */
	public static final String SIGN_QUOT = "\"";

	/**
	 * 百分号
	 */
	public static final String SIGN_PERCENT = "%";

	/**
	 * 中划线
	 */
	public static final String SIGN_CENTER_LINE = "-";

	/**
	 * 反斜杠
	 */
	public static final String SIGN_BACKSLASH = "\\";

	/**
	 * 一个正斜杠
	 */
	public static final String SIGN_SLASH = "/";

	/**
	 * 两个正斜杠
	 */
	public static final String SIGN_TWO_SLASH = "//";

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("逗号=").append(SIGN_COMMA);
		sb.append("点号=").append(SIGN_DOT);
		sb.append("等号=").append(SIGN_EQUAL);
		sb.append("连接号=").append(SIGN_CONNECTOR);
		sb.append("问号=").append(SIGN_QUESTION);
		sb.append("分号=").append(SIGN_SEMICOLONS);
		sb.append("小于号=").append(SIGN_LT);
		sb.append("大于号=").append(SIGN_GT);
		sb.append("双引号=").append(SIGN_QUOT);
		sb.append("百分号=").append(SIGN_PERCENT);
		sb.append("中划线=").append(SIGN_CENTER_LINE);
		sb.append("反斜杠=").append(SIGN_BACKSLASH);
		sb.append("一个正斜杠=").append(SIGN_SLASH);
		sb.append("两个正斜杠=").append(SIGN_TWO_SLASH);
		return sb.toString();
	}
}
