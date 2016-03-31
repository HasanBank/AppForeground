package com.example.mrbank.appforeground.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mrbank on 15/02/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 8;
    protected static final String DATABASE_NAME ="UsingData";

    public DatabaseHandler(Context context) {

        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE programs " +
                "( programName TEXT PRIMARY KEY, " +
                "category TEXT," +
                "totalDuration INTEGER) ";

        db.execSQL(sql);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
        String sql = "DROP TABLE IF EXISTS programs";
        db.execSQL(sql);

        onCreate(db);
    }



}
