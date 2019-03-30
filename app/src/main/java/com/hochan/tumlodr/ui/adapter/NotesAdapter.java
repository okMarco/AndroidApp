package com.hochan.tumlodr.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hochan.tumlodr.jumblr.types.Note;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.ui.activity.BlogPostListActivity;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.ItemNoteBinding;
import com.hochan.tumlodr.tools.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * .
 * Created by hochan on 2017/11/5.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

	private List<Note> mNoteList = new ArrayList<>();

	private Activity mActivity;

	public void setNotes(List<Note> notes) {
		mNoteList = notes;
	}

	public void setActivity(Activity activity) {
		mActivity = activity;
	}

	@Override
	public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
	}

	@Override
	public void onBindViewHolder(final NoteViewHolder holder, int position) {
		final Note note = mNoteList.get(position);

		TumlodrGlide.with(mActivity)
				.load(Tools.getAvatarUrlByBlogName(note.getBlogName(), 64))
				.placeholder(AppUiConfig.sPicHolderResource)
				.transform(new RoundedCorners(10))
				.skipMemoryCache(true)
				.into(holder.mViewBinding.rivNoteAvatar);
		holder.mViewBinding.tvNoteBlog.setText(note.getBlogName());
		holder.mViewBinding.tvNote.setText(note.getType().toUpperCase(Locale.US));
		holder.mViewBinding.tvNoteTime.setText(Tools.getRelativeTime(note.getTimestamp()));

		holder.mViewBinding.llNoteRootView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mActivity == null) {
					return;
				}
				BlogPostListActivity.start(mActivity, note.getBlogName(), note.followed);
			}
		});
	}


	@Override
	public int getItemCount() {
		return mNoteList.size();
	}

	public static class NoteViewHolder extends RecyclerView.ViewHolder {

		ItemNoteBinding mViewBinding;

		NoteViewHolder(View itemView) {
			super(itemView);

			mViewBinding = ItemNoteBinding.bind(itemView);

			mViewBinding.tvNoteBlog.setTextColor(AppUiConfig.sTextColor);
			mViewBinding.tvNote.setTextColor(AppUiConfig.sTextColor);
			mViewBinding.tvNoteTime.setTextColor(AppUiConfig.sTipTextColor);
		}
	}
}
