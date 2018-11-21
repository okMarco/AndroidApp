package com.hochan.tumlodr.model.data.download;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.hochan.tumlodr.R;
import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.tools.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 * Created by hochan on 2018/3/2.
 */

@Entity(tableName = "download_records", indices = {@Index(value = {"path", "url"}, unique = true)})
public class DownloadRecord {

	@Ignore
	public static final String GROUP_TUMBLR = "tumblr";
	@Ignore
	public static final String GROUP_INSTAGRAM = "instagram";
	@Ignore
	public static final String GROUP_GROUP = "group";
	@Ignore
	public static final List<String> GROUP_LIST_NORMAL = new ArrayList<>();
	@Ignore
	public final static String TYPE_VIDEO = "video";
	@Ignore
	public final static String TYPE_IMAGE = "image";

	static {
		GROUP_LIST_NORMAL.add(GROUP_TUMBLR);
		GROUP_LIST_NORMAL.add(GROUP_INSTAGRAM);
		GROUP_LIST_NORMAL.add(GROUP_GROUP);
	}

	@PrimaryKey
	private int id;
	private String name;
	private String path;
	private String url;
	private String thumbnail;
	@ColumnInfo(name = "group_name")
	private String groupName;// tumblr instagram group ins_groupname tum_groupname
	private String type;// jpg mp4 ins_groupname tum_groupname
	@ColumnInfo(name = "created_at")
	private long createdAt;
	private boolean finish;

	public DownloadRecord(int id, String url, String path, String thumbnail, String groupName, String type) {
		this.id = id;
		this.name = TumlodrApp.mContext.getString(R.string.tasks_manager_demo_name, id);
		this.url = url;
		this.path = path;
		this.thumbnail = thumbnail;
		this.groupName = groupName;
		this.type = type;
		this.createdAt = System.currentTimeMillis();
		this.finish = false;
	}

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}
}
