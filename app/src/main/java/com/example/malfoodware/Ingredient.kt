package com.example.malfoodware

enum class FoodType
{
    INGREDIENT,
    RECIPE
}

class Ingredient(var name:String, energy:Float = 0f, fat:Float = 0f, carbs:Float =0f,
                 fibre:Float =0f, protein: Float = 0f, salt:Float = 0f, serving:Float = 0.01f,
                    var hasBeenDeleted: Boolean = false):
    Comparable<Ingredient>, FoodAccess{
    val nut: Nutrition
    val type = FoodType.INGREDIENT

    init
    {
        try {
            nut = Nutrition(energy, fat, carbs, fibre, protein, salt, serving)
        }
        catch(e: InvalidInitializationRequest)
        {
            throw InvalidInitializationRequest("Error initializing " + this::class + " due to invalid" +
                    " nutrition values of:\n" + e.message)
        }
    }

    override fun whatName(): String {
        return name
    }

    override fun whatType(): FoodType {
        return type
    }

    override fun whatServing(): Float {
        return nut.serving
    }

    override fun whatNutirion(): Nutrition {
        return nut
    }

    override fun hasDeleted(): Boolean {
        return hasBeenDeleted
    }

    override fun equals(other: Any?): Boolean {
        return other is Ingredient && other.name.equals(name)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    fun toCSVString(): String {
        return ""
    }

    fun copy(): Ingredient
    {
        return Ingredient(name, nut.energy, nut.fat, nut.carbs, nut.fibre, nut.protein,
        nut.salt, nut.serving, hasBeenDeleted)
    }

    override fun toString(): String {
        return "Ingredient[name: $name, nut: $nut]"
    }

    fun toJSON(tabs: Int): String
    {
        val newTabs = tabs + 1
        return "${addTabs(tabs)}{\n${addTabs(newTabs)}\"name\": \"$name\",\n${addTabs(newTabs)}" +
                "\"hasBeenDeleted\": \"$hasBeenDeleted\",\n${addTabs(newTabs)}\"nutrition\": [\n" +
                "${nut.toJSON(newTabs+1)}\n${addTabs(newTabs)}]\n${addTabs(tabs)}}"
    }

    override fun compareTo(other: Ingredient): Int {
        if (this.name > other.name) return 1 else return -1
    }
}
