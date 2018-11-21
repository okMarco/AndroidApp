package com.hochan.tumlodr.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.FragmentOnlyVideoBinding;
import com.hochan.tumlodr.prensenter.BlogVideoPostListPresenter;
import com.hochan.tumlodr.prensenter.PostListPresenter;
import com.hochan.tumlodr.prensenter.VideoPostListPresenter;
import com.hochan.tumlodr.ui.activity.VideoViewPagerActivity;
import com.hochan.tumlodr.ui.adapter.PostAdapter;
import com.hochan.tumlodr.ui.adapter.VideoPostThumbnailAdapter;
import com.hochan.tumlodr.ui.component.TumlodrBottomAdsLayout;
import com.hochan.tumlodr.ui.component.WrapStaggeredGridLayoutManager;
import com.hochan.tumlodr.util.ViewUtils;
import com.tumblr.jumblr.types.Post;

import java.util.ArrayList;

import static com.hochan.tumlodr.ui.adapter.VideoPostThumbnailAdapter.OnItemClickListener;

/**
 * .
 * Created by hochan on 2018/1/25.
 */

public class VideoPostThumbnailFragment extends PostThumbnailFragment<PostListPresenter> {

	private FragmentOnlyVideoBinding mVideoBinding;
	private TumlodrBottomAdsLayout mAdsLayout;

	@Override
	public void bindView(View rootView) {
		mVideoBinding = FragmentOnlyVideoBinding.bind(rootView);
	}

	@Override
	public int getLayoutRecourseId() {
		return R.layout.fragment_only_video;
	}

	@Override
	protected void iniWidget(View view) {
		super.iniWidget(view);
		View flRootContainer = view.findViewById(R.id.fl_root_container);
		flRootContainer.setBackgroundColor(Color.BLACK);

		Toolbar toolbar = view.findViewById(R.id.toolbar);
		toolbar.setTitleTextColor(Color.WHITE);
		toolbar.setTitleTextAppearance(getActivity(), R.style.TitleTextStyle);
		toolbar.setTitle(getString(R.string.video_list_more_videos));
		toolbar.setNavigationIcon(ViewUtils.getArrowDrawable(Color.WHITE));
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), VideoViewPagerActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && isResumed()) {
			addAdsView();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getUserVisibleHint()) {
			addAdsView();
		}
	}

	private void addAdsView() {
		if (getActivity() != null && mAdsLayout == null) {
			mAdsLayout = new TumlodrBottomAdsLayout(getActivity());
			mAdsLayout.setBackgroundColor(Color.BLACK);
			mVideoBinding.flRootContainer.addView(mAdsLayout,
					new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		}
	}

	@Override
	public void loadDataOnResume() {
		mSmartRefreshLayout.autoLoadMore();
	}

	@Override
	public PostListPresenter initPresenter() {
		if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString(VideoViewPagerActivity.EXTRA_BLOG_NAME))) {
			return new BlogVideoPostListPresenter(this, getArguments().getString(VideoViewPagerActivity.EXTRA_BLOG_NAME));
		}
		return new VideoPostListPresenter(this);
	}

	@Override
	public RecyclerView.LayoutManager initRecyclerLayoutManager() {
		return new WrapStaggeredGridLayoutManager(2, WrapStaggeredGridLayoutManager.VERTICAL);
	}

	@Override
	public PostAdapter initAdapter() {
		mAdapter = new VideoPostThumbnailAdapter(mRecyclerView, new ArrayList<Post>(), null);
		((VideoPostThumbnailAdapter) mAdapter).setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(int position) {
				if (getActivity() instanceof VideoViewPagerActivity) {
					((VideoViewPagerActivity) getActivity()).scrollToPosition(position);
				}
			}
		});
		return mAdapter;
	}
}
