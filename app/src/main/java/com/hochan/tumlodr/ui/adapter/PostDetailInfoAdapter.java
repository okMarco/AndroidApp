package com.hochan.tumlodr.ui.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.jumblr.types.Note;
import com.hochan.tumlodr.jumblr.types.Post;
import com.hochan.tumlodr.jumblr.types.VideoPost;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.glide.TumlodrGlideUtil;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.tools.ScreenTools;
import com.hochan.tumlodr.tools.Tools;
import com.hochan.tumlodr.ui.activity.BlogPostListActivity;
import com.hochan.tumlodr.ui.component.TumlodrBottomAdsLayout;

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
				case PHOTO: {
					return CODE_TYPE_PHOTO;
				}
				case VIDEO: {
					return CODE_TYPE_VIDEO;
				}
				case TEXT: {
					return CODE_TYPE_TEXT;
				}
				default: {
					return CODE_TYPE_UNKNOWN;
				}
			}
		} else if (position >= 1 && position - 1 < mPost.getNotes().size()) {
			return CODE_TYPE_NOTE;
		}
		return CODE_TYPE_UNKNOWN;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == CODE_TYPE_NOTE) {
			return new NotesAdapter.NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
		}
		return super.onCreateViewHolder(parent, viewType);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (getItemViewType(position) == CODE_TYPE_NOTE) {
			int notePosition = position - 1;
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
						BlogPostListActivity.start(mRecyclerView.getContext(), note.getBlogName(), note.followed);
					}
				});
			}
		} else {
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
