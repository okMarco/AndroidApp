package com.hochan.tumlodr.model.data.download;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * .
 * Created by hochan on 2018/3/3.
 */

@Dao
public interface DownloadRecordDao {

	@Query("SELECT * FROM download_records WHERE group_name IN (:groups) order by created_at DESC")
	DataSource.Factory<Integer, DownloadRecord> getDownloadRecordsInGroups(List<String> groups);

	@Query("SELECT * FROM download_records WHERE group_name IN (:groups) and type = (:type) order by created_at DESC")
	DataSource.Factory<Integer, DownloadRecord> getDownloadRecordsByType(List<String> groups, String type);

	@Query("SELECT * FROM download_records WHERE group_name IN (:groups) and finish = 0 order by created_at DESC")
	DataSource.Factory<Integer, DownloadRecord> getUnfinishedDownloadRecords(List<String> groups);

	@Query("SELECT * FROM download_records WHERE id = :id order by created_at DESC")
	DownloadRecord getDownloadRecordById(int id);

	@Query("SELECT * FROM download_records WHERE group_name IN (:groups) and finish = 1 order by created_at DESC")
	DataSource.Factory<Integer, DownloadRecord> getFinishedDownloadRecordsInGroup(List<String> groups);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insertNewDownloadRecord(DownloadRecord downloadRecord);

	@Update
	void updateDownloadFinish(DownloadRecord downloadRecord);

	@Delete
	void deleteDownloadRecords(List<DownloadRecord> downloadRecords);
}
