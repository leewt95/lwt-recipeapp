package com.example.lwt_recipeapp.Model;

public class Recipe {

    private String type;
    private String name;
    private byte[] image;
    private String ingredients;
    private String instructions;


    public Recipe(String name, byte[] image) {
        this.name = name;
        this.image = image;
    }

    public Recipe(String type, String name, byte[] image, String ingredients, String instructions) {
        this.type = type;
        this.name = name;
        this.image = image;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

}
