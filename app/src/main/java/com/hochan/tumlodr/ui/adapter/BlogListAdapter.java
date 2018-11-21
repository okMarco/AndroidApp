package com.hochan.tumlodr.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.ui.activity.BlogPostListActivity;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.tools.Tools;
import com.tumblr.jumblr.types.Blog;

import java.util.List;

/**
 * .
 * Created by hochan on 2016/6/1.
 */
public class BlogListAdapter extends RecyclerView.Adapter {

	private List<Blog> mTumBlogList;

	public BlogListAdapter(List<Blog> blogList) {
		mTumBlogList = blogList;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context context = parent.getContext();
		View view = LayoutInflater.from(context).inflate(R.layout.item_blog, parent, false);
		return new BlogViewHolder(view);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		final Blog tumBlog = mTumBlogList.get(position);
		final BlogViewHolder viewHolder = (BlogViewHolder) holder;
		viewHolder.tvName.setText(tumBlog.getName());
		viewHolder.tvName.setTextColor(AppUiConfig.sTextColor);

		Tools.loadAvatar(viewHolder.ivAvatar, tumBlog.getName());
	}

	@Override
	public int getItemCount() {
		return mTumBlogList == null ? 0 : mTumBlogList.size();
	}

	private class BlogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
			if (getAdapterPosition() < 0 || getAdapterPosition() >= mTumBlogList.size()) {
				return;
			}
			BlogPostListActivity.start(v.getContext(), mTumBlogList.get(getAdapterPosition()).getName(), true);
		}
	}
}
