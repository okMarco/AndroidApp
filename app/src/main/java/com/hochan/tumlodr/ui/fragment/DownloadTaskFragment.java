package com.hochan.tumlodr.ui.fragment;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.databinding.FragmentDownloadManagerBinding;
import com.hochan.tumlodr.model.data.download.DownloadRecord;
import com.hochan.tumlodr.model.data.download.DownloadRecordAdapter;
import com.hochan.tumlodr.model.data.download.DownloadRecordDatabase;
import com.hochan.tumlodr.module.download.TasksManager;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.prensenter.BaseMvpPresenter;
import com.hochan.tumlodr.tools.ScreenTools;
import com.hochan.tumlodr.ui.activity.GroupDownloadTaskManagerActivity;
import com.hochan.tumlodr.ui.activity.Router;
import com.hochan.tumlodr.ui.adapter.BaseLoadingAdapter;
import com.hochan.tumlodr.ui.component.SingleMediaScanner;
import com.hochan.tumlodr.ui.fragment.base.BaseMvpListFragment;
import com.hochan.tumlodr.util.Events;
import com.hochan.tumlodr.util.FragmentLifecycleProvider;
import com.hochan.tumlodr.util.RxBus;
import com.liulishuo.filedownloader.FileDownloader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.hochan.tumlodr.model.data.download.DownloadRecord.GROUP_GROUP;
import static com.hochan.tumlodr.model.data.download.DownloadRecordDatabase.getGroupDownloadsSync;

/**
 * .
 * Created by hochan on 2017/9/9.
 */

@SuppressWarnings("unused")
public class DownloadTaskFragment extends BaseMvpListFragment implements DownloadRecordAdapter.OnDeleteListener,
		View.OnClickListener, DownloadRecordAdapter.OnItemClickListener {

	public static final String EXTRA_DOWNLOAD_GROUP_NAMES = "group_names";

	public static PagedList<DownloadRecord> sDownloadRecordList;

	public LiveData<PagedList<DownloadRecord>> mDownloadRecordList;

	private DownloadRecordAdapter mDownloadRecordAdapter;

	private int mShareEnterIndex;
	private int mShareExitIndex;
	public List<String> mGroupNames;
	private boolean mSelectAll = false;

	private FragmentDownloadManagerBinding mViewBinding;
	private DeleteDialogFragment mDeleteDialogFragment;
	private Spring mDeleteModeSpring;

	public static DownloadTaskFragment newInstance(String groupName) {
		Bundle args = new Bundle();
		List<String> groupNames = new ArrayList<>();
		groupNames.add(groupName);
		args.putStringArrayList(EXTRA_DOWNLOAD_GROUP_NAMES, (ArrayList<String>) groupNames);
		DownloadTaskFragment fragment = new DownloadTaskFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public int getLayoutRecourseId() {
		return R.layout.fragment_download_manager;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mViewBinding = FragmentDownloadManagerBinding.bind(view);
		mViewBinding.btnCancel.setOnClickListener(this);
		mViewBinding.btnDelete.setOnClickListener(this);
		mViewBinding.btnSelectAll.setOnClickListener(this);

		if (getArguments() != null) {
			mGroupNames = getArguments().getStringArrayList(EXTRA_DOWNLOAD_GROUP_NAMES);
		}
		if (mGroupNames == null || mGroupNames.isEmpty()) {
			mGroupNames = DownloadRecord.GROUP_LIST_NORMAL;
		}
		setUpDataSource();
	}

	@Override
	protected void iniWidget(View view) {
		super.iniWidget(view);
		int photoSize = (ScreenTools.getScreenWidth(getContext()) - ScreenTools.dip2px(getContext(), 2) * 6) / 3;
		int heightCount = getResources().getDisplayMetrics().heightPixels / photoSize;
		mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 3 * heightCount * 2);
		mRecyclerView.setItemViewCacheSize(0);
		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					TumlodrGlide.with(DownloadTaskFragment.this).resumeRequests();
				} else {
					TumlodrGlide.with(DownloadTaskFragment.this).pauseRequests();
				}
			}
		});
		mRecyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
			@Override
			public void onViewRecycled(RecyclerView.ViewHolder holder) {
				try {
					if (getActivity() != null && !getActivity().isFinishing()) {
						TumlodrGlide.with(getActivity())
								.clear(((DownloadRecordAdapter.DownloadRecordViewHolder) holder)
										.mViewBinding.ivThumbnail);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		mDownloadRecordAdapter = new DownloadRecordAdapter((AppCompatActivity) getActivity(), this);
		mDownloadRecordAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mDownloadRecordAdapter);
        //((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //mRecyclerView.setItemAnimator(null);

		mDeleteModeSpring = SpringSystem.create().createSpring();
		mDeleteModeSpring.addListener(new SimpleSpringListener() {
			@Override
			public void onSpringUpdate(Spring spring) {
				mViewBinding.btnDelete.setScaleX((float) spring.getCurrentValue());
				mViewBinding.btnDelete.setScaleY((float) spring.getCurrentValue());
				mViewBinding.btnSelectAll.setScaleX((float) spring.getCurrentValue());
				mViewBinding.btnSelectAll.setScaleY((float) spring.getCurrentValue());
				mViewBinding.btnCancel.setScaleX((float) spring.getCurrentValue());
				mViewBinding.btnCancel.setScaleY((float) spring.getCurrentValue());
			}
		});
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		RxBus.with(new FragmentLifecycleProvider(this)).setEndEvent(FragmentEvent.DESTROY).onNext(new Consumer<Object>() {
			@Override
			public void accept(Object o) {
				if (o instanceof Events && ((Events) o).mCode == Events.EVENT_SHAREELEMENT_ENTER_INDEX_CHANGE) {
					mShareEnterIndex = (int) ((Events) o).mContent;
				}
				if (o instanceof Events && ((Events) o).mCode == Events.EVENT_SHAREELEMENT_EXIT_INDEX_CHANGE) {
					mShareExitIndex = (int) ((Events) o).mContent;
					if (getActivity() != null) {
						getActivity().setExitSharedElementCallback(new SharedElementCallback() {
							@TargetApi(Build.VERSION_CODES.LOLLIPOP)
							@Override
							public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
								if (mShareEnterIndex != mShareExitIndex) {
									DownloadRecordAdapter.DownloadRecordViewHolder taskItemViewHolder =
											((DownloadRecordAdapter.DownloadRecordViewHolder) mRecyclerView
													.findViewHolderForLayoutPosition(mShareExitIndex));
									if (taskItemViewHolder != null) {
										sharedElements.clear();
										sharedElements.put(Router.SHARE_ELEMENT_NAME, taskItemViewHolder.mViewBinding.ivThumbnail);
									}
								}

								if (getActivity() != null) {
									getActivity().setExitSharedElementCallback((android.app.SharedElementCallback) null);
								}
							}

							@Override
							public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
								super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
							}
						});
					}
					int firstVisible = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
					int lastVisible = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
					if (mShareExitIndex < firstVisible || mShareExitIndex > lastVisible) {
						mRecyclerView.scrollToPosition(mShareExitIndex);
					} else {
						DownloadRecordAdapter.DownloadRecordViewHolder taskItemViewHolder =
								(DownloadRecordAdapter.DownloadRecordViewHolder) mRecyclerView
										.findViewHolderForAdapterPosition(mShareExitIndex);
						if (taskItemViewHolder.mViewBinding.ivThumbnail.getDrawable() instanceof GifDrawable) {
							taskItemViewHolder.mViewBinding.ivThumbnail.getDrawable().setVisible(true, true);
							((GifDrawable) taskItemViewHolder.mViewBinding.ivThumbnail.getDrawable()).start();
						}
					}
				}
			}
		}).create();
	}

	public void setUpDataSource() {
		mDownloadRecordList = new LivePagedListBuilder<>(DownloadRecordDatabase.getGroupDownloads(mGroupNames), 20).build();
		setUpDataObserver();
	}

	public void setUpDataObserver() {
		if (getActivity() != null) {
			mDownloadRecordList.removeObservers(getActivity());
			mDownloadRecordList.observe(getActivity(), new Observer<PagedList<DownloadRecord>>() {
				@Override
				public void onChanged(@Nullable PagedList<DownloadRecord> downloadRecords) {
					mDownloadRecordAdapter.submitList(downloadRecords);
				}
			});
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		TasksManager.getImpl().onCreate(new WeakReference<>(this));
	}

	@Override
	public RecyclerView.LayoutManager initRecyclerLayoutManager() {
		return new GridLayoutManager(getContext(), 3);
	}

	@Override
	public BaseLoadingAdapter initAdapter() {
		return null;
	}

	@Override
	public void setRecyclerViewDivider() {

	}

	public void fileDownloadConnected() {
		if (mDownloadRecordAdapter == null || getActivity() == null) {
			return;
		}
		for (int i = 0; i < mDownloadRecordAdapter.getItemCount(); i++) {
			if (mDownloadRecordAdapter.getCurrentList() == null) {
				return;
			}
			DownloadRecord downloadRecord = mDownloadRecordAdapter.getCurrentList().get(i);
			if (downloadRecord != null && new File(downloadRecord.getPath()).exists()) {
				try {
					mDownloadRecordAdapter.notifyItemChanged(i);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public BaseMvpPresenter initPresenter() {
		return null;
	}

	@Override
	public void onDestroy() {
		TasksManager.getImpl().onDestroy();
		mDownloadRecordList.removeObservers(this);
		mDownloadRecordList = null;
		sDownloadRecordList = null;
		super.onDestroy();
	}

	@Override
	public void onRefresh(RefreshLayout refreshLayout) {
		showAll();
		refreshLayout.finishRefresh();
	}

	public void showAll() {
		setUpDataSource();
	}

	public void showOnlyPic() {
		mDownloadRecordList = new LivePagedListBuilder<>(DownloadRecordDatabase.getGroupImageDownloads(mGroupNames), 20).build();
		setUpDataObserver();
	}

	public void showOnlyVideo() {
		mDownloadRecordList = new LivePagedListBuilder<>(DownloadRecordDatabase.getGroupVideoDownloads(mGroupNames), 20).build();
		setUpDataObserver();
	}

	public void showUnFinish() {
		mDownloadRecordList = new LivePagedListBuilder<>(DownloadRecordDatabase.getGroupUnFinishDownloads(mGroupNames), 20).build();
		setUpDataObserver();
	}

	public void getGroupDownloads() {
		List<String> groups = new ArrayList<>();
		groups.add(GROUP_GROUP);
		mDownloadRecordList = new LivePagedListBuilder<>(DownloadRecordDatabase.getGroupDownloads(groups), 20).build();
		setUpDataObserver();
	}

	public DownloadRecordAdapter getAdapter() {
		return mDownloadRecordAdapter;
	}

	public void selectAll() {
		mDownloadRecordAdapter.selectAll();
	}

	@Override
	public void onDeleteModeChange(boolean deleteMode) {
		if (deleteMode) {
			mDeleteModeSpring.setCurrentValue(0);
			mDeleteModeSpring.setEndValue(1);
		} else {
			mDeleteModeSpring.setCurrentValue(1);
			mDeleteModeSpring.setEndValue(0);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == mViewBinding.btnDelete.getId() && getActivity() != null) {
			if (mDeleteDialogFragment == null) {
				mDeleteDialogFragment = new DeleteDialogFragment();
				mDeleteDialogFragment.setDownloadTaskDeleteListener(new DeleteDialogFragment.OnDownloadTaskDeleteListener() {
					@Override
					@SuppressWarnings("all")
					public void delete(final boolean deleteFile) {
					    commitDelete(deleteFile);
					}
				});
			}
			mDeleteDialogFragment.show(getActivity().getSupportFragmentManager(), "");
		} else if (v.getId() == mViewBinding.btnSelectAll.getId()) {
			if (mSelectAll) {
				mDownloadRecordAdapter.cancelSelectAll();
				mSelectAll = false;
				mViewBinding.btnSelectAll.setText(R.string.select_all);
			} else {
				mDownloadRecordAdapter.selectAll();
				mSelectAll = true;
				mViewBinding.btnSelectAll.setText(R.string.cancle_select_all);
			}
		} else if (v.getId() == mViewBinding.btnCancel.getId()) {
			mDownloadRecordAdapter.cancelDeleteMode();
            mSelectAll = false;
            mViewBinding.btnSelectAll.setText(R.string.select_all);
			onDeleteModeChange(false);
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    private void commitDelete(final boolean deleteFile) {
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) {
                SparseBooleanArray mSparseBooleanArray = mDownloadRecordAdapter.getSparseBooleanArray();
                List<DownloadRecord> deleteTasks = new ArrayList<>();
                for (int i = 0; i < mSparseBooleanArray.size(); i++) {
                    if (mSparseBooleanArray.valueAt(i) && mDownloadRecordAdapter.getCurrentList() != null) {
                        int position = mSparseBooleanArray.keyAt(i);
                        if (position < 0 || position >= mDownloadRecordAdapter.getCurrentList().size()) {
                            continue;
                        }
                        DownloadRecord downloadRecord = mDownloadRecordAdapter.getCurrentList().get(position);
                        if (downloadRecord != null) {
                            deleteTasks.add(mDownloadRecordAdapter.getCurrentList().get(position));
                            FileDownloader.getImpl().clear(downloadRecord.getId(), downloadRecord.getPath());
                            if (deleteFile) {
                                File file = new File(downloadRecord.getPath());
                                if (downloadRecord.getGroupName().equals(GROUP_GROUP)) {
                                    List<DownloadRecord> groupTasks = getGroupDownloadsSync(Collections.singletonList(downloadRecord.getType()));
                                    for (DownloadRecord tmpRecord : groupTasks) {
                                        FileDownloader.getImpl().clear(tmpRecord.getId(), tmpRecord.getPath());
                                        new File(tmpRecord.getGroupName()).delete();
                                        new SingleMediaScanner(tmpRecord.getPath());
                                    }
                                    deleteTasks.addAll(groupTasks);
                                    //noinspection ResultOfMethodCallIgnored
                                    file.delete();
                                }else {
                                    //noinspection ResultOfMethodCallIgnored
                                    file.delete();
                                }
                                new SingleMediaScanner(file.getAbsolutePath());
                            }
                        }
                        e.onComplete();
                    }
                }
                DownloadRecordDatabase.deleteDownloadRecords(deleteTasks);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
            @Override
            public void run() {
                mDownloadRecordAdapter.cancelDeleteMode();
                onDeleteModeChange(false);
            }
        });
    }


	@Override
	public void onShowGroupDownload(String groupName) {
		GroupDownloadTaskManagerActivity.showGroup(getActivity(), groupName);
	}

	@Override
	public void onShowFullView(int position, View shareView) {
		sDownloadRecordList = mDownloadRecordList.getValue();
		Router.showDownloadFile(getActivity(), position, shareView);
	}
}
