package com.example.malfoodware

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

enum class InsertionDeletion
{
    AMMEND,
    DELETE
}

class ViewIngredientEntryMiddleman(val activity: MainActivity, val type: FoodType): Fragment(), ViewIngredientsListInterface
{
    companion object
    {
        val INS_DEL_BUNDLE_NAME = "insDEL"
        val FRAGMENT_TAG = "ingredientMiddleMan"
    }

    override fun updateSet(ingFrag: ViewInredientsFragment) {
        when (type)
        {
            FoodType.INGREDIENT ->
                ingFrag.set = activity.app.dbHelper.getIngredients()
            FoodType.RECIPE ->
                ingFrag.set = activity.app.dbHelper.getRecipes()
        }
        // make new adapter
        ingFrag.rvEntries.adapter = ViewIngredientEditAdapter(ingFrag.set, ingFrag)
    }

    override fun onInsertFullIng(name: String, qty: Float) {
        // delete ingredient
        if (qty == 1f)
            when (type)
            {
                FoodType.INGREDIENT ->
                {
                    val deleted = activity.app.dbHelper.deleteIngredient(Ingredient(name))
                    if (deleted)
                        Log.d("LOG", "${this::class.java} successfully deleted inredient name: $name")
                    else
                        Log.d("LOG", "${this::class.java} error could not delete ingredient $name")
                }

                FoodType.RECIPE ->
                {
                    val deleted = activity.app.dbHelper.deleteRecipe(Recipe(name))
                    if (deleted)
                        Log.d("LOG", "${this::class.java} successfully deleted recipe name: $name")
                    else
                        Log.d("LOG", "${this::class.java} error could not delete recipe $name")
                }
            }
    }
}