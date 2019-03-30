package com.hochan.tumlodr.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.ActivityInstagramParseBinding;
import com.hochan.tumlodr.model.data.InstagramBlogPostListVo;
import com.hochan.tumlodr.model.data.InstagramDownloadInfo;
import com.hochan.tumlodr.model.data.download.DownloadRecord;
import com.hochan.tumlodr.model.data.download.DownloadRecordDatabase;
import com.hochan.tumlodr.model.data.instagram.com.besjon.pojo.Edges;
import com.hochan.tumlodr.model.data.instagram.com.besjon.pojo.InstagramPageShareDataVo;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.tools.InstagramParse;
import com.hochan.tumlodr.tools.Tools;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseViewBindingActivity;
import com.hochan.tumlodr.ui.component.SingleMediaScanner;
import com.hochan.tumlodr.ui.fragment.DownloadTaskFragment;
import com.hochan.tumlodr.util.FileDownloadUtil;
import com.hochan.tumlodr.util.ViewUtils;
import com.liulishuo.filedownloader.FileDownloader;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class InstagramParseActivity extends BaseViewBindingActivity<ActivityInstagramParseBinding> {

	public static final String EXTRA_INSTAGRAM_BLOG_URL = "name";
    public static final String TAG_INSTAGRAM = "instagram";

	private Handler mHandler = new Handler();
	boolean mPageDown = true;
	private int mParseCount = 0;
	private int mTotalCount;

	private String mLastApiUrl;

	private String mUrl;
	private String mGroupName;
	private String mBlogName;
    private boolean noMoreData;

	private WebViewClient mWebViewClient = new WebViewClient() {
		@Override
		public synchronized WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
			try {
				if (webResourceRequest.getUrl().toString().endsWith("jpg")) {
					return new WebResourceResponse("image/jpg", "UTF-8", null);
				}
				if (webResourceRequest.getUrl().toString().equals(mUrl)) {
					OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
					String html = okHttpClient.newCall(new Request.Builder().get().url(mUrl).build()).execute().body().string();
					Document doc = Jsoup.parse(html);
					Elements elements = doc.getElementsByTag("script");
					for (Element element : elements) {
						if (element.html().contains("window._sharedData")) {
							String json = element.html().replace("window._sharedData = ", "");
							json = json.substring(0, json.length() - 1);
							InstagramPageShareDataVo instagramPageShareDataVo = new Gson().fromJson(json, InstagramPageShareDataVo.class);
							if (instagramPageShareDataVo != null) {
								String avatar = instagramPageShareDataVo.getEntry_data().getProfilePage().get(0).getGraphql().getUser().getProfile_pic_url_hd();
								String fileName = avatar.substring(avatar.lastIndexOf("/"), avatar.length());
								final String toFile = Tools.getStoragePathByFileName(fileName);
								if (!new File(toFile).exists()) {
									FileDownloader.getImpl().create(avatar)
											.setPath(toFile)
											.start();
								}
								DownloadRecordDatabase.insertNewGroupDownload(avatar, mGroupName);
								mTotalCount = instagramPageShareDataVo.getEntry_data().getProfilePage().get(0).getGraphql().getUser().getEdge_owner_to_timeline_media().getCount();
								List<Edges> nodesList = instagramPageShareDataVo.getEntry_data().getProfilePage().get(0).getGraphql().getUser().getEdge_owner_to_timeline_media().getEdges();
								mParseCount += nodesList.size();
								if (nodesList.size() == 0) {
									mHandler.post(new Runnable() {
										@Override
										public void run() {
											Toast.makeText(InstagramParseActivity.this, "This account is private.", Toast.LENGTH_LONG).show();
											mViewBinding.progressBar.setVisibility(View.GONE);
										}
									});
								}
								for (Edges edges : nodesList) {
									switch (edges.getNode().get__typename()) {
										case InstagramBlogPostListVo.POST_TYPE_PHOTO:
											FileDownloadUtil.addInstagramPicDownload(edges.getNode().getDisplay_url(), mGroupName);
											break;
										case InstagramBlogPostListVo.POST_TYPE_VIDEO:
											List<InstagramDownloadInfo> videoDownlaodInfos = InstagramParse.parsePostByUrl("https://www.instagram.com/p/" + edges.getNode().getShortcode() + "/");
											if (videoDownlaodInfos != null && videoDownlaodInfos.size() > 0) {
												FileDownloadUtil.addInstagramVideoDownload((videoDownlaodInfos.get(0)).getThumbnailUrl(), videoDownlaodInfos.get(0).mUrl, mGroupName);
											}
											break;
										case InstagramBlogPostListVo.POST_TYPE_SIDECAR:
											List<InstagramDownloadInfo> downloadInfos = InstagramParse.parsePostByUrl("https://www.instagram.com/p/" + edges.getNode().getShortcode() + "/");
											if (downloadInfos != null && downloadInfos.size() > 0) {
												for (InstagramDownloadInfo downloadInfo : downloadInfos) {
													if (downloadInfo.getType().equals(DownloadRecord.TYPE_VIDEO)) {
														FileDownloadUtil.addInstagramVideoDownload(downloadInfo.getThumbnailUrl(), downloadInfo.mUrl, mGroupName);
													} else {
														FileDownloadUtil.addInstagramPicDownload(downloadInfo.mThumbnailUrl, mGroupName);
													}
												}
											}
											break;
									}
								}
							}
							notifyDataChange();
							break;
						}
					}
					//return new WebResourceResponse("text/html", "UTF-8", new ByteArrayInputStream(html.getBytes()));
				}

				if (webResourceRequest.getUrl().toString().startsWith("https://www.instagram.com/graphql/query/") &&
						!TextUtils.equals(webResourceRequest.getUrl().toString(), mLastApiUrl)) {
					mLastApiUrl = webResourceRequest.getUrl().toString();
					System.out.println("InstagramParseActivity" + ":" + "shouldInterceptRequest" + " --> " + webResourceRequest.getUrl().toString());
					OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
					Request.Builder builder = new Request.Builder().get().url(webResourceRequest.getUrl().toString());
					for (String key : webResourceRequest.getRequestHeaders().keySet()) {
						builder.addHeader(key, webResourceRequest.getRequestHeaders().get(key));
					}
					builder.addHeader("cookie", CookieManager.getInstance().getCookie(mUrl));
					String result = okHttpClient.newCall(builder.build()).execute().body().string();
					InstagramBlogPostListVo instagramBlogPostListVo = new Gson().fromJson(result, InstagramBlogPostListVo.class);
					if (instagramBlogPostListVo != null && instagramBlogPostListVo.status.equals("ok")) {
						List<InstagramBlogPostListVo.Edge> edges = instagramBlogPostListVo.data.user.edge_owner_to_timeline_media.edges;
						if (!instagramBlogPostListVo.data.user.edge_owner_to_timeline_media.page_info.has_next_page) {
							mHandler.removeCallbacks(mWebViewScroll);
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(InstagramParseActivity.this, "No more data.", Toast.LENGTH_LONG).show();
									mViewBinding.progressBar.setVisibility(View.GONE);
									mViewBinding.btnImageUrlCount.setVisibility(View.GONE);
									mViewBinding.btnSaveImage.setVisibility(View.GONE);
									noMoreData = false;
								}
							});
						}
						if (edges != null && edges.size() > 0) {
							mParseCount += edges.size();
							for (InstagramBlogPostListVo.Edge edge : edges) {
								switch (edge.node.__typename) {
									case InstagramBlogPostListVo.POST_TYPE_PHOTO:
										FileDownloadUtil.addInstagramPicDownload(edge.node.display_url, mGroupName);
										break;
									case InstagramBlogPostListVo.POST_TYPE_VIDEO:
										List<InstagramDownloadInfo> videoDownlaodInfos = InstagramParse.parsePostByUrl("https://www.instagram.com/p/" + edge.node.shortcode + "/");
										if (videoDownlaodInfos != null && videoDownlaodInfos.size() > 0) {
											FileDownloadUtil.addInstagramVideoDownload((videoDownlaodInfos.get(0)).getThumbnailUrl(), videoDownlaodInfos.get(0).mUrl, mGroupName);
										}
										break;
									case InstagramBlogPostListVo.POST_TYPE_SIDECAR:
										List<InstagramDownloadInfo> downloadInfos = InstagramParse.parsePostByUrl("https://www.instagram.com/p/" + edge.node.shortcode + "/");
										if (downloadInfos != null && downloadInfos.size() > 0) {
											for (InstagramDownloadInfo downloadInfo : downloadInfos) {
												if (downloadInfo.getType().equals(DownloadRecord.TYPE_VIDEO)) {
													FileDownloadUtil.addInstagramVideoDownload(downloadInfo.getThumbnailUrl(), downloadInfo.mUrl, mGroupName);
												} else {
													FileDownloadUtil.addInstagramPicDownload(downloadInfo.mThumbnailUrl, mGroupName);
												}
											}
										}
										break;
								}
								notifyDataChange();
							}
						}
					} else {
						System.out.println("InstagramParseActivity" + ":" + "shouldInterceptRequest" + " --> " + result);
					}
					return new WebResourceResponse("application/json", "UTF-8", new ByteArrayInputStream(result.getBytes()));
				}
			} catch (Exception e) {
				e.printStackTrace();
				Crashlytics.logException(e);
			}
			return super.shouldInterceptRequest(webView, webResourceRequest);
		}

		@Override
		public void onPageFinished(WebView webView, String s) {
			super.onPageFinished(webView, s);
			mHandler.postDelayed(mWebViewScroll, 2000);
		}
	};

    public InstagramParseActivity() {
    }

	private void notifyDataChange() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mViewBinding.btnImageUrlCount.setText(String.format(Locale.US, "%s %d/%d", getString(R.string.ins_parse_count), mParseCount, mTotalCount));
			}
		});
	}

	private WebChromeClient mWebChromeClient = new WebChromeClient() {
		@Override
		public void onProgressChanged(WebView view, final int newProgress) {
			super.onProgressChanged(view, newProgress);
		}
	};

	@Override
	public void initData() {
		mUrl = getIntent().getStringExtra(EXTRA_INSTAGRAM_BLOG_URL);
		String tmpString = mUrl.substring(0, mUrl.length() - 1);
		int startIndex = tmpString.lastIndexOf("/") + 1;
		int endIndex = tmpString.length();
		mBlogName = tmpString.substring(startIndex, endIndex);
		if (mBlogName.contains("?")) {
			mBlogName = mBlogName.substring(0, mBlogName.indexOf("?"));
		}
		mGroupName = TAG_INSTAGRAM + "_" + mBlogName;
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onResume() {
		super.onResume();
		mViewBinding.flContentContainer.setBackgroundColor(AppUiConfig.sThemeColor);

		mViewBinding.webView.setWebViewClient(mWebViewClient);
		mViewBinding.webView.getSettings().setJavaScriptEnabled(true);
		mViewBinding.webView.setWebChromeClient(mWebChromeClient);

		mHandler.postDelayed(mWebViewScroll, 2000);

		DownloadTaskFragment downloadTaskFragment = (DownloadTaskFragment) getSupportFragmentManager().findFragmentById(R.id.fl_content_container);
		if (downloadTaskFragment == null) {
			downloadTaskFragment = DownloadTaskFragment.newInstance(mGroupName);
			getSupportFragmentManager().beginTransaction().add(R.id.fl_content_container, downloadTaskFragment).commit();
		}

		mViewBinding.toolbar.setTitle(mBlogName);

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mViewBinding.webView.loadUrl(mUrl);
			}
		}, 1000);

//		mInterstitialAd = new InterstitialAd(this);
//		mInterstitialAd.setAdUnitId("ca-app-pub-3870083751110505/5125381905");
//		mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice(TumlodrBottomAdsLayout.DEVICE_ID).build());
//		mInterstitialAd.setAdListener(new AdListener() {
//			@Override
//			public void onAdLoaded() {
//				super.onAdLoaded();
//				mInterstitialAd.show();
//			}
//		});

		mViewBinding.btnSaveImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private Runnable mWebViewScroll = new Runnable() {
		@Override
		public void run() {
			if (mPageDown) {
				mViewBinding.webView.pageDown(true);
			} else {
				mViewBinding.webView.pageUp(false);
			}
			mPageDown = !mPageDown;
			if (!noMoreData) {
                mHandler.postDelayed(mWebViewScroll, 2000);
            }
		}
	};

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_instagram_parse;
	}

	@Override
	public Drawable getToolbarNavigationIcon() {
		return ViewUtils.getArrowDrawable();
	}

	@Override
	protected String getTitleString() {
		return mBlogName;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mHandler != null) {
			mHandler.removeCallbacks(mWebViewScroll);
		}
	}
//video/mp4
    public void showImageDownloadSuccessSnackBar(String imagePath) {
        new SingleMediaScanner(this, imagePath);
    }

    public void showVideoDownloadSuccessSnackBar(String videoPath) {
	    new SingleMediaScanner(this, videoPath);
    }
}
