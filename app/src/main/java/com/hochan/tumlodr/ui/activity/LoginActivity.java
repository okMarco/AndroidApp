package com.hochan.tumlodr.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.ActivityLoginBinding;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseAppUiActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

@SuppressWarnings("ALL")
public class LoginActivity extends BaseAppUiActivity {

	public static final String TUMBLR_REQUEST = "https://www.tumblr.com/oauth/request_token";
	public static final String TUMBLR_ACCESS = "https://www.tumblr.com/oauth/access_token";
	public static final String TUMBLR_AUTH = "https://www.tumblr.com/oauth/authorize";

	public static final String EXTRA_TOKEN = "token";
	public static final String EXTRA_TOKEN_SECRET = "token_secret";

	private ActivityLoginBinding mViewBinding;
	private CommonsHttpOAuthConsumer mCommonsHttpOAuthConsumer;
	private CommonsHttpOAuthProvider mCommonsHttpOAuthProvider;

	@SuppressLint("CheckResult")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//StatusBarCompat.setStatusBarColor(getWindow(), ContextCompat.getColor(this, R.color.colorTumblr));

		mViewBinding = ActivityLoginBinding.inflate(getLayoutInflater());
		setContentView(mViewBinding.getRoot());

		mViewBinding.loadingIndicator.setVisibility(View.VISIBLE);
		mViewBinding.webView.loadData("<html><body style=\"background: #36465D\"></body></html>", "text/html", "utf-8");

		SharedPreferences sharedPreferences = this.getSharedPreferences(AppConfig.SHARE_USER, MODE_PRIVATE);
		final String consumerKey = sharedPreferences.getString(AppConfig.SHARE_CUSTOM_CONSUMER_KEY, AppConfig.CONSUMER_KEY);
		final String secretKey = sharedPreferences.getString(AppConfig.SHARE_CUSTOM_SECRET_KEY, AppConfig.CONSUMER_SECRET);

		io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
			@Override
			public void subscribe(ObservableEmitter<String> e) throws Exception {
				mCommonsHttpOAuthConsumer = new CommonsHttpOAuthConsumer(consumerKey, secretKey);
				//Generate a new oAuthProvider object
				mCommonsHttpOAuthProvider
						= new CommonsHttpOAuthProvider(TUMBLR_REQUEST, TUMBLR_ACCESS, TUMBLR_AUTH);
				//Retrieve the URL to which the user must be sent in order to authorize the consumer
				e.onNext(mCommonsHttpOAuthProvider.retrieveRequestToken(
						mCommonsHttpOAuthConsumer, AppConfig.CALLBACK_URL));
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Consumer<String>() {
					@Override
					public void accept(String authUrl) throws Exception {
						Log.i("LoginActivity.class", "accept: " + authUrl);
						loadAuthUrl(authUrl);
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) throws Exception {
						Crashlytics.logException(throwable);
						if (throwable.getCause() != null) {
							Toast.makeText(LoginActivity.this, throwable.getCause().getMessage(),
									Toast.LENGTH_SHORT).show();
						}
						finish();
					}
				});
	}

	@Override
	protected String getTitleString() {
		return getString(R.string.splash_login);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void loadAuthUrl(String authUrl) {
		mViewBinding.webView.getSettings().setJavaScriptEnabled(true);
		mViewBinding.webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				mViewBinding.loadingProgress.setProgress(newProgress);
				if (newProgress == 100) {
					mViewBinding.loadingProgress.setVisibility(View.INVISIBLE);
				} else {
					mViewBinding.loadingProgress.setVisibility(View.VISIBLE);
				}
			}
		});
		mViewBinding.webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				if (!url.toLowerCase().contains(AppConfig.CALLBACK_URL.toLowerCase())) {
					mViewBinding.loadingIndicator.setVisibility(View.INVISIBLE);
				}
			}

			@SuppressWarnings({"UnusedAssignment", "unused"})
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i("LoginActivity.class", "shouldOverrideUrlLoading: " + url);
				if (url.toLowerCase().contains(AppConfig.CALLBACK_URL.toLowerCase())) {
					Uri uri = Uri.parse(url);
					//instantiate String variables to store OAuth & Verifier tokens
					String strOAuthToken = "";
					String strOAuthVerifier = "";
					//Iterate through Parameters retrieved on the URL
					for (String strQuery : uri.getQueryParameterNames()) {
						switch (strQuery) {
							case "oauth_token":
								//Save OAuth Token
								//Note : This is not the login token we require to set on JumblrToken
								strOAuthToken = uri.getQueryParameter(strQuery);
								break;

							case "oauth_verifier":
								//Save OAuthVerifier
								strOAuthVerifier = uri.getQueryParameter(strQuery);
								break;
						}
					}
					retrieveAccessToken(strOAuthVerifier);
				} else {
					view.loadUrl(url);
				}
				return true;
			}
		});
		mViewBinding.webView.loadUrl(authUrl);
	}

	private void retrieveAccessToken(final String strOAuthVerifier) {
		mViewBinding.loadingIndicator.setVisibility(View.VISIBLE);
		Observable.create(new ObservableOnSubscribe<String[]>() {
			@Override
			public void subscribe(ObservableEmitter<String[]> e) throws Exception {
				String[] loginResults = new String[2];
				mCommonsHttpOAuthProvider.retrieveAccessToken(mCommonsHttpOAuthConsumer, strOAuthVerifier);
				//Check if tokens were received. If Yes, save them to SharedPreferences for later use.
				if (!TextUtils.isEmpty(mCommonsHttpOAuthConsumer.getToken())) {
					//Set the consumer key token in the LoginResult object
					loginResults[0] = mCommonsHttpOAuthConsumer.getToken();
				}
				if (!TextUtils.isEmpty(mCommonsHttpOAuthConsumer.getTokenSecret())) {
					//Set the Secret consumer key token in the LoginResult object
					loginResults[1] = mCommonsHttpOAuthConsumer.getTokenSecret();
				}
				e.onNext(loginResults);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Consumer<String[]>() {
					@Override
					public void accept(String[] strings) throws Exception {
						Intent intent = new Intent();
						intent.putExtra(EXTRA_TOKEN, strings[0]);
						intent.putExtra(EXTRA_TOKEN_SECRET, strings[1]);
						setResult(0, intent);
						finish();
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) throws Exception {
						Crashlytics.logException(throwable);
						if (throwable.getCause() != null) {
							Toast.makeText(LoginActivity.this, throwable.getCause().getMessage(),
									Toast.LENGTH_SHORT).show();
						}
						finish();
					}
				});
	}
}
