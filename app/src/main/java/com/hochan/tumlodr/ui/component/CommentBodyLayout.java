package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.model.data.CommentBody;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.ui.activity.BlogPostListActivity;
import com.hochan.tumlodr.tools.HtmlTool;
import com.hochan.tumlodr.tools.ScreenTools;
import com.hochan.tumlodr.tools.Tools;

import java.util.List;

/**
 * .
 * Created by hochan on 2017/9/24.
 */

public class CommentBodyLayout extends LinearLayout {

	private static final String TAG_LL_TITLE = "tag_ll_title";
	private static final String TAG_RIV_AVATAR = "tag_riv_avatar";
	private static final String TAG_TV_BLOG = "tag_tv_blog";
	private static final String TAG_TV_CONTENT = "tag_tv_content";

	private Context mContext;

	public CommentBodyLayout(Context context) {
		this(context, null);
	}

	public CommentBodyLayout(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CommentBodyLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		mContext = context;
		setOrientation(LinearLayout.VERTICAL);
	}

	private void addCommentBodyUnit(Context context) {
		int padding = ScreenTools.dip2px(context, 10);

		LinearLayout llCommentUnit = new LinearLayout(context);
		llCommentUnit.setOrientation(LinearLayout.VERTICAL);

		LinearLayout llTitle = new LinearLayout(context);
		llTitle.setOrientation(LinearLayout.HORIZONTAL);
		llTitle.setPadding(padding, padding, padding, padding);
		llTitle.setGravity(Gravity.CENTER_VERTICAL);
		llTitle.setTag(TAG_LL_TITLE);

		ImageView ivAvatar = new ImageView(context);
		int rivSize = ScreenTools.dip2px(context, 30);
		llTitle.addView(ivAvatar, new LinearLayoutCompat.LayoutParams(rivSize, rivSize));
		llTitle.setGravity(Gravity.CENTER_VERTICAL);
		ivAvatar.setId(R.id.iv_comment_avatar);

		TextView tvBlog = new TextView(context);
		tvBlog.setTextColor(AppUiConfig.sTextColor);
		tvBlog.setTextSize(14);
		tvBlog.getPaint().setFakeBoldText(true);
		tvBlog.setPadding(padding, 0, 0, 0);
		tvBlog.setTag(TAG_TV_BLOG);
		llTitle.addView(tvBlog, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		llCommentUnit.addView(llTitle, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		TextView tvContent = new TextView(context);
		llCommentUnit.addView(tvContent, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		tvContent.setTextSize(13);
		tvContent.setTextColor(AppUiConfig.sTextColor);
		tvContent.setPadding(padding, padding, padding, padding);
		tvContent.setTag(TAG_TV_CONTENT);

		addView(llCommentUnit, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	public void setCommentBodies(List<CommentBody> commentBodies) {
		if (commentBodies.size() > getChildCount()) {
			int addCount = commentBodies.size() - getChildCount();
			for (int i = 0; i < addCount; i++) {
				addCommentBodyUnit(mContext);
			}
		} else if (commentBodies.size() < getChildCount()) {
			int removeCount = getChildCount() - commentBodies.size();
			for (int i = 0; i < removeCount; i++) {
				removeViewAt(0);
			}
		}

		for (int i = 0; i < commentBodies.size(); i++) {
			LinearLayout llCommentUnit = (LinearLayout) getChildAt(i);
			final TextView tvBlog = llCommentUnit.findViewWithTag(TAG_TV_BLOG);
			TextView tvContent = llCommentUnit.findViewWithTag(TAG_TV_CONTENT);
			if (TextUtils.isEmpty(commentBodies.get(i).getBlog())) {
				llCommentUnit.findViewWithTag(TAG_LL_TITLE).setVisibility(GONE);
			} else {
				tvBlog.setText(commentBodies.get(i).getBlog());
				ImageView ivAvatar = llCommentUnit.findViewById(R.id.iv_comment_avatar);
				TumlodrGlide.with(getContext())
						.load(Tools.getAvatarUrlByBlogName(commentBodies.get(i).getBlog()))
						.transform(new MultiTransformation<>(new CenterCrop(),
								new RoundedCorners(10)))
						.placeholder(AppUiConfig.sPicHolderResource)
						.skipMemoryCache(true)
						.into(ivAvatar);
			}
			if (TextUtils.isEmpty(commentBodies.get(i).getContent())) {
				tvContent.setVisibility(GONE);
			} else {
				tvContent.setText(HtmlTool.fromHtml(commentBodies.get(i).getContent(), tvContent));
			}
			llCommentUnit.findViewWithTag(TAG_LL_TITLE).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					BlogPostListActivity.start(mContext, tvBlog.getText().toString(), false);
				}
			});
		}
	}
}
