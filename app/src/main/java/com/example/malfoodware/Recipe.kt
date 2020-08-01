package com.example.malfoodware

class Recipe (var recName:String, val portion: Int = 1, var hasDeleteIng: Boolean = false): Comparable<Recipe>, FoodAccess{
    val ingList: MutableMap<Ingredient, Float> = mutableMapOf()

    val type = FoodType.RECIPE

    override fun whatName(): String {
        return recName
    }

    override fun whatType(): FoodType {
        return type
    }

    override fun whatServing(): Float{
        var nut = getNutrition()
        return nut.serving / portion.toFloat()
    }

    override fun whatNutirion(): Nutrition {
        return getNutrition()
    }

    override fun hasDeleted(): Boolean {
        return hasDeleteIng
    }

    fun addIngredient(ing: Ingredient, qty: Float)
    {
        ingList[ing] = qty
    }

    fun getNutrition(): Nutrition
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
        if (nutrition == null)
            nutrition = Nutrition()
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


    fun toJSON(tabs: Int): String
    {
        var newTabs = tabs + 1
        var result = "${addTabs(tabs)}{\n${addTabs(newTabs)}\"name\": \"$recName\",\n${addTabs(newTabs)}" +
                "\"portion\": $portion,\n${addTabs(newTabs)}\"hasDeleteIng\": $hasDeleteIng,${addTabs(newTabs)}" +
                "\n${addTabs(newTabs)}\"ingredients\": [\n"
        val ingTabs = newTabs + 2
        for (ing in ingList)
        {
            result += "${addTabs(newTabs+1)}{\n${addTabs(ingTabs)}\"ingName\": \"${ing.key.name}" +
                    ",\n${addTabs(ingTabs)}\"qty\": ${ing.value}\n${addTabs(newTabs+1)}},\n"
        }
        if (!ingList.isEmpty())
        {
            result = result.substring(0,  result.length-2) + "\n"
        }
        result += "${addTabs(newTabs)}]\n${addTabs(tabs)}}"
        return result
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