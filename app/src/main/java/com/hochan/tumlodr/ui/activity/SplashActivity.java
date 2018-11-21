package com.hochan.tumlodr.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.databinding.ActivitySplashBinding;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseAppUiActivity;
import com.hochan.tumlodr.util.statusbar.StatusBarCompat;

import static android.os.Build.VERSION_CODES.M;
import static com.hochan.tumlodr.TumlodrApp.sOAuthToken;
import static com.hochan.tumlodr.TumlodrApp.sOAuthTokenSecret;
import static com.hochan.tumlodr.util.statusbar.PhoneSystemCompat.isMEIZU;
import static com.hochan.tumlodr.util.statusbar.PhoneSystemCompat.isMIUI;
import static com.hochan.tumlodr.util.statusbar.PhoneSystemCompat.isOPPO;


public class SplashActivity extends BaseAppUiActivity implements View.OnClickListener {

	private static final int REQUEST_CODE_OAUTH = 0;
	public static final int REQUEST_CODE_CHANGE_API = 1;

	private ActivitySplashBinding mViewBinding;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		mViewBinding = ActivitySplashBinding.inflate(getLayoutInflater());
		setContentView(mViewBinding.getRoot());
		initWidget();
		checkNetwork();
	}

	@Override
	protected void onResume() {
		super.onResume();
		RotateAnimation rotate = new RotateAnimation(0f, 360f,
				Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setInterpolator(new OvershootInterpolator());
		rotate.setDuration(1000);
		rotate.setFillAfter(true);
		mViewBinding.ivApp.startAnimation(rotate);
	}

	public void initWidget() {
		mViewBinding.btnLogin.setOnClickListener(this);
		if (!AppUiConfig.sIsLightTheme) {
			mViewBinding.btnLogin.setBackgroundResource(R.drawable.selector_dark_btn_bg_round_corner);
			mViewBinding.btnChangeApi.setBackgroundResource(R.drawable.selector_dark_btn_bg_round_corner);
		} else {
			mViewBinding.btnLogin.setBackgroundResource(R.drawable.selector_light_btn_bg_round_corner);
			mViewBinding.btnChangeApi.setBackgroundResource(R.drawable.selector_light_btn_bg_round_corner);
		}
		mViewBinding.btnLogin.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.btnChangeApi.setTextColor(AppUiConfig.sTextColor);

		mViewBinding.btnChangeApi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivityForResult(new Intent(SplashActivity.this, ChangeApiActivity.class),
						REQUEST_CODE_CHANGE_API);
			}
		});
	}

	@Override
	public void initStatusBar() {
		if (Build.VERSION.SDK_INT >= M || isOPPO() || isMIUI() || isMEIZU()) {
			StatusBarCompat.setStatusBarTranslucent(getWindow());
			StatusBarCompat.setLightStatusBar(getWindow(), AppUiConfig.sIsLightTheme);
		} else {
			StatusBarCompat.setStatusBarHalfTranslucent(getWindow());
		}
		StatusBarCompat.setNavigationBarTranslucent(getWindow());
	}

	private void checkNetwork() {
		SharedPreferences sharedPreferences = this.getSharedPreferences(AppConfig.SHARE_USER, MODE_PRIVATE);
		sOAuthToken = sharedPreferences.getString(AppConfig.SHARE_USER_OAUTH_TOKEN, null);
		sOAuthTokenSecret = sharedPreferences.getString(AppConfig.SHARE_USER_OAUTH_TOKEN_SECRET, null);
		if (sOAuthToken != null && sOAuthTokenSecret != null) {
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(0, 0);
			SplashActivity.this.finish();
		}
	}

	public void login() {
		startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE_OAUTH);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_login: {
				login();
				break;
			}
		}
	}

	@SuppressLint("ApplySharedPref")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_OAUTH) {
			if (data != null) {
				String token = data.getStringExtra(LoginActivity.EXTRA_TOKEN);
				String tokenSecret = data.getStringExtra(LoginActivity.EXTRA_TOKEN_SECRET);
				if (TextUtils.isEmpty(token) || TextUtils.isEmpty(tokenSecret)) {
					Toast.makeText(this, "登陆失败, 请重试。", Toast.LENGTH_SHORT).show();
					return;
				}
				TumlodrApp.sOAuthToken = token;
				TumlodrApp.sOAuthTokenSecret = tokenSecret;
				SharedPreferences sharedPreferences = TumlodrApp.getContext().getSharedPreferences(AppConfig.SHARE_USER, MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString(AppConfig.SHARE_USER_OAUTH_TOKEN, token);
				editor.putString(AppConfig.SHARE_USER_OAUTH_TOKEN_SECRET, tokenSecret);
				editor.commit();
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				overridePendingTransition(0, 0);
				SplashActivity.this.finish();
			}
		} else if (requestCode == REQUEST_CODE_CHANGE_API) {
			if (data != null) {
				String consumerKey = data.getStringExtra(ChangeApiActivity.EXTRA_CONSUMER_KEY);
				String secretKey = data.getStringExtra(ChangeApiActivity.EXTRA_SECRET_KEY);
				if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(secretKey)) {
					return;
				}
				login();
			}
		}
	}
}
