package com.jamith.booksformeseller.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jamith.booksformeseller.model.Category;
import com.jamith.booksformeseller.model.Language;

import java.util.ArrayList;
import java.util.List;

public class DataRepository {
    private final AppDatabaseHelper dbHelper;

    public DataRepository(Context context) {
        this.dbHelper = new AppDatabaseHelper(context);
    }

    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + AppDatabaseHelper.TABLE_CATEGORIES, null);

        if (cursor.moveToFirst()) {
            do {
                categories.add(new Category(
                    cursor.getString(0), // id
                    cursor.getString(1)  // name
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }

    public List<Language> getLanguages() {
        List<Language> languages = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + AppDatabaseHelper.TABLE_LANGUAGES, null);

        if (cursor.moveToFirst()) {
            do {
                languages.add(new Language(
                    cursor.getString(0), // id
                    cursor.getString(1)  // name
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return languages;
    }
}