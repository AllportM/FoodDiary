package com.example.malfoodware

enum class FoodType
{
    INGREDIENT,
    RECIPE
}

class Ingredient(var name:String, energy:Float = 0f, fat:Float = 0f, carbs:Float =0f,
                 fibre:Float =0f, protein: Float = 0f, salt:Float = 0f, serving:Float = 0.01f):
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

    override fun equals(other: Any?): Boolean {
        return other is Ingredient && other.name.equals(name)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    fun toCSVString(): String {
        return ""
    }

    override fun toString(): String {
        return "Ingredient[name: $name, nut: $nut]"
    }

    override fun compareTo(other: Ingredient): Int {
        if (this.name > other.name) return 1 else return -1
    }
}
