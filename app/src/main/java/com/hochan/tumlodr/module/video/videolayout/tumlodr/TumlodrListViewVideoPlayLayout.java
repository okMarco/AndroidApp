package com.hochan.tumlodr.module.video.videolayout.tumlodr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.hochan.tumlodr.jumblr.types.VideoPost;
import com.hochan.tumlodr.module.video.videolayout.VideoPlayLayout;

/**
 * .
 * Created by hochan on 2018/7/8.
 */

public class TumlodrListViewVideoPlayLayout extends VideoPlayLayout implements ITumlodrVideoPlayLayout {

	private VideoPost mVideoPost;

	public TumlodrListViewVideoPlayLayout(@NonNull Context context) {
		super(context);
	}

	public TumlodrListViewVideoPlayLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public TumlodrListViewVideoPlayLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void setVideoPost(VideoPost videoPost) {
		mVideoPost = videoPost;
		
	}

	@Override
	public VideoPost getVideoPost() {
		return mVideoPost;
	}
}
