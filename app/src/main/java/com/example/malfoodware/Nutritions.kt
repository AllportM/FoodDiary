package com.example.malfoodware

enum class Nutritions {
    ENERGY, FAT, CARBS, FIBRE, PROTEIN, SALT
}

fun addTabs(times: Int): String{
    var output = ""
    repeat(times) { output += "\t" }
    return output
}

/**
 * Nutrition's purpose is to contain data members and functions relating to the nutritional values
 * of an ingredient/recipe, incluse of +, +=, /, /= operators and a member function to return
 * unit value based on serving weight
 */
data class Nutrition(var energy: Float = 0f, var fat:Float = 0f, var carbs:Float =0f,
                     var fibre:Float = 0f, var protein: Float = 0f, var salt:Float = 0f,
                     var serving:Float = 1f)
{
    /**
     * init ensures values are none negative and weight has value > 0 given divide by zero should
     * not occur
     */
    init
    {
        if (energy < 0f || fat < 0f || carbs < 0f || fibre < 0f || protein < 0f || salt < 0f ||
                serving < 0f || serving == 0f)
            throw InvalidInitializationRequest("Invalid initialization of " + this::class + " with" +
                    "values\nenergy= $energy\nfat= $fat\ncarbs= $carbs\nfibre= $fibre\nprotein= " +
                    "$protein\nsalt= $salt\nserving= $serving")
    }

    fun toUnit(): Nutrition
    {
        if (serving == 0f) throw DivideByZeroException("Error dividing by zero on $this")
        return Nutrition(energy / serving, fat / serving, carbs / serving,
        fibre / serving, protein / serving, salt / serving, serving / serving)
    }

    operator fun plus(other: Nutrition): Nutrition
    {
        return Nutrition(energy + other.energy, fat + other.fat, carbs + other.carbs,
        fibre + other.fibre, protein + other.protein, salt + other.salt,
        serving + other.serving)
    }

    operator fun plusAssign(other: Nutrition)
    {
        energy += other.energy
        fat += other.fat
        carbs += other.carbs
        fibre += other.fibre
        protein += other.protein
        salt += other.salt
        serving += other.serving
    }

    operator fun div(amount: Float): Nutrition
    {
        if (amount == 0f) throw DivideByZeroException("Error dividing by zero on $this")
        return Nutrition(energy * (amount / serving), fat * (amount / serving), carbs * (amount / serving),
        fibre * (amount / serving), protein * (amount / serving), salt * (amount / serving), serving * (amount / serving))
    }

    operator fun divAssign(amount: Float)
    {
        if (amount == 0f) throw DivideByZeroException("Error dividing by zero on $this")
        energy /= amount
        fat /= amount
        carbs /= amount
        fibre /= amount
        protein /= amount
        salt /= amount
        serving /= amount
    }

    fun toCSVString(): String {
        return "$energy,$fat,$carbs,$fibre,$protein,$salt,$serving"
    }

    fun toJSON(tabs: Int): String
    {
        var output = ""
        output += addTabs(tabs)
        output += "{\n"
        val newTabs = tabs + 1
        output += addTabs(newTabs)
        output += "\"energy\": $energy,\n${addTabs(newTabs)}\"fat\": $fat,\n${addTabs(newTabs)}" +
                "\"carbs\": $carbs,\n${addTabs(newTabs)}" +
                "\"fibre\": $fibre,\n${addTabs(newTabs)}\"protein\": $protein,\n${addTabs(newTabs)}" +
                "\"salt\": $salt,\n${addTabs(newTabs)}\"serving\": $serving\n${addTabs(tabs)}}"
        return output
    }

    override fun toString(): String {
        return "Nutrition[energy: $energy, fat: $fat, carbs: $carbs, fibre: $fibre," +
                " protein: $protein, salt: $salt, serving: $serving g ]"
    }
}