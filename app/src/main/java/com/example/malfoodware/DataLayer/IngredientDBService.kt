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

class IngredientDBService{
    companion object: IngredientDataHandler, DBInterface
    {
        val TABLE_INGREDIENTS = "Ingredients"
        val TABLE_RECIPES_W_DELETED_ING = "rec_del"
        val KEY_INGREDIENT_NAME = "ingName"
        val KEY_RECIPE_QTY = "recQty"
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
            CREATE_CONTENTS_TABLE = ("CREATE TABLE IF NOT EXISTS $TABLE_RECIPES_W_DELETED_ING(" +
                    "$KEY_INGREDIENT_NAME TEXT COLLATE NOCASE, " +
                    "${RecipeDBService.KEY_RECIP_NAME} TEXT COLLATE NOCASE, " +
                    "$KEY_RECIPE_QTY REAL NOT NULL," +
                    "$KEY_ENERGY REAL NOT NULL, " +
                    "$KEY_FAT REAL NOT NULL, " +
                    "$KEY_CARBS REAL NOT NULL, " +
                    "$KEY_FIBRE REAL NOT NULL, " +
                    "$KEY_PROTEIN REAL NOT NULL, " +
                    "$KEY_SALT REAL NOT NULL, " +
                    "$KEY_SERVING REAL NOT NULL," +
                    "FOREIGN KEY(${RecipeDBService.KEY_RECIP_NAME}) REFERENCES ${RecipeDBService.TABLE_RECIPE_NAMES}(" +
                        "${RecipeDBService.KEY_RECIP_NAME}) ON DELETE CASCADE ON UPDATE CASCADE," +
                    "PRIMARY KEY($KEY_INGREDIENT_NAME, ${RecipeDBService.KEY_RECIP_NAME}))")
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

        override fun ammendIngredient(dbHelper: FoodDBHelper, oldIngredient: Ingredient, newIngredient: Ingredient): Boolean {
            val db = dbHelper.writableDatabase
            val contentValues = ContentValues()
            val nut = newIngredient.nut
            contentValues.put(KEY_ENERGY, nut.energy)
            contentValues.put(KEY_FAT, nut.fat)
            contentValues.put(KEY_CARBS, nut.carbs)
            contentValues.put(KEY_FIBRE, nut.fibre)
            contentValues.put(KEY_PROTEIN, nut.protein)
            contentValues.put(KEY_SALT, nut.salt)
            contentValues.put(KEY_SERVING, nut.serving)
            contentValues.put(KEY_INGREDIENT_NAME, newIngredient.name)
            val whereClause = "$KEY_INGREDIENT_NAME='${oldIngredient.name}'"
            return db.update(TABLE_INGREDIENTS, contentValues, whereClause, null) > 0
        }

        override fun deleteIngredient(dbHelper: FoodDBHelper, ingredient: Ingredient): Boolean {
            val ingredient = getIngredient(dbHelper, ingredient.name)!!
            insertDeletedRecipeIngredient(dbHelper, ingredient)
            val db = dbHelper.writableDatabase
            return db.delete(TABLE_INGREDIENTS, "$KEY_INGREDIENT_NAME='${ingredient.name}'",
                null) > 0
        }

        fun getDeletedRecipeIngredients(dbHelper: FoodDBHelper, recName: String): Recipe?
        {
            var rec: Recipe? = null
            val db = dbHelper.readableDatabase
            val query = "SELECT * FROM $TABLE_RECIPES_W_DELETED_ING WHERE ${RecipeDBService.KEY_RECIP_NAME}" +
                    "='$recName'"
            val cursor = db.rawQuery(query, null)
            var dataLost: Boolean = false
            if (cursor.moveToFirst())
            {
                var name: String
                var energy: Float
                var fat: Float
                var carbs: Float
                var fibre: Float
                var protein: Float
                var salt: Float
                var serving: Float
                rec = Recipe(recName)
                do {
                    name= cursor.getString(cursor.getColumnIndex(KEY_INGREDIENT_NAME))
                    energy = cursor.getFloat(cursor.getColumnIndex(KEY_ENERGY))
                    fat = cursor.getFloat(cursor.getColumnIndex(KEY_FAT))
                    carbs = cursor.getFloat(cursor.getColumnIndex(KEY_CARBS))
                    fibre = cursor.getFloat(cursor.getColumnIndex(KEY_FIBRE))
                    protein = cursor.getFloat(cursor.getColumnIndex(KEY_PROTEIN))
                    salt = cursor.getFloat(cursor.getColumnIndex(KEY_SALT))
                    serving = cursor.getFloat(cursor.getColumnIndex(KEY_SERVING))
                    var                 ing = Ingredient(
                        name,
                        energy,
                        fat,
                        carbs,
                        fibre,
                        protein,
                        salt,
                        serving,
                        true
                    )
                    var recQty = cursor.getFloat(cursor.getColumnIndex(KEY_RECIPE_QTY))
                    try {
                        rec.ingList[ing] = recQty
                    }
                    catch (e: SQLiteException)
                    {
                        "SQL Insertion Errror inserting deleted ingredient.\ningredient: $ing" +
                                "\nrec: $rec\nSQLError Message:\n${e.toString()}"
                        deleteRecIng(dbHelper, recName, name)
                        dataLost = true
                    }
                } while (cursor.moveToNext())
                rec.hasDeleteIng = true
            }
            if (dataLost)
            {
                val retRec: Recipe? = null
                return retRec
            }
            return rec
        }

        fun deleteRecIng(dbHelper: FoodDBHelper, recName: String, ingName: String): Boolean
        {
            val db = dbHelper.writableDatabase
            return db.delete(TABLE_RECIPES_W_DELETED_ING, "${RecipeDBService.KEY_RECIP_NAME}" +
                    "='$recName AND $KEY_INGREDIENT_NAME='$ingName'", null) > 0
        }

        private fun insertDeletedRecipeIngredient(dbHelper: FoodDBHelper, ingredient: Ingredient)
        {
            var db = dbHelper.writableDatabase
            var contentValues = ContentValues()
            val nut = ingredient.nut
            for (recipe in getRecipesWithIngredient(dbHelper, ingredient))
            {
                val ingRecQty = recipe.ingList[ingredient]!!
                contentValues.put(KEY_ENERGY, nut.energy)
                contentValues.put(KEY_FAT, nut.fat)
                contentValues.put(KEY_CARBS, nut.carbs)
                contentValues.put(KEY_FIBRE, nut.fibre)
                contentValues.put(KEY_PROTEIN, nut.protein)
                contentValues.put(KEY_SALT, nut.salt)
                contentValues.put(KEY_SERVING, nut.serving)
                contentValues.put(KEY_INGREDIENT_NAME, ingredient.name)
                contentValues.put(RecipeDBService.KEY_RECIP_NAME, recipe.recName)
                contentValues.put(KEY_RECIPE_QTY, ingRecQty)
                try{
                    db.insertOrThrow(TABLE_RECIPES_W_DELETED_ING, null, contentValues)
                    Log.d("LOG", "Inserted recipe with deleted ing rec: $recipe")
                }
                catch (e: SQLiteException)
                {
                    Logger.add(
                        "SQL Insertion Errror during deleted recipe insertion.\ningredient: $ingredient" +
                                "\nrec: $recipe\nSQLError Message:\n${e.toString()}"
                    )
                    Logger.last()
                }
            }
        }

        private fun getRecipesWithIngredient(dbHelper: FoodDBHelper, ingredient: Ingredient):
            MutableList<Recipe>
        {
            return RecipeDBService.findRecipeWithIngredient(dbHelper, ingredient)
        }

        fun ammendRecDeleted(db: SQLiteDatabase, newRecName: String,
                             oldRecName: String, qty: Float, ingName: String): Boolean
        {
            val contentValues = ContentValues()
            val whereClause = "${RecipeDBService.KEY_RECIP_NAME}='$newRecName' AND " +
                    "$KEY_INGREDIENT_NAME='ingName'"
            contentValues.put(KEY_RECIPE_QTY, qty)
            contentValues.put(KEY_INGREDIENT_NAME, ingName)
            contentValues.put(RecipeDBService.KEY_RECIP_NAME, newRecName)
            db.delete(TABLE_RECIPES_W_DELETED_ING, whereClause, null)
            try {
                db.insertOrThrow(TABLE_RECIPES_W_DELETED_ING, null, contentValues)
            }
            catch (e: SQLiteException)
            {
                Log.d("LOG", "${this::class.java} SQL Error, failed to insert deleted" +
                        " recipe ing, rec: $newRecName, ing: $ingName\n${e.message}")
            }
            return true
        }

        fun delRecDelIng(db: SQLiteDatabase, ingToDelete:String): Boolean
        {
            val whereClause = "$KEY_INGREDIENT_NAME='$ingToDelete'"
            return db.delete(TABLE_RECIPES_W_DELETED_ING, whereClause, null) > 0
        }
    }
}