package com.hochan.tumlodr.model.data;

/**
 * .
 * Created by hochan on 2018/2/10.
 */

@SuppressWarnings("WeakerAccess")
public class InstagramDownloadInfo {

	public String mType;
	public String mThumbnailUrl;
	public String mUrl;

	public InstagramDownloadInfo(String type, String thumbnail, String url) {
		mType = type;
		mThumbnailUrl = thumbnail;
		mUrl = url;
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		mType = type;
	}

	public String getThumbnailUrl() {
		return mThumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		mThumbnailUrl = thumbnailUrl;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		mUrl = url;
	}
}
