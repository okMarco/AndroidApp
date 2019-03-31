package com.hochan.tumlodr.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * .
 * Created by hochan on 2018/6/3.
 */

public class BlurTransformation extends BitmapTransformation {

	private RenderScript rs;
	private int radius;

	public BlurTransformation(Context context, int radius) {
		rs = RenderScript.create(context);
		this.radius = radius;
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
		return getBlurBitmap(toTransform);
	}

	@SuppressLint("InlinedApi")
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	private Bitmap getBlurBitmap(Bitmap toTransform) {
		Bitmap blurredBitmap = toTransform.copy(Bitmap.Config.ARGB_8888, true);
		@SuppressLint("InlinedApi")
		Allocation input = Allocation.createFromBitmap(rs, blurredBitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
		Allocation output = Allocation.createTyped(rs, input.getType());
		ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
		script.setInput(input);
		script.setRadius(radius);
		script.forEach(output);
		output.copyTo(blurredBitmap);
		return blurredBitmap;
	}

	@Override
	public void updateDiskCacheKey(MessageDigest messageDigest) {
		messageDigest.update("blur transformation".getBytes());
	}
}