package com.thebridgestudio.amwayconference.daos;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.models.Schedule;
import com.thebridgestudio.amwayconference.models.Message;
import com.thebridgestudio.amwayconference.models.ScheduleDetail;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "amway_conference.db";
    private static final int DATABASE_VERSION = 1;
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Message.class);
            TableUtils.createTable(connectionSource, Schedule.class);
            TableUtils.createTable(connectionSource, ScheduleDetail.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Message.class, true);
            TableUtils.dropTable(connectionSource, Schedule.class, true);
            TableUtils.dropTable(connectionSource, ScheduleDetail.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

}
