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

@RunWith(AndroidJUnit4::class)
class UserDBServiceTest {
    companion object {
        lateinit var context1: Context
        lateinit var dbHelper: FoodDBHelper
        val USER1 = User("Mikehboi")
    }

    @Before
    fun init()
    {
        context1 = InstrumentationRegistry.getInstrumentation().targetContext
        dbHelper = FoodDBHelper(context1)
        FileHelper.context = context1
        dbHelper.deleteAll()
        UserDBService.insertUser(dbHelper, USER1)
        Log.d("LOG", "user json:\n${USER1.toJSON(1)}")
//        Logger.last()
    }

    @Test
    fun testGetUserSuccess()
    {
        var user = UserDBService.getUser(dbHelper, "mikehboi")
        assertTrue(user != null && user.uid.toLowerCase().equals("mikehboi"))
    }

    @Test
    fun testGetUserFailInjection()
    {
        assertFalse(UserDBService.getUser(dbHelper, "mikehboi;") != null)
    }

    @Test
    fun testGetUserFailNotFound()
    {
        assertTrue(UserDBService.getUser(dbHelper, "hey") == null)
    }

    @Test
    fun insertUserFailDuplicate()
    {
        assertFalse(UserDBService.insertUser(dbHelper, USER1))
    }

    @Test
    fun insertUserSuccess()
    {
        val user2 = User("New User")
        assertTrue(UserDBService.insertUser(dbHelper, user2))
        assertTrue(UserDBService.getUser(dbHelper, user2.uid)!!.nutritionPerDay.energy == 2000f)
    }

    @Test
    fun ammendUserSuccess()
    {
        val USER2 = USER1.copy()
        val nut = Nutrition(10f, 10f, 10f, 10f, 10f, 10f,
        10f)
        USER2.nutritionPerDay = nut
        assertTrue(dbHelper.ammendUser(USER2))
        val newUser = dbHelper.getUser(USER1.uid)
        assertTrue(newUser!!.nutritionPerDay.energy == 10f)
        assertTrue(dbHelper.ammendUser(USER1))
    }

}