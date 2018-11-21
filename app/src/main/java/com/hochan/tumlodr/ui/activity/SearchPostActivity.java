package com.hochan.tumlodr.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.ActivitySearchPostBinding;
import com.hochan.tumlodr.databinding.ItemSearchBlogBinding;
import com.hochan.tumlodr.model.BaseObserver;
import com.hochan.tumlodr.model.TumlodrService;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.search.SearchBlogInfo;
import com.hochan.tumlodr.module.search.SearchResult;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.tools.HtmlTool;
import com.hochan.tumlodr.tools.Tools;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseViewBindingActivity;
import com.hochan.tumlodr.ui.fragment.SearchPostFragment;
import com.hochan.tumlodr.ui.viewholder.BaseViewBindingViewHolder;

import java.util.List;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class SearchPostActivity extends BaseViewBindingActivity<ActivitySearchPostBinding> {

	private String mSearchWord;
	private SearchPostFragment mPostListFragment;
	private int mCurrentPage = 2;
	private int mCurrentPostSize = 0;
	private String mFormKey;

	@Override
	public void initWidget() {
		super.initWidget();
		mViewBinding.edSearchTag.setTextColor(AppUiConfig.sTextColor);
		mViewBinding.btnSearch.setImageResource(AppUiConfig.sIsLightTheme ? R.drawable.ic_search_black : R.drawable.ic_search_white);
		mViewBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startSearch();
			}
		});

		mViewBinding.edSearchTag.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
			                              KeyEvent event) {
				return actionId == EditorInfo.IME_ACTION_SEARCH && !startSearch();
			}
		});

		setContentFragment(R.id.fragment_container);
	}

	@SuppressWarnings("ConstantConditions")
	private boolean startSearch() {
		try {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception ignored) {

		}
		if (TextUtils.isEmpty(mViewBinding.edSearchTag.getText().toString())) {
			return true;
		}
		searchFromHtml(mViewBinding.edSearchTag.getText().toString());
		if (mPostListFragment != null) {
			mPostListFragment.search(mViewBinding.edSearchTag.getText().toString());
		}
		return false;
	}

	public void searchFromHtml(String tag) {
		mSearchWord = tag;
		mCurrentPage = 2;
		mCurrentPostSize = 0;
		mViewBinding.rvBlogList.setAdapter(null);
		TumlodrService.searchFirstPage(tag)
				.subscribe(new BaseObserver<SearchResult>() {
					@Override
					public void onNext(SearchResult searchResult) {
						if (searchResult != null) {
							mFormKey = searchResult.getFormKey();
						}
						showPostBlogList(searchResult);
						if (searchResult != null && searchResult.getSearchPostList() != null
								&& searchResult.getSearchPostList().size() > 0) {
							mCurrentPostSize += searchResult.getSearchPostList().size();
							if (mPostListFragment != null) {
								mPostListFragment.refreshPostComplete(searchResult.getSearchPostList());
							}
						}
					}
				});
	}

	private void showPostBlogList(SearchResult searchResult) {
		if (searchResult != null && searchResult.getSearchBlogInfoList() != null &&
				searchResult.getSearchBlogInfoList().size() > 0) {
			final List<SearchBlogInfo> searchBlogInfoList = searchResult.getSearchBlogInfoList();
			mViewBinding.rvBlogList.setLayoutManager(new LinearLayoutManager(SearchPostActivity.this, LinearLayoutManager.HORIZONTAL, false));
			mViewBinding.rvBlogList.setAdapter(new RecyclerView.Adapter<ItemSearchBlogViewHolder>() {
				@Override
				public ItemSearchBlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
					return new ItemSearchBlogViewHolder(ItemSearchBlogBinding.inflate(getLayoutInflater()));
				}

				@Override
				public void onBindViewHolder(final ItemSearchBlogViewHolder holder, final int position) {
					SpannableStringBuilder spannableStringBuilder = HtmlTool.fromHtml(searchBlogInfoList.get(position).getName(), holder.mViewBinding.tvName);
					spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.BLACK), 0,
							spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					holder.mViewBinding.tvName.setText(spannableStringBuilder);
					TumlodrGlide.with(SearchPostActivity.this)
							.load(Tools.getAvatarUrlByBlogName(searchBlogInfoList.get(position).getName()))
							.placeholder(AppUiConfig.sPicHolderResource)
							.transform(new RoundedCorners(15))
							.skipMemoryCache(true)
							.into(holder.mViewBinding.ivAvatar);
					holder.mViewBinding.getRoot().setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							BlogPostListActivity.start(SearchPostActivity.this,
									searchBlogInfoList.get(holder.getAdapterPosition()).getName(),
									false);
						}
					});
				}

				@Override
				public int getItemCount() {
					return searchBlogInfoList.size();
				}
			});
		}
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_search_post;
	}

	public void setContentFragment(int id) {
		mPostListFragment = (SearchPostFragment) getSupportFragmentManager().findFragmentById(id);
		if (mPostListFragment == null) {
			mPostListFragment = new SearchPostFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(id, mPostListFragment)
					.commit();
		}
	}

	public class ItemSearchBlogViewHolder extends BaseViewBindingViewHolder<ItemSearchBlogBinding> {

		ItemSearchBlogViewHolder(ItemSearchBlogBinding viewBinding) {
			super(viewBinding);
		}
	}
}
