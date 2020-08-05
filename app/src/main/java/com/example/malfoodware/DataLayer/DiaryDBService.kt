package com.example.malfoodware.DataLayer

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.example.malfoodware.FoodDiaryEntry
import com.example.malfoodware.Ingredient
import com.example.malfoodware.Logger
import com.example.malfoodware.Recipe
import java.util.*

class DiaryDBService {
    companion object: DiaryDataHandler, DBInterface
    {
        private val TABLE_DIARY_ENTRY = "Diary_Entries"
        private val TABLE_DIARY_ENTRY_RECIPES = "Diary_Entry_Recipes"
        private val TABLE_DIARY_ENTRY_INGREDIENTS = "Diary_Entry_Ingredients"
        private val KEY_DATE = "dateDDMMYY"
        private val KEY_DIARY_ID = "diaryID"
        private var KEY_TIME_MILLIS = "timeMillis"
        private var KEY_BLOOD_SUGAR = "bloodSugar"
        private var KEY_INSULIN = "insulin"
        private var KEY_NOTES = "notes"
        private var KEY_SERVING = "qty"
        private var KEY_UID = "uid"

        override fun onCreate(db: SQLiteDatabase?) {
            var CREATE_CONTENTS_TABLE = ("CREATE TABLE $TABLE_DIARY_ENTRY(" +
                    "$KEY_DIARY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$KEY_UID TEXT NOT NULL COLLATE NOCASE," +
                    "$KEY_TIME_MILLIS INTEGER NOT NULL," +
                    "$KEY_DATE TEXT NOT NULL," +
                    "$KEY_BLOOD_SUGAR REAL," +
                    "$KEY_INSULIN INTEGER," +
                    "$KEY_NOTES TEXT," +
                    "CONSTRAINT fk_ui FOREIGN KEY($KEY_UID) REFERENCES ${UserDBService.TABLE_USERS}(" +
                        "${UserDBService.KEY_USER})," +
                    "UNIQUE($KEY_UID, $KEY_TIME_MILLIS))")
            db?.execSQL(CREATE_CONTENTS_TABLE)
            CREATE_CONTENTS_TABLE = ("CREATE TABLE $TABLE_DIARY_ENTRY_INGREDIENTS(" +
                    "$KEY_DIARY_ID INTEGER NOT NULL," +
                    "${IngredientDBService.KEY_INGREDIENT_NAME} TEXT COLLATE NOCASE NOT NULL," +
                    "$KEY_SERVING REAL NOT NULL," +
                    "CONSTRAINT fk_ingredient FOREIGN KEY(${IngredientDBService.KEY_INGREDIENT_NAME})" +
                        " REFERENCES ${IngredientDBService.TABLE_INGREDIENTS}(${IngredientDBService.KEY_INGREDIENT_NAME}) " +
                        "ON DELETE CASCADE ON UPDATE CASCADE, " +
                    "CONSTRAINT fk_diaryID FOREIGN KEY($KEY_DIARY_ID) REFERENCES " +
                        "$TABLE_DIARY_ENTRY($KEY_DIARY_ID) ON DELETE CASCADE ON UPDATE CASCADE, " +
                    "PRIMARY KEY($KEY_DIARY_ID, ${IngredientDBService.KEY_INGREDIENT_NAME}))")
            db?.execSQL(CREATE_CONTENTS_TABLE)
            CREATE_CONTENTS_TABLE = ("CREATE TABLE $TABLE_DIARY_ENTRY_RECIPES(" +
                    "$KEY_DIARY_ID INTEGER NOT NULL," +
                    "${RecipeDBService.KEY_RECIP_NAME} TEXT COLLATE NOCASE NOT NULL," +
                    "$KEY_SERVING REAL NOT NULL," +
                    "CONSTRAINT fk_recipe2 FOREIGN KEY(${RecipeDBService.KEY_RECIP_NAME}) " +
                        "REFERENCES ${RecipeDBService.TABLE_RECIPE_NAMES}(${RecipeDBService.KEY_RECIP_NAME}) " +
                        "ON DELETE CASCADE ON UPDATE CASCADE, " +
                    "CONSTRAINT fk_diaryID FOREIGN KEY($KEY_DIARY_ID) REFERENCES " +
                        "$TABLE_DIARY_ENTRY($KEY_DIARY_ID) ON DELETE CASCADE ON UPDATE CASCADE, " +
                    "PRIMARY KEY($KEY_DIARY_ID, ${RecipeDBService.KEY_RECIP_NAME}))")
            db?.execSQL(CREATE_CONTENTS_TABLE)
        }

        override fun deleteTable(db: SQLiteDatabase) {
            DBHelper.deleteTable(db, TABLE_DIARY_ENTRY_RECIPES)
            DBHelper.deleteTable(db, TABLE_DIARY_ENTRY_INGREDIENTS)
            DBHelper.deleteTable(db, TABLE_DIARY_ENTRY)
        }

        override fun insertDiaryEntry(dbHelper: FoodDBHelper, entry: FoodDiaryEntry, uid: String): Boolean {
            val db = dbHelper.writableDatabase
            db.beginTransaction()
            var contentValues: ContentValues
            // insert into Diary Entry table
            try {
                contentValues = ContentValues()
                contentValues.put(KEY_UID, uid)
                contentValues.put(KEY_TIME_MILLIS, entry.timeMillis)
                contentValues.put(KEY_DATE, entry.dateString)
                if (entry.bloodSugar != null)
                    contentValues.put(KEY_BLOOD_SUGAR, entry.bloodSugar)
                if (entry.insulinTaken != null)
                    contentValues.put(KEY_INSULIN, entry.insulinTaken)
                if (entry.notes != null)
                    contentValues.put(KEY_NOTES, entry.notes)
                db.insertOrThrow(TABLE_DIARY_ENTRY, null, contentValues)
            }
            catch (e: SQLiteException)
            {
                db.endTransaction()
                Logger.add(
                    "SQL insertDiaryEntry insertion error whilst inserting main id\nuid: $uid\nentry: $entry\n$e"
                )
                return false
            }
            var diaryId = getDiaryEntryID(dbHelper, entry.timeMillis, uid)
            // Insert into Ingredients Table
            try {
                contentValues = ContentValues()
                for (ing in entry.ingredients)
                {
                    contentValues.put(KEY_DIARY_ID, diaryId)
                    contentValues.put(IngredientDBService.KEY_INGREDIENT_NAME, ing.key.name)
                    contentValues.put(KEY_SERVING, ing.value)
                    db.insertOrThrow(TABLE_DIARY_ENTRY_INGREDIENTS, null, contentValues)
                }
            }
            catch (e: SQLiteException)
            {
                db.endTransaction()
                Logger.add(
                    "SQL insertDiaryEntry insertion error whilst inserting ingredients\nuid: $uid\nentry: $entry\n$e"
                )
                return false
            }
            // Insert into Recipes Table
            try {
                contentValues = ContentValues()
                for (rec in entry.recipes)
                {
                    contentValues.put(KEY_DIARY_ID, diaryId)
                    contentValues.put(RecipeDBService.KEY_RECIP_NAME, rec.key.recName)
                    contentValues.put(KEY_SERVING, rec.value)
                    db.insertOrThrow(TABLE_DIARY_ENTRY_RECIPES, null, contentValues)
                }
            }
            catch (e: SQLiteException)
            {
                db.endTransaction()
                Logger.add(
                    "SQL insertDiaryEntry insertion error whilst inserting recipes\nuid: $uid\nentry: $entry\n$e"
                )
                return false
            }
            db.setTransactionSuccessful()
            db.endTransaction()
            return true
        }

        private fun getDiaryEntryID(dbHelper: FoodDBHelper, timeMillis: Long, uid: String): Int?
        {
            var db = dbHelper.readableDatabase
            var query = "SELECT * FROM $TABLE_DIARY_ENTRY WHERE $KEY_TIME_MILLIS=$timeMillis AND " +
                    "$KEY_UID='$uid'"
            var result: Int? = null
            var cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst())
                result = cursor.getInt(cursor.getColumnIndex(KEY_DIARY_ID))
            cursor.close()
            return result
        }

        private fun getDiaryIngredients(dbHelper: FoodDBHelper, diaryId: Int):
                SortedMap<Ingredient, Float>
        {
            var result: SortedMap<Ingredient, Float>  = sortedMapOf()
            val db = dbHelper.readableDatabase
            val query = "SELECT * FROM $TABLE_DIARY_ENTRY_INGREDIENTS WHERE $KEY_DIARY_ID=$diaryId"
            val cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst())
            {
                do {
                    val ingId = cursor.getString(cursor.getColumnIndex(IngredientDBService.KEY_INGREDIENT_NAME))
                    val ing = IngredientDBService.getIngredient(dbHelper, ingId)
                    result[ing] = cursor.getFloat(cursor.getColumnIndex(KEY_SERVING))
                }
                    while (cursor.moveToNext())
            }
            cursor.close()
            return result
        }

        private fun getDiaryRecipes(dbHelper: FoodDBHelper, diaryId: Int):
                SortedMap<Recipe, Float>
        {
            var result: SortedMap<Recipe, Float>  = sortedMapOf()
            val db = dbHelper.readableDatabase
            val query = "SELECT * FROM $TABLE_DIARY_ENTRY_RECIPES WHERE $KEY_DIARY_ID=$diaryId"
            val cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst())
            {
                do {
                    val recId = cursor.getString(cursor.getColumnIndex(RecipeDBService.KEY_RECIP_NAME))
                    val rec = RecipeDBService.getRecipe(dbHelper, recId)
                    result[rec] = cursor.getFloat(cursor.getColumnIndex(KEY_SERVING))
                }
                while (cursor.moveToNext())
            }
            cursor.close()
            return result
        }

        override fun getDiaryEntriesDate(dbHelper: FoodDBHelper, uid: String, date: String): SortedSet<FoodDiaryEntry> {
            val db = dbHelper.readableDatabase
            val query = "SELECT * FROM $TABLE_DIARY_ENTRY WHERE $KEY_DATE='$date' AND $KEY_UID='$uid'"
            var cursor = db.rawQuery(query, null)
            var result: SortedSet<FoodDiaryEntry> = sortedSetOf()
            if (cursor.moveToFirst())
            {
                do {
                    val entryID = cursor.getInt(cursor.getColumnIndex(KEY_DIARY_ID))
                    val timeMillis = cursor.getLong(cursor.getColumnIndex(KEY_TIME_MILLIS))
                    val insulin = cursor.getInt(cursor.getColumnIndex(KEY_INSULIN))
                    val bloodSugar = cursor.getFloat(cursor.getColumnIndex(KEY_BLOOD_SUGAR))
                    val notes = cursor.getString(cursor.getColumnIndex(KEY_NOTES))
                    val entry = FoodDiaryEntry(timeMillis)
                    if (!cursor.isNull(cursor.getColumnIndex(KEY_INSULIN)))
                        entry.insulinTaken = insulin
                    if (!cursor.isNull(cursor.getColumnIndex(KEY_BLOOD_SUGAR)))
                        entry.bloodSugar = bloodSugar
                    entry.notes = notes
                    entry.ingredients = getDiaryIngredients(dbHelper, entryID)
                    entry.recipes = getDiaryRecipes(dbHelper, entryID)
                    result.add(entry)
                }
                while (cursor.moveToNext())
            }
            cursor.close()
            return result
        }

        override fun getDiaryEntriesDateRange(dbHelper: FoodDBHelper, uid: String, from: Long, to: Long): SortedSet<FoodDiaryEntry> {
            val db = dbHelper.readableDatabase
            var result: SortedSet<FoodDiaryEntry> = sortedSetOf()
            val query = "SELECT * FROM $TABLE_DIARY_ENTRY WHERE $KEY_TIME_MILLIS>='$from' AND $KEY_TIME_MILLIS<='$to' " +
                    "AND $KEY_UID='$uid'"
            var cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    val entryID = cursor.getInt(cursor.getColumnIndex(KEY_DIARY_ID))
                    val timeMillis = cursor.getLong(cursor.getColumnIndex(KEY_TIME_MILLIS))
                    val insulin = cursor.getInt(cursor.getColumnIndex(KEY_INSULIN))
                    val bloodSugar = cursor.getFloat(cursor.getColumnIndex(KEY_BLOOD_SUGAR))
                    val notes = cursor.getString(cursor.getColumnIndex(KEY_NOTES))
                    val entry = FoodDiaryEntry(timeMillis)
                    if (!cursor.isNull(cursor.getColumnIndex(KEY_INSULIN)))
                        entry.insulinTaken = insulin
                    if (!cursor.isNull(cursor.getColumnIndex(KEY_BLOOD_SUGAR)))
                        entry.bloodSugar = bloodSugar
                    entry.notes = notes
                    entry.ingredients = getDiaryIngredients(dbHelper, entryID)
                    entry.recipes = getDiaryRecipes(dbHelper, entryID)
                    result.add(entry)
                } while (cursor.moveToNext())
            }
            else
            {
                Logger.add("SQL No Results for Diary Entries between range $from - $to")
            }
            cursor.close()
            return result
        }


    }
}