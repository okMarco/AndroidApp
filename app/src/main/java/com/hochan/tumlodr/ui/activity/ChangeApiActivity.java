package com.hochan.tumlodr.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.databinding.ActivityChangeApiBinding;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseViewBindingActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import static com.hochan.tumlodr.ui.activity.LoginActivity.TUMBLR_ACCESS;
import static com.hochan.tumlodr.ui.activity.LoginActivity.TUMBLR_AUTH;
import static com.hochan.tumlodr.ui.activity.LoginActivity.TUMBLR_REQUEST;

public class ChangeApiActivity extends BaseViewBindingActivity<ActivityChangeApiBinding> {

	public static final String EXTRA_CONSUMER_KEY = "consumer_key";
	public static final String EXTRA_SECRET_KEY = "secret_key";
	private static final String CHANGE_API_GUIDE_URL = "https://tumblrapiguide.firebaseapp.com/";

	private WebView mWebView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mWebView = new WebView(this);
		mViewBinding.webViewContainer.addView(mWebView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		mViewBinding.etConsumerKey.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.etSecretKey.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.btnSaveApi.setBackgroundResource(AppUiConfig.sIsLightTheme ? R.drawable.selector_light_btn_bg_round_corner
				: R.drawable.selector_dark_btn_bg_round_corner);
		mViewBinding.btnSaveApi.setTextColor(AppUiConfig.sTextColor);

		final SharedPreferences sharedPreferences = this.getSharedPreferences(AppConfig.SHARE_USER, MODE_PRIVATE);
		String userConsumerKey = sharedPreferences.getString(AppConfig.SHARE_CUSTOM_CONSUMER_KEY, null);
		String userSecretKey = sharedPreferences.getString(AppConfig.SHARE_CUSTOM_SECRET_KEY, null);
		if (!TextUtils.isEmpty(userConsumerKey) && !TextUtils.isEmpty(userSecretKey)) {
			mViewBinding.etConsumerKey.setText(userConsumerKey);
			mViewBinding.etSecretKey.setText(userSecretKey);
			mViewBinding.btnDeleteApi.setVisibility(View.VISIBLE);
			mViewBinding.btnSaveApi.setVisibility(View.GONE);
		} else {
			mViewBinding.btnDeleteApi.setVisibility(View.GONE);
			mViewBinding.btnSaveApi.setVisibility(View.VISIBLE);
		}

		mViewBinding.btnDeleteApi.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("ApplySharedPref")
			@Override
			public void onClick(View view) {
				sharedPreferences.edit().clear().commit();
				startActivity(new Intent(ChangeApiActivity.this, SplashActivity.class));
				finish();
			}
		});

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					mViewBinding.loadingProgress.setVisibility(View.INVISIBLE);
				} else {
					mViewBinding.loadingProgress.setVisibility(View.VISIBLE);
				}
				mViewBinding.loadingProgress.setProgress(newProgress);
			}
		});

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				view.loadUrl("javascript:(function(){document.getElementById('oac_title').value='" + getString(R.string.app_name) + "'})()");
				view.loadUrl("javascript:(function(){document.getElementById('oac_play_store_url').value='https://play.google.com/store/apps/details?id=com.hochan.coldsoup'})()");
				view.loadUrl("javascript:(function(){document.getElementById('oac_description').value='https://play.google.com/store/apps/details?id=com.hochan.coldsoup'})()");
				view.loadUrl("javascript:(function(){document.getElementById('oac_default_callback_url').value='" + AppConfig.CALLBACK_URL + "'})()");
			}
		});
		mWebView.loadUrl(CHANGE_API_GUIDE_URL);
		mViewBinding.btnSaveApi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TextUtils.isEmpty(mViewBinding.etConsumerKey.getText())
						|| TextUtils.isEmpty(mViewBinding.etSecretKey.getText())) {
					return;
				}
				final String consumerKey = mViewBinding.etConsumerKey.getText().toString();
				final String secretKey = mViewBinding.etSecretKey.getText().toString();
				mViewBinding.loadingIndicator.setVisibility(View.VISIBLE);
				Observable.create(new ObservableOnSubscribe<String>() {
					@Override
					public void subscribe(ObservableEmitter<String> e) throws Exception {
						CommonsHttpOAuthConsumer commonsHttpOAuthConsumer = new CommonsHttpOAuthConsumer(
								consumerKey,
								secretKey);
						//Generate a new oAuthProvider object
						CommonsHttpOAuthProvider commonsHttpOAuthProvider =
								new CommonsHttpOAuthProvider(TUMBLR_REQUEST, TUMBLR_ACCESS, TUMBLR_AUTH);
						//Retrieve the URL to which the user must be sent in order to authorize the consumer
						e.onNext(commonsHttpOAuthProvider.retrieveRequestToken(
								commonsHttpOAuthConsumer, AppConfig.CALLBACK_URL));
					}
				}).subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Consumer<String>() {
							@SuppressLint("ApplySharedPref")
							@Override
							public void accept(String s) throws Exception {
								SharedPreferences sharedPreferences = TumlodrApp.getContext().getSharedPreferences(AppConfig.SHARE_USER, MODE_PRIVATE);
								sharedPreferences.edit().clear().commit();
								SharedPreferences.Editor editor = sharedPreferences.edit();
								editor.putString(AppConfig.SHARE_CUSTOM_CONSUMER_KEY, consumerKey);
								editor.putString(AppConfig.SHARE_CUSTOM_SECRET_KEY, secretKey);
								editor.commit();
								Intent intent = new Intent();
								intent.putExtra(EXTRA_CONSUMER_KEY, consumerKey);
								intent.putExtra(EXTRA_SECRET_KEY, secretKey);
								setResult(0, intent);
								finish();
							}
						}, new Consumer<Throwable>() {
							@Override
							public void accept(Throwable throwable) throws Exception {
								mViewBinding.loadingIndicator.setVisibility(View.INVISIBLE);
								Toast.makeText(ChangeApiActivity.this, "Consumer key 或 Secret key 无效，请检查后重试。", Toast.LENGTH_SHORT).show();
							}
						});
			}
		});
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_change_api;
	}

	@Override
	protected String getTitleString() {
		return getString(R.string.set_custom_api_key);
	}

	@Override
	public void onBackPressed() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mWebView != null) {
			mViewBinding.webViewContainer.removeView(mWebView);
			mWebView.destroy();
			mWebView = null;
		}
	}
}
