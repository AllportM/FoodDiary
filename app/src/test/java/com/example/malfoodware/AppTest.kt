package com.example.malfoodware

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class AppTest {

    var app = App()

    @Test
    fun login() {
        assertTrue(app.login("Mikehboi"))
        assertTrue(app.login("mikehboi"))
        assertTrue(app.login("john"))
        assertTrue(app.login("john smith"))
        assertFalse(app.login("Helen"))
    }
}