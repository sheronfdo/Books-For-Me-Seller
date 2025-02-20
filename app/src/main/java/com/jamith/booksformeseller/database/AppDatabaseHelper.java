package com.jamith.booksformeseller.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "app_cache.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_CATEGORIES = "categories";
    public static final String COL_CAT_ID = "cat_id";
    public static final String COL_CAT_NAME = "cat_name";
    public static final String TABLE_LANGUAGES = "languages";
    public static final String COL_LANG_ID = "lang_id";
    public static final String COL_LANG_NAME = "lang_name";

    public AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " (" +
                COL_CAT_ID + " TEXT PRIMARY KEY, " +
                COL_CAT_NAME + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_LANGUAGES + " (" +
                COL_LANG_ID + " TEXT PRIMARY KEY, " +
                COL_LANG_NAME + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGES);
        onCreate(db);
    }

    public void clearTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, null, null);
        db.close();
    }
}