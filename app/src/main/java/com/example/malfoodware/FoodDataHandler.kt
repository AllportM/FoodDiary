package com.example.malfoodware

import java.util.*

interface FoodDataHandler {
    fun getIngredients(): SortedSet<String>
    fun getIngredient(name: String): Ingredient
    fun insertIngredient(ingredient: Ingredient): Boolean
    fun ammendIngredient(ingredient: Ingredient): Boolean
    fun deleteIngredient(ingredient: Ingredient): Boolean
    fun getRecipes(): SortedSet<String>
    fun getRecipe(name: String): Recipe
    fun insertRecipe(recipe: Recipe): Boolean
    fun ammendRecipe(recipe: Recipe): Boolean
    fun deleteRecipe(recipe: Recipe): Boolean
}