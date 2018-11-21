package com.hochan.tumlodr.ui.component;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 *
 * Created by zhendong_chen on 2016/8/24.
 */
public class DrawerToggleDrawable extends Drawable{

	private Paint mPaint;
	private Rect mRect;
	private int mWidthOfStroke;

	public DrawerToggleDrawable(int color){
		mPaint = new Paint();
		mPaint.setColor(color);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(mRect.left, mRect.top, mRect.right, mRect.top + mWidthOfStroke, mPaint);
		canvas.drawRect(mRect.left, mRect.top + mWidthOfStroke * 3, mRect.right, mRect.top + mWidthOfStroke * 4, mPaint);
		canvas.drawRect(mRect.left, mRect.top + mWidthOfStroke * 6, mRect.right, mRect.top + mWidthOfStroke * 7, mPaint);
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(right, bottom, right, bottom);
		mWidthOfStroke = bottom / 2 / 14;
		int rectTop = (bottom - mWidthOfStroke * 7) / 2;
		int rectLeft = right / 3;
		int rectBottom = bottom - rectTop;
		int rectRight = right - rectLeft;
		mRect = new Rect(rectLeft, rectTop, rectRight, rectBottom);
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter colorFilter) {

	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}
}
