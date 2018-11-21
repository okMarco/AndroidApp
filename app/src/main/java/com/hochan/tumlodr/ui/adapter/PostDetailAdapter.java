package com.hochan.tumlodr.ui.adapter;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.ItemPostAudioBinding;
import com.hochan.tumlodr.databinding.ItemPostDetailBinding;
import com.hochan.tumlodr.databinding.ItemPostDetailVideoBinding;
import com.hochan.tumlodr.databinding.LayoutAudioPostDetailBinding;
import com.hochan.tumlodr.model.TumlodrService;
import com.hochan.tumlodr.model.data.CommentBody;
import com.hochan.tumlodr.model.data.TextPostBody;
import com.hochan.tumlodr.model.sharedpreferences.UserInfo;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.glide.TumlodrGlideUtil;
import com.hochan.tumlodr.module.video.videolayout.VideoPlayLayout;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.tools.HtmlTool;
import com.hochan.tumlodr.tools.Tools;
import com.hochan.tumlodr.ui.activity.BlogPostListActivity;
import com.hochan.tumlodr.ui.activity.VideoViewPagerActivity;
import com.hochan.tumlodr.ui.component.CommentBodyLayout;
import com.hochan.tumlodr.ui.component.PostPhotoLayout;
import com.hochan.tumlodr.ui.fragment.NotesFragment;
import com.hochan.tumlodr.util.BlurTransformation;
import com.hochan.tumlodr.util.ColorFilterTransformation;
import com.hochan.tumlodr.util.FileDownloadUtil;
import com.hochan.tumlodr.util.TextPostBodyUtils;
import com.hochan.tumlodr.util.ViewUtils;
import com.tumblr.jumblr.types.AudioPost;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.TextPost;
import com.tumblr.jumblr.types.VideoPost;

import java.util.List;
import java.util.Locale;

/**
 * .
 * Created by hochan on 2017/12/30.
 */

public class PostDetailAdapter extends PostAdapter {

	final static String TYPE_PHOTO = "photo";
	protected final static String TYPE_VIDEO = "video";
	final static String TYPE_TEXT = "text";
	private final static String TYPE_AUDIO = "audio";

	static final int CODE_TYPE_PHOTO = 0;
	static final int CODE_TYPE_VIDEO = 1;
	static final int CODE_TYPE_TEXT = 2;
	private static final int CODE_TYPE_AUDIO = 3;
	static final int CODE_TYPE_UNKNOWN = 11;

	private PostThumbnailAdapter.OnPostCommandEventListener mEventListener;
	private NotesFragment mNotesFragment;

	public PostDetailAdapter(RecyclerView recyclerView, List<Post> postList,
	                         PostThumbnailAdapter.OnPostCommandEventListener eventListener) {
		super(recyclerView, postList);
		mEventListener = eventListener;
	}

	@Override
	public int getItemViewType(int position) {
		if (mDataList != null && position >= 0 && position < mDataList.size() &&
				mDataList.get(position) != null) {
			switch (mDataList.get(position).getType()) {
				case TYPE_PHOTO: {
					return CODE_TYPE_PHOTO;
				}
				case TYPE_VIDEO: {
					return CODE_TYPE_VIDEO;
				}
				case TYPE_TEXT: {
					return CODE_TYPE_TEXT;
				}
				case TYPE_AUDIO: {
					return CODE_TYPE_AUDIO;
				}
				default: {
					return CODE_TYPE_UNKNOWN;
				}
			}
		}
		return super.getItemViewType(position);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		switch (viewType) {
			case CODE_TYPE_PHOTO: {
				return new PhotoPostDetailViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail, parent, false));
			}
			case CODE_TYPE_VIDEO: {
				return new VideoPostDetailViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail_video, parent, false));
			}
			case CODE_TYPE_TEXT: {
				return new TextPostDetailViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail, parent, false));
			}
			case CODE_TYPE_AUDIO: {
				return new AudioPostDetailViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_audio, parent, false));
			}
			case CODE_TYPE_UNKNOWN: {
				return new BasePostDetailViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_detail, parent, false));
			}
		}
		return null;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		onBindBaseViewHolder((BasePostDetailViewHolder) holder, position);
		switch (getItemViewType(position)) {
			case CODE_TYPE_PHOTO: {
				onBindPhotoPostViewHolder((PhotoPostDetailViewHolder) holder, position);
				break;
			}
			case CODE_TYPE_VIDEO: {
				onBindVideoPostViewHolder((VideoPostDetailViewHolder) holder, position);
				break;
			}
			case CODE_TYPE_AUDIO: {
				onBindAudioPostViewHolder((AudioPostDetailViewHolder) holder, position);
				break;
			}
			case CODE_TYPE_TEXT: {
				onBindTextPostViewHolder((TextPostDetailViewHolder) holder, position);
				break;
			}
		}
	}

	protected void onBindBaseViewHolder(BasePostDetailViewHolder holder, int position) {
		Post post = mDataList.get(position);
		holder.tvBlogName.setText(post.getBlogName());
		CharSequence lastPostDate = "";
		lastPostDate = Tools.getRelativeTime(post.getDateGMT(), lastPostDate);
		holder.tvPostTime.setText(lastPostDate);
		if (TumlodrGlideUtil.isContextValid(holder.ivAvatar)) {
			TumlodrGlide.with(holder.ivAvatar)
					.load(Tools.getAvatarUrlByBlogName(post.getBlogName()))
					.transform(new MultiTransformation<>(new CenterCrop(),
							new RoundedCorners(10)))
					.placeholder(AppUiConfig.sPicHolderResource)
					.skipMemoryCache(true)
					.into(holder.ivAvatar);
		}
		holder.tvNoteCount.setText(String.format(Locale.US, "%d%s", post.getNoteCount(), holder.tvNoteCount.getContext().getString(R.string.post_detail_notes)));
		if (post.getSourceTitle() != null && !TextUtils.isEmpty(post.getSourceTitle())) {
			holder.tvSource.setVisibility(View.VISIBLE);
			holder.tvSource.setText(String.format("%s%s", holder.tvSource.getContext().getString(R.string.post_detail_source), post.getSourceTitle()));
		} else {
			holder.tvSource.setVisibility(View.GONE);
		}
		if (post.isLiked() != null && post.isLiked()) {
			holder.ivLike.setImageResource(R.drawable.ic_favorite_red);
		} else {
			holder.ivLike.setImageResource(R.drawable.ic_detail_like);
		}

		holder.cvPost.setCardBackgroundColor(AppUiConfig.sThemeColor);
		holder.tvBlogName.setTextColor(AppUiConfig.sTextColor);
		holder.tvPostBody.setTextColor(AppUiConfig.sTextColor);
		holder.tvSource.setTextColor(AppUiConfig.sSubTextColor);
		holder.tvPostTime.setTextColor(AppUiConfig.sSubTextColor);
		holder.tvNoteCount.setTextColor(AppUiConfig.sTextColor);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			holder.ivDownload.setImageTintList(ColorStateList.valueOf(AppUiConfig.sTextColor));
			holder.ivLike.setImageTintList(ColorStateList.valueOf(AppUiConfig.sTextColor));
			holder.ivReblog.setImageTintList(ColorStateList.valueOf(AppUiConfig.sTextColor));
			holder.ivMoreInfo.setImageTintList(ColorStateList.valueOf(AppUiConfig.sTextColor));
		}
	}

	private void setCaption(BasePostDetailViewHolder holder, String caption) {
		if (!TextUtils.isEmpty(caption)) {
			holder.tvPostBody.setVisibility(View.GONE);
			holder.clCommentBodies.setVisibility(View.VISIBLE);
			List<CommentBody> commentBodyList = CommentBody.parseHtml(caption);
			if (commentBodyList.isEmpty()) {
				holder.tvPostBody.setVisibility(View.VISIBLE);
				holder.tvPostBody.setText(HtmlTool.fromHtml(caption, holder.tvPostBody));
			} else {
				holder.clCommentBodies.setCommentBodies(commentBodyList);
			}
		} else {
			holder.clCommentBodies.setVisibility(View.GONE);
			holder.tvPostBody.setVisibility(View.GONE);
		}
	}

	private void onBindPhotoPostViewHolder(PhotoPostDetailViewHolder holder, int position) {
		holder.mViewBinding.tvPostTitle.setVisibility(View.GONE);
		holder.mViewBinding.ivVideoPlay.setVisibility(View.GONE);
		holder.mViewBinding.llPhotos.setPhotos(((PhotoPost) mDataList.get(position)).getPhotos());
		setCaption(holder, ((PhotoPost) mDataList.get(position)).getCaption());
	}

	protected void onBindVideoPostViewHolder(VideoPostDetailViewHolder holder, int position) {
		final VideoPost videoPost = (VideoPost) mDataList.get(position);
		setCaption(holder, ((VideoPost) mDataList.get(position)).getCaption());
		FileDownloadUtil.TumblrVideoDownloadInfo tumblrVideoDownloadInfo = FileDownloadUtil.getVideoDownloadInfo(videoPost);
		if (tumblrVideoDownloadInfo != null) {
			holder.mViewBinding.videoPlayLayout.setData(tumblrVideoDownloadInfo.getVideoUrl(), tumblrVideoDownloadInfo.getVideoThumbnail());
			holder.mViewBinding.videoPlayLayout.setVideoSize(videoPost.getThumbnailWidth(), videoPost.getThumbnailHeight());
			holder.mViewBinding.videoPlayLayout.setOnSingleTapListener(new VideoPlayLayout.OnSingleTapListener() {
				@Override
				public boolean onSingleTap(VideoPlayLayout videoPlayLayout) {
					VideoViewPagerActivity.playVideo((Activity) mRecyclerView.getContext(), videoPost);
					return true;
				}
			});
			holder.mViewBinding.videoPlayLayout.getVideoControl().showFullScreenButton();
			holder.mViewBinding.videoPlayLayout.getVideoControl().setFullScreenClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					VideoViewPagerActivity.playVideo((Activity) mRecyclerView.getContext(), videoPost);
				}
			});
		}
	}

	private void onBindAudioPostViewHolder(AudioPostDetailViewHolder holder, int position) {
		AudioPost audioPost = (AudioPost) mDataList.get(position);
		holder.mAudioPostDetailBinding.tvTitle.setTextColor(AppUiConfig.sTextColor);
		holder.mAudioPostDetailBinding.tvTitle.setText(audioPost.getTrackName());
		holder.mAudioPostDetailBinding.tvArtist.setText(audioPost.getArtistName());
		holder.mAudioPostDetailBinding.tvArtist.setTextColor(AppUiConfig.sTextColor);
		if (TumlodrGlideUtil.isContextValid(holder.itemView)) {
			TumlodrGlide.with(holder.itemView)
					.load(audioPost.getAlbumArtUrl())
					.placeholder(AppUiConfig.sPicHolderResource)
					.skipMemoryCache(true)
					.transform(new MultiTransformation<>(new RoundedCorners(10),
							new FitCenter()))
					.into(holder.mAudioPostDetailBinding.ivArtist);
			TumlodrGlide.with(holder.itemView)
					.load(audioPost.getAlbumArtUrl())
					.skipMemoryCache(true)
					.placeholder(new ColorDrawable(ContextCompat.getColor(mRecyclerView.getContext(), R.color.colorTranBlack)))
					.transform(new MultiTransformation<>(new CenterCrop(),
							new BlurTransformation(mRecyclerView.getContext(), 25),
							new ColorFilterTransformation(!AppUiConfig.sIsLightTheme ?
									Color.parseColor("#c0000000") : Color.parseColor("#c0ffffff"))))
					.into(holder.mAudioPostDetailBinding.ivBlurBg);
		}
		setCaption(holder, ((AudioPost) mDataList.get(position)).getCaption());
	}

	private void onBindTextPostViewHolder(TextPostDetailViewHolder holder, int position) {
		holder.mViewBinding.tvPostTitle.setVisibility(View.VISIBLE);
		holder.mViewBinding.tvPostTitle.setTextColor(AppUiConfig.sTextColor);
		TextPost textPost = (TextPost) mDataList.get(position);
		holder.mViewBinding.tvPostTitle.setText(textPost.getTitle());
		holder.mViewBinding.ivVideoPlay.setVisibility(View.GONE);
		if (textPost.getBody() != null && !TextUtils.isEmpty(textPost.getBody())) {
			holder.mViewBinding.tvPostBody.setVisibility(View.VISIBLE);
			TextPostBody textPostBody = TextPostBodyUtils.textPostBody(textPost.getBody());
			if (textPostBody.getPhotos() != null && !textPostBody.getPhotos().isEmpty()) {
				holder.mViewBinding.llPhotos.setVisibility(View.VISIBLE);
				holder.mViewBinding.llPhotos.setPhotos(textPostBody.getPhotos());
			} else {
				holder.mViewBinding.llPhotos.setVisibility(View.GONE);
			}
			holder.mViewBinding.tvPostBody.setText(HtmlTool.fromHtml(textPostBody.getContent(), holder.mViewBinding.tvPostBody));
		} else {
			holder.mViewBinding.tvPostBody.setVisibility(View.GONE);
		}
	}

	public class PhotoPostDetailViewHolder extends BasePostDetailViewHolder {

		public ItemPostDetailBinding mViewBinding;

		PhotoPostDetailViewHolder(View itemView) {
			super(itemView);
			mViewBinding = ItemPostDetailBinding.bind(itemView);
			mViewBinding.llPhotos.setImageViewWidth(mImageViewWidth);
			mViewBinding.llPhotos.setEventListener(new PostPhotoLayout.OnPhotoLayoutEventListener() {
				@Override
				public void onImageViewClick(int index) {
					if (mEventListener != null) {
						mEventListener.onShowImageDetail(index, mViewBinding.llPhotos, getItem(getAdapterPosition()));
					}
				}

				@Override
				public void onImageViewLongClick(Point point, ImageView imageView) {
				}
			});
		}
	}

	public class VideoPostDetailViewHolder extends BasePostDetailViewHolder {

		public ItemPostDetailVideoBinding mViewBinding;

		VideoPostDetailViewHolder(View itemView) {
			super(itemView);
			mViewBinding = ItemPostDetailVideoBinding.bind(itemView);
		}
	}

	public class TextPostDetailViewHolder extends BasePostDetailViewHolder {

		private ItemPostDetailBinding mViewBinding;

		TextPostDetailViewHolder(View itemView) {
			super(itemView);
			mViewBinding = ItemPostDetailBinding.bind(itemView);
			mViewBinding.llPhotos.setImageViewWidth(mImageViewWidth);
			mViewBinding.llPhotos.setEventListener(new PostPhotoLayout.OnPhotoLayoutEventListener() {
				@Override
				public void onImageViewClick(int index) {
					if (mEventListener != null) {
						mEventListener.onShowImageDetail(index, mViewBinding.llPhotos, getItem(getAdapterPosition()));
					}
				}

				@Override
				public void onImageViewLongClick(Point point, ImageView imageView) {
				}
			});
		}
	}

	public class BasePostDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		CardView cvPost;
		ImageView ivAvatar;
		TextView tvBlogName;
		TextView tvPostTime;
		TextView tvSource;
		TextView tvNoteCount;
		TextView tvPostBody;
		CommentBodyLayout clCommentBodies;
		ImageView ivDownload;
		ImageView ivReblog;
		ImageView ivLike;
		ImageView ivMoreInfo;

		BasePostDetailViewHolder(View itemView) {
			super(itemView);
			cvPost = itemView.findViewById(R.id.cv_post);
			ivAvatar = itemView.findViewById(R.id.riv_post_avatar);
			tvBlogName = itemView.findViewById(R.id.tv_blog_name);
			tvPostTime = itemView.findViewById(R.id.tv_post_time);
			tvNoteCount = itemView.findViewById(R.id.tv_note_count);

			tvPostBody = itemView.findViewById(R.id.tv_post_body);
			clCommentBodies = itemView.findViewById(R.id.cl_comment_bodies);

			itemView.findViewById(R.id.ll_post_top).setOnClickListener(this);
			tvSource = itemView.findViewById(R.id.tv_source);
			tvSource.setOnClickListener(this);
			ivDownload = itemView.findViewById(R.id.iv_detail_dowanload);
			ivDownload.setOnClickListener(this);
			ivReblog = itemView.findViewById(R.id.iv_detail_reblog);
			ivReblog.setOnClickListener(this);
			ivLike = itemView.findViewById(R.id.iv_detail_like);
			ivLike.setOnClickListener(this);
			ivMoreInfo = itemView.findViewById(R.id.iv_detail_more);
			ivMoreInfo.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (getAdapterPosition() < 0 || getAdapterPosition() >= getItemCount()) {
				return;
			}
			switch (v.getId()) {
				case R.id.ll_post_top: {
					BlogPostListActivity.start(mRecyclerView.getContext(), mDataList.get(getAdapterPosition()).getBlogName(), false);
					break;
				}
				case R.id.tv_source: {
					BlogPostListActivity.start(mRecyclerView.getContext(), mDataList.get(getAdapterPosition()).getSourceTitle(), false);
					break;
				}
				case R.id.iv_detail_dowanload: {
					FileDownloadUtil.download(mDataList.get(getAdapterPosition()));
					break;
				}
				case R.id.iv_detail_reblog: {
					TumlodrService.reblog(UserInfo.sUserName,
							mDataList.get(getAdapterPosition()).getId(), mDataList.get(getAdapterPosition()).getReblogKey());
					break;
				}
				case R.id.iv_detail_like: {
					if (mDataList.get(getAdapterPosition()).isLiked() != null && mDataList.get(getAdapterPosition()).isLiked()) {
						if (mEventListener != null) {
							mEventListener.onDisLike(mDataList.get(getAdapterPosition()), ivLike, true);
						}
					} else {
						if (mEventListener != null) {
							mEventListener.onLikePost(mDataList.get(getAdapterPosition()), ivLike, true);
						}
						ViewUtils.likeAnimation(ivLike);
					}
					break;
				}
				case R.id.iv_detail_more: {
					if (mDataList.get(getAdapterPosition()).getNotes() == null) {
						return;
					}
					if (mNotesFragment == null) {
						mNotesFragment = new NotesFragment();
					}
					mNotesFragment.setNotes(mDataList.get(getAdapterPosition()).getNotes());
					if (!mNotesFragment.isAdded()) {
						mNotesFragment.show(((AppCompatActivity) mRecyclerView.getContext()).getSupportFragmentManager(), null);
					}
					break;
				}
			}
		}
	}

	public class AudioPostDetailViewHolder extends BasePostDetailViewHolder {

		private ItemPostAudioBinding mViewBinding;
		public LayoutAudioPostDetailBinding mAudioPostDetailBinding;

		AudioPostDetailViewHolder(View itemView) {
			super(itemView);
			mViewBinding = ItemPostAudioBinding.bind(itemView);
			mViewBinding.postContentContainer.getViewStub().setLayoutResource(R.layout.layout_audio_post_detail);
			mViewBinding.postContentContainer.getViewStub().inflate();
			mAudioPostDetailBinding = (LayoutAudioPostDetailBinding) mViewBinding.postContentContainer.getBinding();
		}
	}
}
