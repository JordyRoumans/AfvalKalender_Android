package com.example.afvalkalenderv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public DatabaseHelper(@Nullable Context context) {
        super(context, "calendar.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createTableStatement="CREATE TABLE Dates_Table(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "dtstart TEXT, " +
                "dtend TEXT, " +
                "summary TEXT)";

        db.execSQL(createTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public boolean AddDate()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("dtstart","2021-01-07");
        cv.put("dtend","2021-01-08");
        cv.put("summary","Huisvuil");

        long dates_table = db.insert("Dates_Table", null, cv);

        if (dates_table==-1)
        {
            return false;
        }
        else
        {
            return true;
        }


    }
}
