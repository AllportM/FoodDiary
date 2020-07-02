package com.example.malfoodware

class Ingredient(var id:String, var name:String, energy:Float, fat:Float, carbs:Float,
                 fibre:Float, protein: Float, salt:Float, serving:Float) {
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
        var output = ""
        output += "$id,$name," + nut.toCSVString()
        return output
    }
}