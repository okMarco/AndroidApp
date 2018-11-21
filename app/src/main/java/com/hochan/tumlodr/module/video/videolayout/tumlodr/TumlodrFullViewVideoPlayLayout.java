package com.hochan.tumlodr.module.video.videolayout.tumlodr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.hochan.tumlodr.module.video.videolayout.TikTokVideoLayout;
import com.hochan.tumlodr.util.FileDownloadUtil;
import com.tumblr.jumblr.types.VideoPost;

/**
 * .
 * Created by hochan on 2018/7/8.
 */

public class TumlodrFullViewVideoPlayLayout extends TikTokVideoLayout {

	private VideoPost mVideoPost;
	private FileDownloadUtil.TumblrVideoDownloadInfo mTumblrVideoDownloadInfo;

	public TumlodrFullViewVideoPlayLayout(@NonNull Context context) {
		super(context);
	}

	public TumlodrFullViewVideoPlayLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public TumlodrFullViewVideoPlayLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public VideoPost getVideoPost() {
		return mVideoPost;
	}

	public void setVideoPost(VideoPost videoPost) {
		mVideoPost = videoPost;
		mTumblrVideoDownloadInfo = FileDownloadUtil.getVideoDownloadInfo(videoPost);
		setData(mTumblrVideoDownloadInfo.getVideoUrl(), mTumblrVideoDownloadInfo.getVideoThumbnail());
	}
}
