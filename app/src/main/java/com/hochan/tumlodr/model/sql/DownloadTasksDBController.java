package com.hochan.tumlodr.model.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.model.data.TasksManagerModel;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * .
 * Created by hochan on 2017/8/27.
 */


public class DownloadTasksDBController {

	final static String TABLE_ALL_DOWNLOAD_TASKS = "all_download_tasks";

	private final SQLiteDatabase mDb;

	public DownloadTasksDBController() {
		DownloadTasksDBHelper openHelper = new DownloadTasksDBHelper(TumlodrApp.mContext);
		mDb = openHelper.getWritableDatabase();
	}

	public List<TasksManagerModel> getAllTasks() {

		final Cursor c = mDb.rawQuery("SELECT * FROM " + TABLE_ALL_DOWNLOAD_TASKS + " ORDER BY " + TasksManagerModel.CREATE_TIME, null);
		final List<TasksManagerModel> list = new ArrayList<>();
		try {
			if (!c.moveToLast()) {
				return list;
			}
			do {
				TasksManagerModel model = new TasksManagerModel();
				model.setId(c.getInt(c.getColumnIndex(TasksManagerModel.ID)));
				model.setName(c.getString(c.getColumnIndex(TasksManagerModel.NAME)));
				model.setUrl(c.getString(c.getColumnIndex(TasksManagerModel.URL)));
				model.setPath(c.getString(c.getColumnIndex(TasksManagerModel.PATH)));
				model.setThumbnail(c.getString(c.getColumnIndex(TasksManagerModel.THUMBNAIL)));
				model.setType(c.getString(c.getColumnIndex(TasksManagerModel.TYPE)));
				list.add(model);
				if (list.size() > 2000){
					break;
				}
			} while (c.moveToPrevious());
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return list;
	}

	public TasksManagerModel addTask(final String url, final String path, String thumbnail, String type) {
		if (TextUtils.isEmpty(url) || TextUtils.isEmpty(path)) {
			return null;
		}

		// have to use FileDownloadUtils.generateId to associate TasksManagerModel with FileDownloader
		final int id = FileDownloadUtils.generateId(url, path);

		TasksManagerModel model = new TasksManagerModel();
		model.setId(id);
		model.setName(TumlodrApp.mContext.getString(R.string.tasks_manager_demo_name, id));
		model.setUrl(url);
		model.setPath(path);
		model.setThumbnail(thumbnail);
		model.setType(type);
		model.setCreateTime(String.valueOf(System.currentTimeMillis()));

		final boolean succeed = mDb.insert(TABLE_ALL_DOWNLOAD_TASKS, null, model.toContentValues()) != -1;
		return succeed ? model : null;
	}


	public void deleteTask(List<TasksManagerModel> tasksManagerModels, boolean deleteFile) {
		for (TasksManagerModel tasksManagerModel : tasksManagerModels) {
			mDb.delete(TABLE_ALL_DOWNLOAD_TASKS, TasksManagerModel.URL + "=?", new String[]{tasksManagerModel.getUrl()});
			if (deleteFile) {
				File file = new File(tasksManagerModel.getPath());
				if (file.exists()) {
					file.delete();
				}
			}
		}
	}
}