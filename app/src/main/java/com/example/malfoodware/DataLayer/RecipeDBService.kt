package com.example.malfoodware.DataLayer

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
import com.example.malfoodware.Ingredient
import com.example.malfoodware.Logger
import com.example.malfoodware.Recipe
import java.util.*

class RecipeDBService {
    companion object: RecipeDataHandler, DBInterface
    {
        val TABLE_RECIPE_NAMES = "Recipes"
        val TABLE_RECIPES = "Recipe_Ingredients"
        val KEY_RECIP_NAME = "recName"
        val KEY_SERVING = "qty"

        override fun onCreate(db: SQLiteDatabase?) {
            var CREATE_CONTENTS_TABLE = ("CREATE TABLE IF NOT EXISTS $TABLE_RECIPE_NAMES(" +
                    "$KEY_RECIP_NAME TEXT COLLATE NOCASE PRIMARY KEY," +
                    "$KEY_SERVING INT NOT NULL)")
            db?.execSQL(CREATE_CONTENTS_TABLE)
            CREATE_CONTENTS_TABLE = ("CREATE TABLE IF NOT EXISTS $TABLE_RECIPES(" +
                    "$KEY_RECIP_NAME TEXT COLLATE NOCASE, " +
                    "${IngredientDBService.KEY_INGREDIENT_NAME} COLLATE NOCASE, " +
                    "$KEY_SERVING REAL NOT NULL, " +
                    "CONSTRAINT fk_ingredient FOREIGN KEY(${IngredientDBService.KEY_INGREDIENT_NAME}) " +
                        "REFERENCES ${IngredientDBService.TABLE_INGREDIENTS}(${IngredientDBService.KEY_INGREDIENT_NAME}) " +
                        "ON DELETE CASCADE ON UPDATE CASCADE, " +
                    "CONSTRAINT fk_recipe FOREIGN KEY($KEY_RECIP_NAME) REFERENCES $TABLE_RECIPE_NAMES" +
                        "($KEY_RECIP_NAME) ON DELETE CASCADE ON UPDATE CASCADE," +
                    "PRIMARY KEY($KEY_RECIP_NAME, ${IngredientDBService.KEY_INGREDIENT_NAME}))")
            db?.execSQL(CREATE_CONTENTS_TABLE)
        }

        override fun deleteTable(db: SQLiteDatabase) {
            DBHelper.deleteTable(db, TABLE_RECIPES)
            DBHelper.deleteTable(db, TABLE_RECIPE_NAMES)
        }

        override fun getRecipes(dbHelper: FoodDBHelper): SortedSet<String> {
            val result: SortedSet<String> = sortedSetOf()
            val db = dbHelper.writableDatabase
            val query = "SELECT * FROM $TABLE_RECIPE_NAMES"
            val cursor: Cursor
            cursor = db.rawQuery(query, null)
            var recName: String
            if (cursor.moveToFirst())
            {
                do {
                    recName = cursor.getString(cursor.getColumnIndex("$KEY_RECIP_NAME"))
                    result.add(recName)
                } while (cursor.moveToNext())
            }
            cursor.close()
            return result
        }

        override fun getRecipe(dbHelper: FoodDBHelper, name: String): Recipe? {
            val db = dbHelper.readableDatabase
            var rec: Recipe? = null
            if (DBHelper.checkInjection(name))
            {
                Logger.add(
                    "SQL getRecipe query error with injection character detected ';'\n" +
                            "recName: $name"
                )
                return rec
            }
            var ingName: String
            var qty: Float
            var query = "SELECT * FROM $TABLE_RECIPE_NAMES WHERE $KEY_RECIP_NAME='$name'"
            var cursor: Cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst())
            {
                val recName = cursor.getString(cursor.getColumnIndex(KEY_RECIP_NAME))
                val serving = cursor.getInt(cursor.getColumnIndex(KEY_SERVING))
                rec = Recipe(recName, serving)
            }
            else return rec
            query = "SELECT * FROM $TABLE_RECIPES WHERE $KEY_RECIP_NAME='$name'"
            cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst())
            {
                var ing: Ingredient? = null
                // creates and returns ingredient, or throws exception if failure
                do
                {
                    ingName = cursor.getString(cursor.getColumnIndex(IngredientDBService.KEY_INGREDIENT_NAME))
                    ing = IngredientDBService.getIngredient(dbHelper, ingName)!!
                    qty = cursor.getFloat(cursor.getColumnIndex("$KEY_SERVING"))
                    rec.addIngredient(ing, qty)
                } while(cursor.moveToNext())
            }
            // tries to find any deleted ingredients
            val recWithDeleted = IngredientDBService.getDeletedRecipeIngredients(dbHelper, name)
            if (recWithDeleted != null)
            {
                rec.hasDeleteIng = true
                for (ingredient in recWithDeleted.ingList)
                {
                    rec.ingList[ingredient.key] = ingredient.value
                }
            }
            cursor.close()
            return rec
        }

        override fun insertRecipe(dbHelper: FoodDBHelper, recipe: Recipe): Boolean {
            var db = dbHelper.writableDatabase
            var contentValues: ContentValues
            db.beginTransaction()
            try {
                contentValues= ContentValues()
                contentValues.put(KEY_RECIP_NAME, recipe.recName)
                contentValues.put(KEY_SERVING, recipe.portion)
                db.insertOrThrow(TABLE_RECIPE_NAMES, null, contentValues)
            } catch (e: SQLiteException)
            {

                db.endTransaction()
                Logger.add(
                    "SQL Insertion Errror during recipe insertion.\nrecipe: $recipe" +
                            "\nDuplicate Recipe Found\nSQLError Message:\n${e.toString()}"
                )
                return false
            }
            try {
                for (i in recipe.ingList) {
                    contentValues = ContentValues()
                    contentValues.put(KEY_RECIP_NAME, recipe.recName)
                    contentValues.put(IngredientDBService.KEY_INGREDIENT_NAME, i.key.name)
                    contentValues.put(KEY_SERVING, i.value)
                    db.insertOrThrow(TABLE_RECIPES, null, contentValues)
                }
                db.setTransactionSuccessful()
            } catch (e: SQLiteException) {
                Logger.add(
                    "SQL Insertion Errror during recipe insertion.\nrecipe: $recipe" +
                            "\nSQLError Message:\n${e.toString()}"
                )
                return false
            } finally {
                db.endTransaction()
            }
            return true
        }

        fun findRecipeWithIngredient(dbHelper: FoodDBHelper, ing: Ingredient): MutableList<Recipe>
        {
            val result = mutableListOf<Recipe>()
            val query = "SELECT * FROM $TABLE_RECIPES WHERE ${IngredientDBService.KEY_INGREDIENT_NAME}=" +
                    "'${ing.name}'"
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst())
            {
                do {
                    val recipeName = cursor.getString(cursor.getColumnIndex(KEY_RECIP_NAME))
                    val recipe = getRecipe(dbHelper, recipeName)!!
                    result.add(recipe)
                } while (cursor.moveToNext())
            }
            return result
        }

        override fun ammendRecipe(dbHelper: FoodDBHelper, oldRecipe: Recipe, newRecipe: Recipe): Boolean {
            val db = dbHelper.writableDatabase
            var contentValues: ContentValues
            var updated: Boolean = true
            var whereClause = "$KEY_RECIP_NAME='${oldRecipe.recName}'"
            db.beginTransaction()
            contentValues= ContentValues()
            contentValues.put(KEY_RECIP_NAME, newRecipe.recName)
            contentValues.put(KEY_SERVING, newRecipe.portion)
            if(db.update(TABLE_RECIPE_NAMES, contentValues, whereClause, null) <= 0)
                updated = false
            else {
                whereClause = "$KEY_RECIP_NAME='${newRecipe.recName}'"
                db.delete(TABLE_RECIPES, whereClause, null)
                val oldIngredients = mutableListOf<String>()
                for (i in oldRecipe.ingList)
                {
                    oldIngredients.add(i.key.name)
                }
                for (i in newRecipe.ingList)
                {
                    oldIngredients.remove(i.key.name)
                    if (!i.key.hasBeenDeleted)
                    {
                        contentValues = ContentValues()
                        contentValues.put(KEY_RECIP_NAME, newRecipe.recName)
                        contentValues.put(IngredientDBService.KEY_INGREDIENT_NAME, i.key.name)
                        contentValues.put(KEY_SERVING, i.value)
                        // adds new ingredient
                        try {
                            db.insertOrThrow(TABLE_RECIPES, null, contentValues)
                        } catch (e: SQLiteException) {
                            Log.d(
                                "LOG", "${this::class.java} SQL error, tried adding" +
                                        " new ingredient during recipe insertion\nold recipe: $oldRecipe" +
                                        "\nnew recipe: $newRecipe\ning: ${i.key}\n${e.message}"
                            )
                            updated = false
                        }
                    } else if (!IngredientDBService.ammendRecDeleted(
                            db, oldRecipe.recName,
                            newRecipe.recName, i.value, i.key.name
                        )
                    )
                    {
                        Log.d("LOG", "${this::class.java} SQL error, could not insert " +
                                "deleted ingredient\nold recipe: $oldRecipe" +
                                "\nnew recipe: $newRecipe\ning: ${i.key}\n")
                        updated = false
                    }
                }
                // deletes entries of old ingredient names that were deleted
                for (i in oldRecipe.ingList) {
                    if (oldIngredients.contains(i.key.name))
                    {
                        if (i.key.hasBeenDeleted)
                        {
                            if (!IngredientDBService.delRecDelIng(db, i.key.name))
                            {
                                Log.d("LOG", "${this::class.java} SQL error, could not delete")
                                updated = false
                            }
                        }
                    }
                }
            }
            if (updated)
                db.setTransactionSuccessful()
            db.endTransaction()
            return updated
        }

        override fun deleteRecipe(dbHelper: FoodDBHelper, recipe: Recipe): Boolean {
            val db = dbHelper.writableDatabase
            val where = "$KEY_RECIP_NAME='${recipe.recName}'"
            return db.delete(TABLE_RECIPE_NAMES, where, null) > 0
        }

    }
}