package com.example.malfoodware

class Recipe (var recName:String): Comparable<Recipe>{
    val ingList: MutableMap<Ingredient, Float> = mutableMapOf()

    fun addIngredient(ing: Ingredient, qty: Float)
    {
        ingList[ing] = qty
    }

    fun getNutrition(): Nutrition?
    {
        var nutrition: Nutrition? = null
        for (ingredient in ingList)
        {
            if (nutrition == null)
            {
                nutrition = ingredient.key.nut / ingredient.value
            }
            else
                nutrition.plusAssign(ingredient.key.nut / ingredient.value)
        }
        return nutrition
    }

    override fun equals(other: Any?): Boolean {
        return other is Recipe && other.recName == (recName)
    }

    override fun hashCode(): Int {
        return recName.hashCode()
    }

    override fun compareTo(other: Recipe): Int {
        if (this.recName > other.recName) return 1 else return -1
    }

    fun toCSVString(): String
    {
        var output = ""
        output += "$recName,"
        for (ing in ingList)
        {
            output += ing.key.toString() + "," + ing.value
        }
        output = output.substring(0, output.length-1)
        return output
    }

    override fun toString(): String {
        var output: String= "Recipe[name: $recName, Ingredients: ["
        for (i in ingList)
        {
            output += "(ing: ${i.key}, qty: ${i.value}), "
        }
        if (ingList.size > 0)
        {
            output = output.substring(0, output.length - 2)
        }
        output += "]"
        return output
    }
}