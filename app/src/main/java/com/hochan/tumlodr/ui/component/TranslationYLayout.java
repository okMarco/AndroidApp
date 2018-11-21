package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * .
 * Created by hochan on 2017/9/26.
 */

public class TranslationYLayout extends LinearLayout{

	public TranslationYLayout(Context context) {
		super(context);
	}

	public TranslationYLayout(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public TranslationYLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	private float mActionDownY;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN){
			mActionDownY = ev.getY();
		}else if (ev.getAction() == MotionEvent.ACTION_MOVE){
			if (getTranslationY() != 0 && ev.getY() < mActionDownY){
				return true;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
}
