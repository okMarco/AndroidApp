package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.tools.AppUiConfig;

/**
 * .
 * Created by hochan on 2018/1/28.
 */

public class TumlodrBottomAdsLayout extends LinearLayout {

	public static final String MOBILE_ADS_KEY = "ca-app-pub-4515029377297008~3739995737";

	public static final String DEVICE_ID = "4F3820DE232EEA474B1013B1700CF0AC";
	private static final String ADUNIT_ID_MAIN_ACTIVITY = "ca-app-pub-4515029377297008/9067067319";
	private static final String ADUNIT_ID_POST_DETAIL_ACTIVITY = "ca-app-pub-4515029377297008/6608424938";
	private static final String ADUNIT_ID_VIDEO_ACTIVITY = "ca-app-pub-4515029377297008/1491829623";

	private AdView mAdView;

	public TumlodrBottomAdsLayout(@NonNull Context context) {
		this(context, null);
	}

	public TumlodrBottomAdsLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TumlodrBottomAdsLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setBackgroundColor(AppUiConfig.sThemeColor);
		setVisibility(GONE);
		if (TextUtils.isEmpty(getAdUnitId(context))) {
			return;
		}
		setOrientation(VERTICAL);
		View topDivider = new View(context);
		topDivider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTranBlack));

		addView(topDivider, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
		mAdView = new AdView(context);
		mAdView.setAdSize(AdSize.BANNER);
		mAdView.setAdUnitId(getAdUnitId(context));
		mAdView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				setVisibility(VISIBLE);
			}

			@Override
			public void onAdFailedToLoad(int i) {
				super.onAdFailedToLoad(i);
			}
		});
		LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				5, context.getResources().getDisplayMetrics());
		layoutParams.topMargin = margin;
		layoutParams.bottomMargin = margin;
		addView(mAdView, layoutParams);
		mAdView.loadAd(new AdRequest.Builder().addTestDevice(DEVICE_ID).build());

		View bottomDivider = new View(context);
		bottomDivider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTranBlack));
		addView(bottomDivider, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
	}

	public static String getAdUnitId(Context context) {
		return ADUNIT_ID_MAIN_ACTIVITY;
	}

	public void loadAd() {
		mAdView.loadAd(new AdRequest.Builder().addTestDevice(DEVICE_ID).build());
	}
}
