package com.example.malfoodware

class Ingredient(var name:String, energy:Float, fat:Float, carbs:Float,
                 fibre:Float, protein: Float, salt:Float, serving:Float):
    Comparable<Ingredient>{
    val nut: Nutrition

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
