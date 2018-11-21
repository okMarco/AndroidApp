package com.hochan.tumlodr.model.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * Created by hochan on 2016/4/12.
 */
public class SqliteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "tumlodr";

    public static int TB_VERSION = 1;

    public final static String TB_DOWNLOAD_TASK = "download_task";

    public static final String ID = "id";
    public static final String URL = "url";
    public static final String NAME = "name";
    public static final String PATH = "path";
    public static final String STATE = "state";
    public static final String TOTAL_SIZE = "total_size";
    public static final String THUMTAIL = "thumtail";
    public static final String DOWNLOADED_SIZE = "downloaded_size";
    public static final String CREATE_TIME = "create_time";

    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //在调getReadableDatabase或getWritableDatabase时，会判断指定的数据库是否存在，
    //不存在则调SQLiteDatabase.create创建， onCreate只在数据库第一次创建时才执行
    @Override
    public void onCreate(SQLiteDatabase db) {

        //创建表download_task
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_DOWNLOAD_TASK + "(" +
                ID + " varchar primary key," +
                URL + " varchar," +
                NAME + " varchar," +
                PATH + " varchar," +
                THUMTAIL + " varchar," +
                STATE + " int," +
                TOTAL_SIZE + " bigint," +
                DOWNLOADED_SIZE + " bigint," +
		        CREATE_TIME + " bigint" +
                ")");
    }

    //更新表
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + SqliteHelper.TB_DOWNLOAD_TASK);
            onCreate(db);
        }
    }
}
