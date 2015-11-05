package com.huanggusheng.flemerun;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 2015/10/24.
 */
public class DbHelper extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "file_name";
    public static final String COLUMN_DATE = "file_name";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_SPEED = "speed";
    public static final String CREATE_FILE_NAME = "create table file_name(" +
            "id integer primary key autoincrement," +
            "file_name text," +
            "distance text," +
            "duration text," +
            "speed text)";

    private Context mContext;

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_FILE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
