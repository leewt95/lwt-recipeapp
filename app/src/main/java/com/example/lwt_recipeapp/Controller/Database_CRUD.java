package com.example.lwt_recipeapp.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lwt_recipeapp.Model.DatabaseConstant;
import com.example.lwt_recipeapp.Model.DatabaseHelper;

public class Database_CRUD {

    private Context context;
    private SQLiteDatabase db;
    private DatabaseHelper recipe_Helper;

    public Database_CRUD(Context context) {
        this.context = context;
        recipe_Helper = new DatabaseHelper(context);
    }

    public void openRecipeDB() {
        db = recipe_Helper.getWritableDatabase();
    }

    public void closeRecipeDB() {
        recipe_Helper.close();
    }

    /**
     * INSERT query
     */
    public boolean insertRecipeData(String recipe_Type, String recipe_Name, byte[] recipe_Image, String recipe_Ingredients, String recipe_Instructions) {
        db = recipe_Helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstant.COLUMN_2, recipe_Type);
        contentValues.put(DatabaseConstant.COLUMN_3, recipe_Name);
        contentValues.put(DatabaseConstant.COLUMN_4, recipe_Image);
        contentValues.put(DatabaseConstant.COLUMN_5, recipe_Ingredients);
        contentValues.put(DatabaseConstant.COLUMN_6, recipe_Instructions);
        long result = db.insert(DatabaseConstant.TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    /**
     * SELECT query WHERE recipeType
     * Used for RecyclerView.
     */
    public Cursor retrieveRecipeData(String recipeType) {
        return db.rawQuery("SELECT * FROM " + DatabaseConstant.TABLE_NAME + " WHERE " + DatabaseConstant.COLUMN_2 + " = " + "'" + recipeType.trim() + "'", null);
    }

    /**
     * SELECT query WHERE recipeName
     * Used to retrieve specific recipe.
     */
    public Cursor retrieveSelectedRecipeData(String recipeName) {
        return db.rawQuery("SELECT * FROM " + DatabaseConstant.TABLE_NAME + " WHERE " + DatabaseConstant.COLUMN_3 + " = " + "'" + recipeName.trim() + "'", null);
    }

    /**
     * UPDATE query
     */
    public void updateRecipeData(Integer recipeID, ContentValues contentValues) {
        db.update(DatabaseConstant.TABLE_NAME, contentValues, DatabaseConstant.COLUMN_1 + " = " + recipeID, null);
    }

    /**
     * DELETE query
     * VACUUM to renumber ID column after delete.
     */
    public void deleteRecipeData(Integer recipeID) {
        db.delete(DatabaseConstant.TABLE_NAME, DatabaseConstant.COLUMN_1 + " = " + recipeID, null);
        db.execSQL("VACUUM");
    }

}
