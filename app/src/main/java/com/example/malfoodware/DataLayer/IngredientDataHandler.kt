package com.example.malfoodware.DataLayer

import com.example.malfoodware.Ingredient
import java.util.*

/**
 * Utility interface to retrieve ingredient data members from SQL database of given dbHelper
 * connection
 */
interface IngredientDataHandler {
    /**
     * getIngrients retrieves a list of names for all ingredients within the database
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     * @return
     *      SortedSet<String>: Set containing all ingredient names in database
     */
    fun getIngredients(dbHelper: FoodDBHelper): SortedSet<String>

    /**
     * getIngredient returns an Ingredient data member for a given ingredient name
     * from the database class
     *
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     *      name: String, the name of an ingredient to search for
     * @return
     *      Ingredient?: either the ingredient, or a null ingredient if either
     *          an sql error has occurred or no results exist, any errors are logged via the
     *          Logger class
     */
    fun getIngredient(dbHelper: FoodDBHelper, name: String): Ingredient?

    /**
     * insertIngredient attempts to insert an ingredient into the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     *      ingredient: Ingredient, the ingredient to be added
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *          class
     */
    fun insertIngredient(dbHelper: FoodDBHelper, ingredient: Ingredient): Boolean

    /**
     * ammendIngredient attempts to alter an ingredient within the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     *      ingredient: Ingredient, the ingredient to be added
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *          class
     */
    fun ammendIngredient(dbHelper: FoodDBHelper, ingredient: Ingredient): Boolean

    /**
     * deleteIngredient attempts to delete an ingredient within the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     *      ingredient: Ingredient, the ingredient to be added
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *          class
     */
    fun deleteIngredient(dbHelper: FoodDBHelper, ingredient: Ingredient): Boolean
}