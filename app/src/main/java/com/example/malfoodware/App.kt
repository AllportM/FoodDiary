package com.example.malfoodware

import android.content.Context
import android.util.Log
import com.example.malfoodware.DataLayer.FoodDBHelper
import java.util.*
import kotlin.collections.ArrayList
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
    fun getListOfEntries(date: String): MutableList<FoodDiaryEntry>
    {
        var result: MutableList<FoodDiaryEntry> = mutableListOf()
        val entries = dbHelper.getDiaryEntriesDate(user!!.uid!!, date)
        result.addAll(entries)
        return result
    }


    fun getAllDataJSON()
    {
        val ingredients = getIngredientsJSON()
        val recipes = getRecipesJSON()
        val users = getUsersJSON()
        val entries = getEntriesJSON()
        Log.d("LOG", "Ingrediens json: \n$ingredients")
        Log.d("LOG", "Recipes json: \n$recipes")
        Log.d("LOG", "Users json: \n$users")
        Log.d("LOG", "Entries json: \n$entries")
    }

    fun getUsersJSON(): String
    {
        val users = dbHelper.getUsers()
        var result = "[\n"
        for (user in users)
        {
            result += dbHelper.getUser(user)?.toJSON(1) + ",\n"
        }
        if (users.isNotEmpty())
        {
            result = result.substring(0,  result.length-2) + "\n"
        }
        result += "]"
        return result
    }

    private fun getIngredientsJSON(): String
    {
        val ingredients = dbHelper.getIngredients()
        var result = "[\n"
        for (ingredient in ingredients)
        {
            result += dbHelper.getIngredient(ingredient)?.toJSON(1) + ",\n"
        }
        if (ingredients.isNotEmpty())
        {
            result = result.substring(0,  result.length-2) + "\n"
        }
        result += "]"
        return result
    }

    private fun getRecipesJSON(): String
    {
        val recipes = dbHelper.getRecipes()
        var result = "[\n"
        for (recipe in recipes)
        {
            result += dbHelper.getRecipe(recipe)?.toJSON(1) + ",\n"
        }
        if (recipes.isNotEmpty())
        {
            result = result.substring(0,  result.length-2) + "\n"
        }
        result += "]"
        return result
    }

    private fun getEntriesJSON(): String
    {
        val users = dbHelper.getUsers()
        var result = "[\n"
        for (user in users)
        {
            result += "${addTabs(1)}{\n${addTabs(2)}\"uid\": \"$user\",\n" +
                    "${addTabs(2)}\"entries\": [\n"
            val entries = dbHelper.getDiaryEntriesDateRange(user, "0/0/0",
                "31/12/5000")
            for (entry in entries)
            {
                result += entry.toJSON(3) + ",\n"
            }
            if (entries.isNotEmpty())
            {
                result = result.substring(0,  result.length-2) + "\n"
            }
            result += "${addTabs(2)}]\n${addTabs(1)}},\n"
        }
        if (users.isNotEmpty())
        {
            result = result.substring(0,  result.length-2) + "\n"
        }
        result += "]"
        return result
    }
}