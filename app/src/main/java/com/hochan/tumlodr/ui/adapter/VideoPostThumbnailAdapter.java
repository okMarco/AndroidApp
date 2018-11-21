package com.hochan.tumlodr.ui.adapter;

import android.support.v4.util.CircularArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.VideoPost;

import java.util.List;

/**
 * .
 * Created by hochan on 2018/1/31.
 */

public class VideoPostThumbnailAdapter extends PostThumbnailAdapter {

	private OnItemClickListener mOnItemClickListener;

	public VideoPostThumbnailAdapter(RecyclerView recyclerView, List<Post> postList,
	                                 OnPostCommandEventListener eventListener) {
		super(recyclerView, postList, eventListener);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ItemVideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false));
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
		if (mDataList.get(position) instanceof VideoPost) {
			final VideoPost videoPost = (VideoPost) mDataList.get(position);
			int imageViewHeight = (int) (mImageViewWidth * videoPost.getThumbnailHeight() * 1.0 / videoPost.getThumbnailWidth());
			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mImageViewWidth, imageViewHeight);
			((ItemVideoViewHolder) holder).ivVideoCover.setLayoutParams(layoutParams);
			TumlodrGlide.with(mRecyclerView.getContext())
					.load(videoPost.getThumbnailUrl())
					.skipMemoryCache(true)
					.into(((ItemVideoViewHolder) holder).ivVideoCover);
			((ItemVideoViewHolder) holder).ivVideoCover.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onItemClick(holder.getAdapterPosition());
					}
				}
			});
		}
	}

	public class ItemVideoViewHolder extends RecyclerView.ViewHolder {

		ImageView ivVideoCover;

		ItemVideoViewHolder(View itemView) {
			super(itemView);
			ivVideoCover = itemView.findViewById(R.id.iv_video_cover);
		}
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	public interface OnItemClickListener {
		void onItemClick(int position);
	}
}
