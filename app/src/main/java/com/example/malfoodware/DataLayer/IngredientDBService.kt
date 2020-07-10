package com.example.malfoodware.DataLayer

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.example.malfoodware.Ingredient
import com.example.malfoodware.Logger
import java.util.*

class IngredientDBService{
    companion object: IngredientDataHandler, DBInterface
    {
        val TABLE_INGREDIENTS = "Ingredients1"
        val KEY_INGREDIENT_NAME = "ingName"
        val KEY_ENERGY = "energy"
        val KEY_FAT = "fat"
        val KEY_CARBS = "carbs"
        val KEY_FIBRE = "fibre"
        val KEY_PROTEIN = "protein"
        val KEY_SALT = "salt"
        val KEY_SERVING = "serving"

        override fun onCreate(db: SQLiteDatabase?)
        {
            var CREATE_CONTENTS_TABLE = ("CREATE TABLE IF NOT EXISTS $TABLE_INGREDIENTS(" +
                    "$KEY_INGREDIENT_NAME TEXT PRIMARY KEY COLLATE NOCASE, " +
                    "$KEY_ENERGY REAL NOT NULL, " +
                    "$KEY_FAT REAL NOT NULL, " +
                    "$KEY_CARBS REAL NOT NULL, " +
                    "$KEY_FIBRE REAL NOT NULL, " +
                    "$KEY_PROTEIN REAL NOT NULL, " +
                    "$KEY_SALT REAL NOT NULL, " +
                    "$KEY_SERVING REAL NOT NULL)")
            db?.execSQL(CREATE_CONTENTS_TABLE)
        }

        override fun deleteTable(db: SQLiteDatabase)
        {
            DBHelper.deleteTable(db, TABLE_INGREDIENTS)
        }

        override fun getIngredients(dbHelper: FoodDBHelper): SortedSet<String> {
            val result: SortedSet<String> = sortedSetOf()
            val db = dbHelper.writableDatabase
            val query = "SELECT * FROM $TABLE_INGREDIENTS"
            val cursor: Cursor
            cursor = db.rawQuery(query, null)
            var ingName: String
            if (cursor.moveToFirst())
            {
                do {
                    ingName = cursor.getString(cursor.getColumnIndex(KEY_INGREDIENT_NAME))
                    result.add(ingName)
                } while (cursor.moveToNext())
            }
            cursor.close()
            return result
        }

        override fun getIngredient(dbHelper: FoodDBHelper, name: String): Ingredient? {
            var ing: Ingredient? = null
            // checks for injection
            if (DBHelper.checkInjection(name))
            {
                Logger.add(
                    "SQL getIngredient query error with injection character detected ';'\n" +
                            "IngName: $name"
                )
                return ing
            }

            val db = dbHelper.readableDatabase
            var query = "SELECT * FROM $TABLE_INGREDIENTS WHERE $KEY_INGREDIENT_NAME='$name'"
            val cursor = db.rawQuery(query, null)
            // queries DB for nutrition data for given ingID
            val name: String
            val energy: Float
            val fat: Float
            val carbs: Float
            val fibre: Float
            val protein: Float
            val salt: Float
            val serving: Float
            // creates and returns ingredient, or throws exception if failure
            if (cursor.moveToFirst())
            {
                name= cursor.getString(cursor.getColumnIndex(KEY_INGREDIENT_NAME))
                energy = cursor.getFloat(cursor.getColumnIndex(KEY_ENERGY))
                fat = cursor.getFloat(cursor.getColumnIndex(KEY_FAT))
                carbs = cursor.getFloat(cursor.getColumnIndex(KEY_CARBS))
                fibre = cursor.getFloat(cursor.getColumnIndex(KEY_FIBRE))
                protein = cursor.getFloat(cursor.getColumnIndex(KEY_PROTEIN))
                salt = cursor.getFloat(cursor.getColumnIndex(KEY_SALT))
                serving = cursor.getFloat(cursor.getColumnIndex(KEY_SERVING))
                ing = Ingredient(
                    name,
                    energy,
                    fat,
                    carbs,
                    fibre,
                    protein,
                    salt,
                    serving
                )
            }
            cursor.close()
            return ing
        }

        override fun insertIngredient(dbHelper: FoodDBHelper, ingredient: Ingredient): Boolean {
            var db = dbHelper.writableDatabase
            var contentValues = ContentValues()
            contentValues = ContentValues()
            val nut = ingredient.nut
            contentValues.put(KEY_ENERGY, nut.energy)
            contentValues.put(KEY_FAT, nut.fat)
            contentValues.put(KEY_CARBS, nut.carbs)
            contentValues.put(KEY_FIBRE, nut.fibre)
            contentValues.put(KEY_PROTEIN, nut.protein)
            contentValues.put(KEY_SALT, nut.salt)
            contentValues.put(KEY_SERVING, nut.serving)
            contentValues.put(KEY_INGREDIENT_NAME, ingredient.name)
            try{
                db.insertOrThrow(TABLE_INGREDIENTS, null, contentValues)
            }
            catch (e: SQLiteException)
            {
                Logger.add(
                    "SQL Insertion Errror during nutrition insertion.\ningredient: $ingredient" +
                            "\nSQLError Message:\n${e.toString()}"
                )
                return false
            }
            return true
        }

        override fun ammendIngredient(dbHelper: FoodDBHelper, ingredient: Ingredient): Boolean {
            TODO("Not yet implemented")
        }

        override fun deleteIngredient(dbHelper: FoodDBHelper, ingredient: Ingredient): Boolean {
            TODO("Not yet implemented")
        }
    }
}