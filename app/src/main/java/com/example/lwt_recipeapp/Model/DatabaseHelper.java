package com.example.lwt_recipeapp.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DatabaseConstant.DATABASE_NAME, null, 1);
    }

    /**
     * Create SQLiteDatabase file on start or if database file not found.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DatabaseConstant.TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, RECIPE_TYPE TEXT, RECIPE_NAME TEXT, RECIPE_IMAGE BLOB, RECIPE_INGREDIENTS TEXT, RECIPE_INSTRUCTIONS TEXT)");
    }

    /**
     * Drop current then create new table in SQLiteDatabase to upgrade old to new version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstant.TABLE_NAME);
            onCreate(db);
        }
    }
}