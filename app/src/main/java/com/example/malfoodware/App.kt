package com.example.malfoodware

import android.content.Context
import com.example.malfoodware.DataLayer.FoodDBHelper
import java.util.*
import kotlin.math.ln


class App (val context:Context){
    var user: User? = null
    val dbHelper = FoodDBHelper(context)

    init
    {
        // initializes filehelper context and loads users file
        FileHelper.context = context
    }

    /**
     * Creates user from given uname by scanning users file for login name (uid) or throws exception
     * if no such user exists
     * @param: String, uid
     * @return: Boolean, true if login, false otherwise
     */
    fun login(uname: String): Boolean
    {
        user = dbHelper.getUser(uname)
        return user != null
    }
}