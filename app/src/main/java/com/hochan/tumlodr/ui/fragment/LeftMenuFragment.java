package com.hochan.tumlodr.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.FragmentLeftMenuBinding;
import com.hochan.tumlodr.model.LeftMenuItem;
import com.hochan.tumlodr.model.sharedpreferences.UserInfo;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.glide.TumlodrGlideUtil;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.tools.Tools;
import com.hochan.tumlodr.ui.activity.BlogPostListActivity;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseDrawerActivity;
import com.hochan.tumlodr.ui.component.WrapLinearLayoutManager;
import com.hochan.tumlodr.ui.fragment.base.BaseFragment;
import com.hochan.tumlodr.util.Events;
import com.hochan.tumlodr.util.FragmentLifecycleProvider;
import com.hochan.tumlodr.util.RxBus;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

import static android.support.v7.widget.RecyclerView.HORIZONTAL;

/**
 * 。
 * Created by Administrator on 2016/2/26.
 */
public class LeftMenuFragment extends BaseFragment {

	private ImageView ivUserIcon;
	private TextView tvUserName;

	private FragmentLeftMenuBinding mViewBinding;
	private ArrayList<String> mLastVisitedBlogNameList;

	private RecyclerView.Adapter mLastVisitedBlogAdapter = new RecyclerView.Adapter() {
		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new RecyclerView.ViewHolder(new ImageView(parent.getContext())) {
			};
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
			ImageView imageView = (ImageView) holder.itemView;
			int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					40, getResources().getDisplayMetrics());
			ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(size, size);
			layoutParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
					getResources().getDisplayMetrics());
			imageView.setLayoutParams(layoutParams);
			if (TumlodrGlideUtil.isContextValid(getActivity())) {
				TumlodrGlide.with(getActivity())
						.load(Tools.getAvatarUrlByBlogName(mLastVisitedBlogNameList.get(position)))
						.placeholder(AppUiConfig.sPicHolderResource)
						.transform(new RoundedCorners(10))
						.skipMemoryCache(true)
						.into(imageView);
			}
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					BlogPostListActivity.start(getContext(), mLastVisitedBlogNameList.get(position), false);
				}
			});
		}

		@Override
		public int getItemCount() {
			return mLastVisitedBlogNameList == null ? 0 : mLastVisitedBlogNameList.size();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RxBus.with(new FragmentLifecycleProvider(this)).onNext(new Consumer<Object>() {
			@Override
			public void accept(Object o) {
				if (o instanceof Events && ((Events) o).mCode == Events.EVENT_USER_INFO_UPDATE) {
					if (ivUserIcon != null) {
						Tools.loadAvatar(ivUserIcon, UserInfo.sBlogName);
					}
					if (tvUserName != null) {
						//tvUserName.setText(UserInfo.sBlogName);
					}
					setUpLeftMenu();
				} else if (o instanceof Events && ((Events) o).mCode == Events.EVENT_UPDATE_LAST_VISITED_BLOG) {
					updateLastVisitedBlogList();
				}
			}
		}).create();
	}

	@Override
	public int getLayoutRecourseId() {
		return R.layout.fragment_left_menu;
	}

	@Override
	public void bindView(View rootView) {
		mViewBinding = FragmentLeftMenuBinding.bind(rootView);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mViewBinding.llLeftMenu.setBackgroundColor(AppUiConfig.sThemeColor);

		ivUserIcon = view.findViewById(R.id.iv_user_icon);
		Tools.loadAvatar(ivUserIcon, UserInfo.sBlogName);

		tvUserName = view.findViewById(R.id.tv_user_name);
		tvUserName.setTextColor(AppUiConfig.sTextColor);
		//tvUserName.setText(UserInfo.sBlogName);

		setUpLeftMenu();

		LinearLayout linearLayout = view.findViewById(R.id.user_info_ll);
		linearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!TextUtils.isEmpty(UserInfo.sBlogName)) {
					BlogPostListActivity.start(getActivity(), UserInfo.sBlogName, true);
				}
			}
		});

		mViewBinding.rcyVisitedBlog.setLayoutManager(new WrapLinearLayoutManager(getContext(),
				HORIZONTAL, false));

		mViewBinding.rcyVisitedBlog.setAdapter(mLastVisitedBlogAdapter);
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		updateLastVisitedBlogList();
	}

	@SuppressWarnings("ConstantConditions")
	private void updateLastVisitedBlogList() {
		SharedPreferences visitedBlogList = getActivity().getSharedPreferences(AppConfig.SHARE_VISITED_BLOG_LIST, Context.MODE_PRIVATE);
		String blogString = visitedBlogList.getString(AppConfig.SHARE_VISITED_BLOG_LIST, null);
		if (!TextUtils.isEmpty(blogString)) {
			mLastVisitedBlogNameList = new ArrayList<>();
			JsonArray jsonArray = (JsonArray) new JsonParser().parse(blogString);
			for (int i = 0; i < jsonArray.size(); i++) {
				mLastVisitedBlogNameList.add(jsonArray.get(i).getAsString());
			}
			mLastVisitedBlogAdapter.notifyDataSetChanged();
		}
	}

	private void setUpLeftMenu() {
		mViewBinding.llLeftMenuItemContainer.removeAllViews();
		for (int i = 0; i < LeftMenuItem.LEFT_MENU_ITEMS.size(); i++) {
			mViewBinding.llLeftMenuItemContainer.addView(getLeftMeuItemView(i, mViewBinding.llLeftMenuItemContainer));
		}
	}

	@NonNull
	private LinearLayout getLeftMeuItemView(int position, ViewGroup parent) {
		final LeftMenuItem leftMenuItem = LeftMenuItem.LEFT_MENU_ITEMS.get(position);
		LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_left_menu, parent, false);
		ImageView imageView = linearLayout.findViewById(R.id.iv_menu_icon);
		imageView.setImageResource(leftMenuItem.mIconResourceId);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			imageView.setImageTintList(ColorStateList.valueOf(AppUiConfig.sTextColor));
		} else {
			imageView.setBackgroundResource(R.drawable.shape_btn_background);
		}

		TextView textView = linearLayout.findViewById(R.id.tv_menu);
		textView.setText(leftMenuItem.mTitleResourceId);
		textView.setTextColor(AppUiConfig.sTextColor);

		((BaseDrawerActivity) getActivity()).closeDrawer();
		linearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((BaseDrawerActivity) getActivity()).closeDrawer();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						leftMenuItem.launch((BaseDrawerActivity) getContext());
					}
				}, 200);

			}
		});

		if (leftMenuItem.mTitleResourceId == R.string.left_menu_like && UserInfo.sLikeCount != 0) {
			textView.setText(String.format(getString(R.string.left_menu_like_format), UserInfo.sLikeCount));
		} else if (leftMenuItem.mTitleResourceId == R.string.left_menu_follow && UserInfo.sFollowingCount != 0) {
			textView.setText(String.format(getString(R.string.left_menu_follow_format), UserInfo.sFollowingCount));
		}
		return linearLayout;
	}
}
