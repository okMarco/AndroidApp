package com.hochan.tumlodr.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

/**
 * .
 * Created by hochan on 2018/6/9.
 */

public class SystemUtils {

	public static void fixInputMethodManagerLeak(Context destContext) {
		if (destContext == null) {
			return;
		}

		InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm == null) {
			return;
		}

		String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView", "mLastSrvView"};
		Field f;
		Object objGet;
		for (String param : arr) {
			try {
				f = imm.getClass().getDeclaredField(param);
				if (!f.isAccessible()) {
					f.setAccessible(true);
				}
				objGet = f.get(imm);
				if (objGet != null && objGet instanceof View) {
					View vGet = (View) objGet;
					if (vGet.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
						f.set(imm, null); // 置空，破坏掉path to gc节点
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public static void fixInputMethod(Context context) {
		if (context == null) {
			return;
		}
		InputMethodManager inputMethodManager = null;
		try {
			inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		} catch (Throwable th) {
			th.printStackTrace();
		}
		if (inputMethodManager == null) {
			return;
		}
		Field[] declaredFields = inputMethodManager.getClass().getDeclaredFields();
		for (Field declaredField : declaredFields) {
			try {
				if (!declaredField.isAccessible()) {
					declaredField.setAccessible(true);
				}
				Object obj = declaredField.get(inputMethodManager);
				if (obj == null || !(obj instanceof View)) {
					continue;
				}
				View view = (View) obj;
				if (view.getContext() == context) {
					declaredField.set(inputMethodManager, null);
				} else {
					return;
				}
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}
}
