package com.example.malfoodware

import org.junit.Test

import org.junit.Assert.*

class LoggerTest {
    val logger: Logger = Logger("logs.txt")

    @Test
    fun add() {
        logger.add("Test Log")
        logger.saveContents()
    }
}