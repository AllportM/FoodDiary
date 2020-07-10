package com.example.malfoodware.DataLayer

import com.example.malfoodware.Recipe
import java.util.*

interface RecipeDataHandler {
    /**
     * getRecipes retrieves a list of names for all recipes within the database
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     * @return
     *      SortedSet<String>: Set containing all recipe names in database
     */
    fun getRecipes(dbHelper: FoodDBHelper): SortedSet<String>

    /**
     * getRecipe returns an Recipe data member for a given recipe name
     * from the database class
     *
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     *      name: String, the name of an recipe to search for
     * @return
     *      Recipe?: either the recipe, or a null ingredient if either
     *      an sql error has occurred or no results exist, any errors are logged via the
     *      Logger class
     */
    fun getRecipe(dbHelper: FoodDBHelper, name: String): Recipe?

    /**
     * insertRecipe attempts to insert an recipe into the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     *      recipe: Recipe, the recipe to be added
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *      class
     */
    fun insertRecipe(dbHelper: FoodDBHelper, recipe: Recipe): Boolean

    /**
     * ammendRecipe attempts to alter a recipe within the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     *      recipe: Recipe, the ingredient to be added
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *          class
     */
    fun ammendRecipe(dbHelper: FoodDBHelper, recipe: Recipe): Boolean

    /**
     * deleteRecipe attempts to delete an recipe within the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     *      recipe: Recipe, the recipe to be added
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *          class
     */
    fun deleteRecipe(dbHelper: FoodDBHelper, recipe: Recipe): Boolean
}