package com.hochan.tumlodr.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
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
import com.hochan.tumlodr.ui.activity.baseactivity.BaseViewBindingActivity;
import com.hochan.tumlodr.ui.component.TumlodrBottomAdsLayout;
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
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class InstagramParseActivity extends BaseViewBindingActivity<ActivityInstagramParseBinding> {

	public static final String EXTRA_INSTAGRAM_BLOG_URL = "name";
    public static final String TAG_INSTAGRAM = "instagram";

	private InstagramParseHandler instagramParseHandler;
	boolean pageDown = true;
	private int parseCount = 0;
	private int totalCount;

	private String lastApiUrl;
	private String url;
	private String groupName;
	private String blogName;
    private boolean noMoreData;
    private boolean hasExit;

    private boolean isFirstEnter = true;

    private WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, final int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    };

	private WebViewClient mWebViewClient = new WebViewClient() {
		@Override
		public synchronized WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
			try {
				if (webResourceRequest.getUrl().toString().endsWith("jpg")) {
					return new WebResourceResponse("image/jpg", "UTF-8", null);
				}
				if (webResourceRequest.getUrl().toString().equals(url)) {
                    parseSharedData();
				}

				if (webResourceRequest.getUrl().toString().startsWith("https://www.instagram.com/graphql/query/") &&
						!TextUtils.equals(webResourceRequest.getUrl().toString(), lastApiUrl)) {
                    String result = parseJsonData(webResourceRequest);
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
			instagramParseHandler.postDelayed(instagramParseHandler.webViewScroll, 2000);
		}
	};

    private synchronized void parseSharedData() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        String html = okHttpClient.newCall(new Request.Builder().get().url(url).build()).execute().body().string();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByTag("script");
        for (Element element : elements) {
            if (element.html().contains("window._sharedData")) {
                String json = element.html().replace("window._sharedData = ", "");
                json = json.substring(0, json.length() - 1);
                InstagramPageShareDataVo instagramPageShareDataVo = new Gson().fromJson(json, InstagramPageShareDataVo.class);
                if (instagramPageShareDataVo != null) {
                    String avatar = instagramPageShareDataVo.getEntry_data().getProfilePage().get(0).getGraphql().getUser().getProfile_pic_url_hd();
                    String toFile = FileDownloadUtil.getInstagramGroupDownloadDirect(groupName) + groupName + ".jpg";
                    if (!new File(toFile).exists()) {
                        FileDownloader.getImpl().create(avatar)
                                .setPath(toFile)
                                .start();
                    }
                    DownloadRecordDatabase.insertNewGroupDownload(avatar, toFile, groupName);
                    totalCount = instagramPageShareDataVo.getEntry_data().getProfilePage().get(0).getGraphql().getUser().getEdge_owner_to_timeline_media().getCount();
                    List<Edges> nodesList = instagramPageShareDataVo.getEntry_data().getProfilePage().get(0).getGraphql().getUser().getEdge_owner_to_timeline_media().getEdges();
                    parseCount += nodesList.size();
                    if (nodesList.size() == 0) {
                        instagramParseHandler.notifyPrivateAccount();
                    }
                    for (Edges edges : nodesList) {
                        downloadNode(edges);
                    }
                    notifyDataChange();
                }
                break;
            }
        }
    }

    private void downloadNode(Edges edges) {
        switch (edges.getNode().get__typename()) {
            case InstagramBlogPostListVo.POST_TYPE_PHOTO: {
                FileDownloadUtil.addInstagramPicDownload(edges.getNode().getDisplay_url(), groupName);
                break;
            }
            case InstagramBlogPostListVo.POST_TYPE_VIDEO: {
                List<InstagramDownloadInfo> videoDownloadInfos = InstagramParse.parsePostByUrl("https://www.instagram.com/p/" + edges.getNode().getShortcode() + "/");
                if (videoDownloadInfos != null && videoDownloadInfos.size() > 0) {
                    FileDownloadUtil.addInstagramVideoDownload((videoDownloadInfos.get(0)).getThumbnailUrl(), videoDownloadInfos.get(0).mUrl, groupName);
                }
                break;
            }
            case InstagramBlogPostListVo.POST_TYPE_SIDECAR: {
                List<InstagramDownloadInfo> downloadInfos = InstagramParse.parsePostByUrl("https://www.instagram.com/p/" + edges.getNode().getShortcode() + "/");
                if (downloadInfos != null && downloadInfos.size() > 0) {
                    for (InstagramDownloadInfo downloadInfo : downloadInfos) {
                        if (downloadInfo.getType().equals(DownloadRecord.TYPE_VIDEO)) {
                            FileDownloadUtil.addInstagramVideoDownload(downloadInfo.getThumbnailUrl(), downloadInfo.mUrl, groupName);
                        } else {
                            FileDownloadUtil.addInstagramPicDownload(downloadInfo.mThumbnailUrl, groupName);
                        }
                    }
                }
                break;
            }
        }
    }

    private String parseJsonData(WebResourceRequest webResourceRequest) throws IOException {
        lastApiUrl = webResourceRequest.getUrl().toString();
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request.Builder builder = new Request.Builder().get().url(webResourceRequest.getUrl().toString());
        for (String key : webResourceRequest.getRequestHeaders().keySet()) {
            builder.addHeader(key, webResourceRequest.getRequestHeaders().get(key));
        }
        builder.addHeader("cookie", CookieManager.getInstance().getCookie(url));
        String result = okHttpClient.newCall(builder.build()).execute().body().string();
        downloadEdgeList(result);
        return result;
    }

    private synchronized void downloadEdgeList(String result) {
        InstagramBlogPostListVo instagramBlogPostListVo = new Gson().fromJson(result, InstagramBlogPostListVo.class);
        if (instagramBlogPostListVo != null && instagramBlogPostListVo.status.equals("ok")) {
            List<InstagramBlogPostListVo.Edge> edges = instagramBlogPostListVo.data.user.edge_owner_to_timeline_media.edges;
            if (!instagramBlogPostListVo.data.user.edge_owner_to_timeline_media.page_info.has_next_page) {
                instagramParseHandler.notifyNoMoreData();
                instagramParseHandler.removeCallbacks(instagramParseHandler.webViewScroll);
            }
            if (edges != null && edges.size() > 0) {
                parseCount += edges.size();
                for (InstagramBlogPostListVo.Edge edge : edges) {
                    downloadEdge(edge);
                }
                notifyDataChange();
            }
        }
    }

    private void downloadEdge(InstagramBlogPostListVo.Edge edge) {
        switch (edge.node.__typename) {
            case InstagramBlogPostListVo.POST_TYPE_PHOTO:
                FileDownloadUtil.addInstagramPicDownload(edge.node.display_url, groupName);
                break;
            case InstagramBlogPostListVo.POST_TYPE_VIDEO:
                List<InstagramDownloadInfo> videoDownloadInfos = InstagramParse.parsePostByUrl("https://www.instagram.com/p/" + edge.node.shortcode + "/");
                if (videoDownloadInfos != null && videoDownloadInfos.size() > 0) {
                    FileDownloadUtil.addInstagramVideoDownload((videoDownloadInfos.get(0)).getThumbnailUrl(), videoDownloadInfos.get(0).mUrl, groupName);
                }
                break;
            case InstagramBlogPostListVo.POST_TYPE_SIDECAR:
                List<InstagramDownloadInfo> downloadInfos = InstagramParse.parsePostByUrl("https://www.instagram.com/p/" + edge.node.shortcode + "/");
                if (downloadInfos != null && downloadInfos.size() > 0) {
                    for (InstagramDownloadInfo downloadInfo : downloadInfos) {
                        if (downloadInfo.getType().equals(DownloadRecord.TYPE_VIDEO)) {
                            FileDownloadUtil.addInstagramVideoDownload(downloadInfo.getThumbnailUrl(), downloadInfo.mUrl, groupName);
                        } else {
                            FileDownloadUtil.addInstagramPicDownload(downloadInfo.mThumbnailUrl, groupName);
                        }
                    }
                }
                break;
        }
    }

	private void notifyDataChange() {
		instagramParseHandler.updateParsedCount();
	}

	@Override
	public void initData() {
		url = getIntent().getStringExtra(EXTRA_INSTAGRAM_BLOG_URL);
		String tmpString = url.substring(0, url.length() - 1);
		int startIndex = tmpString.lastIndexOf("/") + 1;
		int endIndex = tmpString.length();
		blogName = tmpString.substring(startIndex, endIndex);
		if (blogName.contains("?")) {
			blogName = blogName.substring(0, blogName.indexOf("?"));
		}
		groupName = TAG_INSTAGRAM + "_" + blogName;
	}

    @SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onResume() {
		super.onResume();
		viewBinding.flContentContainer.setBackgroundColor(AppUiConfig.sThemeColor);

		viewBinding.webView.setWebViewClient(mWebViewClient);
		viewBinding.webView.getSettings().setJavaScriptEnabled(true);
		viewBinding.webView.setWebChromeClient(webChromeClient);

		DownloadTaskFragment downloadTaskFragment = (DownloadTaskFragment) getSupportFragmentManager().findFragmentById(R.id.fl_content_container);
		if (downloadTaskFragment == null) {
			downloadTaskFragment = DownloadTaskFragment.newInstance(groupName);
			getSupportFragmentManager().beginTransaction().add(R.id.fl_content_container, downloadTaskFragment).commit();
		}

		viewBinding.toolbar.setTitle(blogName);

		instagramParseHandler = new InstagramParseHandler(InstagramParseActivity.this);
		instagramParseHandler.startLoadUrl();
        instagramParseHandler.postDelayed(instagramParseHandler.webViewScroll, 2000);

		viewBinding.btnSaveImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                if (instagramParseHandler != null) {
                    instagramParseHandler.removeCallbacks(instagramParseHandler.webViewScroll);
                }
				finish();
			}
		});

		if (isFirstEnter) {
		    isFirstEnter = false;
            final InterstitialAd interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId(TumlodrBottomAdsLayout.ADUNIT_ID_INSTAGRAM_ACTIVITY);
            interstitialAd.loadAd(new AdRequest.Builder().addTestDevice(TumlodrBottomAdsLayout.DEVICE_ID).build());
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    interstitialAd.show();
                }
            });
        }
	}

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
		return blogName;
	}

    @Override
    public void setUpObserver() {}

    @Override
	protected void onDestroy() {
		super.onDestroy();
		hasExit = true;
		if (instagramParseHandler != null) {
			instagramParseHandler.removeCallbacks(instagramParseHandler.webViewScroll);
		}
	}

	static class InstagramParseHandler extends Handler {

		private WeakReference<InstagramParseActivity> activityWeakReference;

		InstagramParseHandler(InstagramParseActivity activity) {
			this.activityWeakReference = new WeakReference<>(activity);
		}

		private void startLoadUrl() {
			postDelayed(new Runnable() {
				@Override
				public void run() {
					if (activityWeakReference.get() == null) {
						return;
					}
					InstagramParseActivity activity = activityWeakReference.get();
					activity.viewBinding.webView.loadUrl(activity.url);
				}
			}, 1000);
		}

		private void updateParsedCount() {
			post(new Runnable() {
				@Override
				public void run() {
					if (activityWeakReference.get() == null) {
						return;
					}
					InstagramParseActivity activity = activityWeakReference.get();
					activity.viewBinding.btnImageUrlCount.setText(String.format(Locale.US, "%s %d/%d",
							activity.getString(R.string.ins_parse_count), activity.parseCount, activity.totalCount));
				}
			});
		}

		private Runnable webViewScroll = new Runnable() {
			@Override
			public void run() {
				if (activityWeakReference.get() == null) {
					return;
				}
				InstagramParseActivity activity = activityWeakReference.get();
				if (activity.pageDown) {
					activity.viewBinding.webView.pageDown(true);
				} else {
					activity.viewBinding.webView.pageUp(false);
				}
				activity.pageDown = !activity.pageDown;
				if (!activity.noMoreData && !activity.hasExit) {
					postDelayed(webViewScroll, 2000);
				}
			}
		};

		private void notifyPrivateAccount() {
            post(new Runnable() {
                @Override
                public void run() {
                    if (activityWeakReference.get() == null) {
                        return;
                    }
                    InstagramParseActivity activity = activityWeakReference.get();
                    Toast.makeText(activity, "This account is private.", Toast.LENGTH_LONG).show();
                    activity.viewBinding.progressBar.setVisibility(View.GONE);
                    removeCallbacks(webViewScroll);
                }
            });
        }

        private void notifyNoMoreData() {
            post(new Runnable() {
                @Override
                public void run() {
                    if (activityWeakReference.get() == null) {
                        return;
                    }
                    InstagramParseActivity activity = activityWeakReference.get();
                    Toast.makeText(activity, "No more data.", Toast.LENGTH_LONG).show();
                    activity.viewBinding.progressBar.setVisibility(View.GONE);
                    activity.viewBinding.btnImageUrlCount.setVisibility(View.GONE);
                    activity.viewBinding.btnSaveImage.setVisibility(View.GONE);
                    activity.noMoreData = false;
                }
            });
        }
	}
}
