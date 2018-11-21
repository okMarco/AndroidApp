package com.hochan.tumlodr.model.data;

import android.content.ContentValues;

/**
 *
 * Created by hochan on 2017/8/27.
 */

public  class TasksManagerModel {

	public final static String ID = "id";
	public final static String NAME = "name";
	public final static String URL = "url";
	public final static String PATH = "path";
	public final static String THUMBNAIL = "thumbnail";
	public final static String TYPE = "type";
	public final static String CREATE_TIME = "create_time";

	public final static String TYPE_VIDEO = "video";
	public final static String TYPE_IMAGE = "image";


	private int id;
	private String name;
	private String url;
	private String path;
	private String thumbnail;
	private String type;
	private String createTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(ID, id);
		cv.put(NAME, name);
		cv.put(URL, url);
		cv.put(PATH, path);
		cv.put(THUMBNAIL, thumbnail);
		cv.put(TYPE, type);
		cv.put(CREATE_TIME, createTime);
		return cv;
	}
}
