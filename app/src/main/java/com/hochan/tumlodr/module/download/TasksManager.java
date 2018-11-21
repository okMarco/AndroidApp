package com.hochan.tumlodr.module.download;

import android.util.SparseArray;

import com.hochan.tumlodr.model.sql.DownloadTasksDBController;
import com.hochan.tumlodr.ui.adapter.DownloadTaskItemAdapter;
import com.hochan.tumlodr.ui.fragment.DownloadTaskFragment;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import java.lang.ref.WeakReference;

/**
 * .
 * <p>
 * Created by hochan on 2017/8/27.
 */

public class TasksManager {

	private final static class HolderClass {
		private final static TasksManager INSTANCE = new TasksManager();
	}

	public static TasksManager getImpl() {
		return HolderClass.INSTANCE;
	}

	private DownloadTasksDBController dbController;

	private TasksManager() {
		dbController = new DownloadTasksDBController();
	}

	private SparseArray<BaseDownloadTask> mTaskSparseArray = new SparseArray<>();

	public void addTaskForViewHolder(final BaseDownloadTask task) {
		mTaskSparseArray.put(task.getId(), task);
	}

	public void removeTaskForViewHolder(final int id) {
		mTaskSparseArray.remove(id);
	}

	public void updateViewHolder(final int id, final DownloadTaskItemAdapter.TaskItemViewHolder holder) {
		final BaseDownloadTask task = mTaskSparseArray.get(id);
		if (task == null) {
			return;
		}
		task.setTag(holder);
	}

	private void releaseTask() {
		mTaskSparseArray.clear();
	}

	private FileDownloadConnectListener mFileDownloadConnectListener;

	private void registerServiceConnectionListener(final WeakReference<DownloadTaskFragment> mActivityWeakReference) {
		if (mFileDownloadConnectListener != null) {
			FileDownloader.getImpl().removeServiceConnectListener(mFileDownloadConnectListener);
		}
		mFileDownloadConnectListener = new FileDownloadConnectListener() {
			@Override
			public void connected() {
				if (mActivityWeakReference != null && mActivityWeakReference.get() != null) {
					mActivityWeakReference.get().fileDownloadConnected();
				}
			}

			@Override
			public void disconnected() {
				if (mActivityWeakReference != null && mActivityWeakReference.get() != null) {
					//mActivityWeakReference.get().fileDownloadConnected();
				}
			}
		};

		FileDownloader.getImpl().addServiceConnectListener(mFileDownloadConnectListener);
	}

	private void unregisterServiceConnectionListener() {
		FileDownloader.getImpl().removeServiceConnectListener(mFileDownloadConnectListener);
		mFileDownloadConnectListener = null;
	}

	public void onCreate(final WeakReference<DownloadTaskFragment> activityWeakReference) {
		if (!FileDownloader.getImpl().isServiceConnected()) {
			FileDownloader.getImpl().bindService();
			registerServiceConnectionListener(activityWeakReference);
		}
	}

	public void onDestroy() {
		unregisterServiceConnectionListener();
		releaseTask();
	}

	public boolean isReady() {
		return FileDownloader.getImpl().isServiceConnected();
	}

	public boolean isDownloaded(final int status) {
		return status == FileDownloadStatus.completed;
	}

	public int getStatus(final int id, String path) {
		return FileDownloader.getImpl().getStatus(id, path);
	}

	public long getTotal(final int id) {
		return FileDownloader.getImpl().getTotal(id);
	}

	public long getSoFar(final int id) {
		return FileDownloader.getImpl().getSoFar(id);
	}

}