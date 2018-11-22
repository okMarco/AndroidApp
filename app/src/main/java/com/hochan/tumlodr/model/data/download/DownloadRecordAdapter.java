package com.hochan.tumlodr.model.data.download;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.databinding.ItemDownloadRecordBinding;
import com.hochan.tumlodr.model.data.TasksManagerModel;
import com.hochan.tumlodr.module.download.TasksManager;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.module.glide.TumlodrGlideUtil;
import com.hochan.tumlodr.tools.AppConfig;
import com.hochan.tumlodr.tools.ScreenTools;
import com.hochan.tumlodr.util.Events;
import com.hochan.tumlodr.util.RxBus;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadList;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.lang.ref.WeakReference;

import static com.hochan.tumlodr.model.data.download.DownloadRecord.TYPE_VIDEO;

/**
 * .
 * Created by hochan on 2018/3/3.
 */

public class DownloadRecordAdapter extends PagedListAdapter<DownloadRecord, DownloadRecordAdapter.DownloadRecordViewHolder> {

	private int mPhotoSize;
	private boolean mIsDeleteMode = false;
	private SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
	private OnDeleteListener mOnDeleteListener;
	private OnItemClickListener mOnItemClickListener;

	public DownloadRecordAdapter(AppCompatActivity activity, OnDeleteListener onDeleteListener) {
		super(DIFF_CALLBACK);
		mPhotoSize = (ScreenTools.getScreenWidth(activity) - ScreenTools.dip2px(activity, 2) * 6) / 3;
		mOnDeleteListener = onDeleteListener;
	}

	@Override
	public DownloadRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new DownloadRecordViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download_record, parent, false));
	}

	public void cancelDeleteMode() {
		mIsDeleteMode = false;
		mSparseBooleanArray.clear();
		notifyDataSetChanged();
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}

	public SparseBooleanArray getSparseBooleanArray() {
		return mSparseBooleanArray;
	}

	@Override
	public void onBindViewHolder(DownloadRecordViewHolder holder, int position) {
		DownloadRecord downloadRecord = getItem(position);
		if (downloadRecord != null) {
			holder.bindToDownloadRecord(downloadRecord);
			holder.setId(downloadRecord.getId());
		} else {
			holder.clear();
		}
	}

	private static final DiffCallback<DownloadRecord> DIFF_CALLBACK = new DiffCallback<DownloadRecord>() {
		@Override
		public boolean areItemsTheSame(@NonNull DownloadRecord oldRecord, @NonNull DownloadRecord newRecord) {
			return oldRecord.getId() == newRecord.getId();
		}

		@Override
		public boolean areContentsTheSame(@NonNull DownloadRecord oldRecord, @NonNull DownloadRecord newRecord) {
			return TextUtils.equals(oldRecord.getUrl(), newRecord.getUrl()) &&
					TextUtils.equals(oldRecord.getPath(), newRecord.getPath());
		}
	};

	public class DownloadRecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

		public ItemDownloadRecordBinding mViewBinding;
		private int mId;

		private FileDownloadListener mTaskDownloadListener = new FileDownloadSampleListener() {

			private DownloadRecordViewHolder checkCurrentHolder(final BaseDownloadTask task) {
				if (task.getTag() != null && task.getTag() instanceof WeakReference && ((WeakReference) task.getTag()).get() != null) {
					final DownloadRecordViewHolder viewHolder = (DownloadRecordViewHolder) ((WeakReference) task.getTag()).get();
					if (viewHolder != null && viewHolder.mId == task.getId()) {
						return viewHolder;
					}
				}
				return null;
			}

			@Override
			protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
				super.pending(task, soFarBytes, totalBytes);
				final DownloadRecordViewHolder viewHolder = checkCurrentHolder(task);
				if (viewHolder != null) {
					viewHolder.updateDownloading(FileDownloadStatus.pending, soFarBytes, totalBytes, 0);
					viewHolder.mViewBinding.tvTaskStatus.setText(R.string.tasks_manager_demo_status_pending);
				}
			}

			@Override
			protected void started(BaseDownloadTask task) {
				super.started(task);
				final DownloadRecordViewHolder tag = checkCurrentHolder(task);
				if (tag == null) {
					return;
				}
				tag.mViewBinding.tvTaskStatus.setText(R.string.tasks_manager_demo_status_started);
			}

			@Override
			protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
				super.connected(task, etag, isContinue, soFarBytes, totalBytes);
				final DownloadRecordViewHolder viewHolder = checkCurrentHolder(task);
				if (viewHolder == null) {
					return;
				}
				viewHolder.updateDownloading(FileDownloadStatus.connected, soFarBytes, totalBytes, 0);
				viewHolder.mViewBinding.tvTaskStatus.setText(R.string.tasks_manager_demo_status_connected);
			}

			@Override
			protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
				super.progress(task, soFarBytes, totalBytes);
				final DownloadRecordViewHolder viewHolder = checkCurrentHolder(task);
				if (viewHolder != null) {
					viewHolder.updateDownloading(FileDownloadStatus.progress, soFarBytes, totalBytes, task.getSpeed());
				}
			}

			@Override
			protected void error(BaseDownloadTask task, Throwable e) {
				super.error(task, e);
				e.printStackTrace();
				final DownloadRecordViewHolder viewHolder = checkCurrentHolder(task);
				if (viewHolder != null) {
					viewHolder.updateNotDownloaded(FileDownloadStatus.error, e.getMessage());
					TasksManager.getImpl().removeTaskForViewHolder(task.getId());

					RxBus.getInstance().send(new Events<>(Events.EVENT_CODE_DOWNLOAD_FINISH, task));
				}
			}

			@Override
			protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
				super.paused(task, soFarBytes, totalBytes);
				final DownloadRecordViewHolder viewHolder = checkCurrentHolder(task);
				if (viewHolder != null) {
					viewHolder.updateNotDownloaded(FileDownloadStatus.paused, null);
					viewHolder.mViewBinding.tvTaskStatus.setText(R.string.tasks_manager_demo_status_paused);
					TasksManager.getImpl().removeTaskForViewHolder(task.getId());
				}
			}

			@Override
			protected void completed(BaseDownloadTask task) {
				super.completed(task);
				final DownloadRecordViewHolder viewHolder = checkCurrentHolder(task);
				if (viewHolder != null && viewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
					DownloadRecord downloadRecord = DownloadRecordAdapter.this.getItem(viewHolder.getAdapterPosition());
					if (downloadRecord != null) {
						viewHolder.updateDownloaded(TasksManagerModel.TYPE_VIDEO.equals(downloadRecord.getType()));
						TasksManager.getImpl().removeTaskForViewHolder(task.getId());
						if (new File(task.getPath()).exists()) {
							RxBus.getInstance().send(new Events<>(Events.EVENT_CODE_DOWNLOAD_FINISH, task));
							DownloadRecordDatabase.updateDownloadFinish(task.getId());
						}
						if (TumlodrGlideUtil.isContextValid(viewHolder.mViewBinding.ivThumbnail)) {
							TumlodrGlide.with(viewHolder.mViewBinding.ivThumbnail)
									.load(downloadRecord.getPath())
									.skipMemoryCache(true)
									.into(viewHolder.mViewBinding.ivThumbnail);
						}
					}
				}
			}
		};

		DownloadRecordViewHolder(View itemView) {
			super(itemView);
			mViewBinding = ItemDownloadRecordBinding.bind(itemView);
			mViewBinding.btnTaskAction.setOnClickListener(this);
			mViewBinding.btnTaskAction.setOnLongClickListener(this);
		}

		public void setId(int id) {
			mId = id;
		}

		void bindToDownloadRecord(DownloadRecord downloadRecord) {
			if (mViewBinding.ivThumbnail.getMeasuredWidth() != mPhotoSize || mViewBinding.ivThumbnail.getMeasuredHeight() != mPhotoSize) {
				FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mViewBinding.ivThumbnail.getLayoutParams();
				layoutParams.width = mPhotoSize;
				layoutParams.height = mPhotoSize;
			}
			if (downloadRecord.getType().equals(DownloadRecord.TYPE_VIDEO)) {
				mViewBinding.ivPlayIcon.setVisibility(View.VISIBLE);
			} else {
				mViewBinding.ivPlayIcon.setVisibility(View.INVISIBLE);
			}

			if (mIsDeleteMode && mSparseBooleanArray.get(getAdapterPosition(), false)) {
				mViewBinding.vSelected.setVisibility(View.VISIBLE);
			} else {
				mViewBinding.vSelected.setVisibility(View.INVISIBLE);
			}

			if (DownloadRecord.GROUP_GROUP.equals(downloadRecord.getGroupName())) {
				mViewBinding.tvGroupName.setVisibility(View.VISIBLE);
				mViewBinding.tvGroupName.setText(downloadRecord.getType().substring(downloadRecord.getType().indexOf("_") + 1,
						downloadRecord.getType().length()));
				if (!new File(downloadRecord.getPath()).exists()) {
					TumlodrGlide.with(mViewBinding.ivThumbnail)
							.load(downloadRecord.getUrl())
							.skipMemoryCache(true)
							.into(mViewBinding.ivThumbnail);
				} else {
					TumlodrGlide.with(mViewBinding.ivThumbnail)
							.load(downloadRecord.getPath())
							.skipMemoryCache(true)
							.into(mViewBinding.ivThumbnail);
				}
				mViewBinding.tvTaskStatus.setVisibility(View.INVISIBLE);
				mViewBinding.vTaskMask.setVisibility(View.INVISIBLE);
				mViewBinding.btnTaskAction.setEnabled(true);
				mViewBinding.btnTaskAction.setText(R.string.play);
			} else {
				mViewBinding.tvGroupName.setVisibility(View.INVISIBLE);
			}

			if (new File(downloadRecord.getPath()).exists()) {
				TumlodrGlide.with(mViewBinding.ivThumbnail)
						.load(downloadRecord.getPath())
						.skipMemoryCache(true)
						.into(mViewBinding.ivThumbnail);
				mViewBinding.vTaskMask.setVisibility(View.INVISIBLE);
				mViewBinding.tvTaskStatus.setVisibility(View.INVISIBLE);
				mViewBinding.btnTaskAction.setEnabled(true);
				mViewBinding.btnTaskAction.setText(R.string.play);
			} else {
				if (downloadRecord.getType().equals(TYPE_VIDEO)) {
					TumlodrGlide.with(mViewBinding.ivThumbnail)
							.load(downloadRecord.getThumbnail())
							.skipMemoryCache(true)
							.into(mViewBinding.ivThumbnail);
				}
				mViewBinding.vTaskMask.setVisibility(View.INVISIBLE);
				mViewBinding.ivPlayIcon.setVisibility(View.INVISIBLE);

				if (TasksManager.getImpl().isReady()) {
					try {
						if (FileDownloadList.getImpl().get(downloadRecord.getId()) != null) {
							FileDownloadList.getImpl()
									.get(downloadRecord.getId())
									.getOrigin()
									.setTag(new WeakReference<>(this))
									.setListener(mTaskDownloadListener);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					final int status = TasksManager.getImpl().getStatus(downloadRecord.getId(), downloadRecord.getPath());
					if (status == FileDownloadStatus.pending || status == FileDownloadStatus.started ||
							status == FileDownloadStatus.connected) {
						updateDownloading(status, TasksManager.getImpl().getSoFar(downloadRecord.getId()),
								TasksManager.getImpl().getTotal(downloadRecord.getId()), 0);
					} else if (!new File(downloadRecord.getPath()).exists() &&
							!new File(FileDownloadUtils.getTempPath(downloadRecord.getPath())).exists()) {
						updateNotDownloaded(status, null);
					} else if (TasksManager.getImpl().isDownloaded(status)) {
						updateDownloaded(TasksManagerModel.TYPE_VIDEO.equals(downloadRecord.getType()));
					} else if (status == FileDownloadStatus.progress) {
						updateDownloading(status, TasksManager.getImpl().getSoFar(downloadRecord.getId())
								, TasksManager.getImpl().getTotal(downloadRecord.getId()), 0);
					} else {
						updateNotDownloaded(status, null);
					}
				} else {
					mViewBinding.tvTaskStatus.setText(R.string.tasks_manager_demo_status_loading);
					mViewBinding.btnTaskAction.setEnabled(false);
				}
			}
		}

		void updateNotDownloaded(final int status, String msg) {
			mViewBinding.vTaskMask.setVisibility(View.VISIBLE);
			mViewBinding.tvTaskStatus.setVisibility(View.VISIBLE);
			mViewBinding.btnTaskAction.setText(R.string.start);
			mViewBinding.ivPlayIcon.setVisibility(View.INVISIBLE);

			switch (status) {
				case FileDownloadStatus.error:
					mViewBinding.tvTaskStatus.setText(R.string.tasks_manager_demo_status_error);
					mViewBinding.tvTaskStatus.append(!TextUtils.isEmpty(msg) ? ":" + msg : "");
					mViewBinding.btnTaskAction.setText(R.string.pause);
					break;
				case FileDownloadStatus.paused:
					mViewBinding.tvTaskStatus.setText(R.string.tasks_manager_demo_status_paused);
					break;
				default:
					mViewBinding.tvTaskStatus.setText(R.string.tasks_manager_demo_status_not_downloaded);
					break;
			}
		}

		void updateDownloading(final int status, final long sofar, final long total, int speed) {
			final float percent = sofar / (float) total;
			mViewBinding.vTaskMask.setVisibility(View.VISIBLE);
			mViewBinding.tvTaskStatus.setVisibility(View.VISIBLE);

			switch (status) {
				case FileDownloadStatus.pending:
					mViewBinding.tvTaskStatus.setText(R.string.tasks_manager_demo_status_pending);
					break;
				case FileDownloadStatus.started:
					mViewBinding.tvTaskStatus.setText(R.string.tasks_manager_demo_status_started);
					break;
				case FileDownloadStatus.connected:
					mViewBinding.tvTaskStatus.setText(R.string.tasks_manager_demo_status_connected);
					break;
				case FileDownloadStatus.progress:
					mViewBinding.tvTaskStatus.setText(R.string.tasks_manager_demo_status_progress);
					mViewBinding.tvTaskStatus.append("" + (int) (percent * 100) + "%");
					if (speed > 0) {
						mViewBinding.tvTaskStatus.append("\n" + speed + "kb/s");
					}
					break;
				default:
					mViewBinding.tvTaskStatus.setText(TumlodrApp.mContext.getString(R.string.tasks_manager_demo_status_downloading, status));
					break;
			}

			mViewBinding.btnTaskAction.setText(R.string.pause);
			mViewBinding.ivPlayIcon.setVisibility(View.INVISIBLE);
		}

		void updateDownloaded(boolean isVideo) {
			mViewBinding.vTaskMask.setVisibility(View.INVISIBLE);
			mViewBinding.tvTaskName.setVisibility(View.GONE);
			mViewBinding.tvTaskStatus.setVisibility(View.INVISIBLE);

			mViewBinding.tvTaskStatus.setText(R.string.tasks_manager_demo_status_completed);
			mViewBinding.btnTaskAction.setText(R.string.play);
			if (isVideo) {
				mViewBinding.ivPlayIcon.setVisibility(View.VISIBLE);
			} else {
				mViewBinding.ivPlayIcon.setVisibility(View.INVISIBLE);
			}
		}

		public void clear() {

		}

		@Override
		@SuppressWarnings("all")
		public void onClick(View v) {
			if (getAdapterPosition() == RecyclerView.NO_POSITION) {
				return;
			}
			if (mIsDeleteMode) {
				if (mSparseBooleanArray.get(getAdapterPosition(), false)) {
					mViewBinding.vSelected.setVisibility(View.INVISIBLE);
					mSparseBooleanArray.delete(getAdapterPosition());
					if (mSparseBooleanArray.size() == 0) {
						if (mOnDeleteListener != null) {
							mOnDeleteListener.onDeleteModeChange(false);
						}
						mIsDeleteMode = false;
					}
				} else {
					mViewBinding.vSelected.setVisibility(View.VISIBLE);
					mSparseBooleanArray.append(getAdapterPosition(), true);
				}
				return;
			}

			if (DownloadRecord.GROUP_GROUP.equals(getItem(getAdapterPosition()).getGroupName())) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onShowGroupDownload(getItem(getAdapterPosition()).getType());
				}
				return;
			}

			CharSequence action = ((TextView) v).getText();
			DownloadRecord downloadRecord = getItem(getAdapterPosition());
			if (new File(downloadRecord.getPath()).exists() || action.equals(v.getResources().getString(R.string.play))) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onShowFullView(getAdapterPosition(), mViewBinding.ivThumbnail);
				}
			} else if (action.equals(v.getResources().getString(R.string.pause))) {
				FileDownloader.getImpl().pause(mId);
				updateNotDownloaded(FileDownloadStatus.paused, null);
				mViewBinding.tvTaskStatus.setText(R.string.tasks_manager_demo_status_paused);
			} else if (action.equals(v.getResources().getString(R.string.start))) {
				final DownloadRecord model = getItem(getAdapterPosition());
				if (model != null) {
					BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
							.setPath(model.getPath())
							.setCallbackProgressTimes(100)
							.addFinishListener(AppConfig.mDownloadFinishListener)
							.setListener(mTaskDownloadListener);
					task.setTag(new WeakReference<DownloadRecordViewHolder>(this));
					task.start();
				}
			}
		}

		@Override
		public boolean onLongClick(View v) {
			mIsDeleteMode = true;
			mViewBinding.vSelected.setVisibility(View.VISIBLE);
			mSparseBooleanArray.put(getAdapterPosition(), true);
			if (mOnDeleteListener != null) {
				mOnDeleteListener.onDeleteModeChange(true);
			}
			return true;
		}
	}

	public void selectAll() {
		for (int i = 0; i < getItemCount(); i++) {
			if (getItem(i) != null) {
				mSparseBooleanArray.append(i, true);
				notifyDataSetChanged();
			}
		}
	}

	public void cancelSelectAll() {
		mSparseBooleanArray.clear();
		notifyDataSetChanged();
	}

	public interface OnDeleteListener {
		void onDeleteModeChange(boolean deleteMode);
	}

	public interface OnItemClickListener {
		void onShowGroupDownload(String groupName);

		void onShowFullView(int position, View shareView);
	}
}
