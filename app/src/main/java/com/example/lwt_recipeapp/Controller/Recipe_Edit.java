package com.example.lwt_recipeapp.Controller;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.lwt_recipeapp.Model.Recipe;
import com.example.lwt_recipeapp.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class Recipe_Edit extends Activity {

    private Recipe recipe;
    private Integer recipeID;
    private Spinner spinnerRecipe;
    private EditText editRecipeName, editRecipeIngredients, editRecipeInstructions;
    private Button buttonEdit, buttonCancel;
    private ImageView imageRecipe;
    private Database_CRUD database_Recipe;
    private ArrayList<Recipe> recipe_Info = new ArrayList<>();
    private static final int REQUEST_CODE_GALLERY = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editrecipe);

        initialise();
        recipe = retrieveRecipeData(getIntent().getStringExtra("recipeName"));
        loadRecipeDetail();

        imageRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(Recipe_Edit.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecipeData();
                finish();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Initialise all layout UI elements on startup and SQLiteDatabase.
     */
    public void initialise() {
        spinnerRecipe = (Spinner) findViewById(R.id.spinnerRecipeType);
        imageRecipe = (ImageView) findViewById(R.id.imageRecipe);
        editRecipeName = (EditText) findViewById(R.id.editRecipeName);
        editRecipeIngredients = (EditText) findViewById(R.id.editRecipeIngredients);
        editRecipeInstructions = (EditText) findViewById(R.id.editRecipeInstructions);
        buttonEdit = (Button) findViewById(R.id.buttonEditRecipe);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);

        database_Recipe = new Database_CRUD(this);
    }

    /**
     * Clears recipe_Info ArrayList.
     * Retrieve all information based on recipeName(from RecipeDetail.java intent) from SQLiteDatabase to display information.
     * Fill data to recipe_Info ArrayList.
     */
    public Recipe retrieveRecipeData(String selectedRecipeName) {
        recipe_Info.clear();
        database_Recipe.openRecipeDB();
        String recipeType, recipeName, recipeIngredients, recipeInstructions;
        byte[] recipeImage;
        Cursor cursor = database_Recipe.retrieveSelectedRecipeData(selectedRecipeName);
        while (cursor.moveToNext()) {
            recipeID = cursor.getInt(0);
            recipeType = cursor.getString(1);
            recipeName = cursor.getString(2);
            recipeImage = cursor.getBlob(3);
            recipeIngredients = cursor.getString(4);
            recipeInstructions = cursor.getString(5);
            recipe_Info.add(new Recipe(recipeType, recipeName, recipeImage, recipeIngredients, recipeInstructions));
        }
        Recipe recipe = new Recipe(recipe_Info.get(0).getType(), recipe_Info.get(0).getName(), recipe_Info.get(0).getImage(), recipe_Info.get(0).getIngredients(), recipe_Info.get(0).getInstructions());
        cursor.close();
        database_Recipe.closeRecipeDB();
        return recipe;
    }

    public void loadRecipeDetail() {
        selectSpinnerValue();
        editRecipeName.setText(recipe.getName());
        editRecipeIngredients.setText(recipe.getIngredients());
        editRecipeInstructions.setText(recipe.getInstructions());
        imageRecipe.setImageBitmap(BitmapTools.decodeImageBitmap(recipe.getImage()));
    }

    /**
     * Open image gallery to choose food image.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(this, "Please enable permission to access internal storage", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Decode selected image to Bitmap then assigned to imageRecipe(ImageView).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageRecipe.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Spinner value from RecipeDetail.java intent.
     * To change spinner selected value based on recipeType.
     */
    public void selectSpinnerValue() {

        for (int i = 0; i < spinnerRecipe.getCount(); i++) {
            if (spinnerRecipe.getItemAtPosition(i).toString().equals(getIntent().getStringExtra("recipeType"))) {
                spinnerRecipe.setSelection(i);
                break;
            }
        }
    }

    /**
     * Update recipe information to SQLiteDatabase based on fields.
     * All fields must be filled to update.
     */
    public void updateRecipeData() {
        if (emptyFieldsCheck()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("RECIPE_TYPE", spinnerRecipe.getSelectedItem().toString().trim());
            contentValues.put("RECIPE_NAME", editRecipeName.getText().toString().trim());
            contentValues.put("RECIPE_IMAGE", BitmapTools.imageRecipeToByte(imageRecipe));
            contentValues.put("RECIPE_INGREDIENTS", editRecipeIngredients.getText().toString().trim());
            contentValues.put("RECIPE_INSTRUCTIONS", editRecipeInstructions.getText().toString().trim());
            database_Recipe.openRecipeDB();
            database_Recipe.updateRecipeData(recipeID, contentValues);
            database_Recipe.closeRecipeDB();
            Toast.makeText(getApplicationContext(), editRecipeName.getText().toString() + " " + "successfully edited!", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Please fill up all fields!", Toast.LENGTH_SHORT).show();
    }

    /**
     * To check EditText elements whether are empty.
     */
    public boolean emptyFieldsCheck() {
        if (editRecipeName.getText().toString().equals("") || editRecipeIngredients.getText().toString().equals("") || editRecipeInstructions.getText().toString().equals(""))
            return false;
        return true;
    }
}
