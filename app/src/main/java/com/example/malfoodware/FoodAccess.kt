package com.example.malfoodware

interface FoodAccess {
    fun whatName(): String
    fun whatType(): FoodType
    fun whatServing(): Float
}