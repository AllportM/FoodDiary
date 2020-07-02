package com.example.malfoodware

import java.lang.NumberFormatException

/**
 * User's purpose is to contain data members correlating user file names, paths, settings. Contained
 * are also member functions to import recipes from given users csv files, or creates empty, aswell
 * as importing users ingredients, which are stored within member tree sets
 */
class User (var uid: String){
    val USER_PATH: String
    val USER_RECIPES: String
    val USER_INGREDIENTS: String
    val USER_SETTINGS: String
    var recipes: MutableMap<String, Recipe>
    var ingredients: MutableMap<String, Ingredient>
    var RECIPE_ID = 0 // used to index max recipe id value as to give new recipe ID's correct index
    var ING_ID = 0 // used to index max ingredient id value so as to give new ingredient ids correct index

    // settings
    var INSULIN_PER10G: Float = 1f

    companion object
    {
        val PATH = "res/users/"
    }

    init {
        recipes = mutableMapOf()
        ingredients = mutableMapOf()
        USER_PATH = PATH + uid + "/"
        USER_RECIPES = uid + "-Recipes.csv"
        USER_INGREDIENTS = uid + "-Ingredients.csv"
        USER_SETTINGS = uid + "-Settings.csv"
        loadSettings()
        loadUserIngredients()
        loadUserRecipes()
    }

    private fun loadSettings()
    {
        if (!FileHelper.loadFile(USER_SETTINGS, USER_PATH))
        {
            println("Created $uid settings file")
            saveSettings()
        }
        else
        loadSettings(FileHelper.getFileContentsArr(USER_SETTINGS))
    }

    private fun loadSettings(arr: MutableList<String>)
    {
        //TODO: actually load settings from a file
        var arrSplit = arr[0].split(",")
        INSULIN_PER10G = arrSplit[0].toFloat()
    }

    private fun saveSettings()
    {
        FileHelper.overrideContents(USER_SETTINGS, "$INSULIN_PER10G")
    }

    private fun loadUserRecipes()
    {
        if (!FileHelper.loadFile(USER_RECIPES, USER_PATH))
        {
            val recipe = Recipe("recID0", "Invalid Recipe!")
            recipes["recID0"] = recipe
            appendRecipe(recipe)
        }
        else loadUserRecipes(FileHelper.getFileContentsArr(USER_RECIPES))
    }

    private fun appendRecipe(recipe: Recipe)
    {
        FileHelper.writeContents(USER_RECIPES, recipe.toCSVString())
    }

    private fun loadUserRecipes(arr: MutableList<String>)
    {
        var maxRecip = 0
        arr.forEach {
            row ->
                val individRecipe = row.split(",")
                if (individRecipe.size >= 2 && individRecipe[0].substring(0, 5).equals("recID"))
                {
                    // checks for duplicates
                    val recID = individRecipe[0]
                    if (recipes.containsKey(individRecipe[0]))
                    {
                        println("Error parsing recipe, duplicate found," +
                                " from user: $uid id: $recID, line: $row")
                        return@forEach
                    }
                    //creates recipe and adds ingredient id's from split list
                    val recipe: Recipe = Recipe(recID, individRecipe[1])
                    try {
                        val recInt = Integer.parseInt(individRecipe[0].substring(5))
                        maxRecip = if (recInt > maxRecip) recInt else maxRecip
                        for (ingredient in 2 until individRecipe.size-1 step 2)
                        {
                            // checks if ingredient exists, if not loads invalid ingredient id
                            if (ingredients.containsKey(individRecipe[ingredient]))
                            {
                                val qty = individRecipe[ingredient + 1].toFloat()
                                recipe.addIngredient(individRecipe[ingredient], qty)
                            }
                            else
                            {
                                recipe.addIngredient("ingID0", 1f)
                                println("Recipe with invalid ingredient loaded, id= " +
                                    individRecipe[ingredient])
                            }
                        }
                        recipes[recID] = recipe
                    }
                    catch(e: NumberFormatException)
                    {
                        println("Error parsing recipe from user: $uid id: $recID, line: $row")
                    }
                }
                else println("Error parsing recipe from user: $uid line: $row")
        }
        RECIPE_ID = maxRecip
    }

    fun loadUserIngredients()
    {
        if (!FileHelper.loadFile(USER_INGREDIENTS, USER_PATH))
        {
            val ing = Ingredient("ingID0", "Invalid Ingredient!", 0f, 0f,
            0f, 0f, 0f, 0f, 1f)
            appendIngredient(ing)
        }
        else loadUserIngredients(FileHelper.getFileContentsArr(USER_INGREDIENTS))
    }

    fun appendIngredient(ing: Ingredient)
    {
        FileHelper.writeContents(USER_INGREDIENTS, ing.toCSVString())
    }

    fun loadUserIngredients(arr: MutableList<String>)
    {
        var maxIng = 0
        arr.forEach {
                row ->
                val individRecipe = row.split(",")
                if (individRecipe.size == 9 && individRecipe[0].substring(0, 5).equals("ingID"))
                {
                    val ingID = individRecipe[0]
                    if (ingredients.containsKey(ingID))
                    {
                        println("Error parsing recipe, duplicate found," +
                                " from user: $uid id: $ingID, line: $row")
                        return@forEach
                    }
                    val ingName = individRecipe[1]
                    try {
                        val recInt = Integer.parseInt(individRecipe[0].substring(5))
                        maxIng = if (recInt > maxIng) recInt else maxIng
                        // converts string values to floats
                        val energy: Float = individRecipe[2].toFloat()
                        val fat = individRecipe[3].toFloat()
                        val carbs = individRecipe[4].toFloat()
                        val fibre = individRecipe[5].toFloat()
                        val protein = individRecipe[6].toFloat()
                        val salt = individRecipe[7].toFloat()
                        val serving = individRecipe[8].toFloat()
                        val ing = Ingredient(ingID, ingName, energy, fat, carbs, fibre, protein,
                            salt, serving)
                        ingredients[ingID] = ing
                    }
                    catch(e: NumberFormatException)
                    {
                        println("Error parsing ingredient from user: $uid id: $ingID, line: $row")
                    }
                }
                else println("Error parsing ingredient from user: $uid line: $row")
        }
        ING_ID = maxIng
    }
}