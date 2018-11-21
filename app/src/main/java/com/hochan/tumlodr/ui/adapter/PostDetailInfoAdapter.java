package com.hochan.tumlodr.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.glide.TumlodrGlideUtil;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.tools.ScreenTools;
import com.hochan.tumlodr.tools.Tools;
import com.hochan.tumlodr.ui.activity.BlogPostListActivity;
import com.hochan.tumlodr.ui.component.TumlodrBottomAdsLayout;
import com.tumblr.jumblr.types.Note;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.VideoPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * .
 * Created by hochan on 2018/6/18.
 */

public class PostDetailInfoAdapter extends PostDetailAdapter {

	protected static final int CODE_TYPE_NOTE = 4;
	protected static final int CODE_TYPE_AD = 5;

	private Post mPost;

	public PostDetailInfoAdapter(RecyclerView recyclerView, List<Post> postList,
	                             PostThumbnailAdapter.OnPostCommandEventListener eventListener) {
		super(recyclerView, postList, eventListener);
		mPost = postList.get(0);
		mDataList = postList;
	}

	@Override
	public int getItemCount() {
		if (mPost == null) {
			return 0;
		}
		return 1 + (mPost.getNotes() == null ? 0 : mPost.getNotes().size());
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			switch (mPost.getType()) {
				case TYPE_PHOTO: {
					return CODE_TYPE_PHOTO;
				}
				case TYPE_VIDEO: {
					return CODE_TYPE_VIDEO;
				}
				case TYPE_TEXT: {
					return CODE_TYPE_TEXT;
				}
				default: {
					return CODE_TYPE_UNKNOWN;
				}
			}
		} else if (position >= 2 && position - 1 < mPost.getNotes().size()) {
			return CODE_TYPE_NOTE;
		} else if (position == 1) {
			return CODE_TYPE_AD;
		}
		return CODE_TYPE_UNKNOWN;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == CODE_TYPE_NOTE) {
			return new NotesAdapter.NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
		} else if (viewType == CODE_TYPE_AD) {
			TumlodrBottomAdsLayout adsLayout = new TumlodrBottomAdsLayout(parent.getContext());
			adsLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			return new RecyclerView.ViewHolder(adsLayout) {
				@Override
				public String toString() {
					return super.toString();
				}
			};
		}
		return super.onCreateViewHolder(parent, viewType);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (getItemViewType(position) == CODE_TYPE_NOTE) {
			int notePosition = position - 2;
			if (notePosition >= 0 && notePosition < mPost.getNotes().size()) {
				final Note note = mPost.getNotes().get(notePosition);
				NotesAdapter.NoteViewHolder noteViewHolder = (NotesAdapter.NoteViewHolder) holder;
				if (TumlodrGlideUtil.isContextValid(mRecyclerView)) {
					TumlodrGlide.with(mRecyclerView.getContext())
							.load(Tools.getAvatarUrlByBlogName(note.getBlogName(), 64))
							.placeholder(AppUiConfig.sPicHolderResource)
							.transform(new RoundedCorners(10))
							.skipMemoryCache(true)
							.into(noteViewHolder.mViewBinding.rivNoteAvatar);
				}
				noteViewHolder.mViewBinding.tvNoteBlog.setText(note.getBlogName());
				noteViewHolder.mViewBinding.tvNote.setText(note.getType().toUpperCase(Locale.US));
				noteViewHolder.mViewBinding.tvNoteTime.setText(Tools.getRelativeTime(note.getTimestamp()));

				noteViewHolder.mViewBinding.llNoteRootView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						BlogPostListActivity.start(mRecyclerView.getContext(), note.getBlogName(), false);
					}
				});
			}
			return;
		} else if (getItemViewType(position) != CODE_TYPE_AD) {
			super.onBindViewHolder(holder, position);
		}
	}

	@Override
	protected void onBindBaseViewHolder(BasePostDetailViewHolder holder, int position) {
		super.onBindBaseViewHolder(holder, position);
		holder.ivMoreInfo.setVisibility(View.GONE);
	}

	@Override
	protected void onBindVideoPostViewHolder(VideoPostDetailViewHolder holder, int position) {
		super.onBindVideoPostViewHolder(holder, position);
		ViewGroup.LayoutParams layoutParams = holder.mViewBinding.videoPlayLayout.getLayoutParams();
		layoutParams.height = (int) (ScreenTools.getScreenWidth(TumlodrApp.getContext()) * 1.0 /
				((VideoPost) mPost).getThumbnailWidth() * ((VideoPost) mPost).getThumbnailHeight());
	}
}
