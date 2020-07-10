package com.example.malfoodware.DataLayer

import com.example.malfoodware.User

interface UserDataHandler {
    /**
     * getUser returns an User data member for a given user name
     * from the database class
     *
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     *      uid: String, the name of the User to search for
     * @return
     *      User?: either the user, or a null ingredient if either
     *          an sql error has occurred or no results exist, any errors are logged via the
     *          Logger class
     */
    fun getUser(dbHelper: FoodDBHelper, uid: String): User?

    /**
     * insertUser attempts to insert a user into the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     *      user: User, the user to be added
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *          class
     */
    fun insertUser(dbHelper: FoodDBHelper, user: User): Boolean

    /**
     * ammendUser attempts to update a user into the database, returning a boolean
     * indicating success or failure
     *
     * @param
     *      dbHelper: FoodDBHelper, the active sql connection class
     *      user: User, the user to be altered
     * @return
     *      Boolean: True if successful, false if any errors, errors are logged via the Logger
     *          class
     */
    fun ammendUser(dbHelper: FoodDBHelper, user: User): Boolean
}