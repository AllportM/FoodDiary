package com.example.malfoodware

import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Test

import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.util.logging.Logger

@RunWith(AndroidJUnit4::class)
class FoodDBHelperTest {
    private lateinit var context: Context
    private lateinit var dbHelper: FoodDBHelper

    @Before
    fun init()
    {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        dbHelper = FoodDBHelper(context)
        dbHelper.deleteAll()
    }

    @Test
    fun getIngredients() {
        var map = dbHelper.getIngredients()
        println(map)
    }

    @Test
    fun insertIngredient()
    {
        assertTrue(dbHelper.insertIngredient("Invalid Recipe"))
        assertTrue(dbHelper.insertIngredient("Mals Recipe"))
        assertFalse(dbHelper.insertIngredient("mals Recipe"))
        println(dbHelper.getIngredients())
    }

}