package com.hochan.tumlodr.module.video.videocontrol;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.LayoutTiktokVideoControlBinding;
import com.hochan.tumlodr.jumblr.types.Post;
import com.hochan.tumlodr.jumblr.types.VideoPost;
import com.hochan.tumlodr.model.BaseObserver;
import com.hochan.tumlodr.model.TumlodrService;
import com.hochan.tumlodr.model.sharedpreferences.UserInfo;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.tools.HtmlTool;
import com.hochan.tumlodr.tools.Tools;
import com.hochan.tumlodr.ui.activity.BlogPostListActivity;
import com.hochan.tumlodr.ui.activity.PostDetailActivity;
import com.hochan.tumlodr.util.FileDownloadUtil;
import com.hochan.tumlodr.util.ViewUtils;

/**
 * .
 * Created by hochan on 2018/6/2.
 */

public class TikTokViewVideoControl extends DefaultVideoControl {

	protected TextView tvBlogName;
	protected ImageView ivBlogAvatar;
	protected TextView tvResolution;
	protected ImageButton btnPost, btnDownload, btnLike, btnReblog;
	private ImageButton btnShowUrl;
	private TextView tvUrl;
	protected Post mPost;

	public TikTokViewVideoControl(@NonNull Context context) {
		super(context);
	}

	@Override
	protected int getRecourseLayout() {
		return R.layout.layout_tiktok_video_control;
	}

	@Override
	protected void retrieveViews(View contentView) {
		LayoutTiktokVideoControlBinding viewBinding = LayoutTiktokVideoControlBinding.bind(contentView);
		tvChangePosition = viewBinding.tvChangePosition;
		tvProgress = viewBinding.tvProgress;
		tvDuration = viewBinding.tvDuration;
		videoSeekBar = viewBinding.videoSeekBar;
		bottomProgress = viewBinding.bottomProgress;
		loadingProgressBar = viewBinding.loadingProgressBar;
		llLoadingProgressBar = viewBinding.llLoadingProgress;
		btnPlayAndPause = viewBinding.btnPlay;
		btnSmallPlayPause = viewBinding.btnSmallPlayPause;
		llProgressLayout = viewBinding.llProgressLayout;
		btnRotate = viewBinding.btnRotate;
		btnFullScreen = viewBinding.btnFullscreen;
		tvBlogName = viewBinding.tvBlogName;
		ivBlogAvatar = viewBinding.ivAvatar;
		btnPost = viewBinding.btnPost;
		btnDownload = viewBinding.btnDownload;
		btnLike = viewBinding.btnLike;
		btnReblog = viewBinding.btnReblog;
		tvLoadingProgress = viewBinding.tvLoadingProgress;
		tvResolution = viewBinding.tvResolution;
		btnShowUrl = viewBinding.btnShowUrl;
		tvUrl = viewBinding.tvUrl;
		tvDownloadSpeed = viewBinding.tvLoadingSpeed;
	}

	public void setPost(final Post post) {
		if (post == null) {
			return;
		}
		mPost = post;
		if (post instanceof VideoPost) {
			setVideoPost((VideoPost) post);
		}
		tvBlogName.setText(post.getBlogName());
		TumlodrGlide.with(getContext())
				.load(Tools.getAvatarUrlByBlogName(post.getBlogName()))
				.placeholder(AppUiConfig.sPicHolderResource)
				.transform(new RoundedCorners(5))
				.skipMemoryCache(true)
				.into(ivBlogAvatar);
		if (post.isLiked() != null && post.isLiked()) {
			btnLike.setImageResource(R.drawable.ic_favorite_red);
		} else {
			btnLike.setImageResource(R.drawable.ic_favorite);
		}
		btnPost.setVisibility(VISIBLE);
		btnPost.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PostDetailActivity.showPostDetail(getContext(), post);
			}
		});
	}

	@Override
	protected void initViews() {
		super.initViews();
		tvBlogName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BlogPostListActivity.start(getContext(), mPost.getBlogName(), mPost.followed);
			}
		});
		ivBlogAvatar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BlogPostListActivity.start(getContext(), mPost.getBlogName(), mPost.followed);
			}
		});
		btnPost.setVisibility(GONE);
		btnDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				downloadVideo();
			}
		});
		btnLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnLike.setImageResource(R.drawable.ic_favorite_red);
				TumlodrService.likePost(mPost.getId(), mPost.getReblogKey())
						.subscribe(new BaseObserver<Long>() {
							@Override
							public void onError(Throwable e) {
								if (btnLike.getTag() != null && btnLike.getTag().equals(mPost.getId())) {
									btnLike.setImageResource(R.drawable.ic_favorite);
								}
							}

							@Override
							public void onNext(Long aLong) {
								TumlodrService.setPostLiked(mPost, true);
							}
						});
				ViewUtils.likeAnimation(btnLike);
			}
		});
		btnReblog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TumlodrService.reblog(UserInfo.sUserName,
						mPost.getId(), mPost.getReblogKey());
			}
		});
		btnShowUrl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				show();
				if (tvUrl.getVisibility() == VISIBLE) {
					tvUrl.setVisibility(GONE);
				} else {
					tvUrl.setVisibility(VISIBLE);
				}
			}
		});
	}

	public void setVideoPost(VideoPost videoPost) {
		mPost = videoPost;
		final FileDownloadUtil.TumblrVideoDownloadInfo tumblrVideoDownloadInfo = FileDownloadUtil.getVideoDownloadInfo(videoPost);
		if (tumblrVideoDownloadInfo == null) {
			return;
		}
		tvUrl.setText(HtmlTool.fromHtml("<a href='" + tumblrVideoDownloadInfo.getVideoUrl()
				+ "'>" + tumblrVideoDownloadInfo.getVideoUrl() + "</a>", tvUrl));
	}

	private void downloadVideo() {
		FileDownloadUtil.download(mPost);
	}
}
