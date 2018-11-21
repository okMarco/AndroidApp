package com.hochan.tumlodr.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;

import com.hochan.tumlodr.R;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.header.material.CircleImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.impl.RefreshFooterWrapper;

/**
 * .
 * Created by hochan on 2017/10/22.
 */

public class AppUiConfig {

	private static final int INVALID = -1;
	public static int sThemId;
	public static int sPostImageProgressColor = R.color.colorPrimary;
	public static int mPostImageProgressBg = R.color.colorPrimaryDark;

	public static int mPopupWindowBg = R.drawable.popup_background;
	public static int mPopupWindowDropDownArrow = R.drawable.ic_drop_down;

	public static int sSpinnerTextLayout = R.layout.spinner_textview;
	public static int sTextColor;
	public static int sTipTextColor;
	public static int sThemeColor;
	public static int sStatusBarColor;
	public static int sThemeColorId;
	public static int sPicHolderResource = R.drawable.bg_avatar_holder_light;
	public static int sPicHolderColor = INVALID;
	public static boolean sIsLightTheme = false;
	public static int sSubTextColor;

	public static void setTumlodrTheme(Context context, int themeId) {
		sThemId = themeId;

		switch (sThemId) {
			case R.style.AppTheme_NoActionBar_Dark: {
				sIsLightTheme = false;
				sTextColor = ContextCompat.getColor(context, R.color.colorTextDark);
				sThemeColor = ContextCompat.getColor(context, R.color.colorPrimaryBlue);
				sThemeColorId = R.color.colorPrimaryBlue;
				sStatusBarColor = ContextCompat.getColor(context, R.color.colorDarkStatusBarColor);
				sTipTextColor = ContextCompat.getColor(context, R.color.colorTipTextColorBlue);
				mPostImageProgressBg = R.color.colorPrimaryBlueDark;
				sPostImageProgressColor = R.color.colorPrimaryBlueDark;
				mPopupWindowBg = R.drawable.popup_background_white;
				mPopupWindowDropDownArrow = R.drawable.ic_drop_down_white;
				sSpinnerTextLayout = R.layout.spinner_textview;
				sPicHolderResource = R.drawable.bg_avatar_holder_dark;
				sPicHolderColor = ContextCompat.getColor(context, R.color.colorBlueHolder);
				sSubTextColor = ContextCompat.getColor(context, R.color.colorSubTextDark);
				break;
			}
			default: {
				sIsLightTheme = true;
				sTextColor = ContextCompat.getColor(context, R.color.colorTextLight);
				sThemeColor = ContextCompat.getColor(context, R.color.colorPrimaryWhite);
				sThemeColorId = R.color.colorPrimaryWhite;
				sStatusBarColor = ContextCompat.getColor(context, R.color.colorLightStatusBarColor);
				sTipTextColor = ContextCompat.getColor(context, R.color.colorTipTextColorWhite);
				mPostImageProgressBg = R.color.colorPrimaryDarkWhite;
				sPostImageProgressColor = R.color.colorPrimaryWhite;
				mPopupWindowBg = R.drawable.popup_background_white;
				mPopupWindowDropDownArrow = R.drawable.ic_drop_down_white;
				sSpinnerTextLayout = R.layout.spinner_textview_white;
				sPicHolderResource = R.drawable.bg_avatar_holder_light;
				sSubTextColor = ContextCompat.getColor(context, R.color.colorSubTextLight);
				sPicHolderColor = ContextCompat.getColor(context, R.color.colorWhiteHolder);
				break;
			}
		}

		SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
			@NonNull
			@Override
			public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
				MaterialHeader materialHeader = new MaterialHeader(context).setColorSchemeColors(ContextCompat.getColor(context, R.color.colorRed),
						ContextCompat.getColor(context, R.color.colorBlue),
						ContextCompat.getColor(context, R.color.colorGreen),
						ContextCompat.getColor(context, R.color.colorOrange),
						ContextCompat.getColor(context, R.color.colorPuple));
				if (materialHeader.getChildCount() > 0 &&
						materialHeader.getChildAt(0) instanceof CircleImageView) {
					materialHeader.getChildAt(0).setBackgroundColor(AppUiConfig.sThemeColor);
				}
				return materialHeader;
			}
		});
		//设置全局的Footer构建器
		SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
			@NonNull
			@Override
			public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
				//指定为经典Footer，默认是 BallPulseFooter
				@SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(
						R.layout.layout_loading, null, false);
				return new RefreshFooterWrapper(view);
			}
		});
	}
}
