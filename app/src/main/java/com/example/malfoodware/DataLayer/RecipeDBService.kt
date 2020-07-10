package com.example.malfoodware.DataLayer

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
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
                    "$KEY_RECIP_NAME TEXT COLLATE NOCASE PRIMARY KEY)")
            db?.execSQL(CREATE_CONTENTS_TABLE)
            CREATE_CONTENTS_TABLE = ("CREATE TABLE IF NOT EXISTS $TABLE_RECIPES(" +
                    "$KEY_RECIP_NAME TEXT COLLATE NOCASE, " +
                    "${IngredientDBService.KEY_INGREDIENT_NAME} COLLATE NOCASE, " +
                    "$KEY_SERVING REAL NOT NULL, " +
                    "CONSTRAINT fk_ingredient FOREIGN KEY(${IngredientDBService.KEY_INGREDIENT_NAME}) " +
                        "REFERENCES ${IngredientDBService.TABLE_INGREDIENTS}(${IngredientDBService.KEY_INGREDIENT_NAME}) " +
                        "ON DELETE CASCADE, " +
                    "CONSTRAINT fk_recipe FOREIGN KEY($KEY_RECIP_NAME) REFERENCES $TABLE_RECIPE_NAMES" +
                        "($KEY_RECIP_NAME) ON DELETE CASCADE," +
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
            var query = "SELECT * FROM $TABLE_RECIPES WHERE $KEY_RECIP_NAME='$name'"
            var cursor: Cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst())
            {
                rec = Recipe(name)
                var recName: String
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
                db.insertOrThrow(TABLE_RECIPE_NAMES, null, contentValues)
            } catch (e: SQLiteException)
            {
                Logger.add(
                    "SQL Insertion Errror during recipe insertion.\nrecipe: $recipe" +
                            "\nDuplicate Recipe Found\nSQLError Message:\n${e.toString()}"
                )
                db.endTransaction()
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

        override fun ammendRecipe(dbHelper: FoodDBHelper, recipe: Recipe): Boolean {
            TODO("Not yet implemented")
        }

        override fun deleteRecipe(dbHelper: FoodDBHelper, recipe: Recipe): Boolean {
            TODO("Not yet implemented")
        }

    }
}