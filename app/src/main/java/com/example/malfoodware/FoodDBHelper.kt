package com.example.malfoodware


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class FoodDBHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
{
    companion object{
        private val DATABASE_NAME = "FoodsDB"
        private val DATABASE_VERSION = 1
        private val TABLE_USERS = "Users"
        private val TABLE_RECIPES = "Recipes"
        private val TABLE_INGREDIENTS = "Ingredients"
        private val TABLE_NUTRITIONS = "Nutritions"
        private val TABLE_DiaryEntries = "DiaryEntries"
        private val KEY_ID = "id"
        private val KEY_NAME = "name"
        private val KEY_INGREDIENT_ID = "ingID"
        private val KEY_ENERGY = "energy"
        private val KEY_FAT = "fat"
        private val KEY_CARBS = "carbs"
        private val KEY_FIBRE = "fibre"
        private val KEY_PROTEIN = "protein"
        private val KEY_SALT = "salt"
        private val KEY_SERVING = "serving"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        var CREATE_CONTENTS_TABLE = ("CREATE TABLE $TABLE_USERS(" +
                "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$KEY_NAME TEXT NOT NULL)")
        db?.execSQL(CREATE_CONTENTS_TABLE)
        CREATE_CONTENTS_TABLE = ("CREATE TABLE $TABLE_INGREDIENTS(" +
                "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$KEY_NAME TEXT COLLATE NOCASE NOT NULL," +
                "UNIQUE($KEY_NAME))")
        db?.execSQL(CREATE_CONTENTS_TABLE)
        CREATE_CONTENTS_TABLE = ("CREATE TABLE $TABLE_NUTRITIONS(" +
                "$KEY_INGREDIENT_ID INTEGER PRIMARY KEY, " +
                "$KEY_ENERGY REAL NOT NULL, " +
                "$KEY_FAT REAL NOT NULL, " +
                "$KEY_CARBS REAL NOT NULL, " +
                "$KEY_FIBRE REAL NOT NULL, " +
                "$KEY_PROTEIN REAL NOT NULL, " +
                "$KEY_SALT REAL NOT NULL, " +
                "$KEY_SERVING REAL NOT NULL, " +
                "FOREIGN KEY($KEY_INGREDIENT_ID) REFERENCES $TABLE_INGREDIENTS($KEY_ID))")
        db?.execSQL(CREATE_CONTENTS_TABLE)
        CREATE_CONTENTS_TABLE = ("CREATE TABLE $TABLE_RECIPES(" +
                "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$KEY_NAME NOT NULL, " +
                "$KEY_INGREDIENT_ID INTEGER, " +
                "$KEY_SERVING REAL NOT NULL, " +
                "FOREIGN KEY($KEY_INGREDIENT_ID) REFERENCES $TABLE_INGREDIENTS($KEY_ID), " +
                "UNIQUE($KEY_ID, $KEY_INGREDIENT_ID))")
        db?.execSQL(CREATE_CONTENTS_TABLE)
    }

    fun deleteAll(){
        var db = this.writableDatabase
        val c =
            db!!.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
        val tables: MutableList<String> = ArrayList()

        // iterate over the result set, adding every table name to a list
        while (c.moveToNext()) {
            tables.add(c.getString(0))
        }

        // call DROP TABLE on every table name
        for (table in tables) {
            try {
                val dropQuery = "DROP TABLE IF EXISTS $table"
                db!!.execSQL(dropQuery)
            } catch (e: SQLiteException){ continue }
        }
        onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        val c =
            db!!.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
        val tables: MutableList<String> = ArrayList()

    // iterate over the result set, adding every table name to a list

    // iterate over the result set, adding every table name to a list
        while (c.moveToNext()) {
            tables.add(c.getString(0))
        }

    // call DROP TABLE on every table name

    // call DROP TABLE on every table name
        for (table in tables) {
            try {
                val dropQuery = "DROP TABLE IF EXISTS $table"
                db!!.execSQL(dropQuery)
            } catch (e: SQLiteException){ continue }
        }
        onCreate(db)
    }

    fun getIngredients(): MutableMap<String, Int>
    {
        val result: MutableMap<String, Int> = mutableMapOf()
        val db = this.writableDatabase
        val query = "SELECT * FROM $TABLE_INGREDIENTS"
        var cursor: Cursor?
        cursor = db.rawQuery(query, null)
        var ingID: Int
        var ingName: String
        if (cursor.moveToFirst())
        {
            do {
                ingID = cursor.getInt(cursor.getColumnIndex("$KEY_ID"))
                ingName = cursor.getString(cursor.getColumnIndex("$KEY_NAME"))
                result[ingName] = ingID
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }

    fun insertIngredient(name: String): Boolean
    {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, name)
        var success: Long = -1
        try {
            success = db.insert(TABLE_INGREDIENTS, null, contentValues)
        } catch (e: SQLiteException) {}
        return success > -1
    }
}