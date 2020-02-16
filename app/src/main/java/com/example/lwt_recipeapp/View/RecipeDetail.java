package com.example.lwt_recipeapp.View;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.example.lwt_recipeapp.Controller.BitmapTools;
import com.example.lwt_recipeapp.Controller.Database_CRUD;
import com.example.lwt_recipeapp.Controller.Recipe_Edit;
import com.example.lwt_recipeapp.Model.Recipe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lwt_recipeapp.R;

import java.util.ArrayList;

public class RecipeDetail extends AppCompatActivity {

    private Recipe recipe;
    private String selectedRecipeName;
    private Integer recipeID;
    private Toolbar toolbar;
    private ArrayList<Recipe> recipe_Info = new ArrayList<>();
    private TextView textRecipeName, textRecipeType, textIngredientsDetail, textInstructionsDetail;
    private ImageView imageRecipe, imageEdit, imageDelete;
    private Database_CRUD database_Recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        initialise();
        recipe = retrieveRecipeData(selectedRecipeName);
        loadRecipeDetail();

        imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRecipeIntent();
            }
        });

        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDeleteRecipe();
            }
        });

    }

    /**
     * Initialise all layout UI elements on startup and SQLiteDatabase.
     */
    public void initialise() {
        selectedRecipeName = getIntent().getStringExtra("recipeName");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        textRecipeName = (TextView) findViewById(R.id.textRecipeName);
        textRecipeType = (TextView) findViewById(R.id.textRecipeType);
        textIngredientsDetail = (TextView) findViewById(R.id.textIngredientsDetail);
        textInstructionsDetail = (TextView) findViewById(R.id.textInstructionsDetail);
        imageRecipe = (ImageView) findViewById(R.id.imageRecipe);
        imageEdit = (ImageView) findViewById(R.id.imageEdit);
        imageDelete = (ImageView) findViewById(R.id.imageDelete);

        database_Recipe = new Database_CRUD(this);
    }

    /**
     * Clears recipe_list ArrayList.
     * Retrieve all recipe information based on recipeName(from RecyclerViewAdapter.java intent) from SQLiteDatabase.
     * Fill data to recipe_list ArrayList.
     */
    public Recipe retrieveRecipeData(String selectedRecipeName) {
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

    /**
     * Fill TextView and ImageView element with recipe information.
     */
    public void loadRecipeDetail() {
        textRecipeName.setText(recipe.getName());
        textRecipeType.setText(recipe.getType());
        textIngredientsDetail.setText(recipe.getIngredients());
        textInstructionsDetail.setText(recipe.getInstructions());
        imageRecipe.setImageBitmap(BitmapTools.decodeImageBitmap(recipe.getImage()));
    }

    /**
     * Send recipe information to Recipe_Edit.java activity to fill fields.
     */
    public void editRecipeIntent() {
        Intent intent = new Intent(this, Recipe_Edit.class);
        intent.putExtra("recipeName", textRecipeName.getText().toString());
        startActivity(intent);
        finish();
    }

    /**
     * Alert dialog interface popup to confirm user delete recipe.
     * If yes, delete recipe in SQLiteDatabase.
     * Else, close alert dialog.
     */
    public void alertDeleteRecipe() {
        final AlertDialog alertDelete = new AlertDialog.Builder(this).setMessage("Delete recipe?").setCancelable(false).setPositiveButton("Yes", null).setNegativeButton("No", null).show();

        Button buttonPositive = alertDelete.getButton(AlertDialog.BUTTON_POSITIVE);
        buttonPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database_Recipe.openRecipeDB();
                database_Recipe.deleteRecipeData(recipeID);
                database_Recipe.closeRecipeDB();
                Toast.makeText(RecipeDetail.this, "Recipe successfully deleted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        Button buttonNegative = alertDelete.getButton(AlertDialog.BUTTON_NEGATIVE);
        buttonNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDelete.dismiss();
            }
        });
    }
}
