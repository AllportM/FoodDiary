package com.example.malfoodware.DataLayer

import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.example.malfoodware.*
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class NewRecipeWithDeletedIngredientTest                 {
    companion object {
        lateinit var context1: Context
        lateinit var dbHelper: FoodDBHelper
        val REC1_NAME = "Recipe 100"
        val REC2_NAME = "Recipe 2"
        val REC1 = Recipe(REC1_NAME)
        val REC2 = Recipe(REC2_NAME)
    }

    @Before
    fun init()
    {
        context1 = InstrumentationRegistry.getInstrumentation().targetContext
        dbHelper = FoodDBHelper(context1)
        FileHelper.context = context1
        dbHelper.deleteAll()
        IngredientDBService.insertIngredient(
            dbHelper,
            IngredientDBServiceTest.ING1
        )
        IngredientDBService.insertIngredient(
            dbHelper,
            IngredientDBServiceTest.ING2
        )
        REC1.addIngredient(IngredientDBServiceTest.ING1, 1f)
        REC1.addIngredient(IngredientDBServiceTest.ING2, 3f)
        REC2.addIngredient(IngredientDBServiceTest.ING2, 10f)
        RecipeDBService.insertRecipe(dbHelper, REC1)
        RecipeDBService.insertRecipe(dbHelper, REC2)
        Log.d("LOG", IngredientDBService.getIngredients(dbHelper).toString())
        Log.d("LOG", RecipeDBService.getRecipes(dbHelper).toString())
    }

    @Test
    fun testDeletedIngredient()
    {
        Log.d("LOG", IngredientDBService.deleteIngredient(dbHelper, IngredientDBServiceTest.ING1).toString())
        val recipe = RecipeDBService.getRecipe(dbHelper, REC1_NAME)
        Log.d("LOG", "$recipe")
        for (ing in recipe!!.ingList)
        {
            if(ing.key.hasBeenDeleted)
            {
                Log.d("LOG", "${ing.key}")
            }
        }
        assertTrue(recipe.hasDeleteIng)
        assertTrue(recipe.ingList.keys.contains(IngredientDBServiceTest.ING1))
    }
}