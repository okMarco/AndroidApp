package com.hochan.tumlodr.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hochan.tumlodr.databinding.LayoutFullPhotoViewBinding;
import com.hochan.tumlodr.model.data.download.DownloadRecord;
import com.hochan.tumlodr.module.glide.GlideRequest;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.video.videolayout.VideoPlayLayout;
import com.hochan.tumlodr.ui.fragment.DownloadTaskFragment;

import java.util.Locale;

import static com.hochan.tumlodr.model.data.TasksManagerModel.TYPE_VIDEO;

public class DownloadFileFullScreenActivity extends FullScreenPhotoViewActivity {

	@Override
	public void initData() {
		super.initData();
		mDefaultIndex = getIntent().getIntExtra(Router.EXTRA_DEFAULT_INDEX, 0);

		mViewBinding.tvPicIndex.setText(String.format(Locale.US, "%d/%d", mDefaultIndex + 1,
				DownloadTaskFragment.sDownloadRecordList == null ?
						0 : DownloadTaskFragment.sDownloadRecordList.size()));
		mViewBinding.viewPager.setAdapter(getAdapter());
		mViewBinding.viewPager.setCurrentItem(mDefaultIndex, false);
	}

	@Override
	public void initWidget() {
		super.initWidget();
		mViewBinding.btnSave.setVisibility(View.GONE);
		mViewBinding.llBlogInfo.setVisibility(View.GONE);
	}

	@Override
	public void setUpBlogInfo() {
		// Do nothing
	}

	@NonNull
	public PagerAdapter getAdapter() {
		return new PagerAdapter() {
			@Override
			public int getCount() {
				return DownloadTaskFragment.sDownloadRecordList == null ?
						0 : DownloadTaskFragment.sDownloadRecordList.size();
			}

			@Override
			public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
				return view == object;
			}

			DownloadRecord getItem(int position) {
				return DownloadTaskFragment.sDownloadRecordList == null ?
						null : DownloadTaskFragment.sDownloadRecordList.get(position);
			}

			@NonNull
			@Override
			public Object instantiateItem(@NonNull ViewGroup container, final int position) {
				if (getItem(position) == null) {
					return new View(DownloadFileFullScreenActivity.this);
				}
				if (TYPE_VIDEO.equals(getItem(position).getType())) {
					return addVideoView(container, position);
				}

				final LayoutFullPhotoViewBinding fullPhotoViewBinding = addPhotoView(container, position);
				return fullPhotoViewBinding.getRoot();
			}

			@NonNull
			private LayoutFullPhotoViewBinding addPhotoView(@NonNull ViewGroup container, final int position) {
				final LayoutFullPhotoViewBinding fullPhotoViewBinding = LayoutFullPhotoViewBinding.inflate(getLayoutInflater());
				fullPhotoViewBinding.photoView.setOnPhotoViewDragListener(mOnPhotoViewDragListener);
				container.addView(fullPhotoViewBinding.getRoot());
				if (position == mDefaultIndex) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						fullPhotoViewBinding.photoView.setTransitionName(Router.SHAREELEMENT_NAME);
					}
				}

				final String path = getItem(position).getPath();

				GlideRequest<Drawable> fullRequest = TumlodrGlide.with(DownloadFileFullScreenActivity.this)
						.asDrawable().listener(mFullRequestListener)
						.priority(position == mDefaultIndex ? Priority.IMMEDIATE : Priority.NORMAL)
						.fitCenter()
						.skipMemoryCache(true)
						.load(path);

				fullRequest.listener(new RequestListener<Drawable>() {
					@Override
					public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
						startShareViewAnimation();
						return false;
					}

					@Override
					public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
						startShareViewAnimation();
						if (resource instanceof GifDrawable) {
							resource.setVisible(true, true);
							((GifDrawable) resource).start();
						}
						fullPhotoViewBinding.progressBar.setVisibility(View.INVISIBLE);
						return false;
					}

					private void startShareViewAnimation() {
						if (position == mDefaultIndex) {
							fullPhotoViewBinding.photoView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
								@Override
								public boolean onPreDraw() {
									fullPhotoViewBinding.photoView.getViewTreeObserver().removeOnPreDrawListener(this);
									supportStartPostponedEnterTransition();
									return false;
								}
							});
						}
					}

				}).placeholder(new ColorDrawable(Color.BLACK))
						.fitCenter()
						.into(fullPhotoViewBinding.photoView);

				mBindingSparseArray.put(position, fullPhotoViewBinding);
				return fullPhotoViewBinding;
			}

			@NonNull
			private View addVideoView(@NonNull ViewGroup container, final int position) {
				VideoPlayLayout videoPlayLayout = new VideoPlayLayout(getApplicationContext());
				videoPlayLayout.getThumbnailImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);
				videoPlayLayout.setData(getItem(position).getPath(), getItem(position).getThumbnail());
				container.addView(videoPlayLayout);
				mBindingSparseArray.put(position, videoPlayLayout);
				if (position == mDefaultIndex) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						videoPlayLayout.setTransitionName(Router.SHAREELEMENT_NAME);
					}
					supportStartPostponedEnterTransition();
				}
				return videoPlayLayout;
			}

			@Override
			public void destroyItem(@NonNull ViewGroup container, int position,
			                        @NonNull Object object) {
				mBindingSparseArray.delete(position);
				container.removeView((View) object);
			}
		};
	}
}
