package com.example.malfoodware.DataLayer


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.malfoodware.*
import com.example.malfoodware.DataLayer.DBHelper.Companion.checkInjection
import java.util.*
import kotlin.collections.ArrayList

class FoodDBHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
{
    companion object{
        private val DATABASE_NAME = "FoodsDB1"
        private val DATABASE_VERSION = 1
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)
        db?.setForeignKeyConstraintsEnabled(true)
    }
    override fun onCreate(db: SQLiteDatabase?) {
        super.onOpen(db);
        IngredientDBService.onCreate(db)
        RecipeDBService.onCreate(db)
        UserDBService.onCreate(db)
        DiaryDBService.onCreate(db)
    }

    fun deleteAll(){
        var db = this.writableDatabase
        IngredientDBService.deleteTable(db)
        RecipeDBService.deleteTable(db)
        UserDBService.deleteTable(db)
        DiaryDBService.deleteTable(db)
        onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        deleteAll()
    }

    /**
     * insertDiaryEntry attempts to insert an entry into the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      entry: FoodDiaryEntry, the entry to be added
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *      class
     */
    fun insertDiaryEntry(
        entry: FoodDiaryEntry,
        uid: String
    ): Boolean {
        return DiaryDBService.insertDiaryEntry(this, entry, uid)
    }

    /**
     * getDiaryEntriesDate returns a set of FoodDiaryEntry's for a given date string in the format
     * of DD/MM/YYYY excluding leading 0's
     *
     * @param
     *      date: String, the given date string in the format of DD/MM/YYYY excluding leading 0's
     * @return
     *      SortedSet<FoodDiaryEntry>: a set either empty or full dependant upon entries matching
     *          the date
     */
    fun getDiaryEntriesDate(
        uid: String,
        date: String
    ): SortedSet<FoodDiaryEntry> {
        return DiaryDBService.getDiaryEntriesDate(this, uid, date)
    }

    /**
     * getDiaryEntriesDateRange returns a set of FoodDiaryEntry's for a given date range
     *
     * @param
     *      from: String, the given lower bound date string in the format of DD/MM/YYYY
     *          excluding leading 0's
     *      to: String, the given upper bound date string in the format of DD/MM/YYYY
     *          excluding leading 0's
     * @return
     *      SortedSet<FoodDiaryEntry>: a set either empty or full dependant upon entries matching
     *           the date
     */
    fun getDiaryEntriesDateRange(
        uid: String,
        from: String,
        to: String
    ): SortedSet<FoodDiaryEntry> {
        return DiaryDBService.getDiaryEntriesDateRange(this, uid,  from, to)
    }

    /**
     * getIngrients retrieves a list of names for all ingredients within the database
     * @return
     *      SortedSet<String>: Set containing all ingredient names in database
     */
    fun getIngredients(): SortedSet<String> {
        return IngredientDBService.getIngredients(this)
    }

    /**
     * getIngredient returns an Ingredient data member for a given ingredient name
     * from the database class
     *
     * @param
     *      name: String, the name of an ingredient to search for
     * @return
     *      Ingredient?: either the ingredient, or a null ingredient if either
     *          an sql error has occurred or no results exist, any errors are logged via the
     *          Logger class
     */
    fun getIngredient(name: String): Ingredient? {
        return IngredientDBService.getIngredient(this, name)
    }

    /**
     * insertIngredient attempts to insert an ingredient into the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      ingredient: Ingredient, the ingredient to be added
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *          class
     */
    fun insertIngredient(ingredient: Ingredient): Boolean {
        return IngredientDBService.insertIngredient(this, ingredient)
    }

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
    fun ammendIngredient(ingredient: Ingredient): Boolean {
        return IngredientDBService.ammendIngredient(this, ingredient)
    }

    /**
     * deleteIngredient attempts to delete an ingredient within the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      ingredient: Ingredient, the ingredient to be added
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *          class
     */
    fun deleteIngredient(ingredient: Ingredient): Boolean {
        return IngredientDBService.deleteIngredient(this, ingredient)
    }

    /**
     * getRecipes retrieves a list of names for all recipes within the database
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     * @return
     *      SortedSet<String>: Set containing all recipe names in database
     */
    fun getRecipes(): SortedSet<String> {
        return RecipeDBService.getRecipes(this)
    }

    /**
     * getRecipe returns an Recipe data member for a given recipe name
     * from the database class
     *
     * @param
     *      name: String, the name of an recipe to search for
     * @return
     *      Recipe?: either the recipe, or a null ingredient if either
     *      an sql error has occurred or no results exist, any errors are logged via the
     *      Logger class
     */
    fun getRecipe(name: String): Recipe? {
        return RecipeDBService.getRecipe(this, name)
    }

    /**
     * insertRecipe attempts to insert an recipe into the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      recipe: Recipe, the recipe to be added
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *      class
     */
    fun insertRecipe(recipe: Recipe): Boolean {
        return RecipeDBService.insertRecipe(this, recipe)
    }

    /**
     * ammendRecipe attempts to alter a recipe within the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      recipe: Recipe, the ingredient to be added
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *          class
     */
    fun ammendRecipe(recipe: Recipe): Boolean {
        return RecipeDBService.ammendRecipe(this, recipe)
    }

    /**
     * deleteRecipe attempts to delete an recipe within the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      recipe: Recipe, the recipe to be added
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *          class
     */
    fun deleteRecipe(recipe: Recipe): Boolean {
        return RecipeDBService.deleteRecipe(this, recipe)
    }

    /**
     * getUser returns an User data member for a given user name
     * from the database class
     *
     * @param
     *      uid: String, the name of the User to search for
     * @return
     *      User?: either the user, or a null ingredient if either
     *          an sql error has occurred or no results exist, any errors are logged via the
     *          Logger class
     */
    fun getUser(uid: String): User? {
        return UserDBService.getUser(this, uid)
    }

    /**
     * insertUser attempts to insert a user into the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      user: User, the user to be added
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *          class
     */
    fun insertUser(user: User): Boolean {
        return UserDBService.insertUser(this, user)
    }

    /**
     * ammendUser attempts to update a user into the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      user: User, the user to be altered
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *          class
     */
    fun ammendUser(user: User): Boolean {
        return UserDBService.ammendUser(this, user)
    }

    /**
     * getLoggedInUser retrieves the last user logged in during the app's lifecycle
     */
    fun getLoggedInUser(): String? {
        return AppDBService.getLoggedInUser(this)
    }

    /**
     * setLoggedInUser updates the last user logged in
     */
    fun setLoggedInUser(name: String?) {
        return AppDBService.setLoggedInUser(this, name)
    }
}