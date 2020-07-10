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
        val ING1 = IngredientDBServiceTest.ING1
        val ING2 = IngredientDBServiceTest.ING2
        val REC1 = RecipeDBServiceTest.REC1
        val REC2 = RecipeDBServiceTest.REC2
        lateinit var ENTRY1: FoodDiaryEntry
        lateinit var ENTRY2: FoodDiaryEntry

        init {
            val date = Calendar.getInstance()
            date.set(2020, 7, 2, 12, 0, 0)
            TIME1 = date.timeInMillis
            date.set(2020, 7, 20, 12, 0, 0)
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
        IngredientDBService.insertIngredient(
            dbHelper,
            ING1
        )
        IngredientDBService.insertIngredient(
            dbHelper,
            ING2
        )
        REC1.addIngredient(ING1, 1f)
        REC1.addIngredient(ING2, 1f)
        REC2.addIngredient(ING2, 10f)
        RecipeDBService.insertRecipe(dbHelper, REC1)
        RecipeDBService.insertRecipe(dbHelper, REC2)
        ENTRY1 = FoodDiaryEntry(TIME1)
        ENTRY2 = FoodDiaryEntry(TIME2)
        ENTRY1.addIngredient(ING1, 10f)
        ENTRY1.addRecipe(REC1, 10f)
        ENTRY2.addIngredient(ING2, 10f)
        ENTRY2.addIngredient(ING1, 10f)
        ENTRY2.addRecipe(REC1, 10f)
        ENTRY2.addRecipe(REC2, 10f)
        var x = DiaryDBService.insertDiaryEntry(dbHelper, ENTRY1, USER.uid)
        var y = DiaryDBService.insertDiaryEntry(dbHelper, ENTRY2, USER.uid)
        println("--------Insertions---------\nx:$x\ny:$y")
        Logger.last()
    }

    @Test
    fun testGetDiaryEntriesDate1Success()
    {
        var set = DiaryDBService.getDiaryEntriesDate(dbHelper, ENTRY2.dateString)
        println("--------getTest------\n$set")
        Logger.last()
        assertTrue(set.size == 1)
    }

    @Test
    fun testGetDiaryEntriesDate1Fail()
    {
        var set = DiaryDBService.getDiaryEntriesDate(dbHelper, "1/7/2020")
        println("--------getTest------\n$set")
        Logger.last()
        assertTrue(set.size == 0)
    }

    @Test
    fun testGetDiaryRange1Success()
    {
        var set = DiaryDBService.getDiaryEntriesDateRange(dbHelper, ENTRY1.dateString, "2/7/2020")
        println("--------getTest------\n$set")
        Logger.last()
        assertTrue(set.size == 1)
    }

    @Test
    fun testGetDiaryRange2Success()
    {
        var set = DiaryDBService.getDiaryEntriesDateRange(dbHelper, ENTRY1.dateString, ENTRY2.dateString)
        println("--------getTest------\n$set")
        Logger.last()
        assertTrue(set.size == 2)
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
