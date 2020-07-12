package com.example.malfoodware.DataLayer

interface AppStateHandler {

    /**
     * getLoggedInUser retrieves the last user logged in during the app's lifecycle
     */
    fun getLoggedInUser(dbHelper: FoodDBHelper): String?

    /**
     * setLoggedInUser updates the last user logged in
     */
    fun setLoggedInUser(dbHelper: FoodDBHelper, name: String?)
}