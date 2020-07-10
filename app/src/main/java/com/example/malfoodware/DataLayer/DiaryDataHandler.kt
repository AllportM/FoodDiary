package com.example.malfoodware.DataLayer

import android.database.sqlite.SQLiteDatabase
import com.example.malfoodware.FoodDiaryEntry
import java.util.*

interface DiaryDataHandler {
    /**
     * insertDiaryEntry attempts to insert an entry into the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     *      entry: FoodDiaryEntry, the entry to be added
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *      class
     */
    fun insertDiaryEntry(dbHelper: FoodDBHelper, entry: FoodDiaryEntry, uid: String): Boolean

    /**
     * getDiaryEntriesDate returns a set of FoodDiaryEntry's for a given date string in the format
     * of DD/MM/YYYY excluding leading 0's
     *
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     *      date: String, the given date string in the format of DD/MM/YYYY excluding leading 0's
     * @return
     *      SortedSet<FoodDiaryEntry>: a set either empty or full dependant upon entries matching
     *          the date
     */
    fun getDiaryEntriesDate(dbHelper: FoodDBHelper, date: String): SortedSet<FoodDiaryEntry>

    /**
     * getDiaryEntriesDateRange returns a set of FoodDiaryEntry's for a given date range
     *
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     *      from: String, the given lower bound date string in the format of DD/MM/YYYY
     *          excluding leading 0's
     *      to: String, the given upper bound date string in the format of DD/MM/YYYY
     *          excluding leading 0's
     * @return
     *      SortedSet<FoodDiaryEntry>: a set either empty or full dependant upon entries matching
     *           the date
     */
    fun getDiaryEntriesDateRange(dbHelper: FoodDBHelper, from: String, to: String): SortedSet<FoodDiaryEntry>
}