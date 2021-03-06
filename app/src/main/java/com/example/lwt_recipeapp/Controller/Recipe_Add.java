package com.example.lwt_recipeapp.Controller;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.example.lwt_recipeapp.R;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class Recipe_Add extends Activity {

    private Spinner spinnerRecipe;
    private EditText editRecipeName, editRecipeIngredients, editRecipeInstructions;
    private Button buttonAdd, buttonCancel;
    private ImageView imageRecipe;
    private Database_CRUD database_Recipe;
    private static final int REQUEST_CODE_GALLERY = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addrecipe);

        initialise();

        imageRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(Recipe_Add.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertRecipeData();
                clearFields();
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
        buttonAdd = (Button) findViewById(R.id.buttonEditRecipe);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);

        database_Recipe = new Database_CRUD(this);
        selectSpinnerValue();
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
     * Spinner value from MainScreen.java intent.
     * To change spinner selected value based on user recipeType choice.
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
     * Insert new recipe information to SQLiteDatabase.
     * All fields must be filled before insert to database.
     */
    public void insertRecipeData() {
        if (emptyFieldsCheck()) {
            database_Recipe.insertRecipeData(spinnerRecipe.getSelectedItem().toString(),
                    editRecipeName.getText().toString().trim(),
                    BitmapTools.imageRecipeToByte(imageRecipe),
                    editRecipeIngredients.getText().toString().trim(),
                    editRecipeInstructions.getText().toString().trim());
            Toast.makeText(getApplicationContext(), editRecipeName.getText().toString() + " " + "successfully added!", Toast.LENGTH_SHORT).show();
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

    /**
     * Clear fields after every new recipe added for user convenience.
     */
    public void clearFields() {
        imageRecipe.setImageResource(R.drawable.img_not_found);
        editRecipeName.setText("");
        editRecipeIngredients.setText("");
        editRecipeInstructions.setText("");
    }

}
