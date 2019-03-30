package com.hochan.tumlodr.ui.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hochan.tumlodr.jumblr.types.Photo;
import com.hochan.tumlodr.module.glide.GlideRequest;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.glide.TumlodrGlideUtil;
import com.hochan.tumlodr.module.glide.TunlodrGlideModelLoder;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.tools.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.hochan.tumlodr.tools.Tools.getPicNameByUrl;

/**
 * .
 * Created by zhendong_chen on 2016/9/5.
 */
public class PostPhotoLayout extends FrameLayout implements View.OnClickListener,
		View.OnTouchListener, View.OnLongClickListener, IPhotoLayout {

	private static final int MAX_IMAGE_VIEW_COUNT = 10;

	private final LinearLayout llIvContainer;

	private OnPhotoLayoutEventListener mEventListener;

	private ArrayList<String> mPhotoUrls = new ArrayList<>();
	private ArrayList<String> mPhotoThumbnailUrls = new ArrayList<>();
	private ArrayList<String> mPhotoNormalUrls = new ArrayList<>();

	private int mImageViewWidth;

	private Point mTouchPoint = new Point(0, 0);

	public PostPhotoLayout(Context context) {
		this(context, null);
	}

	public PostPhotoLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PostPhotoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		llIvContainer = new LinearLayout(context);
		llIvContainer.setOrientation(LinearLayout.VERTICAL);
		addView(llIvContainer, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		for (int i = 0; i < 10; i++) {
			WrapImageView imageView = new WrapImageView(context);
			imageView.setOnClickListener(this);
			llIvContainer.addView(imageView, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
		}
	}

	public void setEventListener(OnPhotoLayoutEventListener eventListener) {
		mEventListener = eventListener;
	}

	public void setImageViewWidth(int imageViewWidth) {
		mImageViewWidth = imageViewWidth;
	}

	public ImageView getImageViewInPosition(int position) {
		return (ImageView) llIvContainer.getChildAt(position);
	}

	public int getImageViewCount() {
		return llIvContainer.getChildCount();
	}

	public void setThumbnailPhotos(final List<Photo> photos) {
		setPhotos(photos);
	}

	@SuppressLint("ClickableViewAccessibility")
	public void setPhotos(List<Photo> photos) {
		mPhotoUrls.clear();
		mPhotoThumbnailUrls.clear();
		mPhotoNormalUrls.clear();
		for (int i = 0; i < MAX_IMAGE_VIEW_COUNT; i++) {
			TumlodrGlide.with(getContext()).clear(llIvContainer.getChildAt(i));
			if (i >= photos.size()) {
				llIvContainer.getChildAt(i).setVisibility(GONE);
			} else {
				Photo photo = photos.get(i);
				mPhotoUrls.add(photo.getOriginalSize().getUrl());
				ImageView imageView = (ImageView) llIvContainer.getChildAt(i);
				imageView.setVisibility(VISIBLE);

				int imageHeight = photo.getOriginalSize().getHeight();
				int imageWidth = photo.getOriginalSize().getWidth();
				int imageViewHeight = (int) (mImageViewWidth * imageHeight * 1.0 / imageWidth * 1.0);
				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
				layoutParams.width = mImageViewWidth;
				layoutParams.height = imageViewHeight;
				imageView.setLayoutParams(layoutParams);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

				String normalUrl = TumlodrGlideUtil.PHOTO_NORMAL_URL_CACHE.get(photo);
				if (normalUrl == null) {
					normalUrl = TunlodrGlideModelLoder.getResolutionPhotoSize(mImageViewWidth, photo).getUrl();
					if (normalUrl == null) {
						normalUrl = photo.getOriginalSize().getUrl();
					}
					TumlodrGlideUtil.PHOTO_NORMAL_URL_CACHE.put(photo, normalUrl);
				}
				mPhotoNormalUrls.add(normalUrl);

				imageView.setOnClickListener(this);
				imageView.setOnLongClickListener(this);
				imageView.setOnTouchListener(this);

				GlideRequest<Drawable> normalRequest = TumlodrGlide.with(getContext())
						.asDrawable()
						.load(photo)
						.placeholder(new ColorDrawable(AppUiConfig.sPicHolderColor))
						.skipMemoryCache(true)
						.override(mImageViewWidth, imageViewHeight);

				String imageUrl = photo.getOriginalSize().getUrl();
				final String imageName = getPicNameByUrl(imageUrl);
				String path = Tools.getStoragePathByFileName(imageName);
				File file = new File(path);
				if (file.exists()) {
					imageUrl = path;
					TumlodrGlide.with(getContext()).asDrawable()
							.placeholder(new ColorDrawable(AppUiConfig.sPicHolderColor))
							.load(imageUrl)
							.override(mImageViewWidth, imageViewHeight)
							.skipMemoryCache(true)
							.into(imageView)
							.clearOnDetach();
				} else {
					normalRequest.into(imageView).clearOnDetach();
				}
			}
		}
	}

	public void reloadImage() {
	}


	@SuppressLint("ClickableViewAccessibility")
	public void setVideoThumbnail(String thumbnailUrl, int thumbnailWidth, int thumbnailHeight) {
		mPhotoUrls.clear();
		mPhotoUrls.add(thumbnailUrl);
		for (int i = 1; i < MAX_IMAGE_VIEW_COUNT; i++) {
			llIvContainer.getChildAt(i).setVisibility(GONE);
		}
		ImageView imageView = (ImageView) llIvContainer.getChildAt(0);
		imageView.setVisibility(VISIBLE);
		imageView.setOnLongClickListener(this);
		imageView.setOnTouchListener(this);
		int imageViewHeight = (int) (mImageViewWidth * thumbnailHeight * 1.0 / thumbnailWidth * 1.0);
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
		layoutParams.width = mImageViewWidth;
		layoutParams.height = imageViewHeight;
		imageView.setLayoutParams(layoutParams);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		TumlodrGlide.with(imageView)
				.load(thumbnailUrl)
				.skipMemoryCache(true)
				.placeholder(new ColorDrawable(AppUiConfig.sPicHolderColor))
				.override(mImageViewWidth, imageViewHeight).into(imageView);
	}

	public ArrayList<String> getPhotoUrls() {
		return mPhotoUrls;
	}

	public ArrayList<String> getThumbnailUrls() {
		return mPhotoThumbnailUrls;
	}

	public ArrayList<String> getPhotoNormalUrls() {
		return mPhotoNormalUrls;
	}

	@Override
	public void onClick(View v) {
		if (mEventListener != null) {
			mEventListener.onImageViewClick(llIvContainer.indexOfChild(v));
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mTouchPoint.set((int) event.getRawX(), (int) event.getRawY());
		}
		return false;
	}

	@Override
	public boolean onLongClick(View v) {
		if (mEventListener != null) {
			mEventListener.onImageViewLongClick(mTouchPoint, (ImageView) v);
			return true;
		}
		return false;
	}

	public interface OnPhotoLayoutEventListener {
		void onImageViewClick(int index);

		void onImageViewLongClick(Point point, ImageView imageView);
	}
}
