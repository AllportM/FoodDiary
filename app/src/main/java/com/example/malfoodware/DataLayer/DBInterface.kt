package com.example.malfoodware.DataLayer

import android.database.sqlite.SQLiteDatabase

interface DBInterface {
    fun onCreate(db: SQLiteDatabase?)
    fun deleteTable(db: SQLiteDatabase)
}