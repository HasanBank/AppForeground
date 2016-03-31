package com.example.mrbank.appforeground.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.example.mrbank.appforeground.MyService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrbank on 15/02/16.
 */
public class TableControllerPrograms extends DatabaseHandler {

    public TableControllerPrograms(Context context) {
        super(context);
    }


    public boolean create(ObjectProgramUsing objectProgramUsing) {

        ContentValues values = new ContentValues();

        values.put("totalDuration", MyService.UPDATE_INTERVAL);
        values.put("programName", objectProgramUsing.programName);
        values.put("category",objectProgramUsing.category);


        SQLiteDatabase db = this.getWritableDatabase();

        boolean createSuccessful = db.insert("programs", null, values) > 0;
        db.close();

        return createSuccessful;
    }

    public List<ObjectProgramUsing> read() {

        List<ObjectProgramUsing> recordsList = new ArrayList<ObjectProgramUsing>();

        String sql = "SELECT * FROM programs";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {

                String programName = cursor.getString(cursor.getColumnIndex("programName"));
                int totalDuration = cursor.getInt(cursor.getColumnIndex("totalDuration"));
                String category = cursor.getString(cursor.getColumnIndex("category"));


                ObjectProgramUsing objectProgram = new ObjectProgramUsing();

                objectProgram.programName = programName;
                objectProgram.totalDuration = totalDuration;
                objectProgram.category = category;

                recordsList.add(objectProgram);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return recordsList;
    }


   public boolean readSingleRecord(String appName) {


      // String sql = "SELECT * FROM programs WHERE programName = " + appName;

       SQLiteDatabase db = this.getWritableDatabase();

       Cursor cursor=db.rawQuery("Select * from programs Where programName='"+appName+"'",null);

       if (cursor.getCount() <= 0) {
           cursor.close();
           return false;
       }

       cursor.close();
       return true;
   }


   public void incrementofUsing(String appName) {

       int duration=0;

       ContentValues content = new ContentValues() ;

       SQLiteDatabase db = this.getWritableDatabase();

       Cursor cursor=db.rawQuery("Select * from programs Where programName='"+appName+"'",null);
       if( cursor.moveToFirst() ) {
           duration = Integer.parseInt(cursor.getString(cursor.getColumnIndex("totalDuration")));
       }

        content.put("totalDuration",duration+MyService.UPDATE_INTERVAL);
        db.update("programs",content,"programName='"+appName+"'",null);


    }


}
