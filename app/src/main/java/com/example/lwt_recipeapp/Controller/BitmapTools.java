package com.example.lwt_recipeapp.Controller;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class BitmapTools {

    /**
     * Convert imageRecipe(ImageView) to byte[] to store in SQLiteDatabase.
     */
    public static byte[] imageRecipeToByte(ImageView imageRecipe) {
        android.graphics.Bitmap bitmap = ((BitmapDrawable) imageRecipe.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageByteArray = stream.toByteArray();

        return imageByteArray;
    }

    /**
     * Decode byte[] image data to full image.
     */
    public static android.graphics.Bitmap decodeImageBitmap(byte[] recipe_Image) {
        return BitmapFactory.decodeByteArray(recipe_Image, 0, recipe_Image.length);
    }

}
