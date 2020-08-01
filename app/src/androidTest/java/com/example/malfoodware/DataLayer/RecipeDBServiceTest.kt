package com.example.malfoodware.DataLayer

import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.example.malfoodware.FileHelper
import com.example.malfoodware.Ingredient
import com.example.malfoodware.Logger
import com.example.malfoodware.Recipe
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeDBServiceTest {
    companion object {
        lateinit var context1: Context
        lateinit var dbHelper: FoodDBHelper
        val REC1_NAME = "Recipe 1"
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
        Logger.last()
        Log.d("LOG", "recipe json:\n${REC1.toJSON(1)}")
    }

    @Test
    fun getRecipes()
    {
        var list = RecipeDBService.getRecipes(dbHelper)
        println("----------getRecipesTest-----------")
        println(list)
        assertTrue(list.size == 2)
    }

    @Test
    fun getRecipe1Fail()
    {
        var rec = RecipeDBService.getRecipe(dbHelper, "hey;")
        assertTrue(rec == null)
    }

    @Test
    fun getRecipe1Success()
    {
        var rec = RecipeDBService.getRecipe(dbHelper, REC1_NAME)
        assertTrue(rec != null && rec.recName == REC1_NAME)
    }

    @Test
    fun getRecipe1NotExists()
    {
        var rec = RecipeDBService.getRecipe(dbHelper, "Nope")
        assertTrue(rec == null)
    }

    @Test
    fun insertRecipeSuccess()
    {
        val rec = Recipe("new")
        assertTrue(RecipeDBService.insertRecipe(dbHelper, rec))
    }

    @Test
    fun insertRecipeFailIngredientFK()
    {
        val rec = Recipe("new")
        val ing = Ingredient("hey", 0f, 0f, 0f, 0f, 0f,
        0f, 1f)
        rec.addIngredient(ing, 10f)
        assertFalse(RecipeDBService.insertRecipe(dbHelper, rec))
    }

    @Test
    fun insertRecipeFailDuplicate()
    {
        assertFalse(RecipeDBService.insertRecipe(dbHelper, REC1))
    }

    @Test
    fun insertRecipeFailDuplicate2()
    {
        val rec = Recipe(REC1_NAME)
        assertFalse(RecipeDBService.insertRecipe(dbHelper, rec))
    }
}
