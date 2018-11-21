package com.hochan.tumlodr.model.data.download;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.hochan.tumlodr.TumlodrApp;
import com.hochan.tumlodr.tools.Tools;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * .
 * Created by hochan on 2018/3/3.
 */

@Database(entities = {DownloadRecord.class}, version = 1, exportSchema = false)
public abstract class DownloadRecordDatabase extends RoomDatabase {

	private static final String DB_NAME = "download_record.db";

	private static DownloadRecordDatabase getDatabase() {
		return HOLDER.INSTANCE;
	}

	private static final class HOLDER {
		private static final DownloadRecordDatabase INSTANCE = Room.databaseBuilder(TumlodrApp.mContext, DownloadRecordDatabase.class, DB_NAME).build();
	}

	public abstract DownloadRecordDao getDownloadRecordDao();

	public static DataSource.Factory<Integer, DownloadRecord> getGroupDownloads(List<String> groupNames) {
		return getDatabase().getDownloadRecordDao().getDownloadRecordsInGroups(groupNames);
	}

	public static DataSource.Factory<Integer, DownloadRecord> getGroupImageDownloads(List<String> groupNames) {
		return getDatabase().getDownloadRecordDao().getDownloadRecordsByType(groupNames, DownloadRecord.TYPE_IMAGE);
	}

	public static DataSource.Factory<Integer, DownloadRecord> getGroupVideoDownloads(List<String> groupNames) {
		return getDatabase().getDownloadRecordDao().getDownloadRecordsByType(groupNames, DownloadRecord.TYPE_VIDEO);
	}

	public static DataSource.Factory<Integer, DownloadRecord> getGroupUnFinishDownloads(List<String> groupNames) {
		return getDatabase().getDownloadRecordDao().getUnfinishedDownloadRecords(groupNames);
	}

	public static void insertNewTumblrNormalDownload(String url, String path, String thumbnail, String type) {
		int id = FileDownloadUtils.generateId(url, path);
		DownloadRecord downloadRecord = new DownloadRecord(id, url, path, thumbnail, DownloadRecord.GROUP_TUMBLR, type);
		insertNewDownload(downloadRecord);
	}

	public static void insertNewFinishedTumlrVideoDownload(String url, String path, String thumbnail, String type) {
		int id = FileDownloadUtils.generateId(url, path);
		DownloadRecord downloadRecord = new DownloadRecord(id, url, path, thumbnail, DownloadRecord.GROUP_TUMBLR, type);
		downloadRecord.setFinish(true);
		insertNewDownload(downloadRecord);
	}

	public static void insertNewInstagramNormalDownload(String url, String path, String thumbnail, String type) {
		int id = FileDownloadUtils.generateId(url, path);
		DownloadRecord downloadRecord = new DownloadRecord(id, url, path, thumbnail, DownloadRecord.GROUP_INSTAGRAM, type);
		insertNewDownload(downloadRecord);
	}

	public static void insertNewInstagramGroupDownload(String group, String url, String path, String thumbnail, String type) {
		int id = FileDownloadUtils.generateId(url, path);
		DownloadRecord downloadRecord = new DownloadRecord(id, url, path, thumbnail, group, type);
		insertNewDownload(downloadRecord);
	}

	public static void insertNewGroupDownload(String url, String type) {
		String path = Tools.getStoragePathByFileName(url.substring(url.lastIndexOf("/") + 1, url.length()));
		int id = FileDownloadUtils.generateId(url, path);
		DownloadRecord downloadRecord = new DownloadRecord(id, url, path, null, DownloadRecord.GROUP_GROUP, type);
		insertNewDownload(downloadRecord);
	}

	private static void insertNewDownload(final DownloadRecord downloadRecord) {
		Completable.create(new CompletableOnSubscribe() {
			@Override
			public void subscribe(CompletableEmitter e) throws Exception {
				getDatabase().getDownloadRecordDao().insertNewDownloadRecord(downloadRecord);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
	}

	public static void updateDownloadFinish(final int id) {
		Completable.create(new CompletableOnSubscribe() {
			@Override
			public void subscribe(CompletableEmitter e) throws Exception {
				DownloadRecord downloadRecord = getDatabase().getDownloadRecordDao().getDownloadRecordById(id);
				if (downloadRecord != null) {
					downloadRecord.setFinish(true);
					getDatabase().getDownloadRecordDao().updateDownloadFinish(downloadRecord);
				}
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
	}

	public static void deleteDownloadRecords(final List<DownloadRecord> downloadRecords) {
		Completable.create(new CompletableOnSubscribe() {
			@Override
			public void subscribe(CompletableEmitter e) throws Exception {
				getDatabase().getDownloadRecordDao().deleteDownloadRecords(downloadRecords);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
	}
}
