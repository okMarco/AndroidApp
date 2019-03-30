package com.hochan.tumlodr.ui.adapter;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hochan.tumlodr.model.data.blog.FollowingBlog;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.ui.activity.BlogPostListActivity;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.tools.Tools;

/**
 * .
 * Created by hochan on 2016/6/1.
 */
public class BlogListAdapter extends PagedListAdapter<FollowingBlog, BlogListAdapter.BlogViewHolder> {

	public BlogListAdapter() {
		super(new DiffCallback<FollowingBlog>() {
			@Override
			public boolean areItemsTheSame(@NonNull FollowingBlog oldItem, @NonNull FollowingBlog newItem) {
				return TextUtils.equals(oldItem.getName(), newItem.getName());
			}

			@Override
			public boolean areContentsTheSame(@NonNull FollowingBlog oldItem, @NonNull FollowingBlog newItem) {
				return TextUtils.equals(oldItem.getName(), newItem.getName());
			}
		});
	}

	@Override
	public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context context = parent.getContext();
		View view = LayoutInflater.from(context).inflate(R.layout.item_blog, parent, false);
		return new BlogViewHolder(view);
	}

	@Override
	public void onBindViewHolder(BlogViewHolder holder, int position) {
		FollowingBlog blog = getItem(position);
		if (blog == null) {
			return;
		}
		holder.tvName.setText(blog.getName());
		holder.tvName.setTextColor(AppUiConfig.sTextColor);
		Tools.loadAvatar(holder.ivAvatar, blog.getName());
	}

	public class BlogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		ImageView ivAvatar;
		TextView tvName, tvTitle;

		BlogViewHolder(View itemView) {
			super(itemView);
			ivAvatar = itemView.findViewById(R.id.iv_avatar);
			tvName = itemView.findViewById(R.id.tv_name);
			tvTitle = itemView.findViewById(R.id.tv_title);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			FollowingBlog followingBlog = getItem(getAdapterPosition());
			if (followingBlog != null) {
				BlogPostListActivity.start(v.getContext(), followingBlog.getName(), true);
			}
		}
	}
}
