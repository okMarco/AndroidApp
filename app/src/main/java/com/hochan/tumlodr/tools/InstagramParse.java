package com.hochan.tumlodr.tools;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.hochan.tumlodr.model.data.InstagramBlogPostListVo;
import com.hochan.tumlodr.model.data.InstagramDownloadInfo;
import com.hochan.tumlodr.model.data.download.DownloadRecord;
import com.hochan.tumlodr.model.data.instagramsidecar.com.besjon.pojo.Edges;
import com.hochan.tumlodr.model.data.instagramsidecar.com.besjon.pojo.PostPageJsonRootBean;
import com.hochan.tumlodr.ui.activity.InstagramParseActivity;
import com.tencent.bugly.crashreport.CrashReport;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * .
 * Created by hochan on 2018/2/10.
 */

public class InstagramParse {

	private static final String MATCH_INS_PHOTO = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|].jpg";
	private static final String MATCH_INS_VIDEO = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|].mp4";

	private static final String MATCH_INS_POST = "https://www.instagram.com/p/[-0-9a-zA-Z_]*/.*";
	private static final String MATCH_INS_BLOG = "https://www.instagram.com/[.0-9a-zA-Z_]*/";

	public static String parseClipboard(Activity activity) {
		try {
			ClipboardManager cbm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
			String url = null;
			if (cbm != null && cbm.getPrimaryClip() != null
					&& cbm.getPrimaryClip().getItemCount() > 0
					&& cbm.getPrimaryClip().getItemAt(0).getText() != null) {
				url = cbm.getPrimaryClip().getItemAt(0).getText().toString();
			}
			if (!TextUtils.isEmpty(url)) {
				if (url.matches(MATCH_INS_POST)) {
					cbm.setPrimaryClip(ClipData.newPlainText(null, ""));
					return url;
				}

				if (url.matches(MATCH_INS_BLOG)) {
					cbm.setPrimaryClip(ClipData.newPlainText(null, ""));
					Intent intent = new Intent(activity, InstagramParseActivity.class);
					intent.putExtra(InstagramParseActivity.EXTRA_INSTAGRAM_BLOG_URL, url);
					activity.startActivity(intent);
				}
			}
		} catch (Exception e) {
			Crashlytics.logException(e);
		}
		return null;
	}

	@Nullable
	public static List<InstagramDownloadInfo> parsePostByUrl(String url) {
		try {
			List<InstagramDownloadInfo> instagramDownloadInfos = new ArrayList<>();
			OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
			String result = okHttpClient.newCall(new Request.Builder().get().url(url).build()).execute().body().string();
			String shareData = getPageShareData(result);
			if (!TextUtils.isEmpty(shareData)) {
				PostPageJsonRootBean postPageJsonRootBean = new Gson().fromJson(shareData, PostPageJsonRootBean.class);
				if (postPageJsonRootBean.getEntry_data().getPostPage().get(0).getGraphql().getShortcode_media().get__typename().equals(InstagramBlogPostListVo.POST_TYPE_SIDECAR)) {
					List<Edges> edgesList = postPageJsonRootBean.getEntry_data().getPostPage().get(0).getGraphql().getShortcode_media().getEdge_sidecar_to_children().getEdges();
					for (int i = 0; i < edgesList.size(); i++) {
						Edges edges = edgesList.get(i);
						if (edges.getNode().getIs_video()) {
							InstagramDownloadInfo instagramDownloadInfo = new InstagramDownloadInfo(DownloadRecord.TYPE_VIDEO, edges.getNode().getDisplay_url(),
									edges.getNode().getVideo_url());
							instagramDownloadInfos.add(instagramDownloadInfo);
						} else {
							InstagramDownloadInfo instagramDownloadInfo = new InstagramDownloadInfo(DownloadRecord.TYPE_IMAGE, edges.getNode().getDisplay_url(),
									edges.getNode().getDisplay_url());
							instagramDownloadInfos.add(instagramDownloadInfo);
						}
					}
				} else if (postPageJsonRootBean.getEntry_data().getPostPage().get(0).getGraphql().getShortcode_media().get__typename().equals(InstagramBlogPostListVo.POST_TYPE_VIDEO)) {
					String thumbnail = postPageJsonRootBean.getEntry_data().getPostPage().get(0).getGraphql().getShortcode_media().getDisplay_url();
					String videoUrl = postPageJsonRootBean.getEntry_data().getPostPage().get(0).getGraphql().getShortcode_media().getVideo_url();
					InstagramDownloadInfo instagramDownloadInfo = new InstagramDownloadInfo(DownloadRecord.TYPE_VIDEO, thumbnail, videoUrl);
					instagramDownloadInfos.add(instagramDownloadInfo);
				} else if (postPageJsonRootBean.getEntry_data().getPostPage().get(0).getGraphql().getShortcode_media().get__typename().equals(InstagramBlogPostListVo.POST_TYPE_PHOTO)) {
					String photoUrl = postPageJsonRootBean.getEntry_data().getPostPage().get(0).getGraphql().getShortcode_media().getDisplay_url();
					InstagramDownloadInfo downloadInfo = new InstagramDownloadInfo(DownloadRecord.TYPE_IMAGE, photoUrl, photoUrl);
					instagramDownloadInfos.add(downloadInfo);
				}
				return instagramDownloadInfos;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getPageShareData(String html) {
		Document doc = Jsoup.parse(html);
		Elements elements = doc.getElementsByTag("script");
		for (Element element : elements) {
			if (element.html().contains("window._sharedData")) {
				String json = element.html().replace("window._sharedData = ", "");
				json = json.substring(0, json.length() - 1);
				return json;
			}
		}
		return null;
	}
}
