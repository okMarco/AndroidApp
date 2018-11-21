package com.hochan.tumlodr.module.video.videolayout;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.module.video.RoundCornerViewOutlineProvider;
import com.hochan.tumlodr.tools.ScreenTools;

/**
 * .
 * Created by hochan on 2018/7/27.
 */

public class WebViewVideoPlayLayout extends VideoPlayLayout {

	public WebViewVideoPlayLayout(@NonNull Context context) {
		super(context);
	}

	public WebViewVideoPlayLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public WebViewVideoPlayLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getVideoTextureView().setOutlineProvider(new RoundCornerViewOutlineProvider(ScreenTools.dip2px(getContext(), 10)));
			getVideoTextureView().setClipToOutline(true);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = (int) (MeasureSpec.getSize(widthMeasureSpec) / (16 / 9.0f));
		super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
	}
}
