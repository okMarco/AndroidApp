package com.hochan.tumlodr.tools.log;

/**
 * ObjectUtils 类
 * <pre>
 * 常用方法
 * </pre>
 * <ul>
 * <strong>判断相等</strong>
 * <li>{@link #isEquals(Object, Object)} 是否相等</li>
 * </ul>
 * <ul>
 * <strong>判断为空</strong>
 * <li>{@link #isAllObjNull(Object...)} 是否所有元素都为空}</li>
 * <li>{@link #isAnyObjNull(Object...)} 是否有任意一个元素为空}</li>
 * </ul>
 * <ul>
 * <strong>返回</strong>
 * <li>{@link #toString(Object)} 转换成字符串}</li>
 * <li>{@link #defaultIfNull(Object, Object)} 如果对象为空则返回默认值}</li>
 * <li>{@link #hashCode(Object)} 返回hashCode值}</li>
 * <li>{@link #hashCode(Object...)} 返回多个对象的hashCode值}</li>
 * </ul>
 * <p/>
 * Created by hnusr on 2014-12-21.
 */
public final class ObjectUtils {

	//
	// ========================== 判断相等 ========================= //
	//

	/**
	 * 是否相等
	 */
	public static boolean isEquals(Object object1, Object object2) {
		if (object1 == object2) {
			return true;
		}
		if (object1 == null || object2 == null) {
			return false;
		}
		return object1.equals(object2);
	}

	//
	// ========================== 判断为空 ========================= //
	//

	/**
	 * 是否有所有元素都为空
	 */
	public static boolean isAllObjNull(Object... objects) {
		for (Object obj : objects) {
			if (obj != null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否所有元素都不为空
	 */
	public static boolean isAllObjNotNull(Object... objects) {
		for (Object obj : objects) {
			if (obj == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否有任意一个元素为空
	 */
	public static boolean isAnyObjNull(Object... objects) {
		for (Object obj : objects) {
			if (obj == null) {
				return true;
			}
		}
		return false;
	}

	//
	// ========================== 返回值 ========================= //
	//

	/**
	 * 转换成字符串
	 */
	public static String toString(Object obj) {
		return obj == null ? "" : obj.toString();
	}

	/**
	 * 如果对象为空则返回默认值
	 */
	public static <T> T defaultIfNull(T object, T defaultValue) {
		return object != null ? object : defaultValue;
	}

	/**
	 * 返回hashCode值
	 */
	public static int hashCode(Object obj) {
		return (obj == null) ? 0 : obj.hashCode();
	}

	/**
	 * 返回hashCode值
	 */
	public static int hashCode(Object... objArray) {
		int hashCode = 0;
		for (Object obj : objArray) {
			hashCode = hashCode ^ hashCode(obj);
		}
		return hashCode;
	}

}
