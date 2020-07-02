package com.example.malfoodware

import android.content.Context
import kotlin.math.ln


class App (val context:Context? = null){
    companion object {
        val USERS = "users.txt"
        val USERS_DIR = "res"
        val GLOBALS_DIR = "res/globals"
        var DEBUG = false

        val logger: Logger = Logger("log.txt")
    }

    lateinit var user: User

    init
    {
        // initializes filehelper context and loads users file
        FileHelper.context = context
        FileHelper.loadFile(USERS, USERS_DIR)
    }

    /**
     * Creates user from given uname by scanning users file for login name (uid) or throws exception
     * if no such user exists
     * @param: String, uid
     * @return: Boolean, true if login, false otherwise
     */
    fun login(uname: String): Boolean
    {
        var users = FileHelper.getFileContentsArr(USERS)
        var localUname = ""
        users.forEach {  i -> if (i.toLowerCase().equals(uname.toLowerCase())) localUname = i }
        if (localUname.equals("")) return false
        else
        {
            user = User(uname)
            return true
        }
    }
}

fun main(args: Array<String>)
{
    println("App says hello")
    val app = App(null)
    if (app.login("Mikehboi")) println(app.user.uid)
    var arr: MutableMap<String, Recipe> = mutableMapOf()
    println(JSONParser.parseJSON(arr, app.user.USER_RECIPES))


//    var ingredients = getFileContents("ingredients.txt", "resources")
//    println(ingredients)
//    var individuals = ingredients[0].split(",")
//    println(individuals)
}