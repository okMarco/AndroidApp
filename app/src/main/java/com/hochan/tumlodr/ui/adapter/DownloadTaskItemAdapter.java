package com.hochan.tumlodr.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.hochan.tumlodr.R;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.model.data.TasksManagerModel;
import com.hochan.tumlodr.model.sql.DownloadTasksDBController;
import com.hochan.tumlodr.module.download.TasksManager;
import com.hochan.tumlodr.module.glide.TumlodrGlide;
import com.hochan.tumlodr.tools.ScreenTools;
import com.hochan.tumlodr.ui.activity.DownloadTasksManagerActivity;
import com.hochan.tumlodr.ui.activity.Router;
import com.hochan.tumlodr.ui.fragment.DeleteDialogFragment;
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
import java.util.ArrayList;
import java.util.List;

/**
 * .
 * Created by hochan on 2017/8/27.
 */

@SuppressWarnings("SuspiciousNameCombination")
public class DownloadTaskItemAdapter extends RecyclerView.Adapter<DownloadTaskItemAdapter.TaskItemViewHolder> implements ListPreloader.PreloadModelProvider<String> {

	private List<TasksManagerModel> mDownloadModelList = new ArrayList<>();
	private DownloadTasksManagerActivity mDownloadTasksManagerActivity;
	private boolean mIsDeleteMode = false;
	private SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
	private DeleteDialogFragment mDeleteDialogFragment;
	private int mPhotoSize;

	public DownloadTaskItemAdapter(DownloadTasksManagerActivity activity) {
		mDownloadTasksManagerActivity = activity;
		mPhotoSize = (ScreenTools.getScreenWidth(activity) - ScreenTools.dip2px(activity, 2) * 6) / 3;
	}

	public void setData(List<TasksManagerModel> tasksManagerModels) {
		mDownloadModelList = tasksManagerModels;
		notifyDataSetChanged();
	}

	public void cancelDeleteMode() {
		mIsDeleteMode = false;
		mSparseBooleanArray.clear();
		notifyDataSetChanged();
	}

	public void deDelete() {
		if (mDeleteDialogFragment == null) {
			mDeleteDialogFragment = new DeleteDialogFragment();
			mDeleteDialogFragment.setDownloadTaskDeleteListener(new DeleteDialogFragment.OnDownloadTaskDeleteListener() {
				@Override
				public void delete(boolean deleteFile) {
					List<TasksManagerModel> deleteTasks = new ArrayList<>();
					for (int i = 0; i < mSparseBooleanArray.size(); i++) {
						if (mSparseBooleanArray.valueAt(i)) {
							int position = mSparseBooleanArray.keyAt(i);
							deleteTasks.add(mDownloadModelList.get(position));
						}
					}
					DownloadTasksDBController downloadTasksDBController = new DownloadTasksDBController();
					downloadTasksDBController.deleteTask(deleteTasks, deleteFile);
					mDownloadModelList.removeAll(deleteTasks);
					cancelDeleteMode();
				}
			});
		}
		mDeleteDialogFragment.show(mDownloadTasksManagerActivity.getSupportFragmentManager(), "");
	}

	private FileDownloadListener mTaskDownloadListener = new FileDownloadSampleListener() {

		private TaskItemViewHolder checkCurrentHolder(final BaseDownloadTask task) {
			final TaskItemViewHolder tag = (TaskItemViewHolder) task.getTag();
			if (tag == null) {
				return null;
			}

			if (tag.id != task.getId()) {
				return null;
			}

			return tag;
		}

		@Override
		protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
			super.pending(task, soFarBytes, totalBytes);
			final TaskItemViewHolder tag = checkCurrentHolder(task);
			if (tag == null) {
				return;
			}

			tag.updateDownloading(FileDownloadStatus.pending, soFarBytes, totalBytes, 0);
			tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_pending);
		}

		@Override
		protected void started(BaseDownloadTask task) {
			super.started(task);
			final TaskItemViewHolder tag = checkCurrentHolder(task);
			if (tag == null) {
				return;
			}

			tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_started);
		}

		@Override
		protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
			super.connected(task, etag, isContinue, soFarBytes, totalBytes);
			final TaskItemViewHolder tag = checkCurrentHolder(task);
			if (tag == null) {
				return;
			}

			tag.updateDownloading(FileDownloadStatus.connected, soFarBytes, totalBytes, 0);
			tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_connected);
		}

		@Override
		protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
			super.progress(task, soFarBytes, totalBytes);
			final TaskItemViewHolder tag = checkCurrentHolder(task);
			if (tag == null) {
				return;
			}

			tag.updateDownloading(FileDownloadStatus.progress, soFarBytes, totalBytes, task.getSpeed());
		}

		@Override
		protected void error(BaseDownloadTask task, Throwable e) {
			super.error(task, e);
			e.printStackTrace();
			final TaskItemViewHolder tag = checkCurrentHolder(task);
			if (tag == null) {
				return;
			}

			tag.updateNotDownloaded(FileDownloadStatus.error, task.getLargeFileSoFarBytes()
					, task.getLargeFileTotalBytes(), e.getMessage());
			TasksManager.getImpl().removeTaskForViewHolder(task.getId());

			RxBus.getInstance().send(new Events<>(Events.EVENT_CODE_DOWNLOAD_FINISH, task));
		}

		@Override
		protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
			super.paused(task, soFarBytes, totalBytes);
			final TaskItemViewHolder tag = checkCurrentHolder(task);
			if (tag == null) {
				return;
			}

			tag.updateNotDownloaded(FileDownloadStatus.paused, soFarBytes, totalBytes, null);
			tag.taskStatusTv.setText(R.string.tasks_manager_demo_status_paused);
			TasksManager.getImpl().removeTaskForViewHolder(task.getId());
		}

		@Override
		protected void completed(BaseDownloadTask task) {
			super.completed(task);
			final TaskItemViewHolder tag = checkCurrentHolder(task);
			if (tag == null || tag.getAdapterPosition() < 0) {
				return;
			}
			TasksManagerModel model = mDownloadModelList.get(tag.getAdapterPosition());
			if (model == null) {
				return;
			}
			tag.updateDownloaded(TasksManagerModel.TYPE_VIDEO.equals(model.getType()), task.getPath());
			TasksManager.getImpl().removeTaskForViewHolder(task.getId());

			RxBus.getInstance().send(new Events<>(Events.EVENT_CODE_DOWNLOAD_FINISH, task));
		}
	};

	@Override
	public TaskItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new TaskItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tasks_manager, parent, false));
	}

	@Override
	public void onBindViewHolder(TaskItemViewHolder holder, int position) {
		final TasksManagerModel model = mDownloadModelList.get(position);
		if (model == null) {
			return;
		}

		holder.update(model.getId(), position);
		holder.taskActionBtn.setTag(holder);
		holder.taskNameTv.setText(model.getName());
		holder.maskV.setVisibility(View.INVISIBLE);

		if (mIsDeleteMode) {
			if (mSparseBooleanArray.get(position, false)) {
				holder.vSelected.setVisibility(View.VISIBLE);
			} else {
				holder.vSelected.setVisibility(View.INVISIBLE);
			}
		} else {
			holder.vSelected.setVisibility(View.INVISIBLE);
		}


		holder.thumbnailIv.setLayoutParams(new FrameLayout.LayoutParams(mPhotoSize, mPhotoSize));

		File file = new File(model.getPath());
		if (file.exists()) {
			TumlodrGlide.with(mDownloadTasksManagerActivity).load(model.getPath())
					.override(mPhotoSize, mPhotoSize)
					.skipMemoryCache(true)
					.into(holder.thumbnailIv).clearOnDetach();
		} else if (TasksManagerModel.TYPE_VIDEO.equals(model.getType())) {
			TumlodrGlide.with(mDownloadTasksManagerActivity)
					.load(model.getThumbnail())
					.skipMemoryCache(true)
					.override(mPhotoSize, mPhotoSize)
					.into(holder.thumbnailIv)
					.clearOnDetach();
		}

		TasksManager.getImpl().updateViewHolder(holder.id, holder);

		holder.taskActionBtn.setEnabled(true);

		if (TasksManager.getImpl().isReady()) {
			try {
				FileDownloadList.getImpl().get(model.getId()).getOrigin().setTag(holder).setListener(mTaskDownloadListener);
			} catch (Exception e) {
				e.printStackTrace();
			}
			final int status = TasksManager.getImpl().getStatus(model.getId(), model.getPath());
			if (status == FileDownloadStatus.pending || status == FileDownloadStatus.started ||
					status == FileDownloadStatus.connected) {
				holder.updateDownloading(status, TasksManager.getImpl().getSoFar(model.getId())
						, TasksManager.getImpl().getTotal(model.getId()), 0);
			} else if (!new File(model.getPath()).exists() &&
					!new File(FileDownloadUtils.getTempPath(model.getPath())).exists()) {
				holder.updateNotDownloaded(status, 0, 0, null);
			} else if (TasksManager.getImpl().isDownloaded(status)) {
				holder.updateDownloaded(TasksManagerModel.TYPE_VIDEO.equals(model.getType()), model.getPath());
			} else if (status == FileDownloadStatus.progress) {
				holder.updateDownloading(status, TasksManager.getImpl().getSoFar(model.getId())
						, TasksManager.getImpl().getTotal(model.getId()), 0);
			} else {
				holder.updateNotDownloaded(status, TasksManager.getImpl().getSoFar(model.getId())
						, TasksManager.getImpl().getTotal(model.getId()), null);
			}
		} else {
			holder.taskStatusTv.setText(R.string.tasks_manager_demo_status_loading);
			holder.taskActionBtn.setEnabled(false);
		}
	}

	@Override
	public int getItemCount() {
		return mDownloadModelList.size();
	}

	public void selectAll() {
		for (int i = 0; i < mDownloadModelList.size(); i++) {
			mSparseBooleanArray.append(i, true);
			notifyDataSetChanged();
		}
	}

	@NonNull
	@Override
	public List<String> getPreloadItems(int position) {
		List<String> preloadItems = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			if (position + i >= mDownloadModelList.size()) {
				break;
			}
			preloadItems.add(mDownloadModelList.get(position + i).getPath());
		}

		return preloadItems;
	}

	@Nullable
	@Override
	public RequestBuilder<?> getPreloadRequestBuilder(String item) {
		return TumlodrGlide.with(mDownloadTasksManagerActivity)
				.load(item).override(mPhotoSize, mPhotoSize)
				.skipMemoryCache(true);
	}

	public class TaskItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		public int position;
		public int id;

		TextView taskNameTv;
		TextView taskStatusTv;
		View maskV;
		Button taskActionBtn;
		public ImageView thumbnailIv;
		ImageView videoPlayIv;
		FrameLayout flTaskRoot;
		View vSelected;

		public TaskItemViewHolder(View itemView) {
			super(itemView);
			assignViews();
		}

		private View findViewById(final int id) {
			return itemView.findViewById(id);
		}

		void update(final int id, final int position) {
			this.id = id;
			this.position = position;
		}

		void updateDownloaded(boolean isVideo, String path) {
			maskV.setVisibility(View.INVISIBLE);
			taskNameTv.setVisibility(View.GONE);
			taskStatusTv.setVisibility(View.INVISIBLE);

			taskStatusTv.setText(R.string.tasks_manager_demo_status_completed);
			taskActionBtn.setText(R.string.play);
			if (isVideo) {
				videoPlayIv.setVisibility(View.VISIBLE);
			} else {
				videoPlayIv.setVisibility(View.INVISIBLE);
			}
		}

		void updateNotDownloaded(final int status, final long sofar, final long total, String msg) {
			maskV.setVisibility(View.VISIBLE);
			taskStatusTv.setVisibility(View.VISIBLE);
			taskActionBtn.setText(R.string.start);
			videoPlayIv.setVisibility(View.INVISIBLE);

			switch (status) {
				case FileDownloadStatus.error:
					taskStatusTv.setText(R.string.tasks_manager_demo_status_error);
					taskStatusTv.append(!TextUtils.isEmpty(msg) ? ":" + msg : "");
					taskActionBtn.setText(R.string.pause);

					break;
				case FileDownloadStatus.paused:
					taskStatusTv.setText(R.string.tasks_manager_demo_status_paused);
					break;
				default:
					taskStatusTv.setText(R.string.tasks_manager_demo_status_not_downloaded);
					break;
			}
		}

		void updateDownloading(final int status, final long sofar, final long total, int speed) {
			final float percent = sofar / (float) total;
			maskV.setVisibility(View.VISIBLE);
			taskStatusTv.setVisibility(View.VISIBLE);

			switch (status) {
				case FileDownloadStatus.pending:
					taskStatusTv.setText(R.string.tasks_manager_demo_status_pending);
					break;
				case FileDownloadStatus.started:
					taskStatusTv.setText(R.string.tasks_manager_demo_status_started);
					break;
				case FileDownloadStatus.connected:
					taskStatusTv.setText(R.string.tasks_manager_demo_status_connected);
					break;
				case FileDownloadStatus.progress:
					taskStatusTv.setText(R.string.tasks_manager_demo_status_progress);
					taskStatusTv.append("" + (int) (percent * 100) + "%");
					if (speed > 0) {
						taskStatusTv.append("\n" + speed + "kb/s");
					}
					break;
				default:
					taskStatusTv.setText(TumlodrApp.mContext.getString(
							R.string.tasks_manager_demo_status_downloading, status));
					break;
			}

			taskActionBtn.setText(R.string.pause);
			videoPlayIv.setVisibility(View.INVISIBLE);
		}

		private void assignViews() {
			taskNameTv = (TextView) findViewById(R.id.task_name_tv);
			taskStatusTv = (TextView) findViewById(R.id.task_status_tv);
			maskV = findViewById(R.id.task_pb);
			taskActionBtn = (Button) findViewById(R.id.task_action_btn);
			thumbnailIv = (ImageView) findViewById(R.id.iv_icon);
			videoPlayIv = (ImageView) findViewById(R.id.video_play_iv);
			flTaskRoot = (FrameLayout) findViewById(R.id.fl_task_root);
			vSelected = findViewById(R.id.v_selected);

			taskActionBtn.setLongClickable(true);
			taskActionBtn.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					if (mIsDeleteMode) {
						return true;
					}
					mIsDeleteMode = true;
					mDownloadTasksManagerActivity.setDeleteMode(true);
					vSelected.setVisibility(View.VISIBLE);
					mSparseBooleanArray.clear();
					mSparseBooleanArray.append(getAdapterPosition(), true);
					return true;
				}
			});
			taskActionBtn.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (v.getTag() == null) {
				return;
			}
			TaskItemViewHolder holder = (TaskItemViewHolder) v.getTag();
			if (holder.position == RecyclerView.NO_POSITION) {
				return;
			}
			if (mIsDeleteMode) {
				if (mSparseBooleanArray.get(holder.position, false)) {
					holder.vSelected.setVisibility(View.INVISIBLE);
					mSparseBooleanArray.delete(holder.position);
					if (mSparseBooleanArray.size() == 0) {
						mDownloadTasksManagerActivity.setDeleteMode(false);
						mIsDeleteMode = false;
					}
				} else {
					holder.vSelected.setVisibility(View.VISIBLE);
					mSparseBooleanArray.append(holder.position, true);
				}
				return;
			}

			CharSequence action = ((TextView) v).getText();
			if (action.equals(v.getResources().getString(R.string.pause))) {
				FileDownloader.getImpl().pause(holder.id);
				holder.updateNotDownloaded(FileDownloadStatus.paused, 0, 0, null);
				holder.taskStatusTv.setText(R.string.tasks_manager_demo_status_paused);
			} else if (action.equals(v.getResources().getString(R.string.start))) {
				final TasksManagerModel model = mDownloadModelList.get(holder.position);
				if (model == null) {
					return;
				}
				BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
						.setPath(model.getPath())
						.setCallbackProgressTimes(100)
						.setListener(mTaskDownloadListener);
				TasksManager.getImpl().addTaskForViewHolder(task);
				TasksManager.getImpl().updateViewHolder(holder.id, holder);
				task.start();
			} else if (action.equals(v.getResources().getString(R.string.play))) {
				List<String> filePaths = new ArrayList<>();
				List<String> thumbnails = new ArrayList<>();
				int i = 0;
				if (holder.position > 250) {
					i = holder.position - 250;
				}
				for (; i < mDownloadModelList.size(); i++) {
					filePaths.add(mDownloadModelList.get(i).getPath());
					thumbnails.add(mDownloadModelList.get(i).getPath());

					if (filePaths.size() > 500) {
						break;
					}
				}
				if (!filePaths.isEmpty()) {
					Router.showDownloadFile(mDownloadTasksManagerActivity, filePaths, thumbnails, holder.position, holder.thumbnailIv);
				}
			}
		}
	}
}
