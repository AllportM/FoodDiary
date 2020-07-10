package com.example.malfoodware

import org.junit.Test

import org.junit.Assert.*

class LoggerTest {

    @Test
    fun add() {
        Logger.add("Test Log")
        Logger.saveContents()
    }
}