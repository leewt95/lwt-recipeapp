package com.example.lwt_recipeapp.View;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.example.lwt_recipeapp.Adapter.RecyclerViewAdapter;
import com.example.lwt_recipeapp.Controller.Recipe_Add;
import com.example.lwt_recipeapp.Controller.Database_CRUD;
import com.example.lwt_recipeapp.Model.Recipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.lwt_recipeapp.R;

import java.util.ArrayList;

public class MainScreen extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner spinner_Recipe;
    private RecyclerView recycler_Recipe;
    private RecyclerViewAdapter recyclerViewAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fab;
    private Database_CRUD database_Recipe;
    private ArrayList<Recipe> recipe_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        initialise();
        retrieveRecipes();

        spinner_Recipe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                retrieveRecipes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecipeList();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRecipe();
            }
        });
    }


    /**
     * Initialise all layout UI elements on startup and SQLiteDatabase.
     */
    public void initialise() {
        spinner_Recipe = (Spinner) findViewById(R.id.spinner_Recipe);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        recycler_Recipe = (RecyclerView) findViewById(R.id.recyclerView);
        recycler_Recipe.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewAdapter = new RecyclerViewAdapter(this, recipe_list);
        recycler_Recipe.setAdapter(recyclerViewAdapter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        database_Recipe = new Database_CRUD(this);
    }

    /**
     * Clears recipe_list ArrayList.
     * Retrieve only recipeName and recipeImage based on recipeType(Spinner) from SQLiteDatabase to display necessary information on recyclerView.
     * Fill data to recipe_list ArrayList.
     */
    public void retrieveRecipes() {
        recipe_list.clear();
        database_Recipe.openRecipeDB();
        Cursor cursor = database_Recipe.retrieveRecipeData(spinner_Recipe.getSelectedItem().toString());
        while (cursor.moveToNext()) {
            String recipeName = cursor.getString(2);
            byte[] recipeImage = cursor.getBlob(3);
            recipe_list.add(new Recipe(recipeName, recipeImage));
        }
        cursor.close();
        database_Recipe.closeRecipeDB();

        recyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Refresh recipe information based on recipeType(Spinner) whenever the user executes swipe down refresh.
     */
    public void updateRecipeList() {
        recipe_list.clear();
        database_Recipe = new Database_CRUD(this);
        database_Recipe.openRecipeDB();
        Cursor cursor = database_Recipe.retrieveRecipeData(spinner_Recipe.getSelectedItem().toString());
        while (cursor.moveToNext()) {
            String recipeName = cursor.getString(2);
            byte[] recipeImage = cursor.getBlob(3);
            recipe_list.add(new Recipe(recipeName, recipeImage));
        }
        cursor.close();
        swipeRefresh.setRefreshing(false);
        database_Recipe.closeRecipeDB();

        recyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Open new activity to add new recipe.
     * Send recipeType value based on user choice to new activity.
     */
    public void addRecipe() {
        Intent intent = new Intent(this, Recipe_Add.class);
        intent.putExtra("recipeType", spinner_Recipe.getSelectedItem().toString());
        startActivity(intent);
    }

    /**
     * Whenever user returns to MainScreen, refresh recipe list.
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateRecipeList();
        recyclerViewAdapter.notifyDataSetChanged();
    }
}
