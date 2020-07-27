package com.example.malfoodware

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.create_recipe_fragment.*

class CreateRecipeFragment: Fragment() {

    lateinit var activityApp: CreateRecipeActivityListener
    var ingList = mutableListOf<Pair<Ingredient, Float>>()
    lateinit var rvEntries: RecyclerView

    companion object
    {
        val FRAGMENT_ID = "createRecipeFragment"
    }

    interface CreateRecipeActivityListener
    {
        fun onRecipeAddIngredient()
        fun onRecipeFinalize()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("LOG", "${this::class.java} view created")
        val v = inflater.inflate(R.layout.create_recipe_fragment, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        Log.d("LOG", "${this::class.java} view created")
        super.onViewCreated(view, savedInstanceState)
        createRecipeAddIngredient.setOnClickListener {
            activityApp.onRecipeAddIngredient()
        }
        createRecipeFinishBut.setOnClickListener {
            if (checkInputValid())
                if (recipeExists())
                    createRecipeErrDuplicate.visibility = TextView.VISIBLE
                else
                    activityApp.onRecipeFinalize()
        }
        rvEntries = createRecipeInList
        rvEntries.layoutManager = LinearLayoutManager(context)
        rvEntries.adapter = CreateRecipeAdapter(ingList, this)
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        if (context is CreateRecipeActivityListener)
            activityApp = context
        else
            Log.d("LOG", "${this::class} error attaching activity listener")
    }

    fun removeIngredient(name: String)
    {
        val list = mutableListOf<Pair<Ingredient, Float>>()
        for (ing in ingList)
        {
            if (!ing.first.name.equals(name))
                list.add(Pair(ing.first, ing.second))
        }
        ingList = list
    }

    private fun recipeExists(): Boolean
    {
        val activity = requireActivity() as CreateFoodActivity
        val recipeName = createRecipeNameVal.text.toString()
        val portionQty = createRecipePortionQty.text.toString().toInt()
        val recipe = Recipe(recipeName, portionQty)
        for (pair in ingList)
        {
            recipe.addIngredient(pair.first, pair.second)
        }

        Log.d("LOG", "${this::class.java} attempting to insert recipe: $recipe")
        return !activity.app.dbHelper.insertRecipe(recipe)
    }

    private fun checkInputValid(): Boolean
    {
        hideErrors()
        Log.d("LOG", "${this::class.java} checking valid input")
        var valid = true
        if (ingList.size == 0)
        {
            createRecipeIngErr.visibility = TextView.VISIBLE
            valid = false
        }
        if (createRecipeNameVal.text.toString().equals(""))
        {
            createRecipeErrEmpty.visibility = TextView.VISIBLE
            valid = false
        }
        if (createRecipePortionQty.text.toString().equals(""))
        {
            createRecipePortionErr.visibility = TextView.VISIBLE
            valid = false
        }
        return valid
    }

    private fun hideErrors()
    {
        Log.d("LOG", "${this::class.java} hiding error messages")
        createRecipeErrDuplicate.visibility = TextView.GONE
        createRecipeErrEmpty.visibility = TextView.GONE
        createRecipeIngErr.visibility = TextView.GONE
        createRecipePortionErr.visibility = TextView.GONE
    }
}