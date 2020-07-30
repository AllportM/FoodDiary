package com.example.malfoodware

interface ViewIngredientsListInterface {
    fun updateSet(ingFrag: ViewInredientsFragment)
    fun onInsertFullIng(name: String, qty: Float)
}