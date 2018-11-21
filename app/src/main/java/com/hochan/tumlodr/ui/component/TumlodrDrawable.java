package com.hochan.tumlodr.ui.component;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 *
 * Created by hochan on 2016/9/7.
 */
public class TumlodrDrawable extends BitmapDrawable{

	private final BitmapShader mBitmapShader;
	private Paint mPaint = new Paint();
	private RectF mRextF = new RectF();

	public TumlodrDrawable(Bitmap bitmap){
		super(bitmap);
		mBitmapShader=new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		mPaint.setShader(mBitmapShader);
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);
		mRextF.right = right;
		mRextF.bottom = bottom;
	}

	@Override
	public void draw(Canvas canvas) {
		if(getBitmap() != null){
			canvas.drawRoundRect(mRextF, 5, 5, mPaint);
		}
	}
}
