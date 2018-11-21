package com.hochan.tumlodr.tools.log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ArrayUtils 类
 * <pre>
 * 列表各种常用方法
 * </pre>
 * <ul>
 * <strong>常用</strong>
 * <li>{@link #nullToEmpty(List)} 如果为null则转换成Empty</li>
 * <li>{@link #getEntry(List, int)} 安全根据索引值得到实体</li>
 * <li>{@link #getSize(List)} 在原先的size判断上添加null时为0</li>
 * </ul>
 * <ul>
 * <strong>判断为空</strong>
 * <li>{@link #isEmpty(Collection)} 是否列表为空null or size=0 都算为空</li>
 * <li>{@link #isListNullOrEmpty(Collection)} 是否列表为空null or size=0 都算为空</li>
 * <li>{@link #isListNotNullOrEmpty(Collection)} 是否列表不为空， 不是null 也不是size=0</li>
 * <li>{@link #isAllListNullOrEmpty(Collection...)} 是否所有列表都为空，null或size = 0都算空</li>
 * <li>{@link #isAnyListNullOrEmpty(Collection...)} 是否列表中有一个元素为空，null或size = 0都算空</li>
 * </ul>
 * <p/>
 * Created by hnusr on 2015-01-01.
 */
public final class ArrayUtils {

	//
	// ========================== 常用 ========================= //
	//

	/**
	 * 如果为null则转换成Empty
	 */
	public static <T> List<T> nullToEmpty(final List<T> sourceList) {
		if (sourceList == null) {
			return new ArrayList<T>();
		}
		return sourceList;
	}

	/**
	 * 安全根据索引值得到实体
	 */
	public static <T> T getEntry(final List<T> sourceList, int position) {
		if (sourceList != null && sourceList.size() > position) {
			return sourceList.get(position);
		}
		return null;
	}

	/**
	 * 在原先的size判断上添加null时为0
	 */
	public static <T> int getSize(final List<T> sourceList) {
		return sourceList == null ? 0 : sourceList.size();
	}


	//
	// ========================== 判断为空 ========================= //
	//

	/**
	 * 是否列表为空null or size=0 都算为空
	 */
	public static boolean isEmpty(final Collection sourceList) {
		return sourceList == null || sourceList.isEmpty();
	}

	public static boolean isEmpty(final String[] sourceArray) {
		return sourceArray == null || sourceArray.length == 0;
	}

	/**
	 * 是否列表为空null or size=0 都算为空
	 */
	public static boolean isListNullOrEmpty(final Collection sourceList) {
		return sourceList == null || sourceList.isEmpty();
	}

	/**
	 * 是否列表不为空， 不是null 也不是size=0
	 */
	public static boolean isListNotNullOrEmpty(final Collection sourceList) {
		return !isListNullOrEmpty(sourceList);
	}

	/**
	 * 是否所有列表都为空，null或size = 0都算空
	 */
	public static boolean isAllListNullOrEmpty(final Collection... sourceListArray) {
		for (Collection sourceList : sourceListArray) {
			if (isListNotNullOrEmpty(sourceList)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否列表中有一个元素为空，null或size = 0都算空
	 */
	public static boolean isAnyListNullOrEmpty(final Collection... sourceListArray) {
		for (Collection sourceList : sourceListArray) {
			if (sourceList == null || sourceList.size() == 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsIgnoreCase(List<String> list, String target) {
		if (isEmpty(list) || target == null) {
			return false;
		}
		for (String a : list) {
			if (a.equalsIgnoreCase(target)) {
				return true;
			}
		}
		return false;
	}

	public static boolean contains(List<String> list, String target) {
		if (isEmpty(list) || target == null) {
			return false;
		}
		for (String a : list) {
			if (a.equals(target)) {
				return true;
			}
		}
		return false;
	}

	public static boolean contains(String[] array, String obj) {

		if (isEmpty(array) || obj == null) {
			return false;
		}
		for (String a : array) {
			if (a.equals(obj)) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsIgnoreCase(String[] array, String obj) {

		if (isEmpty(array) || obj == null) {
			return false;
		}
		for (String a : array) {
			if (a.equalsIgnoreCase(obj)) {
				return true;
			}
		}
		return false;
	}

}
