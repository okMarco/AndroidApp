package com.hochan.tumlodr.model.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hochan.tumlodr.model.data.TasksManagerModel;


/**
 *
 * Created by hochan on 2017/8/27.
 */

class DownloadTasksDBHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "download";
	private final static int DATABASE_VERSION = 6;

	DownloadTasksDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTableAllDownloadTasks(db);

		createTableFollowingBlogs(db);
	}

	private void createTableAllDownloadTasks(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ DownloadTasksDBController.TABLE_ALL_DOWNLOAD_TASKS
				+ String.format(
				"("
						+ "%s INTEGER PRIMARY KEY, " // id, download id
						+ "%s VARCHAR, " // name
						+ "%s VARCHAR, " // url
						+ "%s VARCHAR, " // path
						+ "%s VARCHAR, " // thumbnail
						+ "%s VARCHAR, " // type
						+ "%s VARCHAR "  // time
						+ ")"
				, TasksManagerModel.ID
				, TasksManagerModel.NAME
				, TasksManagerModel.URL
				, TasksManagerModel.PATH
				, TasksManagerModel.THUMBNAIL
				, TasksManagerModel.TYPE
				, TasksManagerModel.CREATE_TIME

		));
	}

	public void createTableFollowingBlogs(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ FollowingsBlogDBController.TABLE_FOLLOWING_BLOGS
				+ String.format(
				"("
						+ "%s VARCHAR PRIMARY KEY, " // name
						+ "%s VARCHAR "
						+ ")"
				, FollowingsBlogDBController.BLOG_NAME
				, FollowingsBlogDBController.BLOG_TITLE
		));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		if(oldVersion < newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DownloadTasksDBController.TABLE_ALL_DOWNLOAD_TASKS);
			onCreate(db);
		}
	}
}