package com.hochan.tumlodr.module.webbrowser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.ActivityWebViewBinding;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseViewBindingActivity;
import com.hochan.tumlodr.util.FileUtils;

public class WebViewActivity extends BaseViewBindingActivity<ActivityWebViewBinding> {

	private static final String EXTRA_URL = "url";
	private Toolbar mToolbar;

	public static void showUrl(Context context, String url) {
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra(EXTRA_URL, url);
		context.startActivity(intent);
	}

	private Handler mHandler = new Handler();
	private WebView mWebView;
	private String mUrl;

	@Override
	public void initData() {
		mUrl = getIntent().getStringExtra(EXTRA_URL);
	}

	@Override
	protected String getTitleString() {
		return mUrl;
	}

	@SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
	@Override
	public void initWidget() {
		mToolbar = findViewById(R.id.toolbar);

		mWebView = new WebView(this);
		viewBinding.flWebviewContainer.addView(mWebView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new AndroidJS(), "AndroidJS");
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, final String url) {
				mUrl = url;
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mToolbar.setTitle(url);
					}
				});
				view.loadUrl(url);
				return true;
			}

			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, final String url) {
				return super.shouldInterceptRequest(view, url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				//TODO 提取video标签
				mToolbar.setTitle(view.getTitle());
				view.loadUrl("javascript:" + FileUtils.getAssetsFile("getVideoTags.js"));
				super.onPageFinished(view, url);
			}
		});

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					viewBinding.loadingProgress.setVisibility(View.INVISIBLE);
				} else {
					viewBinding.loadingProgress.setProgress(newProgress);
					viewBinding.loadingProgress.setVisibility(View.VISIBLE);
				}
			}
		});

		mWebView.loadUrl(mUrl);
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_web_view;
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_web_browser, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_open_in_system) {
			Uri uri = Uri.parse(mUrl);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		} else if (item.getItemId() == R.id.menu_video_history) {
			//startActivity(new Intent(this, WebVideoListActivity.class));
		} else if (item.getItemId() == R.id.menu_web_refresh) {
			mWebView.reload();
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWebView.destroy();
		viewBinding.flWebviewContainer.removeView(mWebView);
		mWebView = null;
	}

	@SuppressWarnings("unused")
	public class AndroidJS {

		@JavascriptInterface
		public void getVideo(String url, String title, String videoUrl) {
		}
	}
}
