package com.example.malfoodware

import java.lang.NumberFormatException

/**
 * User's purpose is to contain data members correlating user file names, paths, settings. Contained
 * are also member functions to import recipes from given users csv files, or creates empty, aswell
 * as importing users ingredients, which are stored within member tree sets
 */
class User (var uid: String){
    // settings
    var INSULIN_PER10G: Float = 10f
    var nutritionPerDay = Nutrition(2000f, 70f, 260f, 30f, 50f,
    6f, 1f)

    override fun toString(): String {
        return "User[id: $uid, insulin_per10g: $INSULIN_PER10G, energy: ${nutritionPerDay.energy}," +
                "fat: ${nutritionPerDay.fat}, carbs: ${nutritionPerDay.carbs}, fibre: " +
                "${nutritionPerDay.fibre}, protein: ${nutritionPerDay.protein}, salt: " +
                "${nutritionPerDay.salt}]"
    }

    fun copy(): User
    {
        val user = User(uid)
        user.INSULIN_PER10G = INSULIN_PER10G
        user.nutritionPerDay = nutritionPerDay.copy()
        return user
    }
}