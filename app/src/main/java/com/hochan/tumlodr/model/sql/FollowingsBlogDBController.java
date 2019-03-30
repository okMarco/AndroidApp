package com.hochan.tumlodr.model.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.jumblr.types.Blog;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by hochan on 2017/9/9.
 */

public class FollowingsBlogDBController {

	static final String TABLE_FOLLOWING_BLOGS = "following_blogs";
	static final String BLOG_NAME = "blog_name";
	static final String BLOG_TITLE = "blog_title";

	private final SQLiteDatabase mDb;
	private final DownloadTasksDBHelper openHelper;

	public FollowingsBlogDBController() {
		openHelper = new DownloadTasksDBHelper(TumlodrApp.mContext);
		mDb = openHelper.getWritableDatabase();
	}

	public void addBlogs(List<Blog> blogs){
		for (Blog blog : blogs){
			ContentValues values = new ContentValues();
			values.put(BLOG_NAME, blog.getName());
			values.put(BLOG_TITLE, blog.getTitle());
			final boolean succeed = mDb.insert(TABLE_FOLLOWING_BLOGS, null, values) != -1;
		}
	}

	public void addNewBlogs(List<Blog> newBlogs){
		List<Blog> blogs = getAllBlogs(0);
		mDb.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLLOWING_BLOGS);
		openHelper.createTableFollowingBlogs(mDb);
		addBlogs(newBlogs);
		addBlogs(blogs);
	}

	public List<Blog> getAllBlogs(int offset){
		final Cursor c = mDb.rawQuery("SELECT * FROM " + TABLE_FOLLOWING_BLOGS, null);
		final List<Blog> list = new ArrayList<>();

		try {
			if (!c.moveToLast() || c.getCount() <= offset) {
				return list;
			}
			c.moveToFirst();
			do {
				Blog blog = new Blog();
				blog.setName(c.getString(c.getColumnIndex(BLOG_NAME)));
				list.add(blog);
			} while (c.moveToNext());
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return list;
	}
}
