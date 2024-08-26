package com.example.musicplayer.Database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "myfavoritesongs";
    public static final String TABLE_NAME = "songstable";
    public static final String LAST_SESSION_TABLE = "lastsessiontable";
    public static final int VERSION = 1;
    String query = "", query2 = "";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        query = "create table " + TABLE_NAME + "(albumId INTEGER,songId INTEGER,title text,imageUri text,artistName text)";
        db.execSQL(query);

        query2 = "create table " + LAST_SESSION_TABLE + "(albumId INTEGER,songId INTEGER,title text,imageUri text,artistName text)";
        db.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        query = "drop table if exists " + TABLE_NAME + "";
        db.execSQL(query);

        query2 = "drop table if exists " + LAST_SESSION_TABLE + "";
        db.execSQL(query2);
    }
}
