package com.hochan.tumlodr.ui.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringChain;
import com.facebook.rebound.SpringUtil;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.LayoutPopupWindowBinding;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.glide.TumlodrGlideUtil;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.tools.ScreenTools;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.tools.Tools;
import com.hochan.tumlodr.ui.activity.BlogPostListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 * Created by hochan on 2016/8/27.
 */
public class TumlodrPopupWindow extends PopupWindow {

	private final SpringChain springChain;
	private final LayoutPopupWindowBinding mViewBinding;
	private int mStatusBarHeight;
	private View mViewParent;

	private List<View> mAnimationViews = new ArrayList<>();

	private int mMaxArrowViewTransitionX;

	public TumlodrPopupWindow(View parent) {
		mViewParent = parent;
		mViewBinding = LayoutPopupWindowBinding.inflate(LayoutInflater.from(TumlodrApp.getContext()));

		mViewBinding.llPopUpWindowBg.setBackgroundResource(AppUiConfig.mPopupWindowBg);
		mViewBinding.vDropDown.setImageResource(AppUiConfig.mPopupWindowDropDownArrow);

		setContentView(mViewBinding.getRoot());
		setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
		setOutsideTouchable(false);
		setAnimationStyle(R.style.PopupAnimation);

		mViewBinding.getRoot().measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

		setWidth(mViewBinding.getRoot().getMeasuredWidth());
		setHeight(mViewBinding.getRoot().getMeasuredHeight());

		mAnimationViews.add(mViewBinding.getRoot());
		mAnimationViews.add(mViewBinding.llPopupOriginalBlog);
		mAnimationViews.add(mViewBinding.llPopupDownload);
		mAnimationViews.add(mViewBinding.llPopupLike);
		mAnimationViews.add(mViewBinding.llPopupReblog);
		mAnimationViews.add(mViewBinding.llPopupBlogInfo);

		final List<View> iconViews = new ArrayList<>();
		iconViews.add(mViewBinding.ivPopupOriginalBlog);
		iconViews.add(mViewBinding.ivPopupDownload);
		iconViews.add(mViewBinding.ivPopupLike);
		iconViews.add(mViewBinding.ivPopupReblog);

		springChain = SpringChain.create(90, 7, 90, 8);
		for (int i = 0; i < mAnimationViews.size(); i++) {
			final View view = mAnimationViews.get(i);
			springChain.addSpring(new SimpleSpringListener() {
				@Override
				public void onSpringUpdate(Spring spring) {
					view.setTranslationY((float) spring.getCurrentValue());
				}
			});
		}

		for (int i = 0; i < iconViews.size(); i++) {
			final View view = iconViews.get(i);
			springChain.addSpring(new SimpleSpringListener() {
				@Override
				public void onSpringUpdate(Spring spring) {
					float scale = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(),
							mViewBinding.getRoot().getMeasuredHeight(), 0,
							0.7, 1);
					view.setScaleX(scale);
					view.setScaleY(scale);
				}
			});
		}

		mMaxArrowViewTransitionX = ScreenTools.dip2px(TumlodrApp.mContext, 210);

		int resourceId = parent.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			//根据资源ID获取响应的尺寸值
			mStatusBarHeight = parent.getResources().getDimensionPixelSize(resourceId);
		}
	}

	public void setOnClickListener(View.OnClickListener clickListener) {
		for (View view : mAnimationViews) {
			view.setOnClickListener(clickListener);
		}

		mViewBinding.llPopupBlogInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(mViewBinding.tvPopupBlogName.getText().toString())) {
					BlogPostListActivity.start(v.getContext(),
							mViewBinding.tvPopupBlogName.getText().toString(), false);
				}
			}
		});
	}

	public void showOtherPopup(Context context, Point touchPoint, boolean liked, String blogName) {
		mViewBinding.llPopupDownload.setAlpha(0.5f);
		if (TumlodrGlideUtil.isContextValid(context)) {
			TumlodrGlide.with(context)
					.load(Tools.getAvatarUrlByBlogName(blogName))
					.placeholder(R.drawable.bg_avatar_holder_light)
					.skipMemoryCache(true)
					.transform(new RoundedCorners(5))
					.into(mViewBinding.ivPopupBlogAvatar);
		}
		mViewBinding.tvPopupBlogName.setText(blogName);
		touchPoint.set(touchPoint.x, touchPoint.y + mStatusBarHeight - mViewBinding.vDropDown.getMeasuredHeight());
		showWithAnimation(touchPoint, liked);
	}

	public void showVideoPopup(Context context, Point touchPoint, boolean liked, String blogName) {
		mViewBinding.llPopupDownload.setAlpha(1);
		mViewBinding.tvPopupDownload.setText(R.string.popup_window_download_video);
		if (TumlodrGlideUtil.isContextValid(context)) {
			TumlodrGlide.with(context)
					.load(Tools.getAvatarUrlByBlogName(blogName))
					.placeholder(R.drawable.bg_avatar_holder_light)
					.transform(new RoundedCorners(5))
					.skipMemoryCache(true)
					.into(mViewBinding.ivPopupBlogAvatar);
		}
		mViewBinding.tvPopupBlogName.setText(blogName);
		showWithAnimation(touchPoint, liked);
	}

	public void showPhotoPopup(Context context, Point touchPoint, boolean liked, String blogName) {
		mViewBinding.llPopupDownload.setAlpha(1);
		mViewBinding.tvPopupDownload.setText(R.string.popup_window_download_picture);
		if (TumlodrGlideUtil.isContextValid(context)) {
			TumlodrGlide.with(context)
					.load(Tools.getAvatarUrlByBlogName(blogName))
					.placeholder(R.drawable.bg_avatar_holder_light)
					.transform(new RoundedCorners(5))
					.skipMemoryCache(true)
					.into(mViewBinding.ivPopupBlogAvatar);
		}
		mViewBinding.tvPopupBlogName.setText(blogName);
		showWithAnimation(touchPoint, liked);
	}

	@SuppressLint("RtlHardcoded")
	private void showWithAnimation(Point touchPoint, boolean liked) {
		setLike(liked);
		int x = touchPoint.x - mViewBinding.getRoot().getWidth() / 2;
		int y = touchPoint.y - mViewBinding.getRoot().getMeasuredHeight() - mViewBinding.vDropDown.getMeasuredHeight();

		int translationX;
		if (x <= 0) {
			translationX = 0;
		} else if (x + mViewBinding.getRoot().getMeasuredWidth() >= ScreenTools.getScreenWidth(TumlodrApp.getContext())) {
			translationX = ScreenTools.getScreenWidth(TumlodrApp.getContext()) - mViewBinding.getRoot().getMeasuredWidth();
		} else {
			translationX = x;
		}
		translationX = touchPoint.x - translationX -
				mViewBinding.vDropDown.getMeasuredWidth() / 2;
		int minTranslationX = ScreenTools.dip2px(TumlodrApp.getContext(), 30);
		if (translationX < minTranslationX) {
			translationX = minTranslationX;
		} else if (translationX >= mViewBinding.getRoot().getWidth() - minTranslationX - mViewBinding.vDropDown.getMeasuredWidth()) {
			translationX = mViewBinding.getRoot().getMeasuredWidth() - minTranslationX - mViewBinding.vDropDown.getMeasuredWidth();
		}
		mViewBinding.vDropDown.setTranslationX(translationX);

		for (View view : mAnimationViews) {
			view.setTranslationY(mViewBinding.getRoot().getMeasuredHeight());
		}

		List<Spring> springs = springChain.getAllSprings();
		for (Spring spring : springs) {
			spring.setCurrentValue(mViewBinding.getRoot().getMeasuredHeight());
		}

		springChain.setControlSpringIndex(0).getControlSpring().setEndValue(0);

		showAtLocation(mViewParent, Gravity.TOP | Gravity.LEFT, x, y);
	}

	private void setLike(boolean liked) {
		if (liked) {
			mViewBinding.ivPopupLike.setImageResource(R.drawable.ic_favorite_red);
			mViewBinding.tvPopupLike.setText(R.string.popup_window_cancle);
		} else {
			mViewBinding.ivPopupLike.setImageResource(R.drawable.ic_favorite);
			mViewBinding.tvPopupLike.setText(R.string.popup_window_like);
		}
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
	}

	@Override
	public void setOnDismissListener(OnDismissListener onDismissListener) {
		super.setOnDismissListener(onDismissListener);
	}
}
