package com.hochan.tumlodr.ui.component;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.google.android.flexbox.AlignContent;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.hochan.tumlodr.module.glide.GlideRequest;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.glide.TumlodrGlideUtil;
import com.hochan.tumlodr.module.glide.TunlodrGlideModelLoder;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.tools.Tools;
import com.tumblr.jumblr.types.Photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.flexbox.FlexDirection.ROW;
import static com.hochan.tumlodr.tools.Tools.getPicNameByUrl;

/**
 * .
 * Created by hochan on 2018/6/5.
 */

public class PostDetailPhotoLayout extends FlexboxLayout implements IPhotoLayout {

	private final int mDividerSize;
	private List<Photo> mPhotoList;
	private ArrayList<String> mPhotoUrls = new ArrayList<>();
	private ArrayList<String> mPhotoNormalUrls = new ArrayList<>();
	private ArrayList<String> mPhotoThumbnailUrls = new ArrayList<>();

	private int mImageViewWidth;
	private PostPhotoLayout.OnPhotoLayoutEventListener mEventListener;

	public PostDetailPhotoLayout(Context context) {
		this(context, null);
	}

	public PostDetailPhotoLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PostDetailPhotoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setFlexDirection(ROW);
		setShowDivider(SHOW_DIVIDER_MIDDLE);
		setFlexWrap(FlexWrap.WRAP);
		setAlignItems(AlignItems.FLEX_START);
		setAlignContent(AlignContent.FLEX_START);
		mDividerSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
		setDividerDrawableHorizontal(new ColorDrawable(Color.TRANSPARENT) {

			@Override
			public int getIntrinsicHeight() {
				return mDividerSize;
			}
		});
		setDividerDrawableVertical(new ColorDrawable(Color.TRANSPARENT) {
			@Override
			public int getIntrinsicWidth() {
				return mDividerSize;
			}
		});
	}

	public void setImageViewWidth(int imageViewWidth) {
		mImageViewWidth = imageViewWidth;
	}

	public void setEventListener(PostPhotoLayout.OnPhotoLayoutEventListener eventListener) {
		mEventListener = eventListener;
	}

	public void setPhotos(List<Photo> photos) {
		removeAllViews();
		mPhotoList = photos;
		mPhotoUrls.clear();
		mPhotoThumbnailUrls.clear();
		mPhotoNormalUrls.clear();
		if (photos == null || photos.size() == 0) {
			return;
		}
		for (Photo photo : photos) {
			mPhotoUrls.add(photo.getOriginalSize().getUrl());
		}
		if (photos.size() == 1) {
			Photo photo = photos.get(0);
			addFullWidthImageView(photo);
		} else if (photos.size() == 2) {
			addTwoPhotos(photos);
		} else if (photos.size() == 3) {
			addThreePhotos(photos);
		} else if (photos.size() == 4) {
			addFourPhotos(photos);
		} else if (photos.size() == 5) {
			addFivePhotos(photos);
		} else if (photos.size() == 6) {
			addSixPhotos(photos);
		} else if (photos.size() == 7) {
			addSevenPhotos(photos);
		} else if (photos.size() == 8) {
			addEightPhotos(photos);
		} else if (photos.size() == 9) {
			addNinePhotos(photos);
		} else if (photos.size() == 10) {
			addTenPhotos(photos);
		} else {
			for (Photo photo : photos) {
				addHalfWidthImageView(photo);
			}
		}
	}

	private void addMultiPhotos(List<Photo> photos) {
		switch (photos.size()) {
			case 1: {
				addFullWidthImageView(photos.get(0));
				break;
			}
			case 2: {
				addTwoPhotos(photos);
				break;
			}
			case 3: {
				addThreePhotos(photos);
				break;
			}
			case 4: {
				addFourPhotos(photos);
				break;
			}
			case 5: {
				addFivePhotos(photos);
				break;
			}
			case 6: {
				addSixPhotos(photos);
				break;
			}
			case 7: {
				addSevenPhotos(photos);
				break;
			}
			case 8: {
				addEightPhotos(photos);
				break;
			}
			case 9: {
				addNinePhotos(photos);
				break;
			}
		}
	}

	private void addTwoPhotos(List<Photo> photos) {
		if (isWidthLonger(photos.get(0)) && isWidthLonger(photos.get(1))) {
			addFullWidthImageView(photos.get(0));
			addFullWidthImageView(photos.get(1));
		} else {
			addHalfWidthImageView(photos.get(0));
			addHalfWidthImageView(photos.get(1));
		}
	}

	private void addThreePhotos(List<Photo> photos) {
		int widthLongerIndex = getWidthLongerIndex(photos);
		if (widthLongerIndex < 0) {
			for (Photo photo : photos) {
				addOneOfThreeWidthImageView(photo);
			}
		} else {
			addPhotosByWidthLongerIndex(photos, widthLongerIndex);
		}
	}

	private void addFourPhotos(List<Photo> photos) {
		int widthLongerIndex = getWidthLongerIndex(photos);
		if (widthLongerIndex < 0) {
			for (Photo photo : photos) {
				addHalfWidthImageView(photo);
			}
		} else {
			addPhotosByWidthLongerIndex(photos, widthLongerIndex);
		}
	}

	private void addFivePhotos(List<Photo> photos) {
		int widthLongerIndex = getWidthLongerIndex(photos);
		if (widthLongerIndex < 0) {
			addHalfWidthImageView(photos.get(0));
			addHalfWidthImageView(photos.get(1));
			addOneOfThreeWidthImageView(photos.get(2));
			addOneOfThreeWidthImageView(photos.get(3));
			addOneOfThreeWidthImageView(photos.get(4));
		} else {
			addPhotosByWidthLongerIndex(photos, widthLongerIndex);
		}
	}

	private void addSixPhotos(List<Photo> photos) {
		int widthLongerIndex = getWidthLongerIndex(photos);
		if (widthLongerIndex < 0) {
			for (Photo photo : photos) {
				addOneOfThreeWidthImageView(photo);
			}
		} else {
			addPhotosByWidthLongerIndex(photos, widthLongerIndex);
		}
	}

	private void addSevenPhotos(List<Photo> photos) {
		int widthLongerIndex = getWidthLongerIndex(photos);
		if (widthLongerIndex < 0) {
			for (int i = 0; i < photos.size(); i++) {
				if (i < 3) {
					addOneOfThreeWidthImageView(photos.get(i));
				} else {
					addHalfWidthImageView(photos.get(i));
				}
			}
		} else {
			addPhotosByWidthLongerIndex(photos, widthLongerIndex);
		}
	}

	private void addEightPhotos(List<Photo> photos) {
		int widthLongerIndex = getWidthLongerIndex(photos);
		if (widthLongerIndex < 0) {
			for (int i = 0; i < photos.size(); i++) {
				if (i < 2) {
					addHalfWidthImageView(photos.get(i));
				} else {
					addOneOfThreeWidthImageView(photos.get(i));
				}
			}
		} else {
			addPhotosByWidthLongerIndex(photos, widthLongerIndex);
		}
	}

	private void addNinePhotos(List<Photo> photos) {
		int widthLongerIndex = getWidthLongerIndex(photos);
		if (widthLongerIndex < 0) {
			for (int i = 0; i < photos.size(); i++) {
				addOneOfThreeWidthImageView(photos.get(i));
			}
		} else {
			addPhotosByWidthLongerIndex(photos, widthLongerIndex);
		}
	}

	private void addTenPhotos(List<Photo> photos) {
		int widthLongerIndex = getWidthLongerIndex(photos);
		if (widthLongerIndex < 0) {
			for (int i = 0; i < photos.size() - 1; i++) {
				addOneOfThreeWidthImageView(photos.get(i));
			}
			addFullWidthImageView(photos.get(photos.size() - 1));
		} else {
			addPhotosByWidthLongerIndex(photos, widthLongerIndex);
		}
	}

	private void addPhotosByWidthLongerIndex(List<Photo> photos, int widthLongerIndex) {
		if (widthLongerIndex > 0) {
			List<Photo> leftPhotos = photos.subList(0, widthLongerIndex);
			addMultiPhotos(leftPhotos);
		}
		addFullWidthImageView(photos.get(widthLongerIndex));
		if (widthLongerIndex + 1 < photos.size()) {
			List<Photo> rightPhotos = photos.subList(widthLongerIndex + 1, photos.size());
			addMultiPhotos(rightPhotos);
		}
	}


	private int getWidthLongerIndex(List<Photo> photos) {
		int widthLongerIndex = -1;
		for (int i = 0; i < photos.size(); i++) {
			if (isWidthLonger(photos.get(i))) {
				widthLongerIndex = i;
				break;
			}
		}
		return widthLongerIndex;
	}

	private void addFullWidthImageView(Photo photo) {
		int imageViewHeight = (int) (mImageViewWidth * 1.0 / photo.getOriginalSize().getWidth() * photo.getOriginalSize().getHeight());
		if (imageViewHeight >= mImageViewWidth) {
			imageViewHeight = (int) (mImageViewWidth * 3.0f / 4);
		}
		addImageViewOfWidthAndHeight(mImageViewWidth, imageViewHeight, photo);
	}

	@SuppressWarnings("SuspiciousNameCombination")
	private void addHalfWidthImageView(Photo photo) {
		int imageViewWidth = (mImageViewWidth - mDividerSize * 2) / 2;
		addImageViewOfWidthAndHeight(imageViewWidth, imageViewWidth, photo);
	}

	@SuppressWarnings("SuspiciousNameCombination")
	private void addOneOfThreeWidthImageView(Photo photo) {
		int imageViewWidth = (mImageViewWidth - mDividerSize * 3) / 3;
		addImageViewOfWidthAndHeight(imageViewWidth, imageViewWidth, photo);
	}

	private void addImageViewOfWidthAndHeight(int imageViewWidth, int imageViewHeight, final Photo photo) {
		ImageView imageView = new WrapImageView(getContext());
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		FlexboxLayout.LayoutParams layoutParams = new LayoutParams(imageViewWidth, imageViewHeight);
		imageView.setLayoutParams(layoutParams);
		layoutParams.setFlexGrow(1);
		addView(imageView);

		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mEventListener != null) {
					mEventListener.onImageViewClick(mPhotoList.indexOf(photo));
				}
			}
		});

		loadPhoto(imageViewWidth, imageViewHeight, photo, imageView);
	}

	private void loadPhoto(int imageViewWidth, int imageViewHeight, Photo photo, ImageView imageView) {
		if (photo == null) {
			return;
		}
		String normalUrl = TumlodrGlideUtil.PHOTO_NORMAL_URL_CACHE.get(photo);
		if (normalUrl == null) {
			normalUrl = TunlodrGlideModelLoder.getResolutionPhotoSize(imageViewWidth, photo).getUrl();
			if (normalUrl == null) {
				normalUrl = photo.getOriginalSize().getUrl();
			}
			if (normalUrl != null) {
				TumlodrGlideUtil.PHOTO_NORMAL_URL_CACHE.put(photo, normalUrl);
			}
		}
		mPhotoNormalUrls.add(normalUrl);

		GlideRequest<Drawable> normalRequest = TumlodrGlide
				.with(getContext())
				.asDrawable()
				.load(photo)
				.override(imageViewWidth, imageViewHeight);

		String imageUrl = photo.getOriginalSize().getUrl();
		final String imageName = getPicNameByUrl(imageUrl);
		String path = Tools.getStoragePathByFileName(imageName);
		File file = new File(path);
		if (file.exists()) {
			imageUrl = path;
		}

		TumlodrGlide.with(getContext()).asDrawable()
				.placeholder(new ColorDrawable(AppUiConfig.sPicHolderColor))
				.thumbnail(file.exists() ? null : normalRequest)
				.onlyRetrieveFromCache(!file.exists())
				.load(imageUrl)
				.override(imageViewWidth, imageViewHeight)
				.skipMemoryCache(true)
				.into(imageView)
				.clearOnDetach();
	}

	private boolean isWidthLonger(Photo photo) {
		return photo.getOriginalSize().getWidth() > photo.getOriginalSize().getHeight();
	}

	@Override
	public ArrayList<String> getPhotoUrls() {
		return mPhotoUrls;
	}

	@Override
	public ArrayList<String> getThumbnailUrls() {
		return mPhotoThumbnailUrls;
	}

	@Override
	public ArrayList<String> getPhotoNormalUrls() {
		return mPhotoNormalUrls;
	}

	@Override
	public ImageView getImageViewInPosition(int position) {
		return (ImageView) getChildAt(position);
	}

	@Override
	public int getImageViewCount() {
		return getChildCount();
	}

	@Override
	public void reloadImage() {
	}
}
