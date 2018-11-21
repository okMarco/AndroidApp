package com.hochan.tumlodr.ui.adapter;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.databinding.ItemPostThumbnailBinding;
import com.hochan.tumlodr.model.TumlodrService;
import com.hochan.tumlodr.model.data.TextPostBody;
import com.hochan.tumlodr.model.sharedpreferences.UserInfo;
import com.hochan.tumlodr.tools.HtmlTool;
import com.hochan.tumlodr.tools.ScreenTools;
import com.hochan.tumlodr.tools.Tools;
import com.hochan.tumlodr.module.webbrowser.WebViewActivity;
import com.hochan.tumlodr.ui.component.IPhotoLayout;
import com.hochan.tumlodr.ui.component.PostPhotoLayout;
import com.hochan.tumlodr.ui.component.TumlodrPopupWindow;
import com.hochan.tumlodr.util.FileDownloadUtil;
import com.hochan.tumlodr.util.TextPostBodyUtils;
import com.hochan.tumlodr.util.ViewUtils;
import com.tumblr.jumblr.types.LinkPost;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.TextPost;
import com.tumblr.jumblr.types.VideoPost;

import java.io.File;
import java.util.List;

/**
 * .
 * Created by hochan on 2017/12/26.
 */

public class PostThumbnailAdapter extends PostAdapter implements ListPreloader.PreloadModelProvider<PhotoPost> {

	private TumlodrPopupWindow mPopupWindow;
	private OnPostCommandEventListener mEventListener;

	public PostThumbnailAdapter(RecyclerView recyclerView, List<Post> postList,
	                            OnPostCommandEventListener eventListener) {
		super(recyclerView, postList);
		mEventListener = eventListener;
	}

	public void setColumn(int column) {
		mColumn = column;
		mImageViewWidth = (ScreenTools.getScreenWidth(TumlodrApp.mContext) - (mColumn + 1) * 10) / mColumn;
	}

	@NonNull
	@Override
	public List<PhotoPost> getPreloadItems(int position) {
		return null;
	}

	@Nullable
	@Override
	public RequestBuilder<?> getPreloadRequestBuilder(PhotoPost item) {
		return null;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new PostThumbnailViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_thumbnail, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		PostThumbnailViewHolder postThumbnailViewHolder = (PostThumbnailViewHolder) holder;
		postThumbnailViewHolder.mItemViewBinding.ivVideoPlay.setVisibility(View.INVISIBLE);
		postThumbnailViewHolder.mItemViewBinding.tvTextTitleSimple.setVisibility(View.GONE);
		postThumbnailViewHolder.mItemViewBinding.plPostPhotos.setVisibility(View.VISIBLE);
		postThumbnailViewHolder.mItemViewBinding.plPostPhotos.setImageViewWidth(mImageViewWidth);
		if (TYPE_PHOTO.equals(mDataList.get(position).getType())) {
			postThumbnailViewHolder.mItemViewBinding.plPostPhotos
					.setThumbnailPhotos(((PhotoPost) mDataList.get(position)).getPhotos());
		} else if (TYPE_VIDEO.equals(mDataList.get(position).getType())) {
			postThumbnailViewHolder.mItemViewBinding.ivVideoPlay.setVisibility(View.VISIBLE);
			VideoPost videoPost = (VideoPost) mDataList.get(position);
			postThumbnailViewHolder.mItemViewBinding.plPostPhotos.setVideoThumbnail(videoPost.getThumbnailUrl(),
					videoPost.getThumbnailWidth(), videoPost.getThumbnailHeight());
		} else if (TYPE_TEXT.equals(mDataList.get(position).getType())) {
			TextPost textPost = (TextPost) mDataList.get(position);
			postThumbnailViewHolder.mItemViewBinding.ivVideoPlay.setVisibility(View.INVISIBLE);
			postThumbnailViewHolder.mItemViewBinding.tvTextTitleSimple.setVisibility(View.VISIBLE);
			TextPostBody textPostBody = TextPostBodyUtils.textPostBody(textPost.getBody());
			if (TextUtils.isEmpty(textPost.getTitle())) {
				postThumbnailViewHolder.mItemViewBinding.tvTextTitleSimple.setText(HtmlTool.fromHtml(textPost.getBody(),
						postThumbnailViewHolder.mItemViewBinding.tvTextTitleSimple));
			} else {
				postThumbnailViewHolder.mItemViewBinding.tvTextTitleSimple.setText(HtmlTool.fromHtml(textPostBody.getContent(),
						postThumbnailViewHolder.mItemViewBinding.tvTextTitleSimple));
			}
			if (textPostBody != null && textPostBody.getPhotos() != null && textPostBody.getPhotos().size() > 0) {
				postThumbnailViewHolder.mItemViewBinding.plPostPhotos.setVisibility(View.VISIBLE);
				postThumbnailViewHolder.mItemViewBinding.plPostPhotos.setThumbnailPhotos(textPostBody.getPhotos());
			} else {
				postThumbnailViewHolder.mItemViewBinding.plPostPhotos.setVisibility(View.GONE);
			}
		} else if (TYPE_LINK.equals(mDataList.get(position).getType())) {
			LinkPost linkPost = (LinkPost) mDataList.get(position);
			postThumbnailViewHolder.mItemViewBinding.tvTextTitleSimple.setVisibility(View.VISIBLE);
			postThumbnailViewHolder.mItemViewBinding.plPostPhotos.setVisibility(View.GONE);
			String url = linkPost.getLinkUrl();
			String text = linkPost.getTitle() + ":<a href='" + url + "'>" + url + "</a>";
			postThumbnailViewHolder.mItemViewBinding.tvTextTitleSimple.setText(HtmlTool.fromHtml(text,
					postThumbnailViewHolder.mItemViewBinding.tvTextTitleSimple));
		}

		if (null != mDataList.get(position).isLiked() && mDataList.get(position).isLiked()) {
			((PostThumbnailViewHolder) holder).mItemViewBinding.ivLike.setVisibility(View.VISIBLE);
		} else {
			((PostThumbnailViewHolder) holder).mItemViewBinding.ivLike.setVisibility(View.GONE);
		}

		if (mDataList.get(position).getType().equals(TYPE_PHOTO)) {
			String originalUrl = ((PhotoPost) mDataList.get(position)).getPhotos().get(0).getOriginalSize().getUrl();
			String path = Tools.getStoragePathByFileName(originalUrl.substring(originalUrl.lastIndexOf("/"), originalUrl.length()));
			if (new File(path).exists()) {
				postThumbnailViewHolder.mItemViewBinding.ivDownload.setVisibility(View.VISIBLE);
			} else {
				postThumbnailViewHolder.mItemViewBinding.ivDownload.setVisibility(View.GONE);
			}
		} else if (mDataList.get(position).getType().equals(TYPE_VIDEO)) {
			FileDownloadUtil.TumblrVideoDownloadInfo tumblrVideoDownloadInfo = FileDownloadUtil.getVideoDownloadInfo((VideoPost) mDataList.get(position));
			try {
				if (tumblrVideoDownloadInfo != null && new File(tumblrVideoDownloadInfo.getVideoPath()).exists()) {
					postThumbnailViewHolder.mItemViewBinding.ivDownload.setVisibility(View.VISIBLE);
				} else {
					postThumbnailViewHolder.mItemViewBinding.ivDownload.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void dismissPopupWindow() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}

	public class PostThumbnailViewHolder extends RecyclerView.ViewHolder implements PostPhotoLayout.OnPhotoLayoutEventListener, View.OnClickListener {

		public ItemPostThumbnailBinding mItemViewBinding;

		PostThumbnailViewHolder(View itemView) {
			super(itemView);
			mItemViewBinding = ItemPostThumbnailBinding.bind(itemView);
			mItemViewBinding.tvTextTitleSimple.setOnClickListener(this);
			mItemViewBinding.plPostPhotos.setEventListener(this);
			mItemViewBinding.plPostPhotos.setImageViewWidth(mImageViewWidth);
			mItemViewBinding.tvTextTitleSimple.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					int[] location = new int[2];
					v.getLocationOnScreen(location);
					Point point = new Point(location[0] + v.getMeasuredWidth() / 2, location[1]);
					onImageViewLongClick(point, null);
					return true;
				}
			});
		}

		@Override
		public void onImageViewClick(int index) {
			if (isAdapterPositionValid(getAdapterPosition())) {
				if (mEventListener != null) {
					if (mDataList.get(getAdapterPosition()).getType().equals(TYPE_PHOTO)) {
						mEventListener.onShowImageDetail(index, mItemViewBinding.plPostPhotos, getItem(getAdapterPosition()));
					} else if (mDataList.get(getAdapterPosition()).getType().equals(TYPE_VIDEO)) {
						mEventListener.onPlayVideo(getAdapterPosition());
					}
				}
			}
		}

		@Override
		public void onImageViewLongClick(Point point, ImageView imageView) {
			if (isAdapterPositionValid(getAdapterPosition())) {
				int position = getAdapterPosition();
				View itemView = null;
				try {
					itemView = mRecyclerView.getLayoutManager().findViewByPosition(position);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (itemView == null) {
					return;
				}
				if (mPopupWindow == null) {
					mPopupWindow = new TumlodrPopupWindow(mRecyclerView);
				}
				mPopupWindow.setOnClickListener(this);
				if (mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				}
				if (mDataList.get(getAdapterPosition()).getType().equals(TYPE_PHOTO)) {
					mPopupWindow.showPhotoPopup(mRecyclerView.getContext(), point,
							mDataList.get(getAdapterPosition()).isLiked() != null && mDataList.get(getAdapterPosition()).isLiked(),
							mDataList.get(getAdapterPosition()).getBlogName());
				} else if (mDataList.get(getAdapterPosition()).getType().equals(TYPE_VIDEO)) {
					mPopupWindow.showVideoPopup(mRecyclerView.getContext(), point,
							mDataList.get(getAdapterPosition()).isLiked() != null && mDataList.get(getAdapterPosition()).isLiked(),
							mDataList.get(getAdapterPosition()).getBlogName());
				} else {
					mPopupWindow.showOtherPopup(mRecyclerView.getContext(), point,
							mDataList.get(getAdapterPosition()).isLiked() != null && mDataList.get(getAdapterPosition()).isLiked(),
							mDataList.get(getAdapterPosition()).getBlogName());
				}
			}
		}

		@Override
		public void onClick(View v) {
			if (isAdapterPositionValid(getAdapterPosition())) {
				switch (v.getId()) {
					case R.id.ll_popup_original_blog: {
						if (mEventListener != null) {
							mEventListener.onShowPostDetail(getAdapterPosition(), mItemViewBinding.plPostPhotos);
						}
						break;
					}
					case R.id.ll_popup_like: {
						if (mDataList.get(getAdapterPosition()).isLiked() != null && mDataList.get(getAdapterPosition()).isLiked()) {
							mItemViewBinding.ivLike.setVisibility(View.INVISIBLE);
							if (mEventListener != null) {
								mEventListener.onDisLike(mDataList.get(getAdapterPosition()), mItemViewBinding.ivLike, false);
							}
						} else {
							mItemViewBinding.ivLike.setVisibility(View.VISIBLE);
							if (mEventListener != null) {
								mEventListener.onLikePost(mDataList.get(getAdapterPosition()), mItemViewBinding.ivLike, false);
							}
							ViewUtils.likeAnimation(v);
						}
						break;
					}
					case R.id.ll_popup_download: {
						FileDownloadUtil.download(mDataList.get(getAdapterPosition()));
						break;
					}
					case R.id.ll_popup_reblog: {
						TumlodrService.reblog(UserInfo.sUserName,
								mDataList.get(getAdapterPosition()).getId(), mDataList.get(getAdapterPosition()).getReblogKey());
						break;
					}
					case R.id.tv_text_title_simple: {
						if (mDataList.get(getAdapterPosition()) instanceof LinkPost) {
							LinkPost linkPost = (LinkPost) mDataList.get(getAdapterPosition());
							WebViewActivity.showUrl(mItemViewBinding.getRoot().getContext(), linkPost.getLinkUrl());
						}
						break;
					}
				}
			}
			if (mPopupWindow != null && mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
			}
		}
	}

	private boolean isAdapterPositionValid(int adapterPosition) {
		return mDataList != null && adapterPosition > RecyclerView.NO_POSITION && adapterPosition < mDataList.size();
	}

	public interface OnPostCommandEventListener {
		void onShowPostDetail(int adapterPosition, PostPhotoLayout postPhotoLayout);

		void onShowImageDetail(int index, IPhotoLayout postPhotoLayout, Post post);

		void onPlayVideo(int index);

		void onLikePost(Post post, ImageView likeView, boolean isDetail);

		void onDisLike(Post post, ImageView likeView, boolean isDetail);
	}
}
