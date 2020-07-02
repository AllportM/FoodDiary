package com.example.malfoodware

class Recipe (val recID: String, val recName:String): Comparable<Recipe>{
    val ingList: MutableMap<String, Float> = mutableMapOf()

    fun addIngredient(ingId: String, qty: Float)
    {
        ingList[ingId] = qty
    }

//    fun getNutritionalValue(ingredients: MutableMap<String, Ingredient>): Nutrition
//    {
//        var nutrition: Nutrition? = null
//        for (id in ingList)
//        {
//            if(ingredients[id] != null)
//            {
//                if (nutrition == null)
//                {
//                    nutrition = ingredients[id]!!.nut.copy()
//                    continue
//                }
//                nutrition.plusAssign(ingredients[id]!!.nut)
//            }
//        }
//        return if (nutrition != null) nutrition
//        else Nutrition(0f,0f,0f,0f,0f,0f,0.01f)
//    }

    override fun equals(other: Any?): Boolean {
        return other is Recipe && other.recID.equals(recID)
    }

    override fun hashCode(): Int {
        return recID.hashCode()
    }

    override fun compareTo(other: Recipe): Int {
        if (other.equals(this)) return 1 else return -1
    }

    fun toCSVString(): String
    {
        var output = ""
        output += "$recID,$recName,"
        for (ing in ingList)
        {
            output += ing.key + "," + ing.value
        }
        output = output.substring(0, output.length-1)
        return output
    }
}