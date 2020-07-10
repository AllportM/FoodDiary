package com.example.malfoodware.DataLayer

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.example.malfoodware.Logger
import com.example.malfoodware.Nutrition
import com.example.malfoodware.User

class UserDBService {
    companion object: UserDataHandler, DBInterface
    {
        val TABLE_USERS = "Users"
        val KEY_USER = "uid"
        val KEY_INS = "insPer10g"
        val KEY_ENERGY = "energy"
        val KEY_FAT = "fat"
        val KEY_CARBS = "carbs"
        val KEY_FIBRE = "fibre"
        val KEY_PROTEIN = "protein"
        val KEY_SALT = "salt"

        override fun onCreate(db: SQLiteDatabase?) {
            val CREATE_CONTENTS_TABLE = ("CREATE TABLE IF NOT EXISTS $TABLE_USERS(" +
                    "$KEY_USER TEXT PRIMARY KEY COLLATE NOCASE," +
                    "$KEY_INS REAL NOT NULL," +
                    "$KEY_ENERGY REAL NOT NULL," +
                    "$KEY_FAT REAL NOT NULL," +
                    "$KEY_CARBS REAL NOT NULL," +
                    "$KEY_FIBRE REAL NOT NULL," +
                    "$KEY_PROTEIN REAL NOT NULL," +
                    "$KEY_SALT REAL NOT NULL)")
            db?.execSQL(CREATE_CONTENTS_TABLE)
        }

        override fun deleteTable(db: SQLiteDatabase) {
            DBHelper.deleteTable(db, TABLE_USERS)
        }

        override fun getUser(dbHelper: FoodDBHelper, uid: String): User? {
            val db = dbHelper.readableDatabase
            val query = "SELECT * FROM $TABLE_USERS WHERE $KEY_USER='$uid'"
            var user: User? = null
            if (DBHelper.checkInjection(uid))
            {
                Logger.add(
                    "SQL getUser query error with injection character detected ';'\n" +
                            "uid: $uid"
                )
                return user
            }
            val cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst())
            {
                val ins = cursor.getFloat(cursor.getColumnIndex(KEY_INS))
                val energy = cursor.getFloat(cursor.getColumnIndex(KEY_ENERGY))
                val fat = cursor.getFloat(cursor.getColumnIndex(KEY_FAT))
                val carbs = cursor.getFloat(cursor.getColumnIndex(KEY_CARBS))
                val fibre = cursor.getFloat(cursor.getColumnIndex(KEY_FIBRE))
                val protein = cursor.getFloat(cursor.getColumnIndex(KEY_PROTEIN))
                val salt = cursor.getFloat(cursor.getColumnIndex(KEY_SALT))
                user = User(uid)
                val nut = Nutrition(energy, fat, carbs, fibre, protein, salt, 1f)
                user.nutritionPerDay = nut
                user.INSULIN_PER10G = ins
            }
            return user
        }

        override fun insertUser(dbHelper: FoodDBHelper, user: User): Boolean {
            if (DBHelper.checkInjection(user.uid))
            {
                Logger.add(
                    "SQL insertUser query error with injection character detected ';'\n" +
                            "uid: ${user.uid}"
                )
                return false
            }
            val db = dbHelper.writableDatabase
            try {
                val contentValues = ContentValues()
                contentValues.put(KEY_USER, user.uid)
                contentValues.put(KEY_INS, user.INSULIN_PER10G)
                contentValues.put(KEY_ENERGY, user.nutritionPerDay.energy)
                contentValues.put(KEY_FAT, user.nutritionPerDay.fat)
                contentValues.put(KEY_CARBS, user.nutritionPerDay.carbs)
                contentValues.put(KEY_FIBRE, user.nutritionPerDay.fibre)
                contentValues.put(KEY_PROTEIN, user.nutritionPerDay.protein)
                contentValues.put(KEY_SALT, user.nutritionPerDay.salt)
                db.insertOrThrow(TABLE_USERS, null, contentValues)
            }
            catch (e: SQLiteException)
            {
                Logger.add(
                    "SQL insertUser whilst adding user ';'\n" +
                            "user: $user"
                )
                return false
            }
            return true
        }

        override fun ammendUser(dbHelper: FoodDBHelper, user: User): Boolean {
            var db = dbHelper.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(KEY_SALT, user.nutritionPerDay.salt)
            contentValues.put(KEY_PROTEIN, user.nutritionPerDay.protein)
            contentValues.put(KEY_FIBRE, user.nutritionPerDay.fibre)
            contentValues.put(KEY_CARBS, user.nutritionPerDay.carbs)
            contentValues.put(KEY_FAT, user.nutritionPerDay.fat)
            contentValues.put(KEY_ENERGY, user.nutritionPerDay.energy)
            contentValues.put(KEY_INS, user.INSULIN_PER10G)
            var retVal: Int = 0
            try {
                retVal = db.update(
                    "$TABLE_USERS", contentValues, "$KEY_USER" +
                            "='${user.uid}'", null
                )
            } catch (e: SQLiteException) {
                Logger.add(
                    "SQL updateUser whilst updating user entry\n" +
                            "user: $user\n${e.message}"

                )
            }
            if (retVal > 0) return true
            Logger.last()
            return false
        }

    }
}