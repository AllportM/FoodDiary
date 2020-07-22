package com.example.malfoodware.DataLayer

import android.content.Context
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
class DiaryDBServiceTest                 {
    companion object {
        lateinit var context1: Context
        lateinit var dbHelper: FoodDBHelper
        var TIME1: Long
        var TIME2: Long
        val USER = UserDBServiceTest.USER1
        lateinit var ENTRY1: FoodDiaryEntry
        lateinit var ENTRY2: FoodDiaryEntry

        init {
            val date = Calendar.getInstance()
            date.set(2020, 6, 2, 12, 0, 0)
            TIME1 = date.timeInMillis
            date.set(2020, 6, 20, 12, 0, 0)
            TIME2 = date.timeInMillis
        }
    }

    @Before
    fun init()
    {
        context1 = InstrumentationRegistry.getInstrumentation().targetContext
        dbHelper = FoodDBHelper(context1)
        FileHelper.context = context1
        dbHelper.deleteAll()
        UserDBService.insertUser(dbHelper, USER)
        var ING1: Ingredient
        var ING2: Ingredient
        var ING3: Ingredient
        var ING4: Ingredient
        var ING5: Ingredient
        var ING6: Ingredient
        ING1 = Ingredient("Totilla", 30f, 2f, 64f, 1f, 0f,
        0f, 124f)
        ING2 = Ingredient("Mayonaise", 80f, 0.5f, 0.924f, 1f, 0f,
        0f, 66f)
        ING3 = Ingredient("Tomatoe Sauce", 80f, 0f, 7.08f, 0f, 0f,
        0f, 59f)
        ING4 = Ingredient("Cheese", 58f, 0.5f, 0.152f, 0f, 0f,
        0f, 152f)
        ING5 = Ingredient("Red Onion", 75f, 0f, 4.95f, 0f, 0f,
        0f, 55f)
        ING6 = Ingredient("Red Pepper", 20f, 0f, 2.16f, 0f, 0f,
        0f, 45f)
        IngredientDBService.insertIngredient(
            dbHelper,
            ING1
        )
        IngredientDBService.insertIngredient(
            dbHelper,
            ING2
        )
        IngredientDBService.insertIngredient(dbHelper, ING3)
        IngredientDBService.insertIngredient(dbHelper, ING4)
        IngredientDBService.insertIngredient(dbHelper, ING5)
        IngredientDBService.insertIngredient(dbHelper, ING6)
        var REC1: Recipe
        REC1 = Recipe("Fajita Pizza")
        REC1.addIngredient(ING1, 124f)
        REC1.addIngredient(ING2, 66f)
        REC1.addIngredient(ING3, 59f)
        REC1.addIngredient(ING4, 152f)
        REC1.addIngredient(ING5, 55f)
        REC1.addIngredient(ING6, 45f)

        ING1 = Ingredient("Bread", 70f, 0f, 19f, 0f, 0f,
        0f, 47f)
        ING2 = Ingredient("Honey Ham", 69f, 2f, 0.8f, 0f, 0f,
        0f, 25f)
        ING3 = Ingredient("Lettuce", 5f, 0f, 0.87f, 0f, 0f,
        0f, 30f)
        IngredientDBService.insertIngredient(dbHelper, ING1)
        IngredientDBService.insertIngredient(dbHelper, ING2)
        IngredientDBService.insertIngredient(dbHelper, ING3)

        var REC2: Recipe
        REC2 = Recipe("Ham, cheese, and lettuce sammich")
        REC2.addIngredient(ING1, 94f)
        REC2.addIngredient(ING2, 130f)
        REC2.addIngredient(ING3, 50f)
        REC2.addIngredient(ING4, 130f)
        RecipeDBService.insertRecipe(dbHelper, REC1)
        RecipeDBService.insertRecipe(dbHelper, REC2)

        // entries
        ENTRY1 = FoodDiaryEntry(TIME1)
        ENTRY1.addRecipe(REC1, 501f)
        DiaryDBService.insertDiaryEntry(dbHelper, ENTRY1, USER.uid)

        var newEntry = FoodDiaryEntry(TIME1 + (3600000))
        newEntry.addRecipe(REC2, 100f)
        DiaryDBService.insertDiaryEntry(dbHelper, newEntry, USER.uid)

        ENTRY2 = FoodDiaryEntry(TIME2)
        ENTRY2.addIngredient(ING1, 10f)
        ENTRY2.addRecipe(REC1, 10f)
        ENTRY2.addRecipe(REC2, 10f)
        DiaryDBService.insertDiaryEntry(dbHelper, ENTRY2, USER.uid)
    }

    @Test
    fun testGetDiaryEntriesDate1Success()
    {
        var set = DiaryDBService.getDiaryEntriesDate(dbHelper, USER.uid, ENTRY1.dateString)
        println("--------getTestDate1------\n$set")
        for (entry in set)
        {
            for (recipe in entry.recipes)
                println(recipe)
        }
        assertTrue(set.size == 2)
    }

    @Test
    fun testGetDiaryEntriesDate1Fail()
    {
        var set = DiaryDBService.getDiaryEntriesDate(dbHelper, USER.uid,"1/7/2020")
        println("--------getTest------\n$set")
        assertTrue(set.size == 0)
    }

    @Test
    fun testGetDiaryRange1Success()
    {
        var set = DiaryDBService.getDiaryEntriesDateRange(dbHelper, USER.uid, ENTRY1.dateString, "2/7/2020")
        println("--------getTestRange1------\n$set")
        assertTrue(set.size == 1)
    }

    @Test
    fun testGetDiaryRange2Success()
    {
        var set = DiaryDBService.getDiaryEntriesDateRange(dbHelper, USER.uid, ENTRY1.dateString, ENTRY2.dateString)
        println("--------getTest------\n$set")
        assertTrue(set.size == 3)
    }

    @Test
    fun insertDiaryEntryFailDuplicate()
    {
        assertFalse(DiaryDBService.insertDiaryEntry(dbHelper, ENTRY1, USER.uid))
    }

    @Test
    fun insertDiaryEntryFailRecFK()
    {
        var rec = Recipe("newRec")
        ENTRY1.addRecipe(rec, 10f)
        assertFalse(DiaryDBService.insertDiaryEntry(dbHelper, ENTRY1, USER.uid))
    }

    @Test
    fun insertDiaryEntryFailIngFK()
    {
        var ing = Ingredient("New Ing", 0f,0f,0f,0f,0f,
        0f,1f)
        ENTRY2.addIngredient(ing, 1f)
        assertFalse(DiaryDBService.insertDiaryEntry(dbHelper, ENTRY2, USER.uid))
    }

    @Test
    fun insertDiaryEntryFailUIDFK()
    {
        ENTRY1.timeMillis = ENTRY1.timeMillis + 200
        assertFalse(DiaryDBService.insertDiaryEntry(dbHelper, ENTRY2, "SomeUser"))
    }
}
