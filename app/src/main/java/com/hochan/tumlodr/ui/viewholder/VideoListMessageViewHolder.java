package com.hochan.tumlodr.ui.viewholder;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hochan.tumlodr.ui.component.WrapLinearLayoutManager;

/**
 * .
 * Created by hochan on 2017/12/16.
 */

public class VideoListMessageViewHolder extends RecyclerView.ViewHolder{

	public VideoListMessageViewHolder(final View itemView) {
		super(itemView);

		RecyclerView recyclerView = new RecyclerView(itemView.getContext());

		RecyclerView.SmoothScroller smoothScroller = new RecyclerView.SmoothScroller() {

			@Nullable
			@Override
			public RecyclerView.LayoutManager getLayoutManager() {
				return new WrapLinearLayoutManager(itemView.getContext()){

					@Override
					public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
						super.smoothScrollToPosition(recyclerView, state, position);
					}

					@Override
					public void attachView(View child, int index, RecyclerView.LayoutParams lp) {
						super.attachView(child, index, lp);
					}

					@Override
					public void measureChild(View child, int widthUsed, int heightUsed) {
						super.measureChild(child, widthUsed, heightUsed);
					}
				};
			}

			@Override
			protected void onStart() {

			}

			@Override
			protected void onStop() {

			}

			@Override
			protected void onSeekTargetStep(int dx, int dy, RecyclerView.State state, Action action) {

			}

			@Override
			protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {

			}
		};
	}
}
