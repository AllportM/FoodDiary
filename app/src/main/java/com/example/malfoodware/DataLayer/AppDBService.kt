package com.example.malfoodware.DataLayer

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.example.malfoodware.Logger
import kotlin.coroutines.Continuation

class AppDBService {
    companion object: AppStateHandler, DBInterface
    {
        val TABLE_APPSTATE = "AppState"
        val KEY_USER = "uidLoggedIn"
        val KEY_DATE = "dateSelected"

        /**
         * getLoggedInUser retrieves the last user logged in during the app's lifecycle
         */
        override fun getLoggedInUser(dbHelper: FoodDBHelper): String? {
            var result: String? = null
            val db = dbHelper.readableDatabase
            val query = "SELECT * FROM $TABLE_APPSTATE"
            val cursor: Cursor
            try {
                cursor = db.rawQuery(query, null)
                if (cursor.moveToFirst())
                    result = cursor.getString(cursor.getColumnIndex(KEY_USER))
            }
            catch (e: SQLiteException)
            {

            }
            return result
        }

        override fun getLoggedInSelectedDate(dbHelper: FoodDBHelper): String? {
            var result: String? = null
            val db = dbHelper.readableDatabase
            val query = "SELECT * FROM $TABLE_APPSTATE"
            val cursor: Cursor
            try {
                cursor = db.rawQuery(query, null)
                if (cursor.moveToFirst())
                    result = cursor.getString(cursor.getColumnIndex(KEY_DATE))
            }
            catch (e: SQLiteException)
            {

            }
            return result
        }

        /**
         * setLoggedInUser updates the last user logged in
         */
        override fun setLoggedInUser(dbHelper: FoodDBHelper, name: String, date: String) {
            val db = dbHelper.writableDatabase
            deleteTable(dbHelper.writableDatabase)
            onCreate(dbHelper.writableDatabase)
            val contentValues = ContentValues()
            contentValues.put(KEY_USER, name)
            contentValues.put(KEY_DATE, date)
            try {
                db.insertOrThrow(TABLE_APPSTATE, null, contentValues)
            }
            catch(e: SQLiteException) {
                Logger.add(
                    "SQL insertDiaryEntry insertion error whilst setting active user"
                )
            }
        }

        override fun onCreate(db: SQLiteDatabase?) {
            var CREATE_CONTENTS_TABLE = ("CREATE TABLE $TABLE_APPSTATE(" +
                    "$KEY_USER TEXT PRIMARY KEY," +
                    "$KEY_DATE TEXT" +
                    ")")
            db?.execSQL(CREATE_CONTENTS_TABLE)
        }

        override fun deleteTable(db: SQLiteDatabase) {
            DBHelper.deleteTable(db, TABLE_APPSTATE)
        }
    }
}
