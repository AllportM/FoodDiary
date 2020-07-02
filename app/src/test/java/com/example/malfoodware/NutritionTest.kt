package com.example.malfoodware

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class NutritionTest {
    var nut: Nutrition = Nutrition(100f, 100f, 100f, 100f, 100f,
    100f, 100f)
    var nut2 = nut.copy()

    @Test
    fun toUnit() {
        var test: Nutrition = Nutrition(1f, 1f, 1f, 1f, 1f, 1f,
        1f)
        assertEquals(nut.toUnit() == (test), true)
    }

    @Test
    fun plus() {
        var nut3 = Nutrition(200f, 200f, 200f, 200f, 200f, 200f,
        200f)
        assertEquals(nut + nut2, nut3)
    }

    @Test
    fun plusAssign() {
        var nut3 = Nutrition(200f, 200f, 200f, 200f, 200f, 200f,
            200f)
        var nutTemp = nut.copy()
        nut.plusAssign(nut2)
        assertEquals(nut, nut3)
        nut = nutTemp
    }

    @Test
    fun div() {
        var test: Nutrition = Nutrition(1f, 1f, 1f, 1f, 1f, 1f,
            1f)
        var test2: Nutrition = nut / 100f
        assertEquals(test, test2)
    }

    @Test
    fun divAssign() {
        var test: Nutrition = Nutrition(1f, 1f, 1f, 1f, 1f, 1f,
            1f)
        var nutTemp = nut.copy()
        nut.divAssign(100f)
        assertEquals(nut, test)
        nut = nutTemp
    }
}