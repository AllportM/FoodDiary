package com.example.malfoodware.DataLayer

import android.content.Context
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
class IngredientDBServiceTest {
    companion object {
        lateinit var context1: Context
        lateinit var dbHelper: FoodDBHelper
        val ING1_NAME = "Test Ingredient 1"
        val ING1 = Ingredient(
            ING1_NAME, 10f, 10f, 10f, 10f, 10f, 10f,
            1f
        )
        val ING2_NAME = "Test Ingredient 2"
        val ING2 = Ingredient(
            ING2_NAME, 0f, 0f, 0f, 0f, 0f, 0f,
            1f
        )
    }

    @Before
    fun init()
    {
        context1 = InstrumentationRegistry.getInstrumentation().targetContext
        dbHelper = FoodDBHelper(context1)
        FileHelper.context = context1
        dbHelper.deleteAll()
        IngredientDBService.insertIngredient(dbHelper, ING1)
        IngredientDBService.insertIngredient(dbHelper, ING2)
//        Logger.last()
    }

    @Test
    fun getIngredients()
    {
        var list = IngredientDBService.getIngredients(dbHelper)
        assertTrue(list.size == 2)
        println("------GetIngredientsTest--------")
        println(list)
    }

    @Test
    fun getIngredient1Fail()
    {
        var ing = IngredientDBService.getIngredient(dbHelper, "hey;")
        assertTrue(ing == null)
    }

    @Test
    fun getIngredient1Exists()
    {
        var ing = IngredientDBService.getIngredient(dbHelper, ING1_NAME)
        assertTrue(ing != null && ing.name == ING1_NAME)
    }

    @Test
    fun getIngredient1NotExists()
    {
        var ing = IngredientDBService.getIngredient(dbHelper, "No Ing")
        assertTrue(ing == null)
    }

    @Test
    fun getIngredient2Exists()
    {
        var ing = IngredientDBService.getIngredient(dbHelper, ING2_NAME)
        assertTrue(ing != null && ing.name == ING2_NAME)
    }

    @Test
    fun insertIngredientPass()
    {
        var ingNew = Ingredient("New", 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.0f)
        assertTrue(IngredientDBService.insertIngredient(dbHelper, ingNew))
        assertTrue(IngredientDBService.getIngredient(dbHelper, "new") != null)
    }

    @Test
    fun insertIngredientFail()
    {
        assertFalse(IngredientDBService.insertIngredient(dbHelper, ING1))
    }
}
