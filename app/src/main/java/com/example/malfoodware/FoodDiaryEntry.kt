package com.example.malfoodware

import java.util.*

class FoodDiaryEntry(var timeMillis: Long = Calendar.getInstance().timeInMillis): Comparable<FoodDiaryEntry>
{
    var bloodSugar: Float? = null
    var insulinTaken: Int? = null
    var notes: String? = null
    var recipes: SortedMap<Recipe, Float> = sortedMapOf()
    var ingredients: SortedMap<Ingredient, Float> = sortedMapOf()
    var dateString: String
    init {
        val date = Calendar.getInstance()
        date.timeInMillis = timeMillis
        val month = date.get(Calendar.MONTH) + 1
        val day = date.get(Calendar.DAY_OF_MONTH)
        val year = date.get(Calendar.YEAR)
        dateString = "$day/$month/$year"
    }

    fun addRecipe(rec: Recipe, qty: Float): Boolean
    {
        if (recipes.containsKey(rec)) return false
            recipes[rec] = qty
        return true
    }

    fun addIngredient(ing: Ingredient, qty: Float): Boolean
    {
        if (ingredients.containsKey(ing)) return false
            ingredients[ing] = qty
        return true
    }
//    var ingRec: MutableMap<String, Float> // contains ingredient/recipe id as key, weight as value

    override fun equals(other: Any?): Boolean {
        return other != null && other is FoodDiaryEntry && other.timeMillis == this.timeMillis
    }

    override fun compareTo(other: FoodDiaryEntry): Int {
        if (this.timeMillis > other.timeMillis) return 1 else return -1
    }

    override fun toString(): String {
        return "DiaryEntry[timeMillis: $timeMillis, date: $dateString, bloodsugar: $bloodSugar, " +
                "insulinTaken: $insulinTaken, notes: $notes, ingredientsNo: ${ingredients.size}, " +
                "recipeNo: ${recipes.size}]"
    }
}