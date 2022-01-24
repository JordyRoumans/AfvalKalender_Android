package com.example.afvalkalenderv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private String date;

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

    public List SelectFromDate(String date)
    {
        String query = "SELECT summary FROM Dates_Table WHERE dtend = ?";
        String data = null;
        List summary = new ArrayList();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query,new String[] {date});

        if (c.moveToFirst())
        {
            do {
                data = c.getString(0);
                summary.add(data);

            } while (c.moveToNext());

        }
        else {
            summary = Collections.singletonList("geen afval ophaal verwacht");
        }
        c.close();
        return summary;

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
