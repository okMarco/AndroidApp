package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.ui.activity.BlogPostListActivity;
import com.hochan.tumlodr.ui.activity.DownloadTasksManagerActivity;
import com.hochan.tumlodr.ui.activity.FollowingActivity;
import com.hochan.tumlodr.ui.activity.LikePostListActivity;
import com.hochan.tumlodr.ui.activity.MainActivity;
import com.hochan.tumlodr.ui.activity.PostDetailActivity;
import com.hochan.tumlodr.ui.activity.SettingActivity;
import com.hochan.tumlodr.ui.activity.VideoViewPagerActivity;

/**
 * .
 * Created by hochan on 2018/1/28.
 */

public class TumlodrBottomAdsLayout extends FrameLayout {

	//ca-app-pub-3870083751110505~5629704979
	public static final String MOBILE_ADS_KEY = "ca-app-pub-3870083751110505~5629704979";

	public static final String DEVICE_ID = "4F3820DE232EEA474B1013B1700CF0AC";
	private static final String ADUNIT_ID_MAIN_ACTIVITY = "ca-app-pub-3870083751110505/3023244007";
	private static final String ADUNIT_ID_POST_DETAIL_ACTIVITY = "ca-app-pub-3870083751110505/2596824127";
	private static final String ADUNIT_ID_VIDEO_ACTIVITY = "ca-app-pub-3870083751110505/6217271605";
    private static final String ADUNIT_ID_BLOG_ACTIVITY = "ca-app-pub-3870083751110505/3713509140";
    private static final String ADUNIT_ID_DOWNLOAD_ACTIVITY = "ca-app-pub-3870083751110505/5705726593";
    private static final String ADUNIT_ID_FOLLOWING_ACTIVITY = "ca-app-pub-3870083751110505/5925218441";
    private static final String ADUNIT_ID_LIKE_ACTIVITY = "ca-app-pub-3870083751110505/8906605004";
    private static final String ADUNIT_ID_SETTINGS_ACTIVITY = "ca-app-pub-3870083751110505/3817929853";

	public static final String ADUNIT_ID_INSTAGRAM_ACTIVITY = "ca-app-pub-3870083751110505/5125381905";

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
		//setOrientation(VERTICAL);
		View divider = new View(context);
		divider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTranBlack));
		FrameLayout.LayoutParams dividerLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
		dividerLayoutParams.gravity = Gravity.TOP;
		//addView(divider, dividerLayoutParams);

		divider = new View(context);
		divider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTranBlack));
		dividerLayoutParams.gravity = Gravity.BOTTOM;
		//addView(divider, dividerLayoutParams);

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
		LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER;
		//int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
		//		5, context.getResources().getDisplayMetrics());
		layoutParams.topMargin = 0;
		layoutParams.bottomMargin = 0;
		addView(mAdView, layoutParams);
		mAdView.loadAd(new AdRequest.Builder().addTestDevice(DEVICE_ID).build());

		View bottomDivider = new View(context);
		bottomDivider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTranBlack));
		//addView(bottomDivider, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
	}

	public static String getAdUnitId(Context context) {
	    if (context instanceof MainActivity) {
            return ADUNIT_ID_MAIN_ACTIVITY;
        }else if (context instanceof LikePostListActivity) {
	        return ADUNIT_ID_LIKE_ACTIVITY;
        }else if (context instanceof FollowingActivity) {
	        return ADUNIT_ID_FOLLOWING_ACTIVITY;
        }else if (context instanceof DownloadTasksManagerActivity) {
	        return ADUNIT_ID_DOWNLOAD_ACTIVITY;
        }else  if (context instanceof SettingActivity) {
	        return ADUNIT_ID_SETTINGS_ACTIVITY;
        }else  if (context instanceof VideoViewPagerActivity) {
	        return ADUNIT_ID_VIDEO_ACTIVITY;
        }else if (context instanceof PostDetailActivity) {
	        return ADUNIT_ID_POST_DETAIL_ACTIVITY;
        }else if (context instanceof BlogPostListActivity) {
	        return ADUNIT_ID_BLOG_ACTIVITY;
        }
		return ADUNIT_ID_MAIN_ACTIVITY;
	}

	public void loadAd() {
		mAdView.loadAd(new AdRequest.Builder().addTestDevice(DEVICE_ID).build());
	}
}
