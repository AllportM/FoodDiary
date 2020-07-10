package com.example.malfoodware.DataLayer

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.example.malfoodware.Logger
import com.example.malfoodware.SQLInjectionError

class DBHelper {
    companion object {
        fun deleteTable(db: SQLiteDatabase, tableName: String) {
            try {
                var dropQuery = "DROP TABLE IF EXISTS $tableName"
                db.execSQL(dropQuery)
            } catch (e: SQLiteException) {
                Logger.add("SQLError - Failed to drop table $tableName\n$e")
            }
        }

        @Throws(SQLInjectionError::class)

        fun checkInjection(message: String): Boolean
        {
            if (message.contains(';')) return true
            return false
        }
    }
}