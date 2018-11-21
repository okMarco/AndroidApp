package com.hochan.tumlodr.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.ActivityPostDetailBinding;
import com.hochan.tumlodr.prensenter.PostListPresenter;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseViewBindingActivity;
import com.hochan.tumlodr.ui.adapter.PostDetailInfoAdapter;
import com.hochan.tumlodr.ui.adapter.PostThumbnailAdapter;
import com.hochan.tumlodr.ui.component.IPhotoLayout;
import com.hochan.tumlodr.ui.component.PostPhotoLayout;
import com.hochan.tumlodr.ui.component.WrapLinearLayoutManager;
import com.hochan.tumlodr.util.ActivityLifecycleProvider;
import com.hochan.tumlodr.util.Events;
import com.hochan.tumlodr.util.RxBus;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.tumblr.jumblr.types.Post;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

public class PostDetailActivity extends BaseViewBindingActivity<ActivityPostDetailBinding>
		implements PostThumbnailAdapter.OnPostCommandEventListener {

	public static Post sDetailPost;

	public static void showPostDetail(Context context, Post post) {
		sDetailPost = post;
		Intent intent = new Intent(context, PostDetailActivity.class);
		context.startActivity(intent);
	}

	private int mShareEnterIndex;
	private WeakReference<IPhotoLayout> mIPhotoLayoutWeakReference;
	private int mShareExitIndex;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RxBus.with(new ActivityLifecycleProvider(this))
				.setEndEvent(ActivityEvent.DESTROY)
				.onNext(new Consumer<Object>() {
					@Override
					public void accept(Object o) throws Exception {
						if (o instanceof Events) {
							switch (((Events) o).mCode) {
								case Events.EVENT_IMAGE_SHAREELEMENT_CONTAINER: {
									mIPhotoLayoutWeakReference = new WeakReference<>((IPhotoLayout) ((Events) o).mContent);
									break;
								}
								case Events.EVENT_SHAREELEMENT_ENTER_INDEX_CHANGE: {
									mShareEnterIndex = (int) ((Events) o).mContent;
									setExitSharedElementCallback(new SharedElementCallback() {
										@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
										@Override
										public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
											if (mShareExitIndex != mShareEnterIndex &&
													mIPhotoLayoutWeakReference != null
													&& mIPhotoLayoutWeakReference.get() != null) {
												sharedElements.clear();
												sharedElements.put(Router.SHAREELEMENT_NAME,
														mIPhotoLayoutWeakReference.get().getImageViewInPosition(mShareExitIndex));
												mIPhotoLayoutWeakReference.get().getImageViewInPosition(mShareEnterIndex).setTransitionName(null);
											}
											setExitSharedElementCallback((android.app.SharedElementCallback) null);
										}

										@Override
										public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
											if (mIPhotoLayoutWeakReference != null && mIPhotoLayoutWeakReference.get() != null) {
												mIPhotoLayoutWeakReference.get().reloadImage();
												mIPhotoLayoutWeakReference.clear();
											}
										}
									});
									break;
								}
								case Events.EVENT_SHAREELEMENT_EXIT_INDEX_CHANGE: {
									mShareExitIndex = (int) ((Events) o).mContent;
									break;
								}
							}
						}
					}
				}).create();
	}

	@Override
	public void initWidget() {
		mViewBinding.rvPostInfo.setLayoutManager(new WrapLinearLayoutManager(this));
		PostDetailInfoAdapter adapter = new PostDetailInfoAdapter(mViewBinding.rvPostInfo,
				java.util.Collections.singletonList(sDetailPost), this);
		mViewBinding.rvPostInfo.setAdapter(adapter);
	}

	@Override
	protected String getTitleString() {
		return getString(R.string.post_detail);
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_post_detail;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		sDetailPost = null;
	}

	@Override
	public void onShowPostDetail(int adapterPosition, PostPhotoLayout postPhotoLayout) {

	}

	@Override
	public void onShowImageDetail(int index, IPhotoLayout postPhotoLayout, Post post) {
		Router.showImage(this, postPhotoLayout, index, post);
	}

	@Override
	public void onPlayVideo(int index) {

	}

	@Override
	public void onLikePost(Post post, ImageView likeView, boolean isDetail) {
		PostListPresenter.likePost(post, likeView, true);
	}

	@Override
	public void onDisLike(Post post, ImageView likeView, boolean isDetail) {
		PostListPresenter.unlikePost(post, likeView, true);
	}
}
