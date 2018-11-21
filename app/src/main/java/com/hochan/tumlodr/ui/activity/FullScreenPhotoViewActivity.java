package com.hochan.tumlodr.ui.activity;

import android.app.SharedElementCallback;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.transition.Transition;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.ActivityPhotoViewBinding;
import com.hochan.tumlodr.databinding.LayoutFullPhotoViewBinding;
import com.hochan.tumlodr.model.TumlodrService;
import com.hochan.tumlodr.model.data.TasksManagerModel;
import com.hochan.tumlodr.model.data.download.DownloadRecordDatabase;
import com.hochan.tumlodr.model.sharedpreferences.UserInfo;
import com.hochan.tumlodr.module.glide.GlideRequest;
import com.hochan.tumlodr.module.glide.ProgressTarget;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.glide.TumlodrGlideUtil;
import com.hochan.tumlodr.tools.AppUiConfig;
import com.hochan.tumlodr.tools.Tools;
import com.hochan.tumlodr.ui.activity.baseactivity.BaseViewBindingActivity;
import com.hochan.tumlodr.ui.component.DragToFinishPhotoView;
import com.hochan.tumlodr.ui.component.FullPhotoViewProgressTarget;
import com.hochan.tumlodr.ui.component.listener.SimpleTransitionListener;
import com.hochan.tumlodr.util.Events;
import com.hochan.tumlodr.util.FileDownloadUtil;
import com.hochan.tumlodr.util.FileUtils;
import com.hochan.tumlodr.util.RxBus;
import com.hochan.tumlodr.util.SimpleCompletableObserver;
import com.hochan.tumlodr.util.ViewUtils;
import com.hochan.tumlodr.util.statusbar.StatusBarCompat;
import com.tumblr.jumblr.types.Post;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.hochan.tumlodr.tools.Tools.getPicNameByUrl;

public class FullScreenPhotoViewActivity extends BaseViewBindingActivity<ActivityPhotoViewBinding> {

	public static Post sPost;

	protected int mDefaultIndex = 0;
	private ArrayList<String> mPhotoUrls;
	private List<String> mPhotoNormalUrls;
	private GlideRequest<Drawable> mFullRequest;
	private ProgressTarget<String, Drawable> mDefaultTarget;

	private boolean mIsFirstEnter = true;

	public SparseArray<Object> mBindingSparseArray = new SparseArray<>();

	public RequestListener<Drawable> mFullRequestListener = new RequestListener<Drawable>() {
		@Override
		public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
			if (target instanceof FullPhotoViewProgressTarget) {
				((FullPhotoViewProgressTarget) target).hideProgress();
			}
			return false;
		}

		@Override
		public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
			if (target instanceof FullPhotoViewProgressTarget) {
				((FullPhotoViewProgressTarget) target).hideProgress();
			}
			return false;
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportPostponeEnterTransition();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().getSharedElementEnterTransition().setDuration(150);
			getWindow().getEnterTransition().addListener(new SimpleTransitionListener() {

				// 最后调用
				@Override
				public void onTransitionEnd(@NonNull Transition transition) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						getWindow().getEnterTransition().removeListener(this);
					}
					if (isFinishing()) {
						return;
					}
					if (TumlodrGlideUtil.isContextValid(FullScreenPhotoViewActivity.this)) {
						TumlodrGlide.with(FullScreenPhotoViewActivity.this)
								.resumeRequests();
					}
					if (mFullRequest != null) {
						mFullRequest.priority(Priority.IMMEDIATE).listener(new RequestListener<Drawable>() {
							@Override
							public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
								return false;
							}

							@Override
							public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
								mDefaultTarget.onResourceReady(resource, null);
								if (resource instanceof GifDrawable) {
									resource.setVisible(true, true);
									((GifDrawable) resource).start();
								}
								if (mDefaultTarget instanceof FullPhotoViewProgressTarget) {
									((FullPhotoViewProgressTarget) mDefaultTarget).hideProgress();
								}
								return true;
							}
						}).preload();
					}
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						getWindow().getSharedElementEnterTransition().removeListener(this);
					}
					setUpBlogInfo();
					mViewBinding.tvPicIndex.animate().alpha(1);
				}

				@Override
				public void onTransitionCancel(@NonNull Transition transition) {
					if (TumlodrGlideUtil.isContextValid(FullScreenPhotoViewActivity.this)) {
						TumlodrGlide.with(FullScreenPhotoViewActivity.this).resumeRequests();
					}
					mFullRequest.into(mDefaultTarget);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						getWindow().getSharedElementEnterTransition().removeListener(this);
					}
				}
			});
			getWindow().getSharedElementEnterTransition().addListener(new SimpleTransitionListener() {

				// 最先调用
				@Override
				public void onTransitionStart(@NonNull Transition transition) {
					if (TumlodrGlideUtil.isContextValid(FullScreenPhotoViewActivity.this)) {
						TumlodrGlide.with(FullScreenPhotoViewActivity.this).pauseRequests();
					}
				}

				@Override
				public void onTransitionEnd(@NonNull Transition transition) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						getWindow().getSharedElementEnterTransition().removeListener(this);
					}
				}
			});
		}
	}

	@Override
	public int getThemeId() {
		return R.style.AppTheme_NoActionBar_White_TranBg;
	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.activity_photo_view;
	}

	@Override
	public void initStatusBar() {
		StatusBarCompat.setStatusBarHalfTranslucent(getWindow());
		StatusBarCompat.setNavigationBarTranslucent(getWindow());
		StatusBarCompat.setLightStatusBar(getWindow(), false);
	}

	@Override
	public void initWidget() {
		super.initWidget();
		supportPostponeEnterTransition();

		mViewBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				mViewBinding.tvPicIndex.setText(String.format(Locale.US, "%d/%d", position + 1,
						mViewBinding.viewPager.getAdapter() != null ? mViewBinding.viewPager.getAdapter().getCount() : 0));
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		mViewBinding.btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mPhotoUrls != null) {
					if (mViewBinding.viewPager.getCurrentItem() < 0
							|| mViewBinding.viewPager.getCurrentItem() >= mPhotoUrls.size()) {
						return;
					}
					savePicture();
				}
			}
		});

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			mViewBinding.tvPicIndex.animate().alpha(1);
			setUpBlogInfo();
		}
	}

	public void setUpBlogInfo() {
		if (sPost == null) {
			return;
		}
		mViewBinding.llBlogInfo.setVisibility(View.VISIBLE);
		mViewBinding.llBlogInfo.animate().alpha(1);
		mViewBinding.tvBlogName.setText(sPost.getBlogName());
		TumlodrGlide.with(this)
				.load(Tools.getAvatarUrlByBlogName(sPost.getBlogName()))
				.transform(new RoundedCorners(5))
				.placeholder(AppUiConfig.sPicHolderResource)
				.skipMemoryCache(true)
				.into(mViewBinding.ivAvatar);
		if (sPost.isLiked() != null && sPost.isLiked()) {
			mViewBinding.btnLike.setImageResource(R.drawable.ic_popup_liked);
		} else {
			mViewBinding.btnLike.setImageResource(R.drawable.ic_popup_like);
		}
		mViewBinding.btnLike.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mViewBinding.btnLike.setImageResource(R.drawable.ic_popup_liked);
				if (sPost == null) {
					return;
				}
				TumlodrService.likePost(sPost.getId(), sPost.getReblogKey()).subscribe();
				ViewUtils.likeAnimation(view);
			}
		});
		mViewBinding.btnPost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PostDetailActivity.showPostDetail(FullScreenPhotoViewActivity.this, sPost);
			}
		});
		mViewBinding.btnReblog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (sPost == null) {
					return;
				}
				TumlodrService.reblog(UserInfo.sUserName, sPost.getId(), sPost.getReblogKey());
			}
		});
		mViewBinding.tvBlogName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (sPost == null) {
					return;
				}
				BlogPostListActivity.start(FullScreenPhotoViewActivity.this, sPost.getBlogName(), false);
			}
		});
	}

	public DragToFinishPhotoView.OnPhotoViewDragListener mOnPhotoViewDragListener = new DragToFinishPhotoView.OnPhotoViewDragListener() {
		@Override
		public void onDragToFinish() {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				finishAfterTransition();
			} else {
				finish();
			}
		}

		@Override
		public void onDragOffset(float offset, float maxOffset) {
			mViewBinding.vBackgroundMask.setAlpha(1 - offset / maxOffset);
			mViewBinding.flTitleLayout.setAlpha(1 - offset / maxOffset);
		}
	};

	@Override
	public void initData() {
		mDefaultIndex = getIntent().getIntExtra(Router.EXTRA_DEFAULT_INDEX, 0);
		mPhotoUrls = getIntent().getStringArrayListExtra(Router.EXTRA_IMAGE_URL_LIST);
		mPhotoNormalUrls = getIntent().getStringArrayListExtra(Router.EXTRA_NORMAL_IMAGE_URL_LIST);

		if (mPhotoUrls != null) {
			mViewBinding.tvPicIndex.setText(String.format(Locale.US, "%d/%d", mDefaultIndex + 1, mPhotoUrls.size()));
			mViewBinding.viewPager.setAdapter(getAdapter());
			mViewBinding.viewPager.setCurrentItem(mDefaultIndex, false);
		} else {
			mPhotoUrls = new ArrayList<>();
			mPhotoNormalUrls = new ArrayList<>();
		}
	}

	@NonNull
	public PagerAdapter getAdapter() {
		return new PagerAdapter() {
			@Override
			public int getCount() {
				return mPhotoUrls.size();
			}

			@Override
			public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
				return view == object;
			}

			@NonNull
			@Override
			public Object instantiateItem(@NonNull ViewGroup container, final int position) {
				final LayoutFullPhotoViewBinding fullPhotoViewBinding = LayoutFullPhotoViewBinding.inflate(getLayoutInflater());
				fullPhotoViewBinding.photoView.setOnPhotoViewDragListener(mOnPhotoViewDragListener);

				container.addView(fullPhotoViewBinding.getRoot());
				if (position == mDefaultIndex) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						fullPhotoViewBinding.photoView.setTransitionName(Router.SHAREELEMENT_NAME);
					}
				}
				final String url = mPhotoUrls.get(position);
				final String imageName = getPicNameByUrl(url);
				final String path = Tools.getStoragePathByFileName(imageName);
				String normalUrl = (mPhotoNormalUrls == null || mPhotoNormalUrls.size() == 0) ? null : mPhotoNormalUrls.get(position);
				File localFile = new File(path);
				final String fullUrl = localFile.exists() ? path : url;

				ProgressTarget<String, Drawable> target = new FullPhotoViewProgressTarget<>(fullUrl, new DrawableImageViewTarget(fullPhotoViewBinding.photoView),
						fullPhotoViewBinding.progressBar,
						fullPhotoViewBinding.tvProgress);

				GlideRequest<Drawable> normalRequest = TumlodrGlide
						.with(FullScreenPhotoViewActivity.this)
						.asDrawable()
						.onlyRetrieveFromCache(true)
						.fitCenter()
						.load(normalUrl);

				GlideRequest<Drawable> fullRequest = TumlodrGlide
						.with(FullScreenPhotoViewActivity.this)
						.asDrawable()
						.listener(mFullRequestListener)
						.priority(position == mDefaultIndex ? Priority.IMMEDIATE : Priority.NORMAL)
						.fitCenter()
						.load(fullUrl);

				if (position == mDefaultIndex && mIsFirstEnter && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					mIsFirstEnter = false;
					GlideRequest<Drawable> tmpRequest = localFile.exists() ? fullRequest : normalRequest;
					tmpRequest.placeholder(new ColorDrawable(Color.BLACK))
							.listener(new RequestListener<Drawable>() {
								@Override
								public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
									startShareViewEnterAnimation(fullPhotoViewBinding.photoView);
									return false;
								}

								@Override
								public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
									startShareViewEnterAnimation(fullPhotoViewBinding.photoView);
									if (model.equals(fullUrl) && target instanceof FullPhotoViewProgressTarget) {
										((FullPhotoViewProgressTarget) target).hideProgress();
									}
									return false;
								}
							}).into(target);

					mFullRequest = localFile.exists() ? null : fullRequest;
					mDefaultTarget = target;
				} else {
					fullRequest.thumbnail(localFile.exists() ? null : normalRequest)
							.placeholder(new ColorDrawable(Color.BLACK))
							.into(target);
				}
				mBindingSparseArray.put(position, fullPhotoViewBinding);
				return fullPhotoViewBinding.getRoot();
			}

			@Override
			public void destroyItem(@NonNull ViewGroup container, int position,
			                        @NonNull Object object) {
				mBindingSparseArray.delete(position);
				container.removeView((View) object);
			}
		};
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	public void finishAfterTransition() {
		RxBus.getInstance().send(new Events<>(Events.EVENT_SHAREELEMENT_EXIT_INDEX_CHANGE, mViewBinding.viewPager.getCurrentItem()));
		setEnterSharedElementCallback(new SharedElementCallback() {
			@Override
			public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
				names.clear();
				sharedElements.clear();
				names.add(Router.SHAREELEMENT_NAME);
				if (mBindingSparseArray.get(mViewBinding.viewPager.getCurrentItem()) instanceof LayoutFullPhotoViewBinding) {
					sharedElements.put(Router.SHAREELEMENT_NAME, ((LayoutFullPhotoViewBinding) mBindingSparseArray.get(mViewBinding.viewPager.getCurrentItem())).photoView);
				} else if (mBindingSparseArray.get(mViewBinding.viewPager.getCurrentItem()) instanceof View) {
					if (((View) mBindingSparseArray.get(mViewBinding.viewPager.getCurrentItem())).findViewById(R.id.iv_video_cover) != null) {
						sharedElements.put(Router.SHAREELEMENT_NAME, ((View) mBindingSparseArray.get(mViewBinding.viewPager.getCurrentItem())).findViewById(R.id.iv_video_cover));
					}
				}
				super.onMapSharedElements(names, sharedElements);
			}
		});
		super.finishAfterTransition();
	}


	public void startShareViewEnterAnimation(final View view) {
		view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				view.getViewTreeObserver().removeOnPreDrawListener(this);
				supportStartPostponedEnterTransition();
				return false;
			}
		});
	}

	private void savePicture() {
		final String url = mPhotoUrls.get(mViewBinding.viewPager.getCurrentItem());
		final String imageName = getPicNameByUrl(url);
		final String path = Tools.getStoragePathByFileName(imageName);
		final File file = new File(path);
		if (file.exists()) {
			showSimpleGreenSnackBar(getString(R.string.snackbar_save_finish) + file.getAbsolutePath());
			return;
		}
		Completable.create(new CompletableOnSubscribe() {
			@Override
			public void subscribe(CompletableEmitter e) throws Exception {
				File cacheFile = TumlodrGlide.with(FullScreenPhotoViewActivity.this)
						.downloadOnly()
						.load(url)
						.onlyRetrieveFromCache(true)
						.submit().get();
				FileUtils.copyFile(cacheFile.getAbsolutePath(), path);
				e.onComplete();
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new SimpleCompletableObserver() {
					@Override
					public void onComplete() {
						DownloadRecordDatabase.insertNewTumblrNormalDownload(url,
								path, null, TasksManagerModel.TYPE_IMAGE);
						showImageDownloadSuccessSnackBar(path);
					}

					@Override
					public void onError(Throwable e) {
						showFileAddedToDownloadSnackBar();
						FileDownloadUtil.downloadPicture(mPhotoUrls.get(mViewBinding.viewPager.getCurrentItem()),
								file, null);
					}
				});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
