package com.example.lwt_recipeapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lwt_recipeapp.Controller.BitmapTools;
import com.example.lwt_recipeapp.Model.Recipe;
import com.example.lwt_recipeapp.R;
import com.example.lwt_recipeapp.View.RecipeDetail;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Recipe> recipe_List;

    public RecyclerViewAdapter(Context context, ArrayList<Recipe> recipe_List) {
        this.context = context;
        this.recipe_List = recipe_List;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item, parent, false);
        return new RecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Recipe recipe = recipe_List.get(position);
        holder.textRecipeName.setText(recipe.getName());
        holder.imageRecipe.setImageBitmap(BitmapTools.decodeImageBitmap(recipe.getImage()));

        // Send recipeName value to RecipeDetail.java activity to retrieve recipe information from SQLiteDatabase.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecipeDetail.class);
                intent.putExtra("recipeName", recipe.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipe_List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textRecipeName;
        ImageView imageRecipe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textRecipeName = (TextView) itemView.findViewById(R.id.textRecipeName);
            imageRecipe = (ImageView) itemView.findViewById(R.id.imageRecipe);
        }
    }

}