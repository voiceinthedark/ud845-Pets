package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.pets.data.PetContract.PetEntry;

public class PetDbHelper extends SQLiteOpenHelper {

    /**
     * The database version
     */
    public static final int DATABASE_VERSION = 1;
    /**
     * The database name
     */
    public static final String DATABASE_NAME = "shelter.db";

    /**
     *
     */
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + PetEntry.TABLE_NAME +
            "(" + PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," +
            PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL" + "," +
            PetEntry.COLUMN_PET_BREED + " TEXT" + "," +
            PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL" + "," +
            PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0" +
            ");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PetEntry.TABLE_NAME + ";";



    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //delete table and then recreate the table
        db.execSQL(SQL_DELETE_ENTRIES);
        onOpen(db);
    }


}
