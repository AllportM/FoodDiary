package com.example.malfoodware

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.example.malfoodware.DataLayer.DiaryDBServiceTest
import com.example.malfoodware.DataLayer.UserDBServiceTest

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AppTest {
    lateinit var app: App
    val USER1 = UserDBServiceTest.USER1

    @Before
    fun init()
    {
        app = App(InstrumentationRegistry.getInstrumentation().targetContext)
        app.dbHelper.insertUser(USER1)
    }

    @Test
    fun login() {
        assertTrue(app.login("Mikehboi"))
        assertTrue(app.login("mikehboi"))
        assertFalse(app.login("john"))
    }
}